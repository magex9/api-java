package ca.magex.crm.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import ca.magex.crm.api.authentication.CrmAuthenticationService;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.exceptions.PermissionDeniedException;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.CrmConfigurationService;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.AuthenticationGroupIdentifier;
import ca.magex.crm.api.system.id.BusinessGroupIdentifier;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;

@Transactional
public abstract class AbstractLocationServiceTests {

	/**
	 * Configuration Service used to setup the system for testing
	 * @return
	 */
	protected abstract CrmConfigurationService config();
	
	/**
	 * Configuration Service used to setup the system for testing
	 * @return
	 */
	protected abstract CrmServices crm();
	
	/**
	 * Authentication service used to allow an authenticated test
	 * @return
	 */
	protected abstract CrmAuthenticationService auth();
	
	/**
	 * The CRM Services to be tested
	 * @return
	 */
	protected abstract CrmLocationService locations();
	
	@Before
	public void setup() {
		config().reset();
		config().initializeSystem(CrmAsserts.SYSTEM_ORG, CrmAsserts.SYSTEM_PERSON, CrmAsserts.SYSTEM_EMAIL, "admin", "admin");
		auth().login("admin", "admin");
	}
	
	@After
	public void cleanup() {
		auth().logout();
	}
	
	@Test
	public void testLocations() {		
		OrganizationIdentifier mlbId = crm().createOrganization("MLB", List.of(new AuthenticationGroupIdentifier("ORG")), List.of(new BusinessGroupIdentifier("ORG"))).getOrganizationId();
		
		/* create */
		MailingAddress newyork = new MailingAddress("1 E 161 St", "The Bronx", CrmAsserts.NEW_YORK, CrmAsserts.UNITED_STATES, "10451");
		LocationDetails l1 = locations().createLocation(mlbId, "NYY", "New York", newyork);
		Assert.assertEquals("New York", l1.getDisplayName());
		Assert.assertEquals("NYY", l1.getReference());
		Assert.assertEquals(Status.ACTIVE, l1.getStatus());
		Assert.assertEquals(newyork, l1.getAddress());
		Assert.assertEquals(l1, locations().findLocationDetails(l1.getLocationId()));

		MailingAddress boston = new MailingAddress("4 Jersey St", "Boston", CrmAsserts.MASSACHUSETTS, CrmAsserts.UNITED_STATES, "02215");
		LocationDetails l2 = locations().createLocation(mlbId, "BOS", "Boston", boston);
		Assert.assertEquals("Boston", l2.getDisplayName());
		Assert.assertEquals("BOS", l2.getReference());
		Assert.assertEquals(Status.ACTIVE, l2.getStatus());
		Assert.assertEquals(boston, l2.getAddress());
		Assert.assertEquals(l2, locations().findLocationDetails(l2.getLocationId()));

		MailingAddress chicago = new MailingAddress("1060 W Addison St", "Chicago", CrmAsserts.ILLINOIS, CrmAsserts.UNITED_STATES, "60613");
		LocationDetails l3 = locations().createLocation(mlbId, "CHC", "Chicago", chicago);
		Assert.assertEquals("Chicago", l3.getDisplayName());
		Assert.assertEquals("CHC", l3.getReference());
		Assert.assertEquals(Status.ACTIVE, l3.getStatus());
		Assert.assertEquals(chicago, l3.getAddress());
		Assert.assertEquals(l3, locations().findLocationDetails(l3.getLocationId()));

		/* update */
		l1 = locations().updateLocationName(l1.getLocationId(), "Yankee Stadium");
		Assert.assertEquals("Yankee Stadium", l1.getDisplayName());
		Assert.assertEquals("NYY", l1.getReference());
		Assert.assertEquals(Status.ACTIVE, l1.getStatus());
		Assert.assertEquals(newyork, l1.getAddress());
		Assert.assertEquals(l1, locations().updateLocationName(l1.getLocationId(), "Yankee Stadium"));

		l2 = locations().updateLocationName(l2.getLocationId(), "Fenway Park");
		Assert.assertEquals("Fenway Park", l2.getDisplayName());
		Assert.assertEquals("BOS", l2.getReference());
		Assert.assertEquals(Status.ACTIVE, l2.getStatus());
		Assert.assertEquals(boston, l2.getAddress());
		Assert.assertEquals(l2, locations().updateLocationName(l2.getLocationId(), "Fenway Park"));

		l3 = locations().updateLocationName(l3.getLocationId(), "Wrigley Field");
		Assert.assertEquals("Wrigley Field", l3.getDisplayName());
		Assert.assertEquals("CHC", l3.getReference());
		Assert.assertEquals(Status.ACTIVE, l3.getStatus());
		Assert.assertEquals(chicago, l3.getAddress());
		Assert.assertEquals(l3, locations().updateLocationName(l3.getLocationId(), "Wrigley Field"));

		l1 = locations().updateLocationAddress(l1.getLocationId(), boston);
		Assert.assertEquals("Yankee Stadium", l1.getDisplayName());
		Assert.assertEquals("NYY", l1.getReference());
		Assert.assertEquals(Status.ACTIVE, l1.getStatus());
		Assert.assertEquals(boston, l1.getAddress());
		Assert.assertEquals(l1, locations().updateLocationAddress(l1.getLocationId(), boston));

		l2 = locations().updateLocationAddress(l2.getLocationId(), chicago);
		Assert.assertEquals("Fenway Park", l2.getDisplayName());
		Assert.assertEquals("BOS", l2.getReference());
		Assert.assertEquals(Status.ACTIVE, l2.getStatus());
		Assert.assertEquals(chicago, l2.getAddress());
		Assert.assertEquals(l2, locations().updateLocationAddress(l2.getLocationId(), chicago));

		l3 = locations().updateLocationAddress(l3.getLocationId(), newyork);
		Assert.assertEquals("Wrigley Field", l3.getDisplayName());
		Assert.assertEquals("CHC", l3.getReference());
		Assert.assertEquals(Status.ACTIVE, l3.getStatus());
		Assert.assertEquals(newyork, l3.getAddress());
		Assert.assertEquals(l3, locations().updateLocationAddress(l3.getLocationId(), newyork));

		/* disable */
		LocationSummary ls1 = locations().disableLocation(l1.getLocationId());
		Assert.assertEquals("Yankee Stadium", ls1.getDisplayName());
		Assert.assertEquals("NYY", ls1.getReference());
		Assert.assertEquals(Status.INACTIVE, ls1.getStatus());
		try {
			Assert.assertEquals(ls1, locations().disableLocation(l1.getLocationId()));
			Assert.fail("Already disabled");
		} catch (PermissionDeniedException e) { }
		Assert.assertEquals(ls1, locations().findLocationSummary(l1.getLocationId()));

		LocationSummary ls2 = locations().disableLocation(l2.getLocationId());
		Assert.assertEquals("Fenway Park", ls2.getDisplayName());
		Assert.assertEquals("BOS", ls2.getReference());
		Assert.assertEquals(Status.INACTIVE, ls2.getStatus());
		try {
			Assert.assertEquals(ls2, locations().disableLocation(l2.getLocationId()));
			Assert.fail("Already enabled");
		} catch (PermissionDeniedException e) { }
		Assert.assertEquals(ls2, locations().findLocationSummary(l2.getLocationId()));

		LocationSummary ls3 = locations().disableLocation(l3.getLocationId());
		Assert.assertEquals("Wrigley Field", ls3.getDisplayName());
		Assert.assertEquals("CHC", ls3.getReference());
		Assert.assertEquals(Status.INACTIVE, ls3.getStatus());
		try {
			Assert.assertEquals(ls3, locations().disableLocation(l3.getLocationId()));
			Assert.fail("Already disabled");
		} catch (PermissionDeniedException e) { }
		Assert.assertEquals(ls3, locations().findLocationSummary(l3.getLocationId()));
		
		/* enable */
		ls1 = locations().enableLocation(l1.getLocationId());
		Assert.assertEquals("Yankee Stadium", ls1.getDisplayName());
		Assert.assertEquals("NYY", ls1.getReference());
		Assert.assertEquals(Status.ACTIVE, ls1.getStatus());
		try {
			Assert.assertEquals(ls1, locations().enableLocation(l1.getLocationId()));
			Assert.fail("Already enabled");
		} catch (PermissionDeniedException e) { }
		Assert.assertEquals(ls1, locations().findLocationSummary(l1.getLocationId()));

		ls2 = locations().enableLocation(l2.getLocationId());
		Assert.assertEquals("Fenway Park", ls2.getDisplayName());
		Assert.assertEquals("BOS", ls2.getReference());
		Assert.assertEquals(Status.ACTIVE, ls2.getStatus());
		try {
			Assert.assertEquals(ls2, locations().enableLocation(l2.getLocationId()));
			Assert.fail("Already enabled");
		} catch (PermissionDeniedException e) { }
		Assert.assertEquals(ls2, locations().findLocationSummary(l2.getLocationId()));

		ls3 = locations().enableLocation(l3.getLocationId());
		Assert.assertEquals("Wrigley Field", ls3.getDisplayName());
		Assert.assertEquals("CHC", ls3.getReference());
		Assert.assertEquals(Status.ACTIVE, ls3.getStatus());
		try {
			Assert.assertEquals(ls3, locations().enableLocation(l3.getLocationId()));
			Assert.fail("Already enabled");
		} catch (PermissionDeniedException e) { }
		Assert.assertEquals(ls3, locations().findLocationSummary(l3.getLocationId()));

		/* count locations */
		Assert.assertEquals(4, locations().countLocations(new LocationsFilter(null, null, null, null)));
		Assert.assertEquals(3, locations().countLocations(new LocationsFilter(mlbId, null, null, null)));
		Assert.assertEquals(3, locations().countLocations(new LocationsFilter(mlbId, null, null, Status.ACTIVE)));
		Assert.assertEquals(1, locations().countLocations(new LocationsFilter(mlbId, "Yankee Stadium", null, Status.ACTIVE)));
		Assert.assertEquals(0, locations().countLocations(new LocationsFilter(mlbId, "Rogers Centre", null, Status.ACTIVE)));
		Assert.assertEquals(0, locations().countLocations(new LocationsFilter(mlbId, "Yankee Stadium", null, Status.INACTIVE)));
		Assert.assertEquals(0, locations().countLocations(new LocationsFilter(new OrganizationIdentifier("MLS"), "TD Place", null, Status.ACTIVE)));
		
		/* find locations details */
		Page<LocationDetails> detailsPage = locations().findLocationDetails(
				new LocationsFilter(null, null, null, null), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5, detailsPage.getSize());
		Assert.assertEquals(4, detailsPage.getNumberOfElements());		
		Assert.assertEquals(1, detailsPage.getTotalPages());
		Assert.assertEquals(4, detailsPage.getTotalElements());
		
		detailsPage = locations().findLocationDetails(
				new LocationsFilter(mlbId, null, null, null), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5, detailsPage.getSize());
		Assert.assertEquals(3, detailsPage.getNumberOfElements());		
		Assert.assertEquals(1, detailsPage.getTotalPages());
		Assert.assertEquals(3, detailsPage.getTotalElements());
		
		detailsPage = locations().findLocationDetails(
				new LocationsFilter(mlbId, null, null, Status.ACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5, detailsPage.getSize());
		Assert.assertEquals(3, detailsPage.getNumberOfElements());		
		Assert.assertEquals(1, detailsPage.getTotalPages());
		Assert.assertEquals(3, detailsPage.getTotalElements());
		
		detailsPage = locations().findLocationDetails(
				new LocationsFilter(mlbId, "Yankee Stadium", null, Status.ACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5, detailsPage.getSize());
		Assert.assertEquals(1, detailsPage.getNumberOfElements());		
		Assert.assertEquals(1, detailsPage.getTotalPages());
		Assert.assertEquals(1, detailsPage.getTotalElements());
		
		detailsPage = locations().findLocationDetails(
				new LocationsFilter(mlbId, "Rogers Centre", null, Status.ACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5, detailsPage.getSize());
		Assert.assertEquals(0, detailsPage.getNumberOfElements());		
		Assert.assertEquals(0, detailsPage.getTotalPages());
		Assert.assertEquals(0, detailsPage.getTotalElements());
		
		detailsPage = locations().findLocationDetails(
				new LocationsFilter(mlbId, "Yankee Stadium", null, Status.INACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5, detailsPage.getSize());
		Assert.assertEquals(0, detailsPage.getNumberOfElements());		
		Assert.assertEquals(0, detailsPage.getTotalPages());
		Assert.assertEquals(0, detailsPage.getTotalElements());
		
		detailsPage = locations().findLocationDetails(
				new LocationsFilter(new OrganizationIdentifier("MLS"), "TD Place", null, Status.ACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5, detailsPage.getSize());
		Assert.assertEquals(0, detailsPage.getNumberOfElements());		
		Assert.assertEquals(0, detailsPage.getTotalPages());
		Assert.assertEquals(0, detailsPage.getTotalElements());
		
		/* find locations summaries */
		Page<LocationSummary> summariesPage = locations().findLocationSummaries(
				new LocationsFilter(null, null, null, null), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(5,  summariesPage.getSize());
		Assert.assertEquals(4, summariesPage.getNumberOfElements());		
		Assert.assertEquals(1, summariesPage.getTotalPages());
		Assert.assertEquals(4, summariesPage.getTotalElements());
		
		summariesPage = locations().findLocationSummaries(
				new LocationsFilter(mlbId, null, null, null), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(5,  summariesPage.getSize());
		Assert.assertEquals(3, summariesPage.getNumberOfElements());		
		Assert.assertEquals(1, summariesPage.getTotalPages());
		Assert.assertEquals(3, summariesPage.getTotalElements());
		
		summariesPage = locations().findLocationSummaries(
				new LocationsFilter(mlbId, null, null, Status.ACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(5,  summariesPage.getSize());
		Assert.assertEquals(3, summariesPage.getNumberOfElements());		
		Assert.assertEquals(1, summariesPage.getTotalPages());
		Assert.assertEquals(3, summariesPage.getTotalElements());
		
		summariesPage = locations().findLocationSummaries(
				new LocationsFilter(mlbId, "Yankee Stadium", null, Status.ACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(5,  summariesPage.getSize());
		Assert.assertEquals(1, summariesPage.getNumberOfElements());		
		Assert.assertEquals(1, summariesPage.getTotalPages());
		Assert.assertEquals(1, summariesPage.getTotalElements());
		
		summariesPage = locations().findLocationSummaries(
				new LocationsFilter(mlbId, "Rogers Centre", null, Status.ACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(5,  summariesPage.getSize());
		Assert.assertEquals(0, summariesPage.getNumberOfElements());		
		Assert.assertEquals(0, summariesPage.getTotalPages());
		Assert.assertEquals(0, summariesPage.getTotalElements());
		
		summariesPage = locations().findLocationSummaries(
				new LocationsFilter(mlbId, "Yankee Stadium", null, Status.INACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(5,  summariesPage.getSize());
		Assert.assertEquals(0, summariesPage.getNumberOfElements());		
		Assert.assertEquals(0, summariesPage.getTotalPages());
		Assert.assertEquals(0, summariesPage.getTotalElements());
		
		summariesPage = locations().findLocationSummaries(
				new LocationsFilter(new OrganizationIdentifier("MLS"), "TD Place", null, Status.ACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(5,  summariesPage.getSize());
		Assert.assertEquals(0, summariesPage.getNumberOfElements());		
		Assert.assertEquals(0, summariesPage.getTotalPages());
		Assert.assertEquals(0, summariesPage.getTotalElements());
	}
	
	@Test
	public void testCannotDisabledMainLocation() throws Exception {
		OrganizationIdentifier organizationId = crm().createOrganization("ORG", List.of(AuthenticationGroupIdentifier.ORG), List.of(BusinessGroupIdentifier.EXTERNAL)).getOrganizationId();
		LocationIdentifier locationId = locations().createLocation(organizationId, "MAIN", "Main Location", CrmAsserts.US_ADDRESS).getLocationId();
		Assert.assertEquals(Status.ACTIVE, locations().findLocationSummary(locationId).getStatus());

		// Disable and enable a new location
		locations().disableLocation(locationId).getStatus();
		Assert.assertEquals(Status.INACTIVE, locations().findLocationSummary(locationId).getStatus());
		locations().enableLocation(locationId).getStatus();
		Assert.assertEquals(Status.ACTIVE, locations().findLocationSummary(locationId).getStatus());
		
		// Make the location a main location which cannot be disabled.
		crm().updateOrganizationMainLocation(organizationId, locationId);
		try {
			locations().disableLocation(locationId).getStatus();
			Assert.fail("Cannot disable main location");
		} catch (PermissionDeniedException e) { }
		Assert.assertEquals(Status.ACTIVE, locations().findLocationSummary(locationId).getStatus());
		try {
			locations().enableLocation(locationId).getStatus();
			Assert.fail("Location already active");
		} catch (PermissionDeniedException e) { }
		Assert.assertEquals(Status.ACTIVE, locations().findLocationSummary(locationId).getStatus());
		
		// Set the main location back to null and try again
		crm().updateOrganizationMainLocation(organizationId, null);
		locations().disableLocation(locationId).getStatus();
		Assert.assertEquals(Status.INACTIVE, locations().findLocationSummary(locationId).getStatus());
		locations().enableLocation(locationId).getStatus();
		Assert.assertEquals(Status.ACTIVE, locations().findLocationSummary(locationId).getStatus());
	}

	@Test
	public void testInvalidLocId() {
		try {
			locations().findLocationDetails(new LocationIdentifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Location ID '/locations/abc'", e.getMessage());
		}

		try {
			locations().findLocationSummary(new LocationIdentifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Location ID '/locations/abc'", e.getMessage());
		}

		try {
			locations().updateLocationName(new LocationIdentifier("abc"), "name");
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Location ID '/locations/abc'", e.getMessage());
		}
		
		try {
			locations().updateLocationAddress(new LocationIdentifier("abc"), CrmAsserts.MAILING_ADDRESS);
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Location ID '/locations/abc'", e.getMessage());
		}
		
		try {
			locations().disableLocation(new LocationIdentifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Location ID '/locations/abc'", e.getMessage());
		}
		
		try {
			locations().enableLocation(new LocationIdentifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Location ID '/locations/abc'", e.getMessage());
		}
	}
	
	@Test
	public void testWrongIdentifiers() throws Exception {
		try {
			locations().findLocationDetails(new LocationIdentifier("ABC"));
			fail("Not a valid identifier");
		} catch (ItemNotFoundException e) { }
	}

}
