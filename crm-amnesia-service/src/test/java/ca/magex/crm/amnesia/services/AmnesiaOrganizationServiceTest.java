package ca.magex.crm.amnesia.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.magex.crm.amnesia.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class AmnesiaOrganizationServiceTest extends AbstractJUnit4SpringContextTests {

	@Autowired private CrmServicesTestSuite crmServicesTest;
	
	@Test
	public void testCrmServices() {
		crmServicesTest.runAllTests();
	}
}