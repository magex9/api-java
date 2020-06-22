package ca.magex.crm.test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.authentication.CrmPasswordRepository;
import ca.magex.crm.api.authentication.basic.BasicPasswordRepository;
import ca.magex.crm.api.config.CrmConfigurer;
import ca.magex.crm.api.observer.CrmUpdateNotifier;
import ca.magex.crm.api.policies.basic.BasicPolicies;
import ca.magex.crm.api.repositories.basic.BasicRepositories;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.services.basic.BasicServices;
import ca.magex.crm.api.store.basic.BasicStore;

@Configuration
public class BasicTestConfig implements CrmConfigurer {
	
	@Bean
	public CrmPasswordRepository passwords() {
		return new BasicPasswordRepository();
	}
	
	@Bean
	public Crm crm() {
		CrmServices services = new BasicServices(
			new BasicRepositories(
				new BasicStore(), 
				new CrmUpdateNotifier()
			), 
			passwords()
		);
		return new Crm(services, new BasicPolicies(services));
	}
	
}