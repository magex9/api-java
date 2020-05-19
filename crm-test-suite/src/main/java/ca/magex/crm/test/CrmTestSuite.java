package ca.magex.crm.test;

import static ca.magex.crm.test.CrmAsserts.*;
import static ca.magex.crm.test.CrmAsserts.assertSinglePage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import ca.magex.crm.api.authentication.CrmPasswordService;
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
import ca.magex.crm.api.exceptions.UnauthenticatedException;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.services.CrmClient;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.test.restful.RestfulCrmClient;

public class CrmTestSuite {
	
	private static final Logger logger = LoggerFactory.getLogger(CrmTestSuite.class);
	
	public static void main(String[] args) {
		CrmClient crm = new RestfulCrmClient("http://localhost:9002", Lang.ENGLISH);
		Map<String, String> credentials = new HashMap<String, String>();

		logger.info("Asserting that the server has not been setup yet");
		assertFalse(crm.isInitialized());
		
		logger.info("Setting up the initial user");
		crm.initializeSystem("DevOps", new PersonName(null, "System", null, "Admin"), "scott@magex.ca", "admin", "admin");
		assertTrue(crm.isInitialized());
		credentials.put("admin", "admin");

		logger.info("Try to create a new organization before logging in");
		assertFalse(crm.canCreateOrganization());
		try {
			crm.createOrganization("MageX", List.of("CRM"));
		} catch (UnauthenticatedException expected) { }

		logger.info("Login as the system admin and try to create the organization");
		crm.login("admin", credentials.get("admin"));
		Page<OrganizationDetails> orgs = crm.findOrganizationDetails(crm.defaultOrganizationsFilter());
		assertSinglePage(orgs, 1);
		assertTrue(crm.canCreateOrganization());
		crm.createOrganization("CRM Management", List.of("CRM"));
		orgs = crm.findOrganizationDetails(crm.defaultOrganizationsFilter());
		assertSinglePage(orgs, 2);
		
		logger.info("Make sure the organization can be found using case-insensitive filters with the default no user or location.");
		OrganizationDetails org = crm.findOrganizationByDisplayName("crm");
		assertEquals("CRM Management", org.getDisplayName());
		assertNull(org.getMainLocationId());
		assertNull(org.getMainContactId());
		crm.logout();
		
		createCrmOrg(crm, credentials);
		verifyCrmOrg(crm, credentials);
		
	}
	
	/**
	 * Create the main administrator org thats has access to all organizations
	 */
	public static String createCrmOrg(CrmClient crm, Map<String, String> credentials) {
		Identifier organizationId = crm.createOrganization("MageX", List.of("CRM")).getOrganizationId();

		MailingAddress address = new MailingAddress("1234 Alta Vista Drive", "Ottawa", ONTARIO.getCode(), CANADA.getCode(), "K3J 3I3");
		Identifier mainLocationId = crm.createLocation(organizationId, "Headquarters", "HQ", address).getLocationId();
		crm.updateOrganizationMainLocation(organizationId, mainLocationId);
		
		PersonName scottName = new PersonName("Mr.", "Scott", null, "Finlay");
		Communication scottComm = new Communication("Developer", ENGLISH.getCode(), "scott@work.ca", new Telephone("6132345535"), null);
		BusinessPosition scottJob = new BusinessPosition("IM/IT", "Development", "Developer");
		Identifier scottId = crm.createPerson(organizationId, scottName, address, scottComm, scottJob).getPersonId();
		crm.createUser(scottId, "magex", Arrays.asList("ORG_ADMIN", "CRM_ADMIN"));
		crm.updateOrganizationMainContact(organizationId, scottId);
		credentials.put("magex", crm.resetPassword(scottId));
		
		return "magex";
	}
	
	public static void verifyCrmOrg(CrmClient crm, Map<String, String> credentials) {
		logger.info("Make sure that the user needs to login before they can search for the organizations");
		try {
			crm.findOrganizationDetails(new OrganizationsFilter(), new Paging(Sort.by("displayName")));
		} catch (UnauthenticatedException expected) { }
		
		crm.login("magex", "magex");
		logger.info("The crm organization should be able to search for organizations");
		
		Page<OrganizationDetails> orgs = crm.findOrganizationDetails(crm.defaultOrganizationsFilter());
		assertSinglePage(orgs, 2);
		
		Identifier organizationId = crm.findOrganizationByDisplayName("MageX").getOrganizationId();
		OrganizationDetails org = crm.findOrganizationDetails(organizationId);
		assertEquals("MageX", org.getDisplayName());
		assertEquals(List.of("CRM"), org.getGroups());
		assertEquals(Status.ACTIVE, org.getStatus());
		
		LocationDetails mainLocation = crm.findLocationDetails(org.getMainLocationId());
		assertEquals("HQ", mainLocation.getReference());
		assertEquals(Status.ACTIVE, mainLocation.getStatus());
		
		PersonDetails mainContact = crm.findPersonDetails(org.getMainContactId());
		assertEquals("scott@magex.ca", mainContact.getCommunication().getEmail());
		assertEquals(Status.ACTIVE, mainContact.getStatus());
		
		Page<User> users = crm.findUsers(new UsersFilter().withOrganizationId(organizationId));
		assertSinglePage(users, 1);
		
		crm.logout();
	}
	
	/**
	 * Create a sample organization with a regular user.
	 */
	public static Identifier createOmniTech(CrmOrganizationService orgs, CrmLocationService locations, CrmPersonService persons, CrmUserService users, CrmPasswordService passwords) {
		Identifier organizationId = orgs.createOrganization("Omni Tech", List.of("ORG")).getOrganizationId();
		
		MailingAddress mainAddress = new MailingAddress("1761 Township Road", "Leduc", ALBERTA.getCode(), CANADA.getCode(), "T9E 2X2");
		Identifier mainLocationId = locations.createLocation(organizationId, "HQ", "HQ", mainAddress).getLocationId();
		orgs.updateOrganizationMainLocation(organizationId, mainLocationId);
		
		PersonName jennaName = new PersonName("Mrs.", "Jenna", "J", "Marshall");
		Communication jennaComm = new Communication("Chief Technology Officer", FRENCH.getCode(), "jenna@omnitech.com", new Telephone("4168814588"), "4169985565");
		BusinessPosition jennaJob = new BusinessPosition("Corporate Services", "Information Technology", "Executive");
		Identifier jennaId = persons.createPerson(organizationId, jennaName, mainAddress, jennaComm, jennaJob).getPersonId();
		users.createUser(jennaId, "jenna", List.of("ORG_ADMIN"));
		
		PersonName chaseName = new PersonName("Mr.", "Chase", "L", "Montgomery");
		Communication chaseComm = new Communication("Financial Advisor", ENGLISH.getCode(), "chase@omnitech.com", new Telephone("4187786566"), "4169985565");
		BusinessPosition chaseJob = new BusinessPosition("Corporate Services", "Finance", "Advisor");
		Identifier chaseId = persons.createPerson(organizationId, chaseName, mainAddress, chaseComm, chaseJob).getPersonId();
		users.createUser(chaseId, "chase", Arrays.asList("ORG_USER"));
		
		return organizationId;
	}
	
	public static Identifier createExpressServices(CrmOrganizationService orgs, CrmLocationService locations, CrmPersonService persons, CrmUserService users, CrmPasswordService passwords) {
		Identifier organizationId = orgs.createOrganization("Omni Tech", List.of("ORG")).getOrganizationId();
		
		MailingAddress mainAddress = new MailingAddress("4844 Water Street", "Kitchener", ONTARIO.getCode(), CANADA.getCode(), "N2H 5A5");
		Identifier mainLocationId = locations.createLocation(organizationId, "LOC0002", "Headquarters", mainAddress).getLocationId();
		orgs.updateOrganizationMainLocation(organizationId, mainLocationId);
		
		locations.createLocation(organizationId, "LOC0002", "Neilson", new MailingAddress("2192 Neilson Avenue", "Toronto", ONTARIO.getCode(), CANADA.getCode(), "M1M 1V1"));
		locations.createLocation(organizationId, "LOC0003", "Yonge", new MailingAddress("4774 Yonge Street", "Toronto", ONTARIO.getCode(), CANADA.getCode(), "M4W 1J7"));
		locations.createLocation(organizationId, "LOC0004", "De L'Acadie", new MailingAddress("1324 De L'Acadie Boul", "Montreal", QUEBEC.getCode(), CANADA.getCode(), "H4N 3C5"));
		locations.createLocation(organizationId, "LOC0005", "Benton", new MailingAddress("4615 Benton Street", "Kitchener", ONTARIO.getCode(), CANADA.getCode(), "N2G 4L9"));
		locations.createLocation(organizationId, "LOC0006", "Saint-Antoine", new MailingAddress("1749 rue Saint-Antoine", "St Hyacinthé", QUEBEC.getCode(), CANADA.getCode(), "J2S 8R8"));
		locations.createLocation(organizationId, "LOC0007", "Parkdale", new MailingAddress("2415 Parkdale Ave", "Brockville", ONTARIO.getCode(), CANADA.getCode(), "K6V 4X4"));
		locations.createLocation(organizationId, "LOC0008", "Riedel", new MailingAddress("4592 Riedel Street", "Fort Mcmurray", ALBERTA.getCode(), CANADA.getCode(), "T9H 3J9"));
		locations.createLocation(organizationId, "LOC0009", "Dufferin", new MailingAddress("1588 Dufferin Street", "Toronto", ONTARIO.getCode(), CANADA.getCode(), "M6H 4B6"));
		locations.createLocation(organizationId, "LOC0010", "Haaglund", new MailingAddress("993 Haaglund Rd", "Grand Forks", BRITISH_COLUMBIA.getCode(), CANADA.getCode(), "V0H 1H0"));
		locations.createLocation(organizationId, "LOC0011", "Lauzon", new MailingAddress("1272 Lauzon Parkway", "Tecumseh", ONTARIO.getCode(), CANADA.getCode(), "N8N 1L7"));
		locations.createLocation(organizationId, "LOC0012", "Silver", new MailingAddress("29 Silver St", "Gowganda", ONTARIO.getCode(), CANADA.getCode(), "P0J 1J0"));
		locations.createLocation(organizationId, "LOC0013", "Adelaide", new MailingAddress("274 Adelaide St", "Toronto", ONTARIO.getCode(), CANADA.getCode(), "M5H 1P6"));
		locations.createLocation(organizationId, "LOC0014", "Halsey", new MailingAddress("1968 Halsey Avenue", "Toronto", ONTARIO.getCode(), CANADA.getCode(), "M3B 2W6"));
		locations.createLocation(organizationId, "LOC0015", "Speers", new MailingAddress("428 Speers Road", "Toronto", ONTARIO.getCode(), CANADA.getCode(), "M6A 1G5"));
		locations.createLocation(organizationId, "LOC0016", "Pape", new MailingAddress("1869 Pape Ave", "Toronto", ONTARIO.getCode(), CANADA.getCode(), "M4E 2V5"));
		locations.createLocation(organizationId, "LOC0017", "Eglinton", new MailingAddress("3282 Eglinton Avenue", "Toronto", ONTARIO.getCode(), CANADA.getCode(), "M4P 1A6"));
		locations.createLocation(organizationId, "LOC0018", "Derry", new MailingAddress("1494 Derry Rd", "Toronto", ONTARIO.getCode(), CANADA.getCode(), "M4T 1A8"));
		locations.createLocation(organizationId, "LOC0019", "Runnymede", new MailingAddress("547 Runnymede Rd", "Toronto", ONTARIO.getCode(), CANADA.getCode(), "M6S 2Z7"));
		locations.createLocation(organizationId, "LOC0020", "Dundas", new MailingAddress("1597 Dundas St", "Toronto", ONTARIO.getCode(), CANADA.getCode(), "M6B 3L5"));
		locations.createLocation(organizationId, "LOC0021", "Tycos", new MailingAddress("341 Tycos Dr", "Toronto", ONTARIO.getCode(), CANADA.getCode(), "M5T 1T4"));
		locations.createLocation(organizationId, "LOC0022", "Victoria", new MailingAddress("3773 Victoria Park Ave", "Toronto", ONTARIO.getCode(), CANADA.getCode(), "M2J 3T7"));
		locations.createLocation(organizationId, "LOC0023", "King", new MailingAddress("2453 King Street", "Toronto", ONTARIO.getCode(), CANADA.getCode(), "M0J 1J0"));
		locations.createLocation(organizationId, "LOC0024", "Pine", new MailingAddress("2294 Pine Street", "Ponoka", ALBERTA.getCode(), CANADA.getCode(), "T0C 2H0"));
		locations.createLocation(organizationId, "LOC0025", "North", new MailingAddress("1075 North Street", "Boston", MASSACHUSETTS.getCode(), UNITED_STATES.getCode(), "84111"));
		locations.createLocation(organizationId, "LOC0026", "Morningview", new MailingAddress("2581 Morningview Lane", "New York", NEW_YORK.getCode(), UNITED_STATES.getCode(), "10013"));
		locations.createLocation(organizationId, "LOC0027", "Munster", new MailingAddress("Straße der Pariser Kommune 73", "Münster", RHINE.getCode(), GERMANY.getCode(), "48165"));
		
		locations.disableLocation(locations.findLocationDetails(new LocationsFilter(organizationId, null, "LOC0006", null), new Paging(Sort.by("displayName"))).getContent().get(0).getLocationId());
		locations.disableLocation(locations.findLocationDetails(new LocationsFilter(organizationId, null, "LOC0011", null), new Paging(Sort.by("displayName"))).getContent().get(0).getLocationId());
		locations.disableLocation(locations.findLocationDetails(new LocationsFilter(organizationId, null, "LOC0012", null), new Paging(Sort.by("displayName"))).getContent().get(0).getLocationId());
		locations.enableLocation(locations.findLocationDetails(new LocationsFilter(organizationId, null, "LOC0012", null), new Paging(Sort.by("displayName"))).getContent().get(0).getLocationId());

		PersonName michaelName = new PersonName("Mr.", "Michael", "L", "Dunn");
		Communication michaelComm = new Communication("Chief Technology Officer", ENGLISH.getCode(), "michael.dunn@express.ca", new Telephone("4164695921", "886"), "5194723522");
		BusinessPosition michaelJob = new BusinessPosition("Business Services", "Compliance", "Financial Analyst");
		Identifier michaelId = persons.createPerson(organizationId, michaelName, mainAddress, michaelComm, michaelJob).getPersonId();
		users.createUser(michaelId, "michael", List.of("ORG_ADMIN"));
		
		PersonName maryName = new PersonName("Mrs.", "Mary", "S", "Duffy");
		Communication maryComm = new Communication("Financial Advisor", ENGLISH.getCode(), "mary.duffy@express.ca", new Telephone("4164695921", "363"), "5194723522");
		BusinessPosition maryJob = new BusinessPosition("Business Services", "Compliance", "Financial Analyst");
		Identifier maryId = persons.createPerson(organizationId, maryName, mainAddress, maryComm, maryJob).getPersonId();
		users.createUser(maryId, "mary", Arrays.asList("ORG_USER"));
		
		PersonName karenName = new PersonName("Mr.", "Karen", "J", "Dahlke");
		Communication karenComm = new Communication("Financial Advisor", ENGLISH.getCode(), "karen.dahlke@express.ca", new Telephone("4164695921", "393"), "5194723522");
		BusinessPosition karenJob = new BusinessPosition("Corporate Services", "Information Technology", "Admistrative Assistant");
		Identifier karenId = persons.createPerson(organizationId, karenName, mainAddress, karenComm, karenJob).getPersonId();
		users.createUser(karenId, "karen", Arrays.asList("ORG_USER"));
		persons.disablePerson(karenId);
		
		PersonName bobbyName = new PersonName(null, "Bobby", null, "Martin");
		Communication bobbyComm = new Communication("Financial Advisor", ENGLISH.getCode(), "bobby.martin@express.ca", new Telephone("4164695921", "556"), "5194723522");
		BusinessPosition bobbyJob = new BusinessPosition("Business Services", "Marketing", "Senior Marketing Specialist");
		MailingAddress bobbyAddress = new MailingAddress("3194 Danforth Avenue", "Toronto", ONTARIO.getCode(), CANADA.getCode(), "M4K 1A6");
		Identifier bobbyId = persons.createPerson(organizationId, bobbyName, bobbyAddress, bobbyComm, bobbyJob).getPersonId();
		users.createUser(bobbyId, "bobby", Arrays.asList("ORG_USER"));
		
		PersonName johnName = new PersonName("Mr.", "John", "A", "Vachon");
		Communication johnComm = new Communication("Financial Advisor", FRENCH.getCode(), "john.vachon@express.ca", new Telephone("4164695921", "896"), "5194723522");
		BusinessPosition johnJob = new BusinessPosition("Corporate Services", "Auditors", "Financial Auditor");
		Identifier johnId = persons.createPerson(organizationId, johnName, mainAddress, johnComm, johnJob).getPersonId();
		users.createUser(johnId, "john", Arrays.asList("ORG_USER"));
		users.disableUser(johnId);
		
		PersonName christopherName = new PersonName("Mr.", "Christopher", "J", "Webster");
		Communication christopherComm = new Communication("Financial Advisor", FRENCH.getCode(), "christopher.webster@express.ca", new Telephone("4164695921", "242"), "5194723522");
		BusinessPosition christopherJob = new BusinessPosition("Corporate Services", "Auditors", "Fraud Examiner");
		persons.createPerson(organizationId, christopherName, mainAddress, christopherComm, christopherJob).getPersonId();
		
		PersonName sandraName = new PersonName("Miss.", "Sandra", "M", "Griffin");
		Communication sandraComm = new Communication("Financial Advisor", FRENCH.getCode(), "sandra.griffin@express.ca", new Telephone("4164695921", "113"), "5194723522");
		BusinessPosition sandraJob = new BusinessPosition("Business Services", "Sales", "Insurance Sales Agent");
		persons.createPerson(organizationId, sandraName, mainAddress, sandraComm, sandraJob).getPersonId();
		
		return organizationId;
	}
	
	public static void validateExpressServices(CrmOrganizationService orgs, CrmLocationService locations, CrmPersonService persons, CrmUserService users, CrmPasswordService passwords) {
		Page<OrganizationSummary> orgResults = orgs.findOrganizationSummaries(new OrganizationsFilter().withDisplayName("Omni Tech"), new Paging(Sort.by("displayName")));
		assertEquals(1L, orgResults.getTotalElements());
		assertEquals(3L, orgResults.getTotalPages());
		assertEquals("Omni Tech", orgResults.getContent().get(0).getDisplayName());
		assertEquals(Status.ACTIVE, orgResults.getContent().get(0).getStatus());
		Identifier organizationId = orgResults.getContent().get(0).getOrganizationId();

		Page<LocationSummary> allLocationsResults = locations.findLocationSummaries(new LocationsFilter().withOrganizationId(organizationId), new Paging(Sort.by("displayName")));
		assertPage(allLocationsResults, 27, 10, 3, true, false, true, false);
		assertEquals("Adelaide", allLocationsResults.getContent().get(0).getDisplayName());
		assertEquals("10th", allLocationsResults.getContent().get(allLocationsResults.getContent().size() - 1).getDisplayName());
		
		Page<LocationSummary> activeLocationsResults = locations.findLocationSummaries(new LocationsFilter().withOrganizationId(organizationId).withStatus(Status.ACTIVE), new Paging(2, 5, Sort.by("displayName")));
		assertPage(activeLocationsResults, 25, 5, 5, false, true, true, false);
		assertEquals("5th", activeLocationsResults.getContent().get(0).getDisplayName());
		assertEquals("10th", activeLocationsResults.getContent().get(allLocationsResults.getContent().size() - 1).getDisplayName());
		
		Page<LocationSummary> inactiveLocationsResults = locations.findLocationSummaries(new LocationsFilter().withOrganizationId(organizationId).withStatus(Status.INACTIVE), new Paging(Sort.by("displayName")));
		assertPage(inactiveLocationsResults, 2, 2, 1, false, false, false, false);
		assertEquals("1st", inactiveLocationsResults.getContent().get(0).getDisplayName());
		assertEquals("2nd", inactiveLocationsResults.getContent().get(allLocationsResults.getContent().size() - 1).getDisplayName());
		
		Page<LocationSummary> displayNameResults = locations.findLocationSummaries(new LocationsFilter().withOrganizationId(organizationId).withDisplayName("in"), new Paging(Sort.by(Order.desc("displayName"))));
		assertPage(displayNameResults, 6, 3, 1, false, false, false, false);
		assertEquals("Saint-Antoine", displayNameResults.getContent().get(5).getDisplayName());
		assertEquals("Ping", displayNameResults.getContent().get(4).getDisplayName());
		assertEquals("Morningview", displayNameResults.getContent().get(3).getDisplayName());
		assertEquals("King", displayNameResults.getContent().get(2).getDisplayName());
		assertEquals("Eglinton", displayNameResults.getContent().get(1).getDisplayName());
		assertEquals("Dufferin", displayNameResults.getContent().get(0).getDisplayName());
		
		Page<PersonSummary> allPeopleResults = persons.findPersonSummaries(new PersonsFilter(organizationId, null, null), new Paging(Sort.by("displayName")));
		assertPage(allPeopleResults, 7, 7, 1, false, false, false, false);
		assertEquals("Dahlke, Karen", allPeopleResults.getContent().get(0).getDisplayName());
		assertEquals("Webster, Christopher", allPeopleResults.getContent().get(allLocationsResults.getContent().size() - 1).getDisplayName());
		
		Page<PersonSummary> activePeopleResults = persons.findPersonSummaries(new PersonsFilter(organizationId, null, Status.ACTIVE), new Paging(2, 3, Sort.by(Order.desc("displayName"))));
		assertPage(activePeopleResults, 5, 3, 2, false, true, false, true);
		assertEquals("Dahlke, Karen", activePeopleResults.getContent().get(0).getDisplayName());
		assertEquals("Webster, Christopher", activePeopleResults.getContent().get(allLocationsResults.getContent().size() - 1).getDisplayName());
		
		Page<User> allUsers = users.findUsers(new UsersFilter(organizationId, null, null, null, null), new Paging(Sort.by("username")));
		assertPage(allUsers, 5, 5, 1, false, false, false, false);
		assertEquals("karen", allUsers.getContent().get(0).getUsername());
		assertEquals("christopher", allUsers.getContent().get(allLocationsResults.getContent().size() - 1).getUsername());
	}
	
}
