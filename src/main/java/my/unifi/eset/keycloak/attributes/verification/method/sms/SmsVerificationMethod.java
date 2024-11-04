package my.unifi.eset.keycloak.attributes.verification.method.sms;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import my.unifi.eset.keycloak.attributes.verification.method.VerificationChallenge;
import my.unifi.eset.keycloak.attributes.verification.method.VerificationMethod;
import org.apache.commons.text.StringSubstitutor;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.jpa.entities.UserAttributeEntity;
import org.keycloak.representations.userprofile.config.UPAttribute;

public class SmsVerificationMethod implements VerificationMethod {

    protected KeycloakSession session;

    public SmsVerificationMethod(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public VerificationChallenge initiate(UserAttributeEntity uae, UPAttribute upa) throws Exception {
        VerificationChallenge verificationChallenge = new VerificationChallenge();
        String message = "[RM0] OTP "
                + verificationChallenge.getChallengeValue()
                + " to verify your "
                + upa.getDisplayName()
                + " @ "
                + session.getContext().getRealm().getDisplayName();

        SmsVerificationConfiguration configuration = SmsVerificationConfiguration.forRealm(session.getContext().getRealm());
        String smsGatewayEndpoint = configuration.getEndpoint();
        String smsGatewayHttpMethod = configuration.getMethod();
        String smsGatewayAuthorizationHttpHeader = configuration.getAuthorization();
        String smsGatewayContenTypeHttpHeader = configuration.getContentType();
        String smsGatewayBody = StringSubstitutor.replace(
                configuration.getBody(),
                Map.ofEntries(
                        java.util.Map.entry("dest", uae.getValue().replaceAll("[^0-9]", "")),
                        java.util.Map.entry("message", message),
                        java.util.Map.entry("realm", session.getContext().getRealm().getDisplayName()),
                        java.util.Map.entry("field", upa.getDisplayName()),
                        java.util.Map.entry("otp", verificationChallenge.getChallengeValue())
                )
        );

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(smsGatewayEndpoint))
                .method(smsGatewayHttpMethod, HttpRequest.BodyPublishers.ofString(smsGatewayBody))
                .timeout(Duration.ofSeconds(10));
        if (smsGatewayAuthorizationHttpHeader != null && !smsGatewayAuthorizationHttpHeader.isBlank()) {
            requestBuilder.setHeader("Authorization", smsGatewayAuthorizationHttpHeader);
        }
        if (smsGatewayContenTypeHttpHeader != null && !smsGatewayContenTypeHttpHeader.isBlank()) {
            requestBuilder.setHeader("Content-Type", smsGatewayContenTypeHttpHeader);
        }
        HttpClient.newHttpClient().send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
        return verificationChallenge;
    }

    @Override
    public String generatePreInitiateMessage(UserAttributeEntity uae, UPAttribute upa) {
        return "Click the button below to proceed to send a challenge text via SMS to your " + upa.getDisplayName().toLowerCase() + ". Please be ready to check your phone.";
    }

    @Override
    public String generatePostInitiateMessage(UserAttributeEntity uae, UPAttribute upa) {
        return "A challenge text has been sent to via SMS to your " + upa.getDisplayName().toLowerCase() + ". Please check your phone for the SMS message that contains the challenge value.";
    }
}
