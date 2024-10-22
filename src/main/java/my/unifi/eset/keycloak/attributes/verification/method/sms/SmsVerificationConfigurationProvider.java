package my.unifi.eset.keycloak.attributes.verification.method.sms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.keycloak.Config;
import org.keycloak.component.ComponentModel;
import org.keycloak.component.ComponentValidationException;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.services.ui.extend.UiTabProvider;
import org.keycloak.services.ui.extend.UiTabProviderFactory;

public class SmsVerificationConfigurationProvider implements UiTabProvider, UiTabProviderFactory<SmsVerificationConfiguration> {

    static public final String ID = "Attribute Verification SMS";
    //static public final String[] FIELDS = {"endpoint", "method", "authorization", "content_type", "body"};

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getHelpText() {
        return "The settings for sending attribute verification SMS";
    }

    @Override
    public String getPath() {
        return "/:realm/realm-settings/:tab?";
    }

    @Override
    public Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        params.put("tab", "attribute-verification-sms");
        return params;
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
    public List<ProviderConfigProperty> getConfigProperties() {
        final ProviderConfigurationBuilder builder = ProviderConfigurationBuilder.create();
        builder.property()
                .name("endpoint")
                .label("API Endpoint")
                .helpText("(Required) The URL of the API endpoint")
                .type(ProviderConfigProperty.STRING_TYPE)
                .add();
        builder.property()
                .name("method")
                .label("HTTP Method")
                .helpText("The HTTP method to use to call the API endpoint")
                .type(ProviderConfigProperty.LIST_TYPE)
                .options("POST", "PUT")
                .add();
        builder.property()
                .name("authorization")
                .label("Authorization Header")
                .helpText("Authorization HTTP header to set if needed for API authentication")
                .type(ProviderConfigProperty.STRING_TYPE)
                .add();
        builder.property()
                .name("content_type")
                .label("Content-Type Header")
                .helpText("Content-Type HTTP header to set")
                .type(ProviderConfigProperty.STRING_TYPE)
                .add();
        builder.property()
                .name("request_body")
                .label("Request Body")
                .helpText("(Required) Request body that contains placeholders using the following variables: ${dest} for destination number (numeric digits only), ${message} for the SMS message, ${realm} for the realm name, ${field} for the field name, ${otp} for the OTP value")
                .type(ProviderConfigProperty.SCRIPT_TYPE)
                .add();
        return builder.build();
    }

    @Override
    public void validateConfiguration(KeycloakSession session, RealmModel realm, ComponentModel model) throws ComponentValidationException {
        List<String> errors = new ArrayList<>();
        if (model.get("endpoint", "").isBlank()) {
            errors.add("API endpoint is required");
        } else if (!model.get("endpoint").matches("^https?://.+")) {
            errors.add("API endpoint must be a valid URL");
        }
        if (model.get("request_body", "").isBlank()) {
            errors.add("Request body is required");
        }
        if (model.get("content_type", "").isBlank()) {
            errors.add("Content-Type is required");
        }
        if (!errors.isEmpty()) {
            throw new ComponentValidationException(errors.stream().reduce("Please fix the following issues: ", (t, u) -> t + u + ". "));
        }
    }

    @Override
    public void onCreate(KeycloakSession session, RealmModel realm, ComponentModel model) {
        saveConfiguration(session, realm, model);
        UiTabProviderFactory.super.onCreate(session, realm, model);
    }

    @Override
    public void onUpdate(KeycloakSession session, RealmModel realm, ComponentModel oldModel, ComponentModel newModel) {
        saveConfiguration(session, realm, newModel);
        UiTabProviderFactory.super.onUpdate(session, realm, oldModel, newModel);
    }

    void saveConfiguration(KeycloakSession session, RealmModel realm, ComponentModel model) {
        SmsVerificationConfiguration.update(
                realm,
                model.get("endpoint"),
                model.get("method"),
                model.get("authorization"),
                model.get("content_type"),
                model.get("request_body")
        );
    }

}
