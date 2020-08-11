package ca.magex.crm.mongodb.repository;

import org.junit.After;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.magex.crm.api.authentication.CrmAuthenticationService;
import ca.magex.crm.api.services.CrmConfigurationService;
import ca.magex.crm.mongodb.config.MongoTestConfig;
import ca.magex.crm.test.AbstractConfigurationServiceTests;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { MongoTestConfig.class })
public class MongoBasicConfigurationServiceTests extends AbstractConfigurationServiceTests {

	@Autowired private CrmConfigurationService config;
	
	@Autowired private CrmAuthenticationService auth;
	
	@Override
	public void testDataDump() throws Exception {
	}
	
	@Override
	public void testDataDumpToInvalidFile() throws Exception {
	}
	
	@After
	public void cleanup() {
		config().reset();
	}

	@Override
	protected CrmConfigurationService config() {
		return config;
	}

	@Override
	protected CrmAuthenticationService auth() {
		return auth;
	}
	
}
