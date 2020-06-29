package ca.magex.crm.test;

import static ca.magex.crm.test.CrmAsserts.SYSTEM_EMAIL;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_ORG;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_PERSON;

import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.authentication.CrmAuthenticationService;

public abstract class AbstractOptionServiceTests {

	@Autowired
	protected Crm crm;
	
	@Autowired
	protected CrmAuthenticationService auth;
	
	@Before
	public void setup() {
		crm.reset();
		crm.initializeSystem(SYSTEM_ORG, SYSTEM_PERSON, SYSTEM_EMAIL, "admin", "admin");
		auth.login("admin", "admin");
	}
	
	@After
	public void cleanup() {
		auth.logout();
	}
	
	
}
