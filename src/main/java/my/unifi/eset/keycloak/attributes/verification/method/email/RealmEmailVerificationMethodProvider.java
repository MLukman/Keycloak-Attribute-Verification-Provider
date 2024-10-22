package my.unifi.eset.keycloak.attributes.verification.method.email;

import org.keycloak.models.KeycloakSession;
import my.unifi.eset.keycloak.attributes.verification.method.VerificationMethodProvider;

public class RealmEmailVerificationMethodProvider implements VerificationMethodProvider<RealmEmailVerificationMethod> {

    protected KeycloakSession session;
    protected RealmEmailVerificationMethod method;

    public RealmEmailVerificationMethodProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public RealmEmailVerificationMethod getVerificationMethod() {
        if (method == null) {
            method = new RealmEmailVerificationMethod(session);
        }
        return method;
    }

    @Override
    public void close() {
    }

}
