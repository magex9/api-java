package ca.magex.crm.graphql;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.services.OrganizationService;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.graphql.client.OrganizationServiceGraphQLClient;

public class GraphQLClientWalkthrough {

	public static void main(String[] args) throws Exception {

		OrganizationService orgService = new OrganizationServiceGraphQLClient("http://localhost:9002/crm/graphql");
						
		OrganizationDetails johnnuy = orgService.createOrganization("johnnuy.org");
		System.out.println(johnnuy);
		
		System.out.println(orgService.disableOrganization(johnnuy.getOrganizationId()));
		
		System.out.println(orgService.enableOrganization(johnnuy.getOrganizationId()));
		
		LocationDetails hq = orgService.createLocation(
				johnnuy.getOrganizationId(), 
				"Head Quarters", 
				"HQ", 
				new MailingAddress("132 Cheyenne Way", "Ottawa", "ON", new Country("CA", "Canada"), "K2J 0E9"));
		System.out.println(hq);
		
		hq = orgService.updateLocationName(
				hq.getLocationId(), 
				"Johnnuy HeadQuarters");
		System.out.println(hq);
		
		hq = orgService.updateLocationAddress(
				hq.getLocationId(), 
				new MailingAddress("132 Cheyenne Way", "Nepean", "ON", new Country("CA", "Canada"), "K2J 0E9"));
		System.out.println(hq);
		
		System.out.println(orgService.disableLocation(hq.getLocationId()));
		
		System.out.println(orgService.enableLocation(hq.getLocationId()));
		
		hq = orgService.findLocation(hq.getLocationId());
		System.out.println(hq);
				
		johnnuy = orgService.updateOrganizationMainLocation(
				johnnuy.getOrganizationId(), 
				hq.getLocationId());
		System.out.println(johnnuy);
		
		johnnuy = orgService.updateOrganizationName(
				johnnuy.getOrganizationId(),
				"Johnnuy Technologies");
		System.out.println(johnnuy);
		
		OrganizationDetails johnnuy2 = orgService.findOrganization(johnnuy.getOrganizationId());
		System.out.println(johnnuy2);
		
		
		long orgCount = orgService.countOrganizations(new OrganizationsFilter());
		System.out.println(orgCount + " organizations");
		
		Page<OrganizationDetails> orgDetails = orgService.findOrganizationDetails(new OrganizationsFilter("Johnnuy", Status.ACTIVE), new Paging(1, 5, Sort.by(Order.asc("displayName"))));
		System.out.println(orgDetails + " - " + orgDetails.getContent().size() + " of " + orgDetails.getTotalElements());
		
		Page<OrganizationSummary> orgSummaries = orgService.findOrganizationSummaries(new OrganizationsFilter("Johnnuy", Status.ACTIVE), new Paging(1, 5, Sort.by(Order.asc("displayName"))));
		System.out.println(orgSummaries + " - " + orgSummaries.getContent().size() + " of " + orgSummaries.getTotalElements());
		
		long locCount = orgService.countLocations(new LocationsFilter());
		System.out.println(locCount + " locations");
		
		Page<LocationDetails> locationDetails = orgService.findLocationDetails(new LocationsFilter(), new Paging(1, 5, Sort.by(Order.asc("displayName"))));
		System.out.println(locationDetails + " - " + locationDetails.getContent().size() + " of " + locationDetails.getTotalElements());
		
		Page<LocationSummary> locationSummaries = orgService.findLocationSummaries(new LocationsFilter(), new Paging(1, 5, Sort.by(Order.asc("displayName"))));
		System.out.println(locationSummaries + " - " + locationSummaries.getContent().size() + " of " + locationSummaries.getTotalElements());
		
		
		((OrganizationServiceGraphQLClient) orgService).close();
	}
	
}
