package ca.magex.crm.api.services.basic;

import ca.magex.crm.api.adapters.CrmServicesAdapter;
import ca.magex.crm.api.authentication.CrmPasswordService;
import ca.magex.crm.api.dictionary.CrmDictionary;
import ca.magex.crm.api.repositories.CrmRepositories;
import ca.magex.crm.api.services.CrmServices;

public class BasicServices extends CrmServicesAdapter implements CrmServices {
	
	public BasicServices(CrmRepositories repos, CrmPasswordService passwords, CrmDictionary dictionary) {
		super(
			new BasicConfigurationService(repos, passwords, dictionary),
			new BasicLookupService(repos),
			new BasicOptionService(repos),
			new BasicGroupService(repos),
			new BasicRoleService(repos),
			new BasicOrganizationService(repos),
			new BasicLocationService(repos),
			new BasicPersonService(repos),
			new BasicUserService(repos, passwords)
		);
	}
		
}
