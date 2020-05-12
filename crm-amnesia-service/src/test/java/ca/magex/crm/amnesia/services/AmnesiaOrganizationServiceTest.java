package ca.magex.crm.amnesia.services;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.test.CrmServicesTestSuite;
import ca.magex.crm.test.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
@ActiveProfiles(MagexCrmProfiles.CRM_DATASTORE_CENTRALIZED)
public class AmnesiaOrganizationServiceTest {

	@Autowired private CrmServicesTestSuite crmServicesTest;
	
	@Test
	public void testCrmServices() {
		crmServicesTest.runAllTests();
	}
	
}