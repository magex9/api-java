package ca.magex.crm.restful.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.CrmProfiles;
import ca.magex.crm.api.authentication.CrmAuthenticationService;
import ca.magex.crm.api.authentication.basic.BasicAuthenticationService;
import ca.magex.crm.api.authentication.basic.BasicPasswordService;
import ca.magex.crm.api.config.CrmConfigurer;
import ca.magex.crm.api.dictionary.CrmDictionary;
import ca.magex.crm.api.dictionary.basic.BasicDictionary;
import ca.magex.crm.api.observer.CrmUpdateObserver;
import ca.magex.crm.api.observer.basic.BasicUpdateObserver;
import ca.magex.crm.api.policies.CrmPolicies;
import ca.magex.crm.api.policies.authenticated.AuthenticatedPolicies;
import ca.magex.crm.api.repositories.CrmRepositories;
import ca.magex.crm.api.repositories.basic.BasicRepositories;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.services.basic.BasicServices;
import ca.magex.crm.api.store.CrmStore;
import ca.magex.crm.api.store.basic.BasicStore;

@Configuration
@Profile(CrmProfiles.CRM_NO_AUTH)
public class RestfulCrmNoAuthConfig implements CrmConfigurer {

	@Bean
	public BasicPasswordService passwords() {
		return new BasicPasswordService();
	}

	@Bean
	public Crm crm() {
		CrmStore store = new BasicStore();
		CrmUpdateObserver observer = new BasicUpdateObserver();
		CrmRepositories repos = new BasicRepositories(store, observer);
		CrmDictionary dictionary = new BasicDictionary().initialize();
		CrmServices services = new BasicServices(repos, passwords(), dictionary);
		CrmAuthenticationService auth = new BasicAuthenticationService(services, passwords());
		CrmPolicies policies = new AuthenticatedPolicies(auth, services);
		return new Crm(services, policies);
	}
		
}