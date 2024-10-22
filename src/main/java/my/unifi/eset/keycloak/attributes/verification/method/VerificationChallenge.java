package my.unifi.eset.keycloak.attributes.verification.method;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Random;

public class VerificationChallenge {

    String challengeValue;

    public VerificationChallenge() {
        this(Integer.toString(new Random().nextInt(900000) + 100000));
    }

    @JsonCreator
    public VerificationChallenge(@JsonProperty String challengeValue) {
        this.challengeValue = challengeValue;
    }

    @JsonGetter
    public String getChallengeValue() {
        return challengeValue;
    }

}
