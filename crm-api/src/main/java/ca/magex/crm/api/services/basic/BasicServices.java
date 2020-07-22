package ca.magex.crm.api.services.basic;

import ca.magex.crm.api.adapters.CrmServicesAdapter;
import ca.magex.crm.api.authentication.CrmPasswordService;
import ca.magex.crm.api.repositories.CrmRepositories;

public class BasicServices extends CrmServicesAdapter {	
	
	public BasicServices(CrmRepositories repos, CrmPasswordService passwords) {
		super(
			new BasicConfigurationService(repos, passwords),
			new BasicOptionService(repos),
			new BasicOrganizationService(repos),
			new BasicLocationService(repos),
			new BasicPersonService(repos),
			new BasicUserService(repos, passwords)
		);
	}		
}