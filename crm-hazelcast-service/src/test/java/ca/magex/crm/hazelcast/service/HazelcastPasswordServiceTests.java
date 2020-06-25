package ca.magex.crm.hazelcast.service;

import javax.transaction.Transactional;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.CrmProfiles;
import ca.magex.crm.api.authentication.CrmPasswordService;
import ca.magex.crm.hazelcast.config.HazelcastTestConfig;
import ca.magex.crm.test.AbstractPasswordServiceTests;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { HazelcastTestConfig.class })
@ActiveProfiles(profiles = { CrmProfiles.CRM_DATASTORE_DECENTRALIZED, CrmProfiles.CRM_NO_AUTH })
@EnableTransactionManagement
public class HazelcastPasswordServiceTests extends AbstractPasswordServiceTests {

	@Autowired
	public void setCrm(Crm crm) {
		this.crm = crm;
	}

	@Autowired
	public void setCrmPasswordService(CrmPasswordService crmPasswordService) {
		this.passwords = crmPasswordService;
	}
	
	@Override
	@Transactional
	public void testInvalidUserName() {
		super.testInvalidUserName();
	}
	
	@Override
	@Transactional
	public void testPasswords() {
		super.testPasswords();
	}
}