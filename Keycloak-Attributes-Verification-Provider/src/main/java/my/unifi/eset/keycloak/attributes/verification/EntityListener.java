package my.unifi.eset.keycloak.attributes.verification;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import my.unifi.eset.keycloak.attributes.verification.jpa.UAVerificationEntity;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import org.keycloak.models.jpa.entities.UserAttributeEntity;

import java.util.Arrays;
import java.util.UUID;

/**
 * This entity listener monitors for any new user attribute value and flag it for
 * verification process.
 */
public class EntityListener implements Integrator, PostInsertEventListener {

    @Override
    public void integrate(Metadata metadata, BootstrapContext bootstrapContext, SessionFactoryImplementor sessionFactory) {
        EventListenerRegistry eventListenerRegistry = sessionFactory
                .getServiceRegistry()
                .getService(EventListenerRegistry.class);
        eventListenerRegistry.appendListeners(EventType.POST_INSERT, this);
    }

    @Override
    public void disintegrate(SessionFactoryImplementor sfi, SessionFactoryServiceRegistry sfsr) {
    }

    @Override
    public void onPostInsert(PostInsertEvent pie) {
        if (pie.getEntity() instanceof UserAttributeEntity uae && isVerificationRequired(uae)) {
            EntityManager em = pie.getSession().getSessionFactory().createEntityManager();
            try {
                EntityTransaction transaction = em.getTransaction();
                transaction.begin();
                cleanupUserAttributeVerificationEntities(em, uae);
                insertNewUserAttributeVerificationEntity(em, uae, uae.getName() + "-verified", transaction);
                transaction.commit();
            } catch (Exception e) {
                // Handle the exception
                System.out.println("An error occurred: " + e.getMessage());
            } finally {
                em.close();
            }
        }
    }

    public boolean isVerificationRequired(UserAttributeEntity userAttributeEntity) {
        // Check if this attribute requires verification
        // For example, let's say verification is required for attributes with the name "phone"
        String[] verificationRequiredAttributes = {"phone"};
        return Arrays.asList(verificationRequiredAttributes).contains(userAttributeEntity.getName());
    }

    public void cleanupUserAttributeVerificationEntities(EntityManager em, UserAttributeEntity uae) {
        // Delete existing UserAttributeVerificationEntity
        Query query = em.createQuery("DELETE FROM UAVerificationEntity uave WHERE uave.user = :user AND uave.attributeName = :attributeName");
        query.setParameter("user", uae.getUser());
        query.setParameter("attributeName", uae.getName());
        query.executeUpdate();
    }

    public void insertNewUserAttributeVerificationEntity(EntityManager em, UserAttributeEntity uae, String resultAttributeName, Object verificationId) {
        // Create new UserAttributeVerificationEntity
        UAVerificationEntity verificationEntity = new UAVerificationEntity( generateRandomId(), uae.getUser(),  uae.getName());
        verificationEntity.setResultAttributeName(resultAttributeName);
        em.persist(verificationEntity);
    }

    public String generateRandomId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public boolean requiresPostCommitHandling(EntityPersister ep) {
        return true;
    }
}