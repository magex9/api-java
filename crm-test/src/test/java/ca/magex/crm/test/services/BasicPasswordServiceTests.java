package ca.magex.crm.test.services;

import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.magex.crm.api.CrmProfiles;
import ca.magex.crm.test.AbstractPasswordServiceTests;
import ca.magex.crm.test.config.BasicTestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { BasicTestConfig.class })
@ActiveProfiles(CrmProfiles.CRM_DATASTORE_CENTRALIZED)
public class BasicPasswordServiceTests extends AbstractPasswordServiceTests {

//	public AmnesiaPasswordServiceTests() {
//		this(new AmnesiaDB(new AmnesiaPasswordEncoder(), new CrmLookupLoader()));
//	}
//	
//	private AmnesiaPasswordServiceTests(AmnesiaDB db) {
//		super(new AmnesiaCrm(db), new AmnesiaPasswordService(db), db.getPasswordEncoder());
//	}
//
//	@Test
//	public void testPasswordVerification() throws Exception {
//		AmnesiaPasswordEncoder encoder = new AmnesiaPasswordEncoder();
//		String encoded = encoder.encode("test");
//		assertTrue(encoder.matches("test", encoded));
//	}

}
