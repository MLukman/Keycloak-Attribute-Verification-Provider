package my.unifi.eset.keycloak.attributes.verification.jpa;

import java.util.ArrayList;
import java.util.List;
import org.keycloak.provider.ConfiguredProvider;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.validate.AbstractSimpleValidator;
import org.keycloak.validate.ValidationContext;
import org.keycloak.validate.ValidatorConfig;


public class PiiDataEncryptionValidatorProvider extends AbstractSimpleValidator implements ConfiguredProvider {

    @Override
    public String getId() {
        return "Attribute-Verification";
    }

    @Override
    public String getHelpText() {
        return "This validator validate either an attribute needs a verification or not.";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return new ArrayList<>();
    }

    @Override
    protected boolean skipValidation(Object o, ValidatorConfig vc) {
        return true;
    }

    @Override
    protected void doValidate(Object o, String string, ValidationContext vc, ValidatorConfig vc1) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
