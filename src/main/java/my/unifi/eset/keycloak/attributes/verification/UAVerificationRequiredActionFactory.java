package my.unifi.eset.keycloak.attributes.verification;

import org.keycloak.Config;
import org.keycloak.authentication.RequiredActionFactory;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class UAVerificationRequiredActionFactory implements RequiredActionFactory {

    public static final String ID = "VERIFY_ATTRIBUTE";

    @Override
    public String getDisplayText() {
        return "Verify Attribute";
    }

    @Override
    public RequiredActionProvider create(KeycloakSession ks) {
        return new UAVerificationRequiredAction(ks);
    }

    @Override
    public void init(Config.Scope scope) {

    }

    @Override
    public void postInit(KeycloakSessionFactory ksf) {
    }

    @Override
    public void close() {
    }

    @Override
    public String getId() {
        return ID;
    }

}
