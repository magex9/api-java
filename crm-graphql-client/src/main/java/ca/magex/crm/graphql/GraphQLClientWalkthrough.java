package ca.magex.crm.graphql;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import ca.magex.crm.amnesia.services.AmnesiaLookupService;
import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.common.Telephone;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.lookup.BusinessClassification;
import ca.magex.crm.api.lookup.BusinessSector;
import ca.magex.crm.api.lookup.BusinessUnit;
import ca.magex.crm.api.lookup.Language;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.graphql.client.OrganizationServiceGraphQLClient;

public class GraphQLClientWalkthrough {

	public static void main(String[] args) throws Exception {

		OrganizationServiceGraphQLClient crm = new OrganizationServiceGraphQLClient("http://localhost:9002/crm/graphql");
		
		CrmLookupService lookups = new AmnesiaLookupService();
						
		OrganizationDetails johnnuy = crm.createOrganization("johnnuy.org");
		System.out.println(johnnuy);
		
		System.out.println(crm.disableOrganization(johnnuy.getOrganizationId()));
		
		System.out.println(crm.enableOrganization(johnnuy.getOrganizationId()));
		
		LocationDetails hq = crm.createLocation(
				johnnuy.getOrganizationId(), 
				"Head Quarters", 
				"HQ", 
				new MailingAddress("132 Cheyenne Way", "Ottawa", "ON", lookups.findCountryByCode("CA"), "K2J 0E9"));
		System.out.println(hq);
		
		hq = crm.updateLocationName(
				hq.getLocationId(), 
				"Johnnuy HeadQuarters");
		System.out.println(hq);
		
		hq = crm.updateLocationAddress(
				hq.getLocationId(), 
				new MailingAddress("132 Cheyenne Way", "Nepean", "ON", lookups.findCountryByCode("CA"), "K2J 0E9"));
		System.out.println(hq);
		
		System.out.println(crm.disableLocation(hq.getLocationId()));
		
		System.out.println(crm.enableLocation(hq.getLocationId()));
		
		hq = crm.findLocationDetails(hq.getLocationId());
		System.out.println(hq);
				
		johnnuy = crm.updateOrganizationMainLocation(
				johnnuy.getOrganizationId(), 
				hq.getLocationId());
		System.out.println(johnnuy);
		
		johnnuy = crm.updateOrganizationName(
				johnnuy.getOrganizationId(),
				"Johnnuy Technologies");
		System.out.println(johnnuy);
		
		OrganizationDetails johnnuy2 = crm.findOrganizationDetails(johnnuy.getOrganizationId());
		System.out.println(johnnuy2);
		
		LocationDetails hq2 = crm.findLocationDetails(hq.getLocationId());
		System.out.println(hq2);
		
		
		long orgCount = crm.countOrganizations(new OrganizationsFilter());
		System.out.println(orgCount + " organizations");
		
		Page<OrganizationDetails> orgDetails = crm.findOrganizationDetails(new OrganizationsFilter("Johnnuy", Status.ACTIVE), new Paging(1, 5, Sort.by(Order.asc("displayName"))));
		System.out.println(orgDetails + " - " + orgDetails.getContent().size() + " of " + orgDetails.getTotalElements());
		
		Page<OrganizationSummary> orgSummaries = crm.findOrganizationSummaries(new OrganizationsFilter("Johnnuy", Status.ACTIVE), new Paging(1, 5, Sort.by(Order.asc("displayName"))));
		System.out.println(orgSummaries + " - " + orgSummaries.getContent().size() + " of " + orgSummaries.getTotalElements());
		
		long locCount = crm.countLocations(new LocationsFilter());
		System.out.println(locCount + " locations");
		
		Page<LocationDetails> locationDetails = crm.findLocationDetails(new LocationsFilter(), new Paging(1, 5, Sort.by(Order.asc("displayName"))));
		System.out.println(locationDetails + " - " + locationDetails.getContent().size() + " of " + locationDetails.getTotalElements());
		
		Page<LocationSummary> locationSummaries = crm.findLocationSummaries(new LocationsFilter(), new Paging(1, 5, Sort.by(Order.asc("displayName"))));
		System.out.println(locationSummaries + " - " + locationSummaries.getContent().size() + " of " + locationSummaries.getTotalElements());
		
		
		PersonDetails jonathan = crm.createPerson(
				johnnuy.getOrganizationId(), 
				new PersonName(lookups.findSalutationByCode(1), "Jonathan", "Alexander", "Trafford"), 
				new MailingAddress("132 Cheyenne Way", "Ottawa", "ON", lookups.findCountryByCode("CA"), "K2J 0E9"), 
				new Communication("Developer", new Language("EN", "English"), "Jonny.Trafford@gmail.com", new Telephone("6132629713", null), "6135181067"), 
				new BusinessPosition(new BusinessSector(1, ""), new BusinessUnit(1, ""), new BusinessClassification(1, "")));
		System.out.println(jonathan);
		
		PersonSummary jonathanSummary = crm.disablePerson(jonathan.getPersonId());
		System.out.println(jonathanSummary);
		
		jonathanSummary = crm.enablePerson(jonathan.getPersonId());
		System.out.println(jonathanSummary);
		
		jonathan = crm.findPersonDetails(jonathan.getPersonId());
		System.out.println(jonathan);
		
		jonathan = crm.updatePersonName(jonathan.getPersonId(), new PersonName(lookups.findSalutationByCode(1), "Jonny", "Alexander", "Trafford"));
		System.out.println(jonathan);
		
		jonathan = crm.updatePersonAddress(jonathan.getPersonId(), new MailingAddress("132 Cheyenne Way", "Nepean", "ON", lookups.findCountryByCode("CA"), "K2J 0E9"));
		System.out.println(jonathan);
		
		jonathan = crm.updatePersonCommunication(jonathan.getPersonId(), new Communication("Java Developer", new Language("EN", "English"), "Jonny.Trafford@gmail.com", new Telephone("6132629713", null), "6135181067"));
		System.out.println(jonathan);
		
		jonathan = crm.updatePersonBusinessPosition(jonathan.getPersonId(), new BusinessPosition(new BusinessSector(2, ""), new BusinessUnit(2, ""), new BusinessClassification(2, "")));
		System.out.println(jonathan);
		
		jonathan = crm.addUserRole(jonathan.getPersonId(), lookups.findRoleByCode("SYS_ADMIN"));
		jonathan = crm.addUserRole(jonathan.getPersonId(), lookups.findRoleByCode("RE_ADMIN"));
		System.out.println(jonathan);
		
		jonathan = crm.removeUserRole(jonathan.getPersonId(), lookups.findRoleByCode("SYS_ADMIN"));
		System.out.println(jonathan);
		
		
		long personCount = crm.countPersons(new PersonsFilter());
		System.out.println(personCount + " persons");
		
		Page<PersonDetails> personDetails = crm.findPersonDetails(new PersonsFilter(), new Paging(1, 5, Sort.by(Order.asc("displayName"))));
		System.out.println(personDetails + " - " + personDetails.getContent().size() + " of " + personDetails.getTotalElements());
		
		Page<PersonSummary> personSummaries = crm.findPersonSummaries(new PersonsFilter(), new Paging(1, 5, Sort.by(Order.asc("displayName"))));
		System.out.println(personSummaries + " - " + personSummaries.getContent().size() + " of " + personSummaries.getTotalElements());
		
		
		
		
		((OrganizationServiceGraphQLClient) crm).close();
	}
	
}
