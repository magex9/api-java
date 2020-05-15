package ca.magex.crm.amnesia.services;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.magex.crm.amnesia.AmnesiaDB;
import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.test.AbstractLocationServiceTests;
import ca.magex.crm.test.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
@ActiveProfiles(MagexCrmProfiles.CRM_DATASTORE_CENTRALIZED)
public class AmnesiaLocationServiceTests extends AbstractLocationServiceTests {

	@Autowired
	private AmnesiaDB db;
	
	@Autowired
	private CrmLocationService locationService;

	@Autowired
	private CrmOrganizationService organizationService;
	
	@Before
	public void reset() {
		db.reset();
	}

	@Override
	public CrmLocationService getLocationService() {
		return locationService;
	}
	
	@Override
	public CrmOrganizationService getOrganizationService() {
		return organizationService;
	}
}
