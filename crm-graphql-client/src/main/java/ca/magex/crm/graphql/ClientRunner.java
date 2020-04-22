package ca.magex.crm.graphql;

import java.io.IOException;

import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.graphql.client.CrmServicesGraphQLClientImpl;

public class ClientRunner {

	public static void main(String[] args) throws IOException {
		try (CrmServicesGraphQLClientImpl crmImpl = new CrmServicesGraphQLClientImpl("http://localhost:9002/crm/graphql")) {
			
			crmImpl.authenticateJwt("http://localhost:9002/crm/authenticate", "CXA0", "admin");
			
			OrganizationDetails org = crmImpl.createOrganization("Johnnuy");
			System.out.println(org);
		}
	}
}
