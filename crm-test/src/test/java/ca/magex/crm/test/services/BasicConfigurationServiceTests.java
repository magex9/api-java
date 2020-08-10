package ca.magex.crm.test.services;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.magex.crm.api.authentication.CrmAuthenticationService;
import ca.magex.crm.api.services.CrmConfigurationService;
import ca.magex.crm.test.AbstractConfigurationServiceTests;
import ca.magex.crm.test.config.BasicTestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { BasicTestConfig.class })
public class BasicConfigurationServiceTests extends AbstractConfigurationServiceTests {

	@Autowired private CrmConfigurationService config;
	@Autowired private CrmAuthenticationService auth;

	@Override
	protected CrmConfigurationService config() {
		return config;
	}

	@Override
	protected CrmAuthenticationService auth() {
		return auth;
	}

	
}
