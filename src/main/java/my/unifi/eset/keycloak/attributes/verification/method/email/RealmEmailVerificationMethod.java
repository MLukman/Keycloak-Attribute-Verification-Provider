package my.unifi.eset.keycloak.attributes.verification.method.email;

import my.unifi.eset.keycloak.attributes.verification.method.VerificationChallenge;
import org.keycloak.email.EmailSenderProvider;
import org.keycloak.models.KeycloakSession;
import my.unifi.eset.keycloak.attributes.verification.method.VerificationMethod;
import org.keycloak.models.jpa.entities.UserAttributeEntity;
import org.keycloak.representations.userprofile.config.UPAttribute;

public class RealmEmailVerificationMethod implements VerificationMethod {

    protected KeycloakSession session;

    public RealmEmailVerificationMethod(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public VerificationChallenge initiate(UserAttributeEntity uae, UPAttribute upa) throws Exception {
        VerificationChallenge verificationChallenge = new VerificationChallenge();
        String message = "To verify your "
                + upa.getDisplayName()
                + ", please respond the challenge using the following OTP: "
                + verificationChallenge.getChallengeValue();
        session.getProvider(EmailSenderProvider.class).send(
                session.getContext().getRealm().getSmtpConfig(),
                uae.getValue(),
                session.getContext().getRealm().getDisplayName() + ": Verify your " + upa.getDisplayName(),
                message,
                message);
        return verificationChallenge;
    }

    @Override
    public String generatePreInitiateMessage(UserAttributeEntity uae, UPAttribute upa) {
        return "Click the button below to proceed to send a challenge text to your " + upa.getDisplayName().toLowerCase() + ". Please be ready to check your inbox.";
    }

    @Override
    public String generatePostInitiateMessage(UserAttributeEntity uae, UPAttribute upa) {
        return "A challenge text has been sent to your " + upa.getDisplayName().toLowerCase() + ". Please check your inbox for the e-mail that contains the challenge value.";
    }

}
