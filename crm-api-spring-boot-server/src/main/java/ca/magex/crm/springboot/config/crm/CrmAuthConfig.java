package ca.magex.crm.springboot.config.crm;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.Profile;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.CrmProfiles;
import ca.magex.crm.api.authentication.basic.BasicPasswordService;
import ca.magex.crm.api.config.CrmConfigurer;
import ca.magex.crm.api.observer.basic.BasicUpdateObserver;
import ca.magex.crm.api.policies.authenticated.AuthenticatedPolicies;
import ca.magex.crm.api.repositories.basic.BasicPasswordRepository;
import ca.magex.crm.api.repositories.basic.BasicRepositories;
import ca.magex.crm.api.services.basic.BasicServices;
import ca.magex.crm.api.store.basic.BasicPasswordStore;
import ca.magex.crm.api.store.basic.BasicStore;
import ca.magex.crm.spring.security.auth.SpringSecurityAuthenticationService;

@Configuration
@Profile(CrmProfiles.CRM_AUTH)
@Description("Configures the CRM by adding caching support, and using the Authenticated Policies for CRM Processing")
public class CrmAuthConfig implements CrmConfigurer {	
 	
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
	public AuthenticatedPolicies policies() {
		return new AuthenticatedPolicies(auth(), services());
	}
	
	@Bean 
	public SpringSecurityAuthenticationService auth() {
		return new SpringSecurityAuthenticationService(services());
	}
	
	@Bean
	public BasicPasswordService passwords() {
		return new BasicPasswordService(passwordRepo());
	}
	
	@Bean
	@Override
	public Crm crm() {
		return new Crm(services(), policies());
	}
}