package ca.magex.crm.hazelcast.service;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.magex.crm.api.CrmProfiles;
import ca.magex.crm.api.authentication.CrmPasswordService;
import ca.magex.crm.api.services.Crm;
import ca.magex.crm.hazelcast.config.HazelcastTestConfig;
import ca.magex.crm.test.AbstractPasswordServiceTests;
import ca.magex.crm.test.config.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class, HazelcastTestConfig.class })
@ActiveProfiles(profiles =  {CrmProfiles.CRM_DATASTORE_DECENTRALIZED, CrmProfiles.CRM_NO_AUTH} )
public class HazelcastPasswordServiceTests extends AbstractPasswordServiceTests {

	@Autowired
	public void setCrm(Crm crm) {
		this.crm = crm;
	}
	
	@Autowired
	public void setCrmPasswordService(CrmPasswordService crmPasswordService) {
		this.passwords = crmPasswordService;
	}
	
	@Autowired
	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.encoder = passwordEncoder;
	}
}