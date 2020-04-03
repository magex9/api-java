package ca.magex.crm.graphql;

import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.common.Telephone;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.lookup.BusinessClassification;
import ca.magex.crm.api.lookup.BusinessSector;
import ca.magex.crm.api.lookup.BusinessUnit;
import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.lookup.Language;
import ca.magex.crm.api.lookup.Salutation;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Role;
import ca.magex.crm.graphql.client.OrganizationServiceGraphQLClient;

public class GraphQLClientInitializer {

	public static void main(String[] args) throws Exception {

		CrmServices orgService = new OrganizationServiceGraphQLClient("http://localhost:9002/crm/graphql");
						
		OrganizationDetails johnnuy = orgService.createOrganization("Johnnuy Technologies");		
		LocationDetails hq = orgService.createLocation(
				johnnuy.getOrganizationId(), 
				"Johnnuy HeadQuarters", 
				"HQ", 
				new MailingAddress("132 Cheyenne Way", "Ottawa", "ON", new Country("CA", "Canada", "Canada"), "K2J 0E9"));		
		
		johnnuy = orgService.updateOrganizationMainLocation(
				johnnuy.getOrganizationId(), 
				hq.getLocationId());
		System.out.println(johnnuy);
		
		PersonDetails jonathan = orgService.createPerson(
				johnnuy.getOrganizationId(), 
				new PersonName(new Salutation(1, "Mr", "Mr"), "Jonathan", "Alexander", "Trafford"), 
				new MailingAddress("132 Cheyenne Way", "Ottawa", "ON", new Country("CA", "Canada", "Canada"), "K2J 0E9"), 
				new Communication("Developer", new Language("EN", "English"), "Jonny.Trafford@gmail.com", new Telephone(6132629713L, 0L), 6135181067L), 
				new BusinessPosition(new BusinessSector(1, ""), new BusinessUnit(1, ""), new BusinessClassification(1, "")));		
		
		jonathan = orgService.addUserRole(jonathan.getPersonId(), new Role("5", "", ""));
		
		
		OrganizationDetails magex = orgService.createOrganization("Magex Technologies");		
		LocationDetails mhq = orgService.createLocation(
				johnnuy.getOrganizationId(), 
				"HeadQuarters", 
				"HQ", 
				new MailingAddress("234 Laurier Av", "Ottawa", "ON", new Country("CA", "Canada", "Canada"), "K2M 6L5"));		
		
		magex = orgService.updateOrganizationMainLocation(
				magex.getOrganizationId(), 
				mhq.getLocationId());
		System.out.println(johnnuy);
		
		((OrganizationServiceGraphQLClient) orgService).close();
	}
	
}
