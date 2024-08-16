package my.unifi.eset.keycloak.attributes.verification;

import jakarta.persistence.EntityManager;
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
            cleanupUserAttributeVerificationEntities(em, uae);
            insertNewUserAttributeVerificationEntity(em, uae, uae.getName() + "-verified");
        }
    }

    protected boolean isVerificationRequired(UserAttributeEntity userAttributeEntity) {
        // TODO: Check if this attribute requires verification
        return false;
    }

    protected void cleanupUserAttributeVerificationEntities(EntityManager em, UserAttributeEntity uae) {
        // TODO: delete existing UserAttributeVerificationEntity
    }

    protected void insertNewUserAttributeVerificationEntity(EntityManager em, UserAttributeEntity uae, String resultAttributeName) {
        // TODO: Create new UserAttributeVerificationEntity
    }

    @Override
    public boolean requiresPostCommitHandling(EntityPersister ep) {
        return true;
    }

}
