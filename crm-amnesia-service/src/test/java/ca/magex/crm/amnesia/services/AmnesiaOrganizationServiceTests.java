package ca.magex.crm.amnesia.services;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.services.CrmInitializationService;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPermissionService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.test.AbstractOrganizationServiceTests;
import ca.magex.crm.test.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
@ActiveProfiles(MagexCrmProfiles.CRM_DATASTORE_CENTRALIZED)
public class AmnesiaOrganizationServiceTests extends AbstractOrganizationServiceTests {

	@Autowired
	private CrmInitializationService initializationService;
	
	@Autowired
	private CrmLookupService lookupService;
	
	@Autowired
	private CrmOrganizationService organizationService;

	@Autowired
	private CrmPersonService personService;

	@Autowired
	private CrmLocationService locationService;

	@Autowired
	private CrmPermissionService permissionService;
	
	@Override
	public CrmInitializationService getInitializationService() {
		return initializationService;
	}

	@Override
	public CrmLookupService getLookupService() {
		return lookupService;
	}
	
	@Override
	public CrmOrganizationService getOrganizationService() {
		return organizationService;
	}

	@Override
	public CrmLocationService getLocationService() {
		return locationService;
	}

	@Override
	public CrmPersonService getPersonService() {
		return personService;
	}

	@Override
	public CrmPermissionService getPermissionService() {
		return permissionService;
	}

}
