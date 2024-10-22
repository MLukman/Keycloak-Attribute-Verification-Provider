package my.unifi.eset.keycloak.attributes.verification.method;

import org.keycloak.provider.ProviderFactory;

public interface VerificationMethodProviderFactory<T extends VerificationMethod> extends ProviderFactory<VerificationMethodProvider<T>> {

}
