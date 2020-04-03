package ca.magex.crm.amnesia.services;

import ca.magex.crm.amnesia.AmnesiaDB;
import ca.magex.crm.api.services.CrmLocationPolicy;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.api.services.CrmOrganizationPolicy;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPersonPolicy;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmValidation;
import ca.magex.crm.api.services.SecuredCrmServices;

public class AmnesiaFactory {

	private AmnesiaDB db;
	
	private SecuredCrmServices service;
	
	private AmnesiaFactory(boolean secured) {
		this.db = new AmnesiaDB();
		CrmLookupService lookupService = new AmnesiaLookupService();
		CrmValidation validationService = new AmnesiaValidationService(db, lookupService);
		CrmOrganizationService organizationService = new AmnesiaOrganizationService(db);
		CrmOrganizationPolicy organizationPolicy = secured ? new AmnesiaOrganizationPolicy(db) : new AmnesiaAnonymousPolicies();
		CrmLocationService locationService = new AmnesiaLocationService(db);
		CrmLocationPolicy locationPolicy = secured ? new AmnesiaLocationPolicy(db) : new AmnesiaAnonymousPolicies();
		CrmPersonService personService = new AmnesiaPersonService(db);
		CrmPersonPolicy personPolicy = secured ? new AmnesiaPersonPolicy(db) : new AmnesiaAnonymousPolicies();
		this.service = new SecuredCrmServices(
				lookupService, validationService, 
				organizationService, organizationPolicy, 
				locationService, locationPolicy,
				personService, personPolicy);
	}

	private static AmnesiaFactory anonymous;

	private static AmnesiaFactory secured;

	public static SecuredCrmServices getAnonymousService() {
		if (anonymous == null)
			anonymous = new AmnesiaFactory(false);
		return anonymous.service;
	}
	
	public static SecuredCrmServices getSecuredService() {
		if (secured == null)
			secured = new AmnesiaFactory(true);
		return secured.service;
	}
	
}
