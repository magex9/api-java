package ca.magex.crm.restful;

import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.services.CrmClient;
import ca.magex.crm.test.CrmServicesTestSuite;
import ca.magex.crm.test.restful.RestfulCrmClient;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = {
		MagexCrmProfiles.AUTH_EMBEDDED_JWT,
		MagexCrmProfiles.CRM_NO_AUTH,
		MagexCrmProfiles.CRM_DATASTORE_CENTRALIZED
	})
public class CrmRestfulNoauthClientTest {

	@LocalServerPort private int randomPort;

	@Test
	public void runTests() {
		/* we are running these tests with an embedded authentication server so everything is on the same servlet */
		CrmClient crmServices = new RestfulCrmClient("http://localhost:" + randomPort + "/crm", Locale.ENGLISH);
		
		CrmServicesTestSuite testSuite = new CrmServicesTestSuite();
		ReflectionTestUtils.setField(testSuite, "lookupService", crmServices);
		ReflectionTestUtils.setField(testSuite, "organizationService", crmServices);
		ReflectionTestUtils.setField(testSuite, "locationService", crmServices);
		ReflectionTestUtils.setField(testSuite, "personService", crmServices);
		ReflectionTestUtils.setField(testSuite, "userService", crmServices);

		// TODO fix this
//		testSuite.runAllTests();
	}
}
