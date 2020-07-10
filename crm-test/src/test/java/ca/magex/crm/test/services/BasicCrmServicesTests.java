package ca.magex.crm.test.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.authentication.CrmAuthenticationService;
import ca.magex.crm.test.CrmServicesTestSuite;
import ca.magex.crm.test.config.BasicTestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { BasicTestConfig.class })
public class BasicCrmServicesTests {
	
	@Autowired
	private Crm crm;
	
	@Autowired
	private CrmAuthenticationService auth;

	@Test
	public void testCrmServices() throws Exception {
		new CrmServicesTestSuite(crm, auth).runAllTests();
	}
	
}