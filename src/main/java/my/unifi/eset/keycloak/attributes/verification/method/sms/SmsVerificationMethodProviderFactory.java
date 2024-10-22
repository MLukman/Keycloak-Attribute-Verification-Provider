package my.unifi.eset.keycloak.attributes.verification.method.sms;

import my.unifi.eset.keycloak.attributes.verification.method.VerificationMethodProviderFactory;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class SmsVerificationMethodProviderFactory implements VerificationMethodProviderFactory<SmsVerificationMethod> {

    @Override
    public String getId() {
        return "sms-otp";
    }

    @Override
    public SmsVerificationMethodProvider create(KeycloakSession ks) {
        return new SmsVerificationMethodProvider(ks);
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
