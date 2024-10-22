package my.unifi.eset.keycloak.attributes.verification.method.email;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import my.unifi.eset.keycloak.attributes.verification.method.VerificationMethodProvider;
import my.unifi.eset.keycloak.attributes.verification.method.VerificationMethodProviderFactory;

public class RealmEmailVerificationMethodProviderFactory implements VerificationMethodProviderFactory<RealmEmailVerificationMethod> {

    @Override
    public String getId() {
        return "email-otp-using-realm-email";
    }

    @Override
    public VerificationMethodProvider<RealmEmailVerificationMethod> create(KeycloakSession ks) {
        return new RealmEmailVerificationMethodProvider(ks);
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

}
