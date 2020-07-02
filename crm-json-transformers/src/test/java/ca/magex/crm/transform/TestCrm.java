package ca.magex.crm.transform;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.authentication.CrmPasswordService;
import ca.magex.crm.api.authentication.basic.BasicPasswordService;
import ca.magex.crm.api.observer.CrmUpdateObserver;
import ca.magex.crm.api.observer.basic.BasicUpdateObserver;
import ca.magex.crm.api.policies.CrmPolicies;
import ca.magex.crm.api.policies.basic.BasicPolicies;
import ca.magex.crm.api.repositories.CrmPasswordRepository;
import ca.magex.crm.api.repositories.CrmRepositories;
import ca.magex.crm.api.repositories.basic.BasicPasswordRepository;
import ca.magex.crm.api.repositories.basic.BasicRepositories;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.services.basic.BasicServices;
import ca.magex.crm.api.store.CrmPasswordStore;
import ca.magex.crm.api.store.CrmStore;
import ca.magex.crm.api.store.basic.BasicPasswordStore;
import ca.magex.crm.api.store.basic.BasicStore;

public class TestCrm {

	public static Crm build() {
		CrmStore store = new BasicStore();
		CrmPasswordStore passwordStore = new BasicPasswordStore();
		CrmUpdateObserver observer = new BasicUpdateObserver();
		CrmRepositories repos = new BasicRepositories(store, observer);
		CrmPasswordRepository passwordRepo = new BasicPasswordRepository(passwordStore);
		CrmPasswordService passwords = new BasicPasswordService(passwordRepo);
		CrmServices services = new BasicServices(repos, passwords);
		CrmPolicies policies = new BasicPolicies(services);
		return new Crm(services, policies);
	}
	
}
