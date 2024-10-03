package my.unifi.eset.keycloak.attributes.verification.jpa;

import org.keycloak.Config.Scope;
import org.keycloak.connections.jpa.entityprovider.JpaEntityProvider;
import org.keycloak.connections.jpa.entityprovider.JpaEntityProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class TestingJpaEntityProviderFactory implements JpaEntityProviderFactory {

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public JpaEntityProvider create(KeycloakSession arg0) {
		// TODO Auto-generated method stub
		return new TestingJpaEntityProvider();
	}

	@Override
	public String getId() {
        return "testing-jpa";
	}

	@Override
	public void init(Scope arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postInit(KeycloakSessionFactory arg0) {
		// TODO Auto-generated method stub
		
	}

   


}