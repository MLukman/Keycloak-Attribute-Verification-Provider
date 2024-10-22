package my.unifi.eset.keycloak.attributes.verification.method.sms;

import org.keycloak.models.RealmModel;

public class SmsVerificationConfiguration {

    final String endpoint;
    final String method;
    final String authorization;
    final String contentType;
    final String body;

    static public SmsVerificationConfiguration forRealm(RealmModel realm) {
        return new SmsVerificationConfiguration(
                realm.getAttribute("uav-sms.endpoint"),
                realm.getAttribute("uav-sms.method"),
                realm.getAttribute("uav-sms.authorization"),
                realm.getAttribute("uav-sms.content_type"),
                realm.getAttribute("uav-sms.body")
        );
    }

    static public void update(RealmModel realm, String endpoint, String method, String authorization, String contentType, String body) {
        realm.setAttribute("uav-sms.endpoint", endpoint);
        realm.setAttribute("uav-sms.method", method);
        realm.setAttribute("uav-sms.authorization", authorization);
        realm.setAttribute("uav-sms.content_type", contentType);
        realm.setAttribute("uav-sms.body", body);
    }

    SmsVerificationConfiguration(String endpoint, String method, String authorization, String contentType, String body) {
        this.endpoint = endpoint;
        this.method = method;
        this.authorization = authorization;
        this.contentType = contentType;
        this.body = body;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getMethod() {
        return method;
    }

    public String getAuthorization() {
        return authorization;
    }

    public String getContentType() {
        return contentType;
    }

    public String getBody() {
        return body;
    }

}
