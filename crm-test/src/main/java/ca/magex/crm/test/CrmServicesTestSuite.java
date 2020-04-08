package ca.magex.crm.test;


import java.util.Arrays;

import org.junit.Assert;
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
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.lookup.BusinessClassification;
import ca.magex.crm.api.lookup.BusinessSector;
import ca.magex.crm.api.lookup.BusinessUnit;
import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.lookup.Language;
import ca.magex.crm.api.lookup.Salutation;
import ca.magex.crm.api.services.CrmLocationService;
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
	
	@Autowired private CrmOrganizationService organizationService;
	
	@Autowired private CrmLocationService locationService;
	
	@Autowired private CrmPersonService personService;

	public void runAllTests() {
		Identifier orgIdentifier = runOrganizationServiceTests();
		runLocationServiceTests(orgIdentifier);
		runPersonServiceTests(orgIdentifier);
	}

	private Identifier runOrganizationServiceTests() {
		/* get initial organization count */
		long orgCount = organizationService.countOrganizations(new OrganizationsFilter());		
		
		/* create and verify new organization */
		OrganizationDetails orgDetails = organizationService.createOrganization("ABC");
		Identifier orgId = orgDetails.getOrganizationId();
		verifyOrgDetails(orgDetails, orgId, Status.ACTIVE, "ABC", null);		
		
		/* verify that we our organization count incremented by 1 */
		Assert.assertEquals(orgCount + 1, organizationService.countOrganizations(new OrganizationsFilter()));
		
		/* create and verify new location for organization */
		MailingAddress address = new MailingAddress("54 fifth street", "Toronto", "ON", new Country("CA", "Canada", "Canada"), "T5R5X3");
		long locCount = locationService.countLocations(new LocationsFilter());
		LocationDetails locDetails = locationService.createLocation(
				orgId, 
				"HeadQuarters", 
				"HQ", 
				address);
		Assert.assertEquals(locCount + 1, locationService.countLocations(new LocationsFilter()));
		Identifier locId = locDetails.getLocationId();
		verifyLocationDetails(locDetails, orgId, locId, Status.ACTIVE, "HQ", "HeadQuarters", address);		
		
		/* set and verify organization main location */
		orgDetails = organizationService.updateOrganizationMainLocation(orgDetails.getOrganizationId(), locDetails.getLocationId());
		verifyOrgDetails(orgDetails, orgId, Status.ACTIVE, "ABC", locId);		
		
		/* update and verify organization name */ 
		String newName = "ABC" + System.currentTimeMillis();
		orgDetails = organizationService.updateOrganizationName(orgDetails.getOrganizationId(), newName);		
		verifyOrgDetails(orgDetails, orgId, Status.ACTIVE, newName, locId);		
		
		/* disable and verify organization */
		OrganizationSummary orgSummary = organizationService.disableOrganization(orgDetails.getOrganizationId());
		verifyOrgSummary(orgSummary, orgId, Status.INACTIVE, newName);
		
		orgSummary = organizationService.findOrganizationSummary(orgDetails.getOrganizationId());
		verifyOrgSummary(orgSummary, orgId, Status.INACTIVE, newName);
		
		orgDetails = organizationService.findOrganizationDetails(orgDetails.getOrganizationId());
		verifyOrgDetails(orgDetails, orgId, Status.INACTIVE, newName, locId);
		
		/* enable and verify organization */
		orgSummary = organizationService.enableOrganization(orgDetails.getOrganizationId());
		verifyOrgSummary(orgSummary, orgId, Status.ACTIVE, newName);
		
		orgSummary = organizationService.findOrganizationSummary(orgDetails.getOrganizationId());
		verifyOrgSummary(orgSummary, orgId, Status.ACTIVE, newName);
		
		orgDetails = organizationService.findOrganizationDetails(orgDetails.getOrganizationId());
		verifyOrgDetails(orgDetails, orgId, Status.ACTIVE, newName, locId);
		
		/* validate details paging with 1 match on name filter */
		Page<OrganizationDetails> orgDetailsPage = organizationService.findOrganizationDetails(new OrganizationsFilter(newName, Status.ACTIVE), new Paging(1, 5, Sort.by(Direction.ASC, "displayName")));
		Assert.assertEquals(1, orgDetailsPage.getNumber());
		Assert.assertEquals(1, orgDetailsPage.getTotalPages());
		Assert.assertEquals(1, orgDetailsPage.getNumberOfElements());
		Assert.assertEquals(1, orgDetailsPage.getTotalElements());
		Assert.assertEquals(1, orgDetailsPage.getContent().size());
		Assert.assertEquals(orgDetails, orgDetailsPage.getContent().get(0));
		
		/* validate details paging with no match on name filter */
		orgDetailsPage = organizationService.findOrganizationDetails(new OrganizationsFilter(newName + "00", Status.ACTIVE), new Paging(1, 5, Sort.by(Direction.ASC, "displayName")));
		Assert.assertEquals(1, orgDetailsPage.getNumber());
		Assert.assertEquals(0, orgDetailsPage.getTotalPages());
		Assert.assertEquals(0, orgDetailsPage.getNumberOfElements());
		Assert.assertEquals(0, orgDetailsPage.getTotalElements());
		Assert.assertEquals(0, orgDetailsPage.getContent().size());
		
		/* validate summary paging with 1 match on name filter */
		Page<OrganizationSummary> orgSummaryPage = organizationService.findOrganizationSummaries(new OrganizationsFilter(newName, Status.ACTIVE), new Paging(1, 5, Sort.by(Direction.ASC, "displayName")));
		Assert.assertEquals(1, orgSummaryPage.getNumber());
		Assert.assertEquals(1, orgSummaryPage.getTotalPages());
		Assert.assertEquals(1, orgSummaryPage.getNumberOfElements());
		Assert.assertEquals(1, orgSummaryPage.getTotalElements());
		Assert.assertEquals(1, orgSummaryPage.getContent().size());
		Assert.assertEquals(orgSummary, orgSummaryPage.getContent().get(0));
		
		/* validate summary paging with no match on name filter */
		orgSummaryPage = organizationService.findOrganizationSummaries(new OrganizationsFilter(newName + "00", Status.ACTIVE), new Paging(1, 5, Sort.by(Direction.ASC, "displayName")));
		Assert.assertEquals(1, orgSummaryPage.getNumber());
		Assert.assertEquals(0, orgSummaryPage.getTotalPages());
		Assert.assertEquals(0, orgSummaryPage.getNumberOfElements());
		Assert.assertEquals(0, orgSummaryPage.getTotalElements());
		Assert.assertEquals(0, orgSummaryPage.getContent().size());
		
		return orgDetails.getOrganizationId();
	}

	private void runLocationServiceTests(Identifier orgId) {
		OrganizationDetails orgDetails = organizationService.findOrganizationDetails(orgId);
		Identifier locId = orgDetails.getMainLocationId();
		
		/* retrieve the location details */
		final LocationDetails originalLocationDetails = locationService.findLocationDetails(locId);
		
		/* disable location and verify the result */
		LocationSummary locSummary = locationService.disableLocation(orgDetails.getMainLocationId());
		verifyLocationSummary(locSummary, orgId, locId, Status.INACTIVE, originalLocationDetails.getReference(), originalLocationDetails.getDisplayName());
		
		locSummary = locationService.findLocationSummary(locId);
		verifyLocationSummary(locSummary, orgId, locId, Status.INACTIVE, originalLocationDetails.getReference(), originalLocationDetails.getDisplayName());
		
		LocationDetails locDetails = locationService.findLocationDetails(locId);
		verifyLocationDetails(locDetails, orgId, locId, Status.INACTIVE, originalLocationDetails.getReference(), originalLocationDetails.getDisplayName(), originalLocationDetails.getAddress());
				
		/* enable location and verify the result */
		locSummary = locationService.enableLocation(orgDetails.getMainLocationId());
		verifyLocationSummary(locSummary, orgId, locId, Status.ACTIVE, originalLocationDetails.getReference(), originalLocationDetails.getDisplayName());
		
		locSummary = locationService.findLocationSummary(locId);
		verifyLocationSummary(locSummary, orgId, locId, Status.ACTIVE, originalLocationDetails.getReference(), originalLocationDetails.getDisplayName());
		
		locDetails = locationService.findLocationDetails(locId);
		verifyLocationDetails(locDetails, orgId, locId, Status.ACTIVE, originalLocationDetails.getReference(), originalLocationDetails.getDisplayName(), originalLocationDetails.getAddress());
		
		/* update and verify the location name */
		String newName = originalLocationDetails.getDisplayName() + "XXX";
		locDetails = locationService.updateLocationName(locId, newName);
		verifyLocationDetails(locDetails, orgId, locId, Status.ACTIVE, originalLocationDetails.getReference(), newName, originalLocationDetails.getAddress());
		
		locSummary = locationService.findLocationSummary(locId);
		verifyLocationSummary(locSummary, orgId, locId, Status.ACTIVE, originalLocationDetails.getReference(), newName);
		
		/* update and verify the location address */
		MailingAddress newAddress = new MailingAddress("55 second street", "Toronto", "ON", new Country("CA", "Canada", "Canada"), "T5R5X3");
		locDetails = locationService.updateLocationAddress(locId, newAddress);
		verifyLocationDetails(locDetails, orgId, locId, Status.ACTIVE, originalLocationDetails.getReference(), newName, newAddress);
		
		locSummary = locationService.findLocationSummary(locId);
		verifyLocationSummary(locSummary, orgId, locId, Status.ACTIVE, originalLocationDetails.getReference(), newName);
		
		/* validate details paging with 1 match on name filter */
		Page<LocationDetails> locDetailsPage = locationService.findLocationDetails(new LocationsFilter(orgDetails.getOrganizationId(), newName, Status.ACTIVE), new Paging(1, 5, Sort.by(Direction.ASC, "displayName")));
		Assert.assertEquals(1, locDetailsPage.getNumber());
		Assert.assertEquals(1, locDetailsPage.getTotalPages());
		Assert.assertEquals(1, locDetailsPage.getNumberOfElements());
		Assert.assertEquals(1, locDetailsPage.getTotalElements());
		Assert.assertEquals(1, locDetailsPage.getContent().size());
		Assert.assertEquals(locDetails, locDetailsPage.getContent().get(0));
		
		/* validate details paging with no match on name filter */
		locDetailsPage = locationService.findLocationDetails(new LocationsFilter(orgDetails.getOrganizationId(), newName + "00", Status.ACTIVE), new Paging(1, 5, Sort.by(Direction.ASC, "displayName")));
		Assert.assertEquals(1, locDetailsPage.getNumber());
		Assert.assertEquals(0, locDetailsPage.getTotalPages());
		Assert.assertEquals(0, locDetailsPage.getNumberOfElements());
		Assert.assertEquals(0, locDetailsPage.getTotalElements());
		Assert.assertEquals(0, locDetailsPage.getContent().size());
		
		/* validate summary paging with 1 match on name filter */
		Page<LocationSummary> locSummaryPage = locationService.findLocationSummaries(new LocationsFilter(orgDetails.getOrganizationId(), newName, Status.ACTIVE), new Paging(1, 5, Sort.by(Direction.ASC, "displayName")));
		Assert.assertEquals(1, locSummaryPage.getNumber());
		Assert.assertEquals(1, locSummaryPage.getTotalPages());
		Assert.assertEquals(1, locSummaryPage.getNumberOfElements());
		Assert.assertEquals(1, locSummaryPage.getTotalElements());
		Assert.assertEquals(1, locSummaryPage.getContent().size());
		Assert.assertEquals(locSummary, locSummaryPage.getContent().get(0));
		
		/* validate summary paging with no match on name filter */
		locSummaryPage = locationService.findLocationSummaries(new LocationsFilter(orgDetails.getOrganizationId(), newName + "00", Status.ACTIVE), new Paging(1, 5, Sort.by(Direction.ASC, "displayName")));
		Assert.assertEquals(1, locSummaryPage.getNumber());
		Assert.assertEquals(0, locSummaryPage.getTotalPages());
		Assert.assertEquals(0, locSummaryPage.getNumberOfElements());
		Assert.assertEquals(0, locSummaryPage.getTotalElements());
		Assert.assertEquals(0, locSummaryPage.getContent().size());
	}

	private void runPersonServiceTests(Identifier orgId) {		
		long personCount = personService.countPersons(new PersonsFilter());
		final PersonName originalName = new PersonName(new Salutation(1, "Mr", "Mr"), "Mike", "Peter", "Johns");
		final MailingAddress originalAddress = new MailingAddress("12 ninth street", "Ottawa", "ON", new Country("CA", "Canada", "Canada"), "K4J9O9");
		final Communication originalComms = new Communication("Engineer", new Language("EN", "English", "English"), "Mike.Johns@ABC.ca", new Telephone("6135554545", ""), "6135554545");
		final BusinessPosition originalPosition = new BusinessPosition(new BusinessSector(1, "One", "One"), new BusinessUnit(2, "two", "two"), new BusinessClassification(3, "three", "three"));
		
		/* create a person and verify results */
		PersonDetails personDetails = personService.createPerson(orgId, 
				originalName, 
				originalAddress,
				originalComms, 
				originalPosition);
		Assert.assertEquals(personCount + 1, personService.countPersons(new PersonsFilter()));
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.ACTIVE, originalName.getDisplayName(), originalName, originalAddress, originalComms, originalPosition, null);
		
		/* disable and verify the results */
		PersonSummary personSummary = personService.disablePerson(personDetails.getPersonId());
		verifyPersonSummary(personSummary, orgId, personDetails.getPersonId(), Status.INACTIVE, originalName.getDisplayName());
		
		personSummary = personService.findPersonSummary(personDetails.getPersonId());
		verifyPersonSummary(personSummary, orgId, personDetails.getPersonId(), Status.INACTIVE, originalName.getDisplayName());
		
		personDetails = personService.findPersonDetails(personDetails.getPersonId());
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.INACTIVE, originalName.getDisplayName(), originalName, originalAddress, originalComms, originalPosition, null);

		/* enable and verify the results */
		personSummary = personService.enablePerson(personDetails.getPersonId());
		verifyPersonSummary(personSummary, orgId, personDetails.getPersonId(), Status.ACTIVE, originalName.getDisplayName());
		
		personSummary = personService.findPersonSummary(personDetails.getPersonId());
		verifyPersonSummary(personSummary, orgId, personDetails.getPersonId(), Status.ACTIVE, originalName.getDisplayName());
		
		personDetails = personService.findPersonDetails(personDetails.getPersonId());
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.ACTIVE, originalName.getDisplayName(), originalName, originalAddress, originalComms, originalPosition, null);
		
		/* update person name and verify */
		final PersonName newName = new PersonName(new Salutation(2, "Mrs", "Mrs"), "Susan", "Pauline", "Anderson");
		personDetails = personService.updatePersonName(personDetails.getPersonId(), newName);
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName(), newName, originalAddress, originalComms, originalPosition, null);
		
		personSummary = personService.findPersonSummary(personDetails.getPersonId());
		verifyPersonSummary(personSummary, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName());
		
		personDetails = personService.findPersonDetails(personDetails.getPersonId());
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName(), newName, originalAddress, originalComms, originalPosition, null);
		
		/* update person address and verify */
		final MailingAddress newAddress = new MailingAddress("15 fourth street", "Ottawa", "ON", new Country("CA", "Canada", "Canada"), "K4J9O9");
		personDetails = personService.updatePersonAddress(personDetails.getPersonId(), newAddress);
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName(), newName, newAddress, originalComms, originalPosition, null);
		
		personSummary = personService.findPersonSummary(personDetails.getPersonId());
		verifyPersonSummary(personSummary, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName());
		
		personDetails = personService.findPersonDetails(personDetails.getPersonId());
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName(), newName, newAddress, originalComms, originalPosition, null);
		
		/* update person communications and verify */
		final Communication newComms = new Communication("Supervisor", new Language("EN", "English", "English"), "Susan.Anderson@ABC.ca", new Telephone("6135554543", ""), "6135554543");
		personDetails = personService.updatePersonCommunication(personDetails.getPersonId(), newComms);
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName(), newName, newAddress, newComms, originalPosition, null);
		
		personSummary = personService.findPersonSummary(personDetails.getPersonId());
		verifyPersonSummary(personSummary, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName());
		
		personDetails = personService.findPersonDetails(personDetails.getPersonId());
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName(), newName, newAddress, newComms, originalPosition, null);
		
		/* update person position and verify */
		final BusinessPosition newPosition = new BusinessPosition(new BusinessSector(4, "Four", "Four"), new BusinessUnit(5, "five", "five"), new BusinessClassification(6, "six", "six"));
		personDetails = personService.updatePersonBusinessPosition(personDetails.getPersonId(), newPosition);
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName(), newName, newAddress, newComms, newPosition, null);
		
		personSummary = personService.findPersonSummary(personDetails.getPersonId());
		verifyPersonSummary(personSummary, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName());
		
		personDetails = personService.findPersonDetails(personDetails.getPersonId());
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName(), newName, newAddress, newComms, newPosition, null);
		
		/* add user role (first time we add a role it will generate a user name for us) */
		Role r1 = new Role("ADMIN", "Admin", "Admin");
		personDetails = personService.addUserRole(personDetails.getPersonId(), r1);		
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName(), newName, newAddress, newComms, newPosition, new User("SXA1", Arrays.asList(r1)));
		
		final User originalUser = personDetails.getUser();
		
		personSummary = personService.findPersonSummary(personDetails.getPersonId());
		verifyPersonSummary(personSummary, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName());
		
		personDetails = personService.findPersonDetails(personDetails.getPersonId());
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName(), newName, newAddress, newComms, newPosition, originalUser);
		
		/* add another role and verify */
		Role r2 = new Role("SYSADMIN", "SysAdmin", "SysAdmin");
		personDetails = personService.addUserRole(personDetails.getPersonId(), r2);		
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName(), newName, newAddress, newComms, newPosition, new User("SXA1", Arrays.asList(r1, r2)));
		
		User updateUser = personDetails.getUser();
		
		personSummary = personService.findPersonSummary(personDetails.getPersonId());
		verifyPersonSummary(personSummary, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName());
		
		personDetails = personService.findPersonDetails(personDetails.getPersonId());
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName(), newName, newAddress, newComms, newPosition, updateUser);
		
		/* remove a role and verify */
		personDetails = personService.removeUserRole(personDetails.getPersonId(), r1);		
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName(), newName, newAddress, newComms, newPosition, new User("SXA1", Arrays.asList(r2)));
		
		updateUser = personDetails.getUser();
		
		personSummary = personService.findPersonSummary(personDetails.getPersonId());
		verifyPersonSummary(personSummary, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName());
		
		personDetails = personService.findPersonDetails(personDetails.getPersonId());
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName(), newName, newAddress, newComms, newPosition, updateUser);
		
		/* set roles and verify */
		personDetails = personService.setUserRoles(personDetails.getPersonId(), Arrays.asList(r1, r2));		
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName(), newName, newAddress, newComms, newPosition, new User("SXA1", Arrays.asList(r1, r2)));
		
		updateUser = personDetails.getUser();
		
		personSummary = personService.findPersonSummary(personDetails.getPersonId());
		verifyPersonSummary(personSummary, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName());
		
		personDetails = personService.findPersonDetails(personDetails.getPersonId());
		verifyPersonDetails(personDetails, orgId, personDetails.getPersonId(), Status.ACTIVE, newName.getDisplayName(), newName, newAddress, newComms, newPosition, updateUser);
		
		/* validate details paging with 1 match on name filter */
		Page<PersonDetails> personDetailsPage = personService.findPersonDetails(new PersonsFilter(orgId, newName.getDisplayName(), Status.ACTIVE), new Paging(1, 5, Sort.by(Direction.ASC, "displayName")));
		Assert.assertEquals(1, personDetailsPage.getNumber());
		Assert.assertEquals(1, personDetailsPage.getTotalPages());
		Assert.assertEquals(1, personDetailsPage.getNumberOfElements());
		Assert.assertEquals(1, personDetailsPage.getTotalElements());
		Assert.assertEquals(1, personDetailsPage.getContent().size());
		Assert.assertEquals(personDetails, personDetailsPage.getContent().get(0));
		
		/* validate details paging with no match on name filter */
		personDetailsPage = personService.findPersonDetails(new PersonsFilter(orgId, newName.getDisplayName() + "00", Status.ACTIVE), new Paging(1, 5, Sort.by(Direction.ASC, "displayName")));
		Assert.assertEquals(1, personDetailsPage.getNumber());
		Assert.assertEquals(0, personDetailsPage.getTotalPages());
		Assert.assertEquals(0, personDetailsPage.getNumberOfElements());
		Assert.assertEquals(0, personDetailsPage.getTotalElements());
		Assert.assertEquals(0, personDetailsPage.getContent().size());
		
		/* validate summary paging with 1 match on name filter */
		Page<PersonSummary> personSummaryPage = personService.findPersonSummaries(new PersonsFilter(orgId, newName.getDisplayName(), Status.ACTIVE), new Paging(1, 5, Sort.by(Direction.ASC, "displayName")));
		Assert.assertEquals(1, personSummaryPage.getNumber());
		Assert.assertEquals(1, personSummaryPage.getTotalPages());
		Assert.assertEquals(1, personSummaryPage.getNumberOfElements());
		Assert.assertEquals(1, personSummaryPage.getTotalElements());
		Assert.assertEquals(1, personSummaryPage.getContent().size());
		Assert.assertEquals(personSummary, personSummaryPage.getContent().get(0));
		
		/* validate summary paging with no match on name filter */
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
	}
	
	private void verifyOrgSummary(OrganizationSummary orgSummary, Identifier orgId, Status status, String displayName) {
		Assert.assertNotNull(orgSummary.getOrganizationId());
		Assert.assertEquals(orgId, orgSummary.getOrganizationId());
		Assert.assertEquals(status, orgSummary.getStatus());
		Assert.assertEquals(displayName, orgSummary.getDisplayName());	
	}
	
	private void verifyLocationDetails(LocationDetails locDetails, Identifier orgId, Identifier locId, Status status, String reference, String displayName, MailingAddress address) {
		Assert.assertNotNull(locDetails.getLocationId());		
		Assert.assertEquals(orgId, locDetails.getOrganizationId());
		Assert.assertEquals(locId, locDetails.getLocationId());
		Assert.assertEquals(reference, locDetails.getReference());
		Assert.assertEquals(displayName, locDetails.getDisplayName());
		Assert.assertEquals(address, locDetails.getAddress());
		Assert.assertEquals(status, locDetails.getStatus());
	}
	
	private void verifyLocationSummary(LocationSummary locSummary, Identifier orgId, Identifier locId, Status status, String reference, String displayName) {
		Assert.assertNotNull(locSummary.getLocationId());		
		Assert.assertEquals(orgId, locSummary.getOrganizationId());
		Assert.assertEquals(locId, locSummary.getLocationId());
		Assert.assertEquals(reference, locSummary.getReference());
		Assert.assertEquals(displayName, locSummary.getDisplayName());
		Assert.assertEquals(status, locSummary.getStatus());
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
	}
	
	private void verifyPersonSummary(PersonSummary personSummary, Identifier orgId, Identifier personId, Status status, String displayName) {
		Assert.assertNotNull(personSummary.getPersonId());
		Assert.assertEquals(orgId, personSummary.getOrganizationId());
		Assert.assertEquals(personId, personSummary.getPersonId());
		Assert.assertEquals(status, personSummary.getStatus());
		Assert.assertEquals(displayName, personSummary.getDisplayName());		
	}
}
