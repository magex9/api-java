package ca.magex.crm.transform;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.authentication.CrmAuthenticationService;
import ca.magex.crm.api.authentication.CrmPasswordService;
import ca.magex.crm.api.authentication.basic.BasicAuthenticationService;
import ca.magex.crm.api.authentication.basic.BasicPasswordService;
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

public class TestCrm {

	public static Crm build() {
		CrmStore store = new BasicStore();
		CrmUpdateObserver observer = new BasicUpdateObserver();
		CrmRepositories repos = new BasicRepositories(store, observer);
		CrmPasswordService passwords = new BasicPasswordService();
		CrmDictionary dictionary = new BasicDictionary().initialize();
		CrmServices services = new BasicServices(repos, passwords, dictionary);
		CrmAuthenticationService auth = new BasicAuthenticationService(services, passwords);
		CrmPolicies policies = new AuthenticatedPolicies(auth, services);
		return new Crm(services, policies);
	}
	
}
