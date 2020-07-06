package ca.magex.crm.test.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.authentication.basic.BasicPasswordService;
import ca.magex.crm.api.config.CrmConfigurer;
import ca.magex.crm.api.observer.basic.BasicUpdateObserver;
import ca.magex.crm.api.policies.basic.BasicPolicies;
import ca.magex.crm.api.repositories.basic.BasicPasswordRepository;
import ca.magex.crm.api.repositories.basic.BasicRepositories;
import ca.magex.crm.api.services.basic.BasicServices;
import ca.magex.crm.api.store.basic.BasicPasswordStore;
import ca.magex.crm.api.store.basic.BasicStore;

@Configuration
public class UnauthenticatedTestConfig implements CrmConfigurer {

	@Bean 
	public PlatformTransactionManager txManager() {
		return Mockito.mock(PlatformTransactionManager.class);
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
	public BasicUpdateObserver observer() {
		return new BasicUpdateObserver();
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
	public BasicServices services() {
		return new BasicServices(repos(), passwords());
	}
	
	@Bean
	public BasicPolicies policies() {
		return new BasicPolicies(services());
	}	
	
	@Bean
	public BasicPasswordService passwords() {
		return new BasicPasswordService(passwordRepo());
	}
	
	@Bean
	public Crm crm() {
		return new Crm(services(), policies());
	}
}