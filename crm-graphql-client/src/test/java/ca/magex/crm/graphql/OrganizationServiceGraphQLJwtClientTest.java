package ca.magex.crm.graphql;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import ca.magex.crm.graphql.client.OrganizationServiceGraphQLClient;
import ca.magex.crm.test.CrmServicesTestSuite;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrganizationServiceGraphQLJwtClientTest {

	@LocalServerPort private int randomPort;

	@Test
	public void runTests() {
		OrganizationServiceGraphQLClient crmServices = new OrganizationServiceGraphQLClient("http://localhost:" + randomPort + "/crm/graphql");

		crmServices.authenticateJwt("http://localhost:" + randomPort + "/crm/authenticate", "CXA1", "admin");

		CrmServicesTestSuite testSuite = new CrmServicesTestSuite();
		ReflectionTestUtils.setField(testSuite, "lookupService", crmServices);
		ReflectionTestUtils.setField(testSuite, "organizationService", crmServices);
		ReflectionTestUtils.setField(testSuite, "locationService", crmServices);
		ReflectionTestUtils.setField(testSuite, "personService", crmServices);

		testSuite.runAllTests();
	}
}
