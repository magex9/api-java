package ca.magex.crm.test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.authentication.CrmPasswordService;
import ca.magex.crm.api.authentication.basic.BasicAuthenticationService;
import ca.magex.crm.api.authentication.basic.BasicPasswordService;
import ca.magex.crm.api.observer.CrmUpdateNotifier;
import ca.magex.crm.api.policies.CrmPolicies;
import ca.magex.crm.api.policies.authenticated.AuthenticatedPolicies;
import ca.magex.crm.api.repositories.CrmRepositories;
import ca.magex.crm.api.repositories.basic.BasicRepositories;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.services.basic.BasicServices;
import ca.magex.crm.api.store.CrmStore;
import ca.magex.crm.api.store.basic.BasicStore;

@Configuration
public class BasicTestConfig {

	@Bean 
	public CrmStore store() {
		return new BasicStore();
	}
	
	@Bean 
	public CrmUpdateNotifier notifier() {
		return new CrmUpdateNotifier();
	}
	
	@Bean
	public CrmRepositories repos() {
		return new BasicRepositories(store(), notifier());
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
		return new BasicAuthenticationService(services(), passwords());
	}
	
	@Bean
	public CrmPasswordService passwords() {
		return new BasicPasswordService();
	}
	
	@Bean
	public Crm crm() {
		return new Crm(services(), policies());
	}
	
}