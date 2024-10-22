package my.unifi.eset.keycloak.attributes.verification.method.sms;

import my.unifi.eset.keycloak.attributes.verification.method.VerificationMethodProvider;
import org.keycloak.models.KeycloakSession;

public class SmsVerificationMethodProvider implements VerificationMethodProvider<SmsVerificationMethod> {

    protected KeycloakSession session;
    protected SmsVerificationMethod method;

    public SmsVerificationMethodProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public SmsVerificationMethod getVerificationMethod() {
        if (method == null) {
            method = new SmsVerificationMethod(session);
        }
        return method;
    }

    @Override
    public void close() {
    }

}
