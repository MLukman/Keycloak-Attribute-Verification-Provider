package my.unifi.eset.keycloak.attributes.verification.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import org.keycloak.models.jpa.entities.UserAttributeEntity;
import org.keycloak.models.jpa.entities.UserEntity;

@Entity
@Table(name = "USER_ATTRIBUTE_VERIFICATION")
@NamedQueries({
    @NamedQuery(name = "getPendingRecordsByUser", query = "SELECT v FROM UAVerificationEntity v WHERE v.user = :user AND v.statusValue < 99 ORDER BY v.createdTimestamp ASC"),
    @NamedQuery(name = "getAllRecordsByUserAndAttributeName", query = "SELECT v FROM UAVerificationEntity v WHERE v.user = :user AND v.attributeName = :attributeName ORDER BY v.createdTimestamp DESC")
})
public class UAVerificationEntity {

    @Id
    @Column(name = "ID", length = 36)
    protected String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    protected UserEntity user;

    @Column(name = "ATTRIBUTE_NAME", length = 255)
    protected String attributeName;

    @Column(name = "RESULT_ATTRIBUTE_NAME", length = 255)
    protected String resultAttributeName;

    @Column(name = "CHALLENGE_VALUE", columnDefinition = "TEXT")
    protected String challengeValue;

    @Column(name = "STATUS_VALUE")
    protected int statusValue;

    @Column(name = "STATUS_LABEL", length = 50)
    protected String statusLabel;

    @Column(name = "CREATED_TIMESTAMP")
    protected Long createdTimestamp;

    @Column(name = "STATUS_TIMESTAMP")
    protected Long statusTimestamp;

    public UAVerificationEntity() {
    }

    public UAVerificationEntity(String id, UserEntity user, String attributeName) {
        this.id = id;
        this.user = user;
        this.attributeName = attributeName;
        this.createdTimestamp = new Date().getTime();
        setStatus(UAVerificationStatus.NOT_STARTED);
    }

    public String getId() {
        return id;
    }

    public UserEntity getUser() {
        return user;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public String getAttributeValue() {
        Optional<UserAttributeEntity> attributeEntity = user.getAttributes().stream().filter(
                (t) -> t.getName().equalsIgnoreCase(attributeName)
        ).findFirst();
        return attributeEntity.isPresent() ? attributeEntity.get().getValue() : null;
    }

    public String getResultAttributeName() {
        return resultAttributeName;
    }

    public String getChallengeValue() {
        return challengeValue;
    }

    public int getStatusValue() {
        return statusValue;
    }

    public String getStatusLabel() {
        return statusLabel;
    }

    public Long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public Long getStatusTimestamp() {
        return statusTimestamp;
    }

    public void setResultAttributeName(String resultAttributeName) {
        this.resultAttributeName = resultAttributeName;
    }

    public void setStatus(UAVerificationStatus status) {
        this.statusValue = status.sequence;
        this.statusLabel = status.label;
        this.statusTimestamp = new Date().getTime();
    }

    public void setChallengeValue(String challengeValue) {
        this.challengeValue = challengeValue;
    }

    public UserAttributeEntity getAttributeEntity() {
        return user.getAttributes().stream().filter((t) -> t.getName().equalsIgnoreCase(attributeName)).findFirst().orElse(null);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UAVerificationEntity other = (UAVerificationEntity) obj;
        if (!Objects.equals(this.attributeName, other.attributeName)) {
            return false;
        }
        return Objects.equals(this.user, other.user);
    }

}
