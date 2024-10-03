package my.unifi.eset.keycloak.attributes.verification.jpa;

/**
 * The statuses of the user attribute verification that include both textual
 * label (for user friendliness) and numerical value (for simpler logic)
 */
public enum UAVerificationStatus {

    NOT_STARTED(0, "NOT STARTED"),
    IN_PROGRESS(1, "IN PROGRESS"),
    SUCCESS(99, "SUCCESS"), COMPLETED;

    public final int sequence;
    public final String label;

    private UAVerificationStatus(int sequence, String label) {
        this.sequence = sequence;
        this.label = label;
    }

    UAVerificationStatus() {
        this.sequence = 0;
        this.label = "";
        //TODO Auto-generated constructor stub
    }

    int getValue() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getValue'");
    }

    String getLabel() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getLabel'");
    }
}
