package ca.magex.crm.graphql;

import java.io.IOException;
import java.util.Collections;

import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.graphql.client.CrmServicesGraphQLClientImpl;

/**
 * Used as a test script for the client
 * 
 * @author Jonny
 */
public class ClientRunner {

	public static void main(String[] args) throws IOException {
		CrmServicesGraphQLClientImpl crmImpl = new CrmServicesGraphQLClientImpl("http://localhost:9002/crm/graphql");
			
		crmImpl.authenticateJwt("http://localhost:9002/crm/authenticate", "admin", "admin");
		OrganizationDetails org = crmImpl.createOrganization("Johnnuy", Collections.emptyList());
		System.out.println(org);
		
	}
}
