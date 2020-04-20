package ca.magex.crm.test;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.common.Telephone;
import ca.magex.crm.api.common.User;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.lookup.BusinessClassification;
import ca.magex.crm.api.lookup.BusinessSector;
import ca.magex.crm.api.lookup.BusinessUnit;
import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.lookup.CrmLookupItem;
import ca.magex.crm.api.lookup.Language;
import ca.magex.crm.api.lookup.Salutation;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Role;
import ca.magex.crm.api.system.Status;

/**
 * Test suite for running an end to end test of the CRM Services 
 * 
 * @author Jonny
 *
 */
@Component
public class CrmServicesTestSuite {
	
	private static final Logger logger = LoggerFactory.getLogger(CrmServicesTestSuite.class);
	
	@Autowired private CrmLookupService lookupService;
	
	@Autowired private CrmOrganizationService organizationService;
	
	@Autowired private CrmLocationService locationService;
	
	@Autowired private CrmPersonService personService;

	public void runAllTests() {
		runLookupServiceTests();
		Identifier orgIdentifier = runOrganizationServiceTests();
		runLocationServiceTests(orgIdentifier);
		runPersonServiceTests(orgIdentifier);
	}
	
	private void runLookupServiceTests() {
		logger.info("----------------------------");
		logger.info("Running Lookup Service Tests");
		logger.info("----------------------------");
		runLookupTest(Status.class, lookupService::findStatuses, lookupService::findStatusByCode, lookupService::findStatusByLocalizedName);				
		runLookupTest(Role.class, lookupService::findRoles, lookupService::findRoleByCode, lookupService::findRoleByLocalizedName);
		runLookupTest(Country.class, lookupService::findCountries, lookupService::findCountryByCode, lookupService::findCountryByLocalizedName);
		runLookupTest(Language.class, lookupService::findLanguages, lookupService::findLanguageByCode, lookupService::findLanguageByLocalizedName);
		runLookupTest(Salutation.class, lookupService::findSalutations, lookupService::findSalutationByCode, lookupService::findSalutationByLocalizedName);
		runLookupTest(BusinessSector.class, lookupService::findBusinessSectors, lookupService::findBusinessSectorByCode, lookupService::findBusinessSectorByLocalizedName);
		runLookupTest(BusinessUnit.class, lookupService::findBusinessUnits, lookupService::findBusinessUnitByCode, lookupService::findBusinessUnitByLocalizedName);
		runLookupTest(BusinessClassification.class, lookupService::findBusinessClassifications, lookupService::findBusinessClassificationByCode, lookupService::findBusinessClassificationByLocalizedName);
	}	
	
	private <T extends CrmLookupItem> void runLookupTest(Class<T> item, Supplier<List<T>> supplier, Function<String, T> codeLookup, BiFunction<Locale, String, T> localizedLookup) {		
		/* countries tests */
		List<T> values = supplier.get();
		for (T value : values) {
			Assert.assertEquals(value, codeLookup.apply(value.getCode()));
			Assert.assertEquals(value, localizedLookup.apply(Locale.CANADA, value.getName(Locale.CANADA)));
			Assert.assertEquals(value, localizedLookup.apply(Locale.CANADA_FRENCH, value.getName(Locale.CANADA_FRENCH)));
			try {
				localizedLookup.apply(Locale.CANADA, "????");
				Assert.fail("Unsupported Value");
			}
			catch(ItemNotFoundException e) {}
			
			try {
				localizedLookup.apply(Locale.GERMAN, "");
				Assert.fail("Unsupported Country");
			}
			catch(ItemNotFoundException e) {}
		}
		logger.info("Running lookup tests for " + item.getName() + " Passed");
	}
	
	private Identifier runOrganizationServiceTests() {
		logger.info("----------------------------------");
		logger.info("Running Organization Service Tests");
		logger.info("----------------------------------");
		/* get initial organization count */
		long orgCount = organizationService.countOrganizations(new OrganizationsFilter());
		logger.info("Organization Count: " + orgCount);
		
		/* create and verify new organization */
		logger.info("Creating new Organiztion");
		OrganizationDetails orgDetails = organizationService.createOrganization("ABC");
		Identifier orgId = orgDetails.getOrganizationId();
		logger.info("Generated OrgId: " + orgId);
		verifyOrgDetails(orgDetails, orgId, Status.ACTIVE, "ABC", null);		
		
		/* verify that we our organization count incremented by 1 */		
		long newOrgCount = organizationService.countOrganizations(new OrganizationsFilter());
		logger.info("Organization Count: " + newOrgCount);
		Assert.assertEquals(orgCount + 1, newOrgCount);
				
		/* create and verify new location for organization */
		logger.info("Creating main Location");
		MailingAddress address = new MailingAddress("54 fifth street", "Toronto", "ON", new Country("CA", "Canada", "Canada"), "T5R5X3");		
		LocationDetails locDetails = locationService.createLocation(
				orgId, 
				"HeadQuarters", 
				"HQ", 
				address);
		Identifier locId = locDetails.getLocationId();
		logger.info("Generated locId: " + locId);
		verifyLocationDetails(locDetails, orgId, locId, Status.ACTIVE, "HQ", "HeadQuarters", address);
				
		/* set and verify organization main location */
		logger.info("Updating Organization main Location");
		orgDetails = organizationService.updateOrganizationMainLocation(orgDetails.getOrganizationId(), locDetails.getLocationId());
		verifyOrgDetails(orgDetails, orgId, Status.ACTIVE, "ABC", locId);		
		
		/* update and verify organization name */ 
		logger.info("Updating Organization name");
		String newName = "ABC" + System.currentTimeMillis();
		orgDetails = organizationService.updateOrganizationDisplayName(orgDetails.getOrganizationId(), newName);		
		verifyOrgDetails(orgDetails, orgId, Status.ACTIVE, newName, locId);		
		
		/* disable and verify organization */
		logger.info("Disabling Organization");
		OrganizationSummary orgSummary = organizationService.disableOrganization(orgDetails.getOrganizationId());
		verifyOrgSummary(orgSummary, orgId, Status.INACTIVE, newName);
		
		orgSummary = organizationService.findOrganizationSummary(orgDetails.getOrganizationId());
		verifyOrgSummary(orgSummary, orgId, Status.INACTIVE, newName);
		
		orgDetails = organizationService.findOrganizationDetails(orgDetails.getOrganizationId());
		verifyOrgDetails(orgDetails, orgId, Status.INACTIVE, newName, locId);
		
		/* enable and verify organization */
		logger.info("Enabling Organization");
		orgSummary = organizationService.enableOrganization(orgDetails.getOrganizationId());
		verifyOrgSummary(orgSummary, orgId, Status.ACTIVE, newName);
		
		orgSummary = organizationService.findOrganizationSummary(orgDetails.getOrganizationId());
		verifyOrgSummary(orgSummary, orgId, Status.ACTIVE, newName);
		
		orgDetails = organizationService.findOrganizationDetails(orgDetails.getOrganizationId());
		verifyOrgDetails(orgDetails, orgId, Status.ACTIVE, newName, locId);
		
		/* validate details paging with 1 match on name filter */
		logger.info("Finding Organization Details with Name Match");
		Page<OrganizationDetails> orgDetailsPage = organizationService.findOrganizationDetails(new OrganizationsFilter(newName, Status.ACTIVE), new Paging(1, 5, Sort.by(Direction.ASC, "displayName")));
		Assert.assertEquals(1, orgDetailsPage.getNumber());
		Assert.assertEquals(1, orgDetailsPage.getTotalPages());
		Assert.assertEquals(1, orgDetailsPage.getNumberOfElements());
		Assert.assertEquals(1, orgDetailsPage.getTotalElements());
		Assert.assertEquals(1, orgDetailsPage.getContent().size());
		Assert.assertEquals(orgDetails, orgDetailsPage.getContent().get(0));
		
		/* validate details paging with no match on name filter */
		logger.info("Finding Organization Details without Name Match");
		orgDetailsPage = organizationService.findOrganizationDetails(new OrganizationsFilter(newName + "00", Status.ACTIVE), new Paging(1, 5, Sort.by(Direction.ASC, "displayName")));
		Assert.assertEquals(1, orgDetailsPage.getNumber());
		Assert.assertEquals(0, orgDetailsPage.getTotalPages());
		Assert.assertEquals(0, orgDetailsPage.getNumberOfElements());
		Assert.assertEquals(0, orgDetailsPage.getTotalElements());
		Assert.assertEquals(0, orgDetailsPage.getContent().size());
		
		/* validate summary paging with 1 match on name filter */
		logger.info("Finding Organization Summary without Name Match");
		Page<OrganizationSummary> orgSummaryPage = organizationService.findOrganizationSummaries(new OrganizationsFilter(newName, Status.ACTIVE), new Paging(1, 5, Sort.by(Direction.ASC, "displayName")));
		Assert.assertEquals(1, orgSummaryPage.getNumber());
		Assert.assertEquals(1, orgSummaryPage.getTotalPages());
		Assert.assertEquals(1, orgSummaryPage.getNumberOfElements());
		Assert.assertEquals(1, orgSummaryPage.getTotalElements());
		Assert.assertEquals(1, orgSummaryPage.getContent().size());
		Assert.assertEquals(orgSummary, orgSummaryPage.getContent().get(0));
		
		/* validate summary paging with no match on name filter */
		logger.info("Finding Organization Summary without Name Match");
		orgSummaryPage = organizationService.findOrganizationSummaries(new OrganizationsFilter(newName + "00", Status.ACTIVE), new Paging(1, 5, Sort.by(Direction.ASC, "displayName")));
		Assert.assertEquals(1, orgSummaryPage.getNumber());
		Assert.assertEquals(0, orgSummaryPage.getTotalPages());
		Assert.assertEquals(0, orgSummaryPage.getNumberOfElements());
		Assert.assertEquals(0, orgSummaryPage.getTotalElements());
		Assert.assertEquals(0, orgSummaryPage.getContent().size());
		
		return orgDetails.getOrganizationId();
	}

	private void runLocationServiceTests(Identifier orgId) {
		logger.info("------------------------------");
		logger.info("Running Location Service Tests");
		logger.info("------------------------------");
		OrganizationDetails orgDetails = organizationService.findOrganizationDetails(orgId);
		Identifier locId = orgDetails.getMainLocationId();
		
		/* retrieve the location details */		
		final LocationDetails originalLocationDetails = locationService.findLocationDetails(locId);
		
		/* disable location and verify the result */
		logger.info("Disabling Location");
		LocationSummary locSummary = locationService.disableLocation(orgDetails.getMainLocationId());
		verifyLocationSummary(locSummary, orgId, locId, Status.INACTIVE, originalLocationDetails.getReference(), originalLocationDetails.getDisplayName());
		
		locSummary = locationService.findLocationSummary(locId);
		verifyLocationSummary(locSummary, orgId, locId, Status.INACTIVE, originalLocationDetails.getReference(), originalLocationDetails.getDisplayName());
		
		LocationDetails locDetails = locationService.findLocationDetails(locId);
		verifyLocationDetails(locDetails, orgId, locId, Status.INACTIVE, originalLocationDetails.getReference(), originalLocationDetails.getDisplayName(), originalLocationDetails.getAddress());
				
		/* enable location and verify the result */
		logger.info("Enabling Location");
		locSummary = locationService.enableLocation(orgDetails.getMainLocationId());
		verifyLocationSummary(locSummary, orgId, locId, Status.ACTIVE, originalLocationDetails.getReference(), originalLocationDetails.getDisplayName());
		
		locSummary = locationService.findLocationSummary(locId);
		verifyLocationSummary(locSummary, orgId, locId, Status.ACTIVE, originalLocationDetails.getReference(), originalLocationDetails.getDisplayName());
		
		locDetails = locationService.findLocationDetails(locId);
		verifyLocationDetails(locDetails, orgId, locId, Status.ACTIVE, originalLocationDetails.getReference(), originalLocationDetails.getDisplayName(), originalLocationDetails.getAddress());
		
		/* update and verify the location name */
		logger.info("Updating Location Name");
		String newName = originalLocationDetails.getDisplayName() + System.currentTimeMillis();
		locDetails = locationService.updateLocationName(locId, newName);
		verifyLocationDetails(locDetails, orgId, locId, Status.ACTIVE, originalLocationDetails.getReference(), newName, originalLocationDetails.getAddress());
		
		locSummary = locationService.findLocationSummary(locId);
		verifyLocationSummary(locSummary, orgId, locId, Status.ACTIVE, originalLocationDetails.getReference(), newName);
		
		/* update and verify the location address */
		logger.info("Updating Location Address");
		MailingAddress newAddress = new MailingAddress("55 second street", "Toronto", "ON", new Country("CA", "Canada", "Canada"), "T5R5X3");
		locDetails = locationService.updateLocationAddress(locId, newAddress);
		verifyLocationDetails(locDetails, orgId, locId, Status.ACTIVE, originalLocationDetails.getReference(), newName, newAddress);
		
		locSummary = locationService.findLocationSummary(locId);
		verifyLocationSummary(locSummary, orgId, locId, Status.ACTIVE, originalLocationDetails.getReference(), newName);
		
		/* validate details paging with 1 match on name filter */
		logger.info("Finding Location Details with Name Match");
		Page<LocationDetails> locDetailsPage = locationService.findLocationDetails(new LocationsFilter(orgDetails.getOrganizationId(), newName, Status.ACTIVE), new Paging(1, 5, Sort.by(Direction.ASC, "displayName")));
		Assert.assertEquals(1, locDetailsPage.getNumber());
		Assert.assertEquals(1, locDetailsPage.getTotalPages());
		Assert.assertEquals(1, locDetailsPage.getNumberOfElements());
		Assert.assertEquals(1, locDetailsPage.getTotalElements());
		Assert.assertEquals(1, locDetailsPage.getContent().size());
		Assert.assertEquals(locDetails, locDetailsPage.getContent().get(0));
		
		/* validate details paging with no match on name filter */
		logger.info("Finding Location Details without Name Match");
		locDetailsPage = locationService.findLocationDetails(new LocationsFilter(orgDetails.getOrganizationId(), newName + "00", Status.ACTIVE), new Paging(1, 5, Sort.by(Direction.ASC, "displayName")));
		Assert.assertEquals(1, locDetailsPage.getNumber());
		Assert.assertEquals(0, locDetailsPage.getTotalPages());
		Assert.assertEquals(0, locDetailsPage.getNumberOfElements());
		Assert.assertEquals(0, locDetailsPage.getTotalElements());
		Assert.assertEquals(0, locDetailsPage.getContent().size());
		
		/* validate summary paging with 1 match on name filter */
		logger.info("Finding Location Summary without Name Match");
		Page<LocationSummary> locSummaryPage = locationService.findLocationSummaries(new LocationsFilter(orgDetails.getOrganizationId(), newName, Status.ACTIVE), new Paging(1, 5, Sort.by(Direction.ASC, "displayName")));
		Assert.assertEquals(1, locSummaryPage.getNumber());
		Assert.assertEquals(1, locSummaryPage.getTotalPages());
		Assert.assertEquals(1, locSummaryPage.getNumberOfElements());
		Assert.assertEquals(1, locSummaryPage.getTotalElements());
		Assert.assertEquals(1, locSummaryPage.getContent().size());
		Assert.assertEquals(locSummary, locSummaryPage.getContent().get(0));
		
		/* validate summary paging with no match on name filter */
		logger.info("Finding Location Summary without Name Match");
		locSummaryPage = locationService.findLocationSummaries(new LocationsFilter(orgDetails.getOrganizationId(), newName + "00", Status.ACTIVE), new Paging(1, 5, Sort.by(Direction.ASC, "displayName")));
		Assert.assertEquals(1, locSummaryPage.getNumber());
		Assert.assertEquals(0, locSummaryPage.getTotalPages());
		Assert.assertEquals(0, locSummaryPage.getNumberOfElements());
		Assert.assertEquals(0, locSummaryPage.getTotalElements());
		Assert.assertEquals(0, locSummaryPage.getContent().size());
	}

	private void runPersonServiceTests(Identifier orgId) {		
		logger.info("----------------------------");
		logger.info("Running Person Service Tests");
		logger.info("----------------------------");
		long personCount = personService.countPersons(new PersonsFilter());
		final PersonName originalName = new PersonName(lookupService.findSalutationByCode("1"), "Mike", "Peter", "Johns");
		final MailingAddress originalAddress = new MailingAddress("12 ninth street", "Ottawa", "ON", new Country("CA", "Canada", "Canada"), "K4J9O9");
		final Communication originalComms = new Communication("Engineer", lookupService.findLanguageByCode("en"), "Mike.Johns@ABC.ca", new Telephone("6135554545"), "6135554545");
		final BusinessPosition originalPosition = new BusinessPosition(lookupService.findBusinessSectorByCode("1"), lookupService.findBusinessUnitByCode("2"), lookupService.findBusinessClassificationByCode("3"));
		
		/* create a person and verify results */
		logger.info("Creating new Person");
		PersonDetails personDetails = personService.createPerson(orgId, 
				originalName, 
				originalAddress,
				originalComms, 
				originalPosition);
		Assert.assertEquals(personCount + 1, personService.countPersons(new PersonsFilter()));
		String userName = personDetails.getUser().getUserName();
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.ACTIVE, originalName.getDisplayName(), originalName, originalAddress, originalComms, originalPosition, new User(userName, Collections.emptyList()));
		
		/* disable and verify the results */
		logger.info("Disabling Person");
		PersonSummary personSummary = personService.disablePerson(personDetails.getPersonId());
		verifyPersonSummary(personSummary, orgId, personDetails.getPersonId(), Status.INACTIVE, originalName.getDisplayName());
		
		personSummary = personService.findPersonSummary(personDetails.getPersonId());
		verifyPersonSummary(personSummary, orgId, personDetails.getPersonId(), Status.INACTIVE, originalName.getDisplayName());
		
		personDetails = personService.findPersonDetails(personDetails.getPersonId());
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.INACTIVE, originalName.getDisplayName(), originalName, originalAddress, originalComms, originalPosition, new User(userName, Collections.emptyList()));

		/* enable and verify the results */
		logger.info("Enabling Person");
		personSummary = personService.enablePerson(personDetails.getPersonId());
		verifyPersonSummary(personSummary, orgId, personDetails.getPersonId(), Status.ACTIVE, originalName.getDisplayName());
		
		personSummary = personService.findPersonSummary(personDetails.getPersonId());
		verifyPersonSummary(personSummary, orgId, personDetails.getPersonId(), Status.ACTIVE, originalName.getDisplayName());
		
		personDetails = personService.findPersonDetails(personDetails.getPersonId());
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.ACTIVE, originalName.getDisplayName(), originalName, originalAddress, originalComms, originalPosition, new User(userName, Collections.emptyList()));
		
		/* update person name and verify */
		logger.info("Updating Person Name");
		final PersonName newName = new PersonName(lookupService.findSalutationByCode("2"), "Susan", System.currentTimeMillis() + "", "Anderson");
		personDetails = personService.updatePersonName(personDetails.getPersonId(), newName);
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName(), newName, originalAddress, originalComms, originalPosition, new User(userName, Collections.emptyList()));
		
		personSummary = personService.findPersonSummary(personDetails.getPersonId());
		verifyPersonSummary(personSummary, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName());
		
		personDetails = personService.findPersonDetails(personDetails.getPersonId());
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName(), newName, originalAddress, originalComms, originalPosition, new User(userName, Collections.emptyList()));
		
		/* update person address and verify */
		logger.info("Updating Person Address");
		final MailingAddress newAddress = new MailingAddress("15 fourth street", "Ottawa", "ON", new Country("CA", "Canada", "Canada"), "K4J9O9");
		personDetails = personService.updatePersonAddress(personDetails.getPersonId(), newAddress);
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName(), newName, newAddress, originalComms, originalPosition, new User(userName, Collections.emptyList()));
		
		personSummary = personService.findPersonSummary(personDetails.getPersonId());
		verifyPersonSummary(personSummary, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName());
		
		personDetails = personService.findPersonDetails(personDetails.getPersonId());
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName(), newName, newAddress, originalComms, originalPosition, new User(userName, Collections.emptyList()));
		
		/* update person communications and verify */
		logger.info("Updating Person Communiation");
		final Communication newComms = new Communication("Supervisor", lookupService.findLanguageByCode("en"), "Susan.Anderson@ABC.ca", new Telephone("6135554543", ""), "6135554543");
		personDetails = personService.updatePersonCommunication(personDetails.getPersonId(), newComms);
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName(), newName, newAddress, newComms, originalPosition, new User(userName, Collections.emptyList()));
		
		personSummary = personService.findPersonSummary(personDetails.getPersonId());
		verifyPersonSummary(personSummary, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName());
		
		personDetails = personService.findPersonDetails(personDetails.getPersonId());
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName(), newName, newAddress, newComms, originalPosition, new User(userName, Collections.emptyList()));
		
		/* update person position and verify */
		logger.info("Updating Person Business Position");
		final BusinessPosition newPosition = new BusinessPosition(lookupService.findBusinessSectorByCode("2"), lookupService.findBusinessUnitByCode("3"), lookupService.findBusinessClassificationByCode("4"));
		personDetails = personService.updatePersonBusinessPosition(personDetails.getPersonId(), newPosition);
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName(), newName, newAddress, newComms, newPosition, new User(userName, Collections.emptyList()));
		
		personSummary = personService.findPersonSummary(personDetails.getPersonId());
		verifyPersonSummary(personSummary, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName());
		
		personDetails = personService.findPersonDetails(personDetails.getPersonId());
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName(), newName, newAddress, newComms, newPosition, new User(userName, Collections.emptyList()));
		
		/* add user role (first time we add a role it will generate a user name for us) */
		logger.info("Adding User Role");
		Role r1 = lookupService.findRoleByCode("CRM_ADMIN");
		personDetails = personService.addUserRole(personDetails.getPersonId(), r1);		
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName(), newName, newAddress, newComms, newPosition, new User(userName, Arrays.asList(r1)));
		
		final User originalUser = personDetails.getUser();
		
		personSummary = personService.findPersonSummary(personDetails.getPersonId());
		verifyPersonSummary(personSummary, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName());
		
		personDetails = personService.findPersonDetails(personDetails.getPersonId());
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName(), newName, newAddress, newComms, newPosition, originalUser);
		
		/* add another role and verify */
		logger.info("Adding User Role");
		Role r2 = lookupService.findRoleByCode("SYS_ADMIN");
		personDetails = personService.addUserRole(personDetails.getPersonId(), r2);		
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName(), newName, newAddress, newComms, newPosition, new User(userName, Arrays.asList(r1, r2)));
		
		User updateUser = personDetails.getUser();
		
		personSummary = personService.findPersonSummary(personDetails.getPersonId());
		verifyPersonSummary(personSummary, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName());
		
		personDetails = personService.findPersonDetails(personDetails.getPersonId());
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName(), newName, newAddress, newComms, newPosition, updateUser);
		
		/* remove a role and verify */
		logger.info("Removing User Role");
		personDetails = personService.removeUserRole(personDetails.getPersonId(), r1);		
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName(), newName, newAddress, newComms, newPosition, new User(userName, Arrays.asList(r2)));
		
		updateUser = personDetails.getUser();
		
		personSummary = personService.findPersonSummary(personDetails.getPersonId());
		verifyPersonSummary(personSummary, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName());
		
		personDetails = personService.findPersonDetails(personDetails.getPersonId());
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName(), newName, newAddress, newComms, newPosition, updateUser);
		
		/* set roles and verify */
		logger.info("Setting User Roles");
		personDetails = personService.setUserRoles(personDetails.getPersonId(), Arrays.asList(r1, r2));		
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName(), newName, newAddress, newComms, newPosition, new User(userName, Arrays.asList(r1, r2)));
		
		updateUser = personDetails.getUser();
		
		personSummary = personService.findPersonSummary(personDetails.getPersonId());
		verifyPersonSummary(personSummary, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName());
		
		personDetails = personService.findPersonDetails(personDetails.getPersonId());
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName(), newName, newAddress, newComms, newPosition, updateUser);
		
		/* validate details paging with 1 match on name filter */
		logger.info("Finding Person Details with Name Match");
		Page<PersonDetails> personDetailsPage = personService.findPersonDetails(new PersonsFilter(orgId, newName.getDisplayName(), Status.ACTIVE), new Paging(1, 5, Sort.by(Direction.ASC, "displayName")));
		Assert.assertEquals(1, personDetailsPage.getNumber());
		Assert.assertEquals(1, personDetailsPage.getTotalPages());
		Assert.assertEquals(1, personDetailsPage.getNumberOfElements());
		Assert.assertEquals(1, personDetailsPage.getTotalElements());
		Assert.assertEquals(1, personDetailsPage.getContent().size());
		Assert.assertEquals(personDetails, personDetailsPage.getContent().get(0));
		
		/* validate details paging with no match on name filter */
		logger.info("Finding Person Details without Name Match");
		personDetailsPage = personService.findPersonDetails(new PersonsFilter(orgId, newName.getDisplayName() + "00", Status.ACTIVE), new Paging(1, 5, Sort.by(Direction.ASC, "displayName")));
		Assert.assertEquals(1, personDetailsPage.getNumber());
		Assert.assertEquals(0, personDetailsPage.getTotalPages());
		Assert.assertEquals(0, personDetailsPage.getNumberOfElements());
		Assert.assertEquals(0, personDetailsPage.getTotalElements());
		Assert.assertEquals(0, personDetailsPage.getContent().size());
		
		/* validate summary paging with 1 match on name filter */
		logger.info("Finding Person Summary with Name Match");
		Page<PersonSummary> personSummaryPage = personService.findPersonSummaries(new PersonsFilter(orgId, newName.getDisplayName(), Status.ACTIVE), new Paging(1, 5, Sort.by(Direction.ASC, "displayName")));
		Assert.assertEquals(1, personSummaryPage.getNumber());
		Assert.assertEquals(1, personSummaryPage.getTotalPages());
		Assert.assertEquals(1, personSummaryPage.getNumberOfElements());
		Assert.assertEquals(1, personSummaryPage.getTotalElements());
		Assert.assertEquals(1, personSummaryPage.getContent().size());
		Assert.assertEquals(personSummary, personSummaryPage.getContent().get(0));
		
		/* validate summary paging with no match on name filter */
		logger.info("Finding Person Summary without Name Match");
		personSummaryPage = personService.findPersonSummaries(new PersonsFilter(orgId, newName.getDisplayName() + "00", Status.ACTIVE), new Paging(1, 5, Sort.by(Direction.ASC, "displayName")));
		Assert.assertEquals(1, personSummaryPage.getNumber());
		Assert.assertEquals(0, personSummaryPage.getTotalPages());
		Assert.assertEquals(0, personSummaryPage.getNumberOfElements());
		Assert.assertEquals(0, personSummaryPage.getTotalElements());
		Assert.assertEquals(0, personSummaryPage.getContent().size());
	}
	
	private void verifyOrgDetails(OrganizationDetails orgDetails, Identifier orgId, Status status, String displayName, Identifier mainLocationIdentifier) {
		Assert.assertNotNull(orgDetails.getOrganizationId());
		Assert.assertEquals(orgId, orgDetails.getOrganizationId());
		Assert.assertEquals(status, orgDetails.getStatus());
		Assert.assertEquals(displayName, orgDetails.getDisplayName());
		Assert.assertEquals(mainLocationIdentifier, orgDetails.getMainLocationId());
		logger.info("Verifying Organization Details Passed");
	}
	
	private void verifyOrgSummary(OrganizationSummary orgSummary, Identifier orgId, Status status, String displayName) {
		Assert.assertNotNull(orgSummary.getOrganizationId());
		Assert.assertEquals(orgId, orgSummary.getOrganizationId());
		Assert.assertEquals(status, orgSummary.getStatus());
		Assert.assertEquals(displayName, orgSummary.getDisplayName());
		logger.info("Verifying Organization Summary Passed");
	}
	
	private void verifyLocationDetails(LocationDetails locDetails, Identifier orgId, Identifier locId, Status status, String reference, String displayName, MailingAddress address) {
		Assert.assertNotNull(locDetails.getLocationId());		
		Assert.assertEquals(orgId, locDetails.getOrganizationId());
		Assert.assertEquals(locId, locDetails.getLocationId());
		Assert.assertEquals(reference, locDetails.getReference());
		Assert.assertEquals(displayName, locDetails.getDisplayName());
		Assert.assertEquals(address, locDetails.getAddress());
		Assert.assertEquals(status, locDetails.getStatus());
		logger.info("Verifying Location Details Passed");
	}
	
	private void verifyLocationSummary(LocationSummary locSummary, Identifier orgId, Identifier locId, Status status, String reference, String displayName) {
		Assert.assertNotNull(locSummary.getLocationId());		
		Assert.assertEquals(orgId, locSummary.getOrganizationId());
		Assert.assertEquals(locId, locSummary.getLocationId());
		Assert.assertEquals(reference, locSummary.getReference());
		Assert.assertEquals(displayName, locSummary.getDisplayName());
		Assert.assertEquals(status, locSummary.getStatus());
		logger.info("Verifying Location Summary Passed");
	}
	
	private void verifyPersonDetails(PersonDetails personDetails, Identifier orgId, Identifier personId, Status status, String displayName, PersonName legalName, MailingAddress address, Communication communication, BusinessPosition position, User user) {
		Assert.assertNotNull(personDetails.getPersonId());
		Assert.assertEquals(orgId, personDetails.getOrganizationId());
		Assert.assertEquals(personId, personDetails.getPersonId());
		Assert.assertEquals(status, personDetails.getStatus());
		Assert.assertEquals(displayName, personDetails.getDisplayName());
		Assert.assertEquals(legalName, personDetails.getLegalName());
		Assert.assertEquals(address, personDetails.getAddress());
		Assert.assertEquals(communication, personDetails.getCommunication());
		Assert.assertEquals(position, personDetails.getPosition());
		Assert.assertEquals(user, personDetails.getUser());
		logger.info("Verifying Person Details Passed");
	}
	
	private void verifyPersonSummary(PersonSummary personSummary, Identifier orgId, Identifier personId, Status status, String displayName) {
		Assert.assertNotNull(personSummary.getPersonId());
		Assert.assertEquals(orgId, personSummary.getOrganizationId());
		Assert.assertEquals(personId, personSummary.getPersonId());
		Assert.assertEquals(status, personSummary.getStatus());
		Assert.assertEquals(displayName, personSummary.getDisplayName());	
		logger.info("Verifying Person Summary Passed");
	}
}
