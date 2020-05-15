package ca.magex.crm.hazelcast.service;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.hazelcast.core.HazelcastInstance;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.authentication.CrmPasswordService;
import ca.magex.crm.test.AbstractPasswordServiceTests;
import ca.magex.crm.test.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
@ActiveProfiles(MagexCrmProfiles.CRM_DATASTORE_DECENTRALIZED)
public class HazelcastPasswordServiceTests extends AbstractPasswordServiceTests {

	@Autowired private CrmPasswordService hzPasswordService;
	@Autowired private HazelcastInstance hzInstance;
	@Autowired private PasswordEncoder passwordEncoder;
	
	@Override
	public PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}
	
	@Override
	public CrmPasswordService getPasswordService() {
		return hzPasswordService;
	}
	
	@Before
	public void reset() {
		hzInstance.getMap(HazelcastPasswordService.HZ_PASSWORDS_KEY).clear();
	}
}
