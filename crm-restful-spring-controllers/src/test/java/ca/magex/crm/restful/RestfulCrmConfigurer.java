package ca.magex.crm.restful;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.authentication.basic.BasicAuthenticationService;
import ca.magex.crm.api.authentication.basic.BasicPasswordService;
import ca.magex.crm.api.config.CrmConfigurer;
import ca.magex.crm.api.observer.basic.BasicEventObserver;
import ca.magex.crm.api.policies.CrmPolicies;
import ca.magex.crm.api.policies.authenticated.AuthenticatedPolicies;
import ca.magex.crm.api.repositories.basic.BasicPasswordRepository;
import ca.magex.crm.api.repositories.basic.BasicRepositories;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.services.basic.BasicConfigurationService;
import ca.magex.crm.api.services.basic.BasicServices;
import ca.magex.crm.api.store.basic.BasicPasswordStore;
import ca.magex.crm.api.store.basic.BasicStore;
import ca.magex.crm.transform.json.JsonTransformerFactory;

@Configuration
public class RestfulCrmConfigurer implements CrmConfigurer {

	@Bean 
	public PlatformTransactionManager txManager() {
		return Mockito.mock(PlatformTransactionManager.class);
	}
	
	@Bean 
	public JsonTransformerFactory jsonTransformerFactory() {
		return new JsonTransformerFactory(services());
	}
	
	@Bean 
	public BasicStore store() {
		return new BasicStore();
	}
	
	@Bean
	public BasicPasswordStore passwordStore() {
		return new BasicPasswordStore();
	}
	
	@Bean 
	public BasicEventObserver observer() {
		return new BasicEventObserver();
	}
	
	@Bean
	public BasicRepositories repos() {
		return new BasicRepositories(store(), observer());
	}
	
	@Bean
	public BasicPasswordRepository passwordRepo() {
		return new BasicPasswordRepository(passwordStore());
	}
	
	@Bean 
	public CrmServices services() {
		return new BasicServices(repos(), passwords());
	}
	
	@Bean
	public CrmPolicies policies() {
		return new AuthenticatedPolicies(auth(), services());
	}
	
	@Bean 
	public BasicAuthenticationService auth() {
		return new BasicAuthenticationService(services(), services(), services(), passwords());
	}
	
	@Bean
	public BasicPasswordService passwords() {
		return new BasicPasswordService(repos(), passwordRepo());
	}

	@Bean
	public BasicConfigurationService config() {
		return new BasicConfigurationService(repos(), passwords());
	}
	
	@Bean
	public Crm crm() {
		return new Crm(services(), policies());
	}
	
}
