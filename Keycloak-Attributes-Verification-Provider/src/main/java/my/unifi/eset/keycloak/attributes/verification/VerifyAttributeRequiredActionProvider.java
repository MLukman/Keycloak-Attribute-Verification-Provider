package my.unifi.eset.keycloak.attributes.verification;

import org.hibernate.query.Query;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;
import org.keycloak.models.jpa.entities.UserEntity;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.core.Response;
import my.unifi.eset.keycloak.attributes.verification.jpa.UAVerificationEntity;
import my.unifi.eset.keycloak.attributes.verification.jpa.UAVerificationStatus;
import org.keycloak.connections.jpa.JpaConnectionProvider;

import java.util.List;
import java.util.logging.Logger;

public class VerifyAttributeRequiredActionProvider implements RequiredActionProvider {

    private final EntityManager entityManager;
    private final FreemarkerTemplateRenderer templateRenderer;
    private static final Logger logger = Logger.getLogger(VerifyAttributeRequiredActionProvider.class.getName());

    // Constructor to initialize the template renderer and entity manager
    public VerifyAttributeRequiredActionProvider(KeycloakSession session, FreemarkerTemplateRenderer templateRenderer) {
        this.entityManager = session.getProvider(JpaConnectionProvider.class).getEntityManager();
        this.templateRenderer = templateRenderer;
    }

    @Override
    public void close() {
        // No-op, as we're using a container-managed EntityManager
    }

    @Override
    public void evaluateTriggers(RequiredActionContext context) {
        UAVerificationEntity pendingVerification = getPendingVerificationEntity(context.getUser());
        
        if (pendingVerification != null) {
            // Add the required action if there's a pending verification
            context.getUser().addRequiredAction(context.getAction());
            logger.info("Added required action for verification for user: " + context.getUser().getUsername());
        } else {
            // Remove the required action if no pending verification exists
            context.getUser().removeRequiredAction(context.getAction());
            logger.info("Removed required action for verification for user: " + context.getUser().getUsername());
        }
    }

    @Override
    public void requiredActionChallenge(RequiredActionContext context) {
        UAVerificationEntity uav = getPendingVerificationEntity(context.getUser());
        
        if (uav != null) {
            // Render the verification form using Freemarker and pass it to the context
            String form = templateRenderer.renderForm(uav, context);
            context.challenge((Response) context.form().setInfo(form));
            logger.info("Verification form rendered for user: " + context.getUser().getUsername());
        } else {
            // Handle cases where no pending verification is found
            logger.warning("No pending verification found for user: " + context.getUser().getUsername());
            throw new UnsupportedOperationException("No pending verification entity found");
        }
    }

    @Override
    public void processAction(RequiredActionContext context) {
        UAVerificationEntity uav = getPendingVerificationEntity(context.getUser());
        
        if (uav != null) {
            // Update the verification entity status to "COMPLETED"
            uav.setStatus(UAVerificationStatus.COMPLETED);
            entityManager.merge(uav);
            entityManager.flush();
            context.success();
            logger.info("Verification successfully processed for user: " + context.getUser().getUsername());
        } else {
            logger.severe("No pending verification entity found during action processing for user: " + context.getUser().getUsername());
            throw new UnsupportedOperationException("No pending verification entity found");
        }
    }

    /**
     * Retrieves the pending verification entity for the given user.
     *
     * @param userModel The user model for whom verification is being checked.
     * @return The pending UAVerificationEntity or null if no pending verification exists.
     */
    private UAVerificationEntity getPendingVerificationEntity(UserModel userModel) {
        String userId = userModel.getId();
        UserEntity user = entityManager.find(UserEntity.class, userId);
        
        if (user != null) {
            @SuppressWarnings("rawtypes")
            Query query = (Query) entityManager.createQuery(
                "SELECT uav FROM UAVerificationEntity uav WHERE uav.user = :user AND uav.status <> 'COMPLETED' ORDER BY uav.id");
            query.setParameter("user", user);

            @SuppressWarnings("unchecked")
            List<UAVerificationEntity> results = query.getResultList();
            
            return results.isEmpty() ? null : results.get(0);
        } else {
            logger.warning("User not found for ID: " + userId);
            return null;
        }
    }
}
