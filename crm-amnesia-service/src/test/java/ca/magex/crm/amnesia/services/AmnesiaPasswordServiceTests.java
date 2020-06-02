package ca.magex.crm.amnesia.services;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.magex.crm.amnesia.AmnesiaDB;
import ca.magex.crm.amnesia.AmnesiaPasswordEncoder;
import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.authentication.CrmPasswordService;
import ca.magex.crm.resource.CrmLookupLoader;
import ca.magex.crm.test.AbstractPasswordServiceTests;
import ca.magex.crm.test.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
@ActiveProfiles(MagexCrmProfiles.CRM_DATASTORE_CENTRALIZED)
public class AmnesiaPasswordServiceTests extends AbstractPasswordServiceTests {

	public AmnesiaPasswordServiceTests(CrmPasswordService passwords) {
		this(new AmnesiaDB(new AmnesiaPasswordEncoder(), new CrmLookupLoader()), passwords);
	}

	public AmnesiaPasswordServiceTests(AmnesiaDB db, CrmPasswordService passwords) {
		super(db.getCrm(), passwords, db.getPasswordEncoder());
	}

	@Test
	public void testPasswordVerification() throws Exception {
		AmnesiaPasswordEncoder encoder = new AmnesiaPasswordEncoder();
		String encoded = encoder.encode("test");
		assertTrue(encoder.matches("test", encoded));
	}

}
