package my.unifi.eset.keycloak.attributes.verification.method;

import org.keycloak.provider.Provider;

public interface VerificationMethodProvider<T extends VerificationMethod> extends Provider {

    public T getVerificationMethod();

}
