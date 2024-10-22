package my.unifi.eset.keycloak.attributes.verification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.ConfiguredProvider;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.representations.userprofile.config.UPAttribute;
import org.keycloak.userprofile.DeclarativeUserProfileProvider;
import org.keycloak.userprofile.UserProfileProvider;
import org.keycloak.validate.AbstractSimpleValidator;
import org.keycloak.validate.ValidationContext;
import org.keycloak.validate.ValidatorConfig;
import my.unifi.eset.keycloak.attributes.verification.method.VerificationMethod;
import my.unifi.eset.keycloak.attributes.verification.method.VerificationMethodProvider;
import my.unifi.eset.keycloak.attributes.verification.method.VerificationMethodProviderFactory;
import static org.hibernate.query.results.ResultsHelper.attributeName;

public class UAVerificationValidatorProvider extends AbstractSimpleValidator implements ConfiguredProvider {

    public static final String ID = "attribute-verification";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getHelpText() {
        return "Verify this user attribute upon any modification by challenging the user to provide a six-digit number sent to the user.";
    }

    @Override
    protected void doValidate(Object o, String string, ValidationContext vc, ValidatorConfig vc1) {
    }

    @Override
    protected boolean skipValidation(Object o, ValidatorConfig vc) {
        return true;
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        List<String> methods = ServiceLoader.load(VerificationMethodProviderFactory.class).stream().map((t) -> t.get().getId()).toList();

        return ProviderConfigurationBuilder.create()
                .property()
                .name("method")
                .label("Method")
                .helpText("(Required) The method to use to send the challenge value to the user")
                .type(ProviderConfigProperty.LIST_TYPE)
                .options(methods)
                .required(true)
                .add()
                .property()
                .name("result")
                .label("Result Attribute")
                .helpText("(Required) The attribute to where the result of the verification will be stored")
                .type(ProviderConfigProperty.USER_PROFILE_ATTRIBUTE_LIST_TYPE)
                .required(true)
                .add()
                .build();
    }

    static public UPAttribute getUPAttribute(KeycloakSession session, String attributeName) {
        UserProfileProvider upp = session.getProvider(UserProfileProvider.class);
        if (upp instanceof DeclarativeUserProfileProvider dup) {
            return dup.getConfiguration().getAttribute(attributeName);
        }
        return null;
    }

    static public List<String> getOrderAttributeNames(KeycloakSession session) {
        UserProfileProvider upp = session.getProvider(UserProfileProvider.class);
        List<String> attributeNames = new ArrayList<>();
        if (upp instanceof DeclarativeUserProfileProvider dup) {
            dup.getConfiguration().getAttributes().forEach((t) -> {
                attributeNames.add(t.getName());
            });
        }
        return attributeNames;
    }

    static public Map<String, Object> getConfiguration(KeycloakSession session, String attributeName) {
        UPAttribute upa = getUPAttribute(session, attributeName);
        if (upa != null) {
            return upa.getValidations().get(ID);
        }
        return null;
    }

    static public VerificationMethod getVerificationMethod(KeycloakSession session, String attributeName) {
        Map<String, Object> uavConfig;
        VerificationMethodProvider methodProvider;
        if (null != (uavConfig = getConfiguration(session, attributeName))
                && null != (methodProvider = session.getProvider(VerificationMethodProvider.class, (String) uavConfig.get("method")))) {
            return methodProvider.getVerificationMethod();
        }
        return null;
    }

    static public String getResultAttribute(KeycloakSession session, String attributeName) {
        Map<String, Object> uavConfig;
        if (null != (uavConfig = getConfiguration(session, attributeName))) {
            String resultAttribute = ((String) uavConfig.getOrDefault("result", "")).strip();
            if (!resultAttribute.isBlank()) {
                return resultAttribute;
            }
        }
        return attributeName + "-verified";
    }
}
