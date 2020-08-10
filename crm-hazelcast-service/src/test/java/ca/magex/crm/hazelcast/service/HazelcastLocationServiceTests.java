package ca.magex.crm.hazelcast.service;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.authentication.CrmAuthenticationService;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.hazelcast.config.HazelcastTestConfig;
import ca.magex.crm.test.AbstractLocationServiceTests;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { HazelcastTestConfig.class })
@EnableTransactionManagement
public class HazelcastLocationServiceTests extends AbstractLocationServiceTests {
	
	@Autowired private Crm crm;	
	@Autowired private CrmAuthenticationService auth;
	
	@Override
	protected Crm config() {
		return crm; // use our default crm to configure the system for tests
	}
	
	@Override
	protected CrmServices locations() {
		return crm;
	}
	
	@Override
	protected CrmAuthenticationService auth() {
		return auth;
	}
}
