package ca.magex.crm.amnesia.services;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.magex.crm.amnesia.AmnesiaDB;
import ca.magex.crm.amnesia.AmnesiaPasswordEncoder;
import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.authentication.CrmPasswordService;
import ca.magex.crm.test.AbstractPasswordServiceTests;
import ca.magex.crm.test.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
@ActiveProfiles(MagexCrmProfiles.CRM_DATASTORE_CENTRALIZED)
public class AmnesiaPasswordServiceTests extends AbstractPasswordServiceTests {

	@Autowired
	private AmnesiaDB db;
	
	@Autowired
	private CrmPasswordService passwordService;
	
	public void reset() {
		db.reset();
	}

	@Override
	public CrmPasswordService getPasswordService() {
		return passwordService;
	}

	@Override
	public PasswordEncoder getPasswordEncoder() {
		return db.getPasswordEncoder();
	}
	
	@Test
	public void testPasswordVerification() throws Exception {
		AmnesiaPasswordEncoder encoder = new AmnesiaPasswordEncoder();
		String encoded = encoder.encode("test");
		assertTrue(encoder.matches("test", encoded));
	}

}
