package ca.magex.crm.amnesia.services;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.magex.crm.amnesia.AmnesiaDB;
import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPermissionService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.test.AbstractUserServiceTests;
import ca.magex.crm.test.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
@ActiveProfiles(MagexCrmProfiles.CRM_DATASTORE_CENTRALIZED)
public class AmnesiaUserServiceTests extends AbstractUserServiceTests {

	@Autowired
	private AmnesiaDB db;
	
	@Autowired
	private CrmUserService users;
	
	@Autowired
	private CrmOrganizationService orgs;
	
	@Autowired
	private CrmPersonService persons;
	
	@Autowired
	private CrmPermissionService permissions;

	@Override
	public CrmOrganizationService getOrganizationService() {
		return orgs;
	}
	
	@Override
	public CrmPermissionService getPermissionService() {
		return permissions;
	}
	
	@Override
	public CrmPersonService getPersonService() {
		return persons;
	}
	
	@Override
	public CrmUserService getUserService() {
		return users;
	}

	@Override
	public void reset() {
		db.reset();
	}
}
