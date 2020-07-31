package ca.magex.crm.mongodb.repository;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.authentication.CrmAuthenticationService;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.mongodb.config.MongoTestConfig;
import ca.magex.crm.test.AbstractPersonServiceTests;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { MongoTestConfig.class })
public class MongoBasicPersonServiceTests extends AbstractPersonServiceTests {

	@Autowired private Crm crm;	
	@Autowired private CrmAuthenticationService auth;
	
	@Override
	protected Crm config() {
		return crm; // use our default crm to configure the system for tests
	}
	
	@Override
	protected CrmServices persons() {
		return crm;
	}
	
	@Override
	protected CrmAuthenticationService auth() {
		return auth;
	}
}