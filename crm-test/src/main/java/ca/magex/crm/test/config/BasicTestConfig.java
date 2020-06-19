package ca.magex.crm.test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.config.CrmConfigurer;
import ca.magex.crm.api.policies.CrmPolicies;
import ca.magex.crm.api.policies.basic.BasicPolicies;
import ca.magex.crm.api.repositories.CrmRepositories;
import ca.magex.crm.api.repositories.CrmStore;
import ca.magex.crm.api.repositories.basic.BasicRepositories;
import ca.magex.crm.api.repositories.basic.BasicStore;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.services.basic.BasicServices;

@Configuration
public class BasicTestConfig implements CrmConfigurer {
	
	@Bean 
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	};
	
	@Bean
	@Override
	public Crm crm() {
		CrmStore store = new BasicStore();
		CrmRepositories repos = new BasicRepositories(store, notifier);
		CrmServices services = new BasicServices(repos, passwords);
		CrmPolicies policies = new BasicPolicies(services);
		return new Crm(services, services, policies);
	}
	
}