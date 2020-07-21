package ca.magex.crm.test.services;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.authentication.CrmAuthenticationService;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.test.AbstractUserServiceTests;
import ca.magex.crm.test.config.BasicTestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { BasicTestConfig.class })
public class BasicUserServiceTests extends AbstractUserServiceTests {

	@Autowired private Crm crm;
	@Autowired private CrmAuthenticationService auth;

	@Override
	protected Crm config() {
		return crm;
	}

	@Override
	protected CrmAuthenticationService auth() {
		return auth;
	}

	@Override
	protected CrmUserService users() {
		return crm;
	}
}
