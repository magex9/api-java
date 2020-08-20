package ca.magex.crm.auth.config;

import javax.annotation.PostConstruct;

import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.CrmProfiles;
import ca.magex.crm.api.authentication.basic.BasicPasswordService;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.config.CrmConfigurer;
import ca.magex.crm.api.observer.basic.BasicEventObserver;
import ca.magex.crm.api.policies.CrmPolicies;
import ca.magex.crm.api.policies.basic.BasicPolicies;
import ca.magex.crm.api.repositories.basic.BasicPasswordRepository;
import ca.magex.crm.api.repositories.basic.BasicRepositories;
import ca.magex.crm.api.services.basic.BasicConfigurationService;
import ca.magex.crm.api.services.basic.BasicServices;
import ca.magex.crm.api.store.basic.BasicPasswordStore;
import ca.magex.crm.api.store.basic.BasicStore;

@Configuration
@Profile(CrmProfiles.DEV)
@Description("Configures the CRM used by the Auth Server")
public class DevCrmConfig implements CrmConfigurer {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
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

	@Bean(autowireCandidate = false) // ensure this bean doesn't conflict with our CRM for autowiring
	public BasicServices services() {
		return new BasicServices(repos(), passwords());
	}

	@Bean
	public CrmPolicies policies() {
		return new BasicPolicies(services());
	}

	@Bean
	public BasicPasswordService passwords() {
		return new BasicPasswordService(repos(), passwordRepo(), passwordEncoder());
	}
	
	@Bean 
	public BasicConfigurationService config() {
		return new BasicConfigurationService(repos(), passwords());
	}

	@Bean
	@Override
	public Crm crm() {
		return new Crm(services(), policies());
	}
	
	@PostConstruct
	public void initialize() {
		LoggerFactory.getLogger(DevCrmConfig.class).info("Initializing CRM System for Dev");
		config().initializeSystem("System", new PersonName(null, "System", null, "Admin"), "root@localhost", "admin", "admin");
	}
}