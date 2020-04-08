package ca.magex.crm.graphql.client;

import org.springframework.test.util.ReflectionTestUtils;

import ca.magex.crm.test.CrmServicesTestSuite;

public class OrganizationServiceGraphQLOauthClientTest {

	public static void main(String[] args) throws Exception {

		OrganizationServiceGraphQLClient crmServices = new OrganizationServiceGraphQLClient("http://localhost:9002/crm/graphql");
		
		crmServices.authenticateOauth("http://localhost:9002/crm", "admin", "admin");
		
//		CrmServicesTestSuite testSuite = new CrmServicesTestSuite();		
//		ReflectionTestUtils.setField(testSuite, "organizationService", crmServices);
//		ReflectionTestUtils.setField(testSuite, "locationService", crmServices);
//		ReflectionTestUtils.setField(testSuite, "personService", crmServices);
		
		
//		testSuite.runAllTests();
	}
}
