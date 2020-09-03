package ca.magex.crm.hazelcast.service;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import ca.magex.crm.api.authentication.CrmPasswordService;
import ca.magex.crm.api.services.CrmConfigurationService;
import ca.magex.crm.hazelcast.config.HazelcastTestConfig;
import ca.magex.crm.test.AbstractPasswordServiceTests;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { HazelcastTestConfig.class })
@EnableTransactionManagement
public class HazelcastPasswordServiceTests extends AbstractPasswordServiceTests {

	@Autowired private CrmConfigurationService config;
	@Autowired private CrmPasswordService passwords;

	@Override
	protected CrmConfigurationService config() {
		return config;
	}

	@Override
	protected CrmPasswordService passwords() {
		return passwords;
	}
}
