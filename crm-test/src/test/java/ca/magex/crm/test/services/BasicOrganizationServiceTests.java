package ca.magex.crm.test.services;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.authentication.CrmAuthenticationService;
import ca.magex.crm.api.services.CrmConfigurationService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.test.AbstractOrganizationServiceTests;
import ca.magex.crm.test.config.BasicTestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { BasicTestConfig.class })
public class BasicOrganizationServiceTests extends AbstractOrganizationServiceTests {

	@Autowired private Crm crm;
	@Autowired private CrmConfigurationService config;
	@Autowired private CrmAuthenticationService auth;

	@Override
	protected CrmConfigurationService config() {
		return config; // use our default crm to configure the system for tests
	}
	
	@Override
	protected CrmServices crm() {
		return crm;
	}

	@Override
	protected CrmAuthenticationService auth() {
		return auth;
	}

	@Override
	protected CrmOrganizationService organizations() {
		return crm;
	}
}