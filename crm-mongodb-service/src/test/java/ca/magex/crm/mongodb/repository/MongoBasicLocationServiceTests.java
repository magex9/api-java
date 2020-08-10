package ca.magex.crm.mongodb.repository;

import org.junit.After;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.authentication.CrmAuthenticationService;
import ca.magex.crm.api.services.CrmConfigurationService;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.mongodb.config.MongoTestConfig;
import ca.magex.crm.test.AbstractLocationServiceTests;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { MongoTestConfig.class })
public class MongoBasicLocationServiceTests extends AbstractLocationServiceTests {
	
	@Autowired private CrmConfigurationService config;
	
	@Autowired private CrmAuthenticationService auth;
	
	@Autowired private Crm crm;
	
	@Override
	protected CrmConfigurationService config() {
		return config;
	}

	@Override
	protected CrmAuthenticationService auth() {
		return auth;
	}

	@Override
	protected CrmServices crm() {
		return crm;
	}
	
	@Override
	protected CrmServices locations() {
		return crm;
	}
	
	@After
	public void cleanup() {
		super.cleanup();
		config().reset();
	}
}
