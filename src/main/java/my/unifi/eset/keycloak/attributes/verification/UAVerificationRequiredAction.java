package my.unifi.eset.keycloak.attributes.verification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.core.MultivaluedMap;
import java.util.Date;
import my.unifi.eset.keycloak.attributes.verification.jpa.UAVerificationEntity;
import my.unifi.eset.keycloak.attributes.verification.jpa.UAVerificationStatus;
import my.unifi.eset.keycloak.attributes.verification.jpa.UAVerificationUtil;
import my.unifi.eset.keycloak.attributes.verification.method.VerificationChallenge;
import my.unifi.eset.keycloak.attributes.verification.method.VerificationMethod;
import org.jboss.logging.Logger;
import org.keycloak.authentication.InitiatedActionSupport;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;
import org.keycloak.http.FormPartValue;
import org.keycloak.models.jpa.entities.UserEntity;
import org.keycloak.representations.userprofile.config.UPAttribute;

public class UAVerificationRequiredAction implements RequiredActionProvider {

    KeycloakSession session;
    EntityManager em;
    UAVerificationUtil util;

    private static final Logger logger = Logger.getLogger(UAVerificationRequiredAction.class);

    public UAVerificationRequiredAction(KeycloakSession session) {
        this.session = session;
        util = UAVerificationUtil.forSession(session);
        em = util.getEm();
    }

    @Override
    public void evaluateTriggers(RequiredActionContext rac) {
    }

    @Override
    public InitiatedActionSupport initiatedActionSupport() {
        return InitiatedActionSupport.SUPPORTED;
    }

    @Override
    public void requiredActionChallenge(RequiredActionContext rac) {
        UAVerificationEntity uav = util.getPendingVerificationEntity(util.getUserEntity(rac.getUser().getId()));
        if (uav != null) {
            prepareForm(rac, uav);
        } else {
            rac.getUser().removeRequiredAction(rac.getAction());
            rac.ignore();
        }
    }

    @Override
    public void processAction(RequiredActionContext rac) {
        UserEntity user = util.getUserEntity(rac.getUser().getId());
        UAVerificationEntity uav = util.getPendingVerificationEntity(user);
        if (uav == null) {
            rac.success();
        } else {
            MultivaluedMap<String, FormPartValue> formPost = rac.getHttpRequest().getMultiPartFormParameters();
            switch (formPost.get("action").get(0).asString()) {
                case "generate-send-otp" -> {
                    generateSendChallenge(rac, uav);
                }
                case "verify" -> {
                    verifyChallengeResponse(rac, uav, formPost.get("challenge").get(0).asString());
                }
                case "cancel" -> {
                    cancelVerification(rac, uav);
                }
            }
        }
    }

    protected void generateSendChallenge(RequiredActionContext rac, UAVerificationEntity uav) {
        UPAttribute upa = UAVerificationValidatorProvider.getUPAttribute(session, uav.getAttributeName());
        VerificationMethod method = UAVerificationValidatorProvider.getVerificationMethod(session, uav.getAttributeName());
        VerificationChallenge generatedChallengeValue = null;
        try {
            if (method != null) {
                debug("GENERATE_CHALLENGE", "Started", uav, method);
                if ((generatedChallengeValue = method.initiate(uav.getAttributeEntity(), upa)) != null) {
                    debug("GENERATE_CHALLENGE", "Success", uav, method);
                    uav.setChallengeValue(new ObjectMapper().writeValueAsString(generatedChallengeValue));
                    uav.setStatus(UAVerificationStatus.IN_PROGRESS);
                }
            }
            if (generatedChallengeValue == null) {
                debug("GENERATE_CHALLENGE", "Failure", uav, method);
                rac.getAuthenticationSession().setAuthNote("error", "Unable to generate & send verification challenge. Please click cancel and try again.");
            }
        } catch (Exception ex) {
            logger.error(String.format("Unable to generate & send verification challenge for attribute %s of user %s using method %s", uav.getAttributeName(), uav.getUser().getUsername(), method != null ? method.getClass().getName() : "<Unknown>"), ex);
            rac.getAuthenticationSession().setAuthNote("error", "Unable to generate & send verification challenge. Please click cancel and try again.");
        }
        prepareForm(rac, uav);
    }

    protected void verifyChallengeResponse(RequiredActionContext rac, UAVerificationEntity uav, String challengeResponse) {
        String attributeName = uav.getAttributeName();
        VerificationMethod method = UAVerificationValidatorProvider.getVerificationMethod(session, attributeName);
        if (method == null) {
            util.cleanupAttributeUAVerificationEntities(uav.getUser(), attributeName);
            rac.success();
            return;
        }
        try {
            debug("VERIFY_RESPONSE", "Started", uav, method);
            if (method.verifyResponse(challengeResponse, new ObjectMapper().readValue(uav.getChallengeValue(), method.getVerificationChallengeClass()))) {
                debug("VERIFY_RESPONSE", "Success", uav, method);
                uav.setStatus(UAVerificationStatus.SUCCESS);
                rac.getUser().setSingleAttribute(uav.getResultAttributeName(), Long.toString(new Date().getTime()));
                // fetch next attribute that is pending verification
                uav = util.getPendingVerificationEntity(uav.getUser());
            } else {
                debug("VERIFY_RESPONSE", "Failure", uav, method);
                rac.getAuthenticationSession().setAuthNote("error", "Invalid challenge value");
            }
        } catch (JsonProcessingException ex) {
            rac.getAuthenticationSession().setAuthNote("error", "Failed to fetch the generated challenge value");
            logger.errorf(ex, "Failed to verify verification challenge response for attribute %s of user %s using method %s", attributeName, uav.getUser().getUsername(), method.getClass().getName());
        } catch (Exception ex) {
            rac.getAuthenticationSession().setAuthNote("error", ex.getMessage());
            logger.errorf(ex, "Failed to verify verification challenge response for attribute %s of user %s using method %s", attributeName, uav.getUser().getUsername(), method.getClass().getName());
        }

        if (uav == null) {
            rac.success();
        } else {
            prepareForm(rac, uav);
        }
    }

    protected void cancelVerification(RequiredActionContext rac, UAVerificationEntity uav) {
        em.remove(uav.getAttributeEntity());
        em.remove(uav);
        rac.getUser().addRequiredAction(UserModel.RequiredAction.UPDATE_PROFILE);
        rac.success();
    }

    protected void prepareForm(RequiredActionContext rac, UAVerificationEntity uav) {
        UPAttribute upa = UAVerificationValidatorProvider.getUPAttribute(session, uav.getAttributeName());
        LoginFormsProvider response = rac.form();
        response.setAttribute("attributeConfig", upa);
        response.setAttribute("attributeValue", uav.getAttributeValue());
        response.setAttribute("challengeSent", uav.getChallengeValue() != null);
        response.setAttribute("challengeMessage", generateChallengeMessage(uav, upa));
        response.setAttribute("challengeError", rac.getAuthenticationSession().getAuthNote("error"));
        rac.challenge(response.createForm("attribute_verifier.ftl"));
        rac.getAuthenticationSession().removeAuthNote("error");
        debug("CHALLENGE_FORM", "Displayed", uav, null);
    }

    String generateChallengeMessage(UAVerificationEntity uav, UPAttribute upa) {
        VerificationMethod method = UAVerificationValidatorProvider.getVerificationMethod(session, uav.getAttributeName());
        return uav.getChallengeValue() == null
                ? method.generatePreInitiateMessage(uav.getAttributeEntity(), upa)
                : method.generatePostInitiateMessage(uav.getAttributeEntity(), upa);
    }

    @Override
    public void close() {
    }

    void debug(String event, String status, UAVerificationEntity uav, VerificationMethod method) {
        if (method != null) {
            logger.debugf("User: %s, Attribute: %s, Method %s, Event: %s, Status: %s", uav.getUser().getUsername(), uav.getAttributeName(), method.getClass().getName(), event, status);
        } else {
            logger.debugf("User: %s, Attribute: %s, Event: %s, Status: %s", uav.getUser().getUsername(), uav.getAttributeName(), event, status);
        }
    }

}
