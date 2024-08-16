package my.unifi.eset.keycloak.attributes.verification.jpa;

/**
 * The statuses of the user attribute verification that include both textual
 * label (for user friendliness) and numerical value (for simpler logic)
 */
public enum UAVerificationStatus {

    NOT_STARTED(0, "NOT STARTED"),
    IN_PROGRESS(1, "IN PROGRESS"),
    SUCCESS(99, "SUCCESS");

    public final int sequence;
    public final String label;

    private UAVerificationStatus(int sequence, String label) {
        this.sequence = sequence;
        this.label = label;
    }
}
