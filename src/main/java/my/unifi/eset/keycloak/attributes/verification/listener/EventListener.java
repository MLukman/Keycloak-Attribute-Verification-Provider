package my.unifi.eset.keycloak.attributes.verification.listener;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import my.unifi.eset.keycloak.attributes.verification.UAVerificationRequiredActionFactory;
import my.unifi.eset.keycloak.attributes.verification.UAVerificationValidatorProvider;
import my.unifi.eset.keycloak.attributes.verification.jpa.UAVerificationUtil;
import static org.hibernate.query.results.ResultsHelper.attributeName;
import org.keycloak.Config;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.UserModel;
import org.keycloak.models.jpa.entities.UserEntity;
import org.keycloak.utils.KeycloakSessionUtil;

public class EventListener implements EventListenerProvider, EventListenerProviderFactory {

    protected enum Action {
        ADD,
        DELETE
    }

    @Override
    public String getId() {
        return "user-attribute-verification";
    }

    @Override
    public void onEvent(Event event) {
        if (event.getType() != EventType.REGISTER && event.getType() != EventType.UPDATE_PROFILE) {
            // non-relevant event
            return;
        }

        // initialize some variables
        KeycloakSession session = KeycloakSessionUtil.getKeycloakSession();
        UserModel user = session.users().getUserById(session.realms().getRealm(event.getRealmId()), event.getUserId());
        UAVerificationUtil util = UAVerificationUtil.forSession(session);
        UserEntity userEntity = util.getUserEntity(user.getId());

        // identify related attributes
        LinkedHashMap<String, Action> attributeActions = new LinkedHashMap<>();
        switch (event.getType()) {
            case REGISTER -> {
                Set<String> attributesWithValue = user.getAttributes().keySet();
                UAVerificationValidatorProvider.getOrderAttributeNames(session).forEach((k) -> {
                    if (attributesWithValue.contains(k)) {
                        attributeActions.put(k, Action.ADD);
                    }
                });
            }
            case UPDATE_PROFILE -> {
                event.getDetails().keySet().forEach((k) -> {
                    if (k.startsWith("updated_")) {
                        attributeActions.put(k.substring(8), Action.ADD);
                    } else if (k.startsWith("previous_")) {
                        String attributeName = k.substring(9);
                        if (!attributeActions.containsKey(attributeName)) {
                            attributeActions.put(attributeName, Action.DELETE);
                        }
                    }
                });
            }
        }

        // perform cleanup for all related attributes and then initialize for added ones
        for (Map.Entry<String, Action> attributeAction : attributeActions.entrySet()) {
            String attributeName = attributeAction.getKey();
            if (UAVerificationValidatorProvider.getConfiguration(session, attributeName) != null) {
                // delete existing verification result attribute if any
                user.removeAttribute(UAVerificationValidatorProvider.getResultAttribute(session, attributeName));
                // remove previous completed record from USER_ATTRIBUTE_VERIFICATION
                util.cleanupAttributeUAVerificationEntities(userEntity, attributeName);
                if (attributeAction.getValue() == Action.ADD) {
                    // insert record into USER_ATTRIBUTE_VERIFICATION table
                    util.insertNewUserAttributeVerificationEntity(userEntity, attributeName, UAVerificationValidatorProvider.getResultAttribute(session, attributeName));
                }
            }
        }

        if (null != util.getPendingVerificationEntity(userEntity)) {
            // activate required action to show form to user
            user.addRequiredAction(UAVerificationRequiredActionFactory.ID);
        }
    }

    @Override
    public void onEvent(AdminEvent ae, boolean bln) {
    }

    @Override
    public void close() {
    }

    @Override
    public EventListenerProvider create(KeycloakSession ks) {
        return this;
    }

    @Override
    public void init(Config.Scope scope) {
    }

    @Override
    public void postInit(KeycloakSessionFactory ksf) {
    }

}
