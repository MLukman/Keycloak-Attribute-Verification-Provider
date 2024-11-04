package my.unifi.eset.keycloak.attributes.verification.jpa;

import java.util.Collections;
import java.util.List;
import org.keycloak.Config;
import org.keycloak.connections.jpa.entityprovider.JpaEntityProvider;
import org.keycloak.connections.jpa.entityprovider.JpaEntityProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

/**
 * Apply the Liquibase changelog that contains the database table definition for
 * USER_ATTRIBUTE_VERIFICATION and register the corresponding entity class.
 */
public class UAVerificationEntityProvider implements JpaEntityProviderFactory, JpaEntityProvider {

    @Override
    public JpaEntityProvider create(KeycloakSession ks) {
        return this;
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
    public String getId() {
        return "UAV";
    }

    @Override
    public List<Class<?>> getEntities() {
        return Collections.<Class<?>>singletonList(UAVerificationEntity.class);
    }

    @Override
    public String getChangelogLocation() {
        return "META-INF/user_attribute_verification.xml";
    }

    @Override
    public String getFactoryId() {
        return "USER_ATTRIBUTE_VERIFICATION";
    }

}
