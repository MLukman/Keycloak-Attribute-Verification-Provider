package my.unifi.eset.keycloak.attributes.verification;

import org.keycloak.authentication.RequiredActionFactory;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class VerifyAttributeRequiredActionProviderFactory implements RequiredActionFactory {

    @Override
    public String getDisplayText() {
        return "Verify User Attribute";
    }

    @Override
    public RequiredActionProvider create(KeycloakSession session) {
        // Pass KeycloakSession to the VerifyAttributeRequiredActionProvider
        return new VerifyAttributeRequiredActionProvider(session, null);
    }

    @Override
    public void init(org.keycloak.Config.Scope config) {
        // Initialization logic, if needed
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // Post-initialization logic, if needed
    }

    @Override
    public void close() {
        // Cleanup resources, if needed
    }

    @Override
    public String getId() {
        return "verify-attribute-required-action"; // Unique ID for this provider
    }
}
