package ca.magex.crm.graphql.client;

import org.springframework.test.util.ReflectionTestUtils;

import ca.magex.crm.test.CrmServicesTestSuite;

public class OrganizationServiceGraphQLJwtClientTest {

	public static void main(String[] args) throws Exception {

		OrganizationServiceGraphQLClient crmServices = new OrganizationServiceGraphQLClient("http://localhost:9002/crm/graphql");
		
		crmServices.authenticateJwt("http://localhost:9002/crm/authenticate", "admin", "admin");
		
		CrmServicesTestSuite testSuite = new CrmServicesTestSuite();
		ReflectionTestUtils.setField(testSuite, "lookupService", crmServices);
		ReflectionTestUtils.setField(testSuite, "organizationService", crmServices);
		ReflectionTestUtils.setField(testSuite, "locationService", crmServices);
		ReflectionTestUtils.setField(testSuite, "personService", crmServices);
		
		
		testSuite.runAllTests();
	}
}
