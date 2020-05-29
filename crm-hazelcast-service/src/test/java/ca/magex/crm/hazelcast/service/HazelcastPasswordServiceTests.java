package ca.magex.crm.hazelcast.service;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.authentication.CrmPasswordService;
import ca.magex.crm.api.services.CrmInitializationService;
import ca.magex.crm.test.AbstractPasswordServiceTests;
import ca.magex.crm.test.CrmAsserts;
import ca.magex.crm.test.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
@ActiveProfiles(MagexCrmProfiles.CRM_DATASTORE_DECENTRALIZED)
public class HazelcastPasswordServiceTests extends AbstractPasswordServiceTests {

	@Autowired private CrmInitializationService hzInitializationService;
	@Autowired private CrmPasswordService hzPasswordService;
	@Autowired private PasswordEncoder passwordEncoder;
	
	@Override
	public CrmInitializationService getInitializationService() {
		return hzInitializationService;
	}
	
	@Override
	public PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}
	
	@Override
	public CrmPasswordService getPasswordService() {
		return hzPasswordService;
	}
	
	@Before
	public void loadResource() {
		hzInitializationService.initializeSystem("JUnit", CrmAsserts.PERSON_NAME, "junit@junit.com", "admin", "admin");
	}

}
