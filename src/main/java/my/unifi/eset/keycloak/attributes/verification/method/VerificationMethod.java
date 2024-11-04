package my.unifi.eset.keycloak.attributes.verification.method;

import org.keycloak.models.jpa.entities.UserAttributeEntity;
import org.keycloak.representations.userprofile.config.UPAttribute;

public interface VerificationMethod {

    default public Class<? extends VerificationChallenge> getVerificationChallengeClass() {
        return VerificationChallenge.class;
    }

    /**
     * Initiate the verification for the provided UserAttributeEntity to
     * generate VerificationChallenge
     *
     * @param uae The UserAttributeEntity that holds the attribute value
     * @param upa The UPAttribute (User Profile Attribute) that holds metadata
     * configuration for the attribute
     * @return The VerificationChallenge object to be referred to verify
     * response later
     */
    public VerificationChallenge initiate(UserAttributeEntity uae, UPAttribute upa) throws Exception;

    /**
     * Verify if the challenge response that user provides matches the
     * VerificationChallenge object generated during initiate()
     *
     * @param challengeResponse String that holds the challenge response
     * provided by user
     * @param storedVerificationChallenge The VerificationChallenge generated
     * during initiate()
     * @return Boolean true if matches, false otherwise
     */
    default public boolean verifyResponse(String challengeResponse, VerificationChallenge storedVerificationChallenge) {
        return storedVerificationChallenge.getChallengeValue().equalsIgnoreCase(challengeResponse);
    }

    public String generatePreInitiateMessage(UserAttributeEntity uae, UPAttribute upa);

    public String generatePostInitiateMessage(UserAttributeEntity uae, UPAttribute upa);

}
