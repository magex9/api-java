package ca.magex.crm.graphql;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.crm.Location;
import ca.magex.crm.api.crm.Organization;
import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.services.OrganizationService;
import ca.magex.crm.graphql.client.OrganizationServiceGraphQLClient;

public class GraphQLClientWalkthrough {

	public static void main(String[] args) throws Exception {

		OrganizationService orgService = new OrganizationServiceGraphQLClient("http://localhost:9002/crm/graphql");
		
		Organization johnnuy = orgService.createOrganization("johnnuy.org");
		System.out.println(johnnuy);
		
		johnnuy = orgService.disableOrganization(johnnuy.getOrganizationId());
		System.out.println(johnnuy);
		
		johnnuy = orgService.enableOrganization(johnnuy.getOrganizationId());
		System.out.println(johnnuy);
		
		Location hq = orgService.createLocation(johnnuy.getOrganizationId(), "Head Quarters", "HQ", new MailingAddress("132 Cheyenne Way", "Nepean", "ON", new Country("CA", "Canada"), "K2J 0E9"));
		System.out.println(hq);
		
		((OrganizationServiceGraphQLClient) orgService).close();
	}
	
}
