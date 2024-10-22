package my.unifi.eset.keycloak.attributes.verification.method;

import org.keycloak.provider.Provider;
import org.keycloak.provider.ProviderFactory;
import org.keycloak.provider.Spi;

public class VerificationMethodSpi implements Spi {

    @Override
    public boolean isInternal() {
        return false;
    }

    @Override
    public String getName() {
        return "user-attribute-verification-method";
    }

    @Override
    public Class<? extends Provider> getProviderClass() {
        return VerificationMethodProvider.class;
    }

    @Override
    public Class<? extends ProviderFactory> getProviderFactoryClass() {
        return VerificationMethodProviderFactory.class;
    }

}
