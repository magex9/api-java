package ca.magex.crm.amnesia.services;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.services.CrmInitializationService;
import ca.magex.crm.api.services.CrmPermissionService;
import ca.magex.crm.test.AbstractInitializationServiceTests;
import ca.magex.crm.test.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
@ActiveProfiles(MagexCrmProfiles.CRM_DATASTORE_CENTRALIZED)
public class AmnesiaInitializationServiceTests extends AbstractInitializationServiceTests {

	@Autowired
	private CrmInitializationService initializationService;

	@Autowired 
	private CrmPermissionService permissionService;
	
	@Override
	public CrmInitializationService getInitializationService() {
		return initializationService;
	}
	
	@Override
	public CrmPermissionService getPermissionService() {
		return permissionService;
	}
	
}
