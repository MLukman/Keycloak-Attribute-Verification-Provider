package my.unifi.eset.keycloak.attributes.verification.jpa;

import jakarta.persistence.EntityManager;
import java.util.Iterator;
import java.util.List;
import my.unifi.eset.keycloak.attributes.verification.UAVerificationValidatorProvider;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.jpa.entities.UserEntity;
import org.keycloak.models.utils.KeycloakModelUtils;

public class UAVerificationUtil {

    KeycloakSession session;
    EntityManager em;

    static public UAVerificationUtil forSession(KeycloakSession session) {
        return new UAVerificationUtil(session);
    }

    protected UAVerificationUtil(KeycloakSession session) {
        this.session = session;
        em = session.getProvider(JpaConnectionProvider.class).getEntityManager();
    }

    public EntityManager getEm() {
        return em;
    }

    public UserEntity getUserEntity(String id) {
        return em
                .createQuery("SELECT u FROM UserEntity u WHERE u.id = :id", UserEntity.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    public void cleanupAttributeUAVerificationEntities(UserEntity user, String attributeName) {
        em.createNamedQuery("getAllRecordsByUserAndAttributeName", UAVerificationEntity.class)
                .setParameter("user", user)
                .setParameter("attributeName", attributeName)
                .getResultStream().forEach((u) -> {
                    em.remove(u);
                });
    }

    public void insertNewUserAttributeVerificationEntity(UserEntity user, String attributeName, String resultAttributeName) {
        UAVerificationEntity uave = new UAVerificationEntity(KeycloakModelUtils.generateId(), user, attributeName);
        uave.setResultAttributeName(resultAttributeName);
        em.merge(uave);
    }

    public UAVerificationEntity getPendingVerificationEntity(UserEntity user) {
        UAVerificationEntity pendingVerification = null;
        List<UAVerificationEntity> pending = em
                .createNamedQuery("getPendingRecordsByUser", UAVerificationEntity.class)
                .setParameter("user", user)
                .getResultList();
        for (Iterator<UAVerificationEntity> it = pending.iterator(); it.hasNext() && pendingVerification == null;) {
            pendingVerification = it.next();
            if (UAVerificationValidatorProvider.getConfiguration(session, pendingVerification.getAttributeName()) == null
                    || pendingVerification.getAttributeEntity() == null) {
                // delete the UAVerificationEntity record if the attribute no longer requires verification
                em.remove(pendingVerification);
                pendingVerification = null;
            }
        }
        return pendingVerification;
    }
}
