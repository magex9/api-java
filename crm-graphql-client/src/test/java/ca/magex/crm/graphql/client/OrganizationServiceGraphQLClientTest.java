package ca.magex.crm.graphql.client;

import org.springframework.test.util.ReflectionTestUtils;

import ca.magex.crm.test.CrmServicesTestSuite;

public class OrganizationServiceGraphQLClientTest {

	public static void main(String[] args) throws Exception {

		OrganizationServiceGraphQLClient crmServices = new OrganizationServiceGraphQLClient("http://localhost:9002/crm/graphql");
		
		crmServices.authenticate("http://localhost:9002/crm/authenticate", "admin", "admin");
		
		CrmServicesTestSuite testSuite = new CrmServicesTestSuite();		
		ReflectionTestUtils.setField(testSuite, "organizationService", crmServices);
		ReflectionTestUtils.setField(testSuite, "locationService", crmServices);
		ReflectionTestUtils.setField(testSuite, "personService", crmServices);
		
		
		testSuite.runAllTests();
	}
}
