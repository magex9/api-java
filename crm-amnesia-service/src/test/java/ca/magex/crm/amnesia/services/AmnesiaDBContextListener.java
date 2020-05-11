package ca.magex.crm.amnesia.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.authentication.CrmPasswordService;
import ca.magex.crm.api.authentication.CrmSetup;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPermissionService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.test.CrmTestData;

@Component
@ActiveProfiles(MagexCrmProfiles.CRM_DATASTORE_CENTRALIZED)
public class AmnesiaDBContextListener implements ApplicationListener<ContextRefreshedEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(AmnesiaDBContextListener.class);

	@Autowired private CrmOrganizationService orgs;
	@Autowired private CrmLocationService locations;
	@Autowired private CrmPersonService persons;
	@Autowired private CrmUserService users;
	@Autowired private CrmLookupService lookups;
	@Autowired private CrmPermissionService permissions;
	@Autowired private CrmPasswordService passwords;
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		LOG.info("Initializing Crm Data");
		CrmSetup.initializeLookups(lookups);
		CrmSetup.initializeRoles(permissions);
		CrmTestData.createSysAdmin(orgs, locations, persons, users, passwords);
		CrmTestData.createOmniTech(orgs, locations, persons, users, passwords);
	}
	
}
