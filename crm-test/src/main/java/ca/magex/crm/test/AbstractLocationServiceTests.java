package ca.magex.crm.test;

import static org.junit.Assert.fail;

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
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.CrmConfigurationService;
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
	 * Authentication service used to allow an authenticated test
	 * @return
	 */
	protected abstract CrmAuthenticationService auth();
	
	/**
	 * The CRM Services to be tested
	 * @return
	 */
	protected abstract CrmServices crmServices();
	
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
		OrganizationIdentifier mlbId = crmServices().createOrganization("MLB", List.of(new AuthenticationGroupIdentifier("ORG")), List.of(new BusinessGroupIdentifier("ORG"))).getOrganizationId();
		
		/* create */
		MailingAddress newyork = new MailingAddress("1 E 161 St", "The Bronx", CrmAsserts.NEW_YORK, CrmAsserts.UNITED_STATES, "10451");
		LocationDetails l1 = crmServices().createLocation(mlbId, "NYY", "New York", newyork);
		Assert.assertEquals("New York", l1.getDisplayName());
		Assert.assertEquals("NYY", l1.getReference());
		Assert.assertEquals(Status.ACTIVE, l1.getStatus());
		Assert.assertEquals(newyork, l1.getAddress());
		Assert.assertEquals(l1, crmServices().findLocationDetails(l1.getLocationId()));

		MailingAddress boston = new MailingAddress("4 Jersey St", "Boston", CrmAsserts.MASSACHUSETTS, CrmAsserts.UNITED_STATES, "02215");
		LocationDetails l2 = crmServices().createLocation(mlbId, "BOS", "Boston", boston);
		Assert.assertEquals("Boston", l2.getDisplayName());
		Assert.assertEquals("BOS", l2.getReference());
		Assert.assertEquals(Status.ACTIVE, l2.getStatus());
		Assert.assertEquals(boston, l2.getAddress());
		Assert.assertEquals(l2, crmServices().findLocationDetails(l2.getLocationId()));

		MailingAddress chicago = new MailingAddress("1060 W Addison St", "Chicago", CrmAsserts.ILLINOIS, CrmAsserts.UNITED_STATES, "60613");
		LocationDetails l3 = crmServices().createLocation(mlbId, "CHC", "Chicago", chicago);
		Assert.assertEquals("Chicago", l3.getDisplayName());
		Assert.assertEquals("CHC", l3.getReference());
		Assert.assertEquals(Status.ACTIVE, l3.getStatus());
		Assert.assertEquals(chicago, l3.getAddress());
		Assert.assertEquals(l3, crmServices().findLocationDetails(l3.getLocationId()));

		/* update */
		l1 = crmServices().updateLocationName(l1.getLocationId(), "Yankee Stadium");
		Assert.assertEquals("Yankee Stadium", l1.getDisplayName());
		Assert.assertEquals("NYY", l1.getReference());
		Assert.assertEquals(Status.ACTIVE, l1.getStatus());
		Assert.assertEquals(newyork, l1.getAddress());
		Assert.assertEquals(l1, crmServices().updateLocationName(l1.getLocationId(), "Yankee Stadium"));

		l2 = crmServices().updateLocationName(l2.getLocationId(), "Fenway Park");
		Assert.assertEquals("Fenway Park", l2.getDisplayName());
		Assert.assertEquals("BOS", l2.getReference());
		Assert.assertEquals(Status.ACTIVE, l2.getStatus());
		Assert.assertEquals(boston, l2.getAddress());
		Assert.assertEquals(l2, crmServices().updateLocationName(l2.getLocationId(), "Fenway Park"));

		l3 = crmServices().updateLocationName(l3.getLocationId(), "Wrigley Field");
		Assert.assertEquals("Wrigley Field", l3.getDisplayName());
		Assert.assertEquals("CHC", l3.getReference());
		Assert.assertEquals(Status.ACTIVE, l3.getStatus());
		Assert.assertEquals(chicago, l3.getAddress());
		Assert.assertEquals(l3, crmServices().updateLocationName(l3.getLocationId(), "Wrigley Field"));

		l1 = crmServices().updateLocationAddress(l1.getLocationId(), boston);
		Assert.assertEquals("Yankee Stadium", l1.getDisplayName());
		Assert.assertEquals("NYY", l1.getReference());
		Assert.assertEquals(Status.ACTIVE, l1.getStatus());
		Assert.assertEquals(boston, l1.getAddress());
		Assert.assertEquals(l1, crmServices().updateLocationAddress(l1.getLocationId(), boston));

		l2 = crmServices().updateLocationAddress(l2.getLocationId(), chicago);
		Assert.assertEquals("Fenway Park", l2.getDisplayName());
		Assert.assertEquals("BOS", l2.getReference());
		Assert.assertEquals(Status.ACTIVE, l2.getStatus());
		Assert.assertEquals(chicago, l2.getAddress());
		Assert.assertEquals(l2, crmServices().updateLocationAddress(l2.getLocationId(), chicago));

		l3 = crmServices().updateLocationAddress(l3.getLocationId(), newyork);
		Assert.assertEquals("Wrigley Field", l3.getDisplayName());
		Assert.assertEquals("CHC", l3.getReference());
		Assert.assertEquals(Status.ACTIVE, l3.getStatus());
		Assert.assertEquals(newyork, l3.getAddress());
		Assert.assertEquals(l3, crmServices().updateLocationAddress(l3.getLocationId(), newyork));

		/* disable */
		LocationSummary ls1 = crmServices().disableLocation(l1.getLocationId());
		Assert.assertEquals("Yankee Stadium", ls1.getDisplayName());
		Assert.assertEquals("NYY", ls1.getReference());
		Assert.assertEquals(Status.INACTIVE, ls1.getStatus());
		Assert.assertEquals(ls1, crmServices().disableLocation(l1.getLocationId()));
		Assert.assertEquals(ls1, crmServices().findLocationSummary(l1.getLocationId()));

		LocationSummary ls2 = crmServices().disableLocation(l2.getLocationId());
		Assert.assertEquals("Fenway Park", ls2.getDisplayName());
		Assert.assertEquals("BOS", ls2.getReference());
		Assert.assertEquals(Status.INACTIVE, ls2.getStatus());
		Assert.assertEquals(ls2, crmServices().disableLocation(l2.getLocationId()));
		Assert.assertEquals(ls2, crmServices().findLocationSummary(l2.getLocationId()));

		LocationSummary ls3 = crmServices().disableLocation(l3.getLocationId());
		Assert.assertEquals("Wrigley Field", ls3.getDisplayName());
		Assert.assertEquals("CHC", ls3.getReference());
		Assert.assertEquals(Status.INACTIVE, ls3.getStatus());
		Assert.assertEquals(ls3, crmServices().disableLocation(l3.getLocationId()));
		Assert.assertEquals(ls3, crmServices().findLocationSummary(l3.getLocationId()));
		
		/* enable */
		ls1 = crmServices().enableLocation(l1.getLocationId());
		Assert.assertEquals("Yankee Stadium", ls1.getDisplayName());
		Assert.assertEquals("NYY", ls1.getReference());
		Assert.assertEquals(Status.ACTIVE, ls1.getStatus());
		Assert.assertEquals(ls1, crmServices().enableLocation(l1.getLocationId()));
		Assert.assertEquals(ls1, crmServices().findLocationSummary(l1.getLocationId()));

		ls2 = crmServices().enableLocation(l2.getLocationId());
		Assert.assertEquals("Fenway Park", ls2.getDisplayName());
		Assert.assertEquals("BOS", ls2.getReference());
		Assert.assertEquals(Status.ACTIVE, ls2.getStatus());
		Assert.assertEquals(ls2, crmServices().enableLocation(l2.getLocationId()));
		Assert.assertEquals(ls2, crmServices().findLocationSummary(l2.getLocationId()));

		ls3 = crmServices().enableLocation(l3.getLocationId());
		Assert.assertEquals("Wrigley Field", ls3.getDisplayName());
		Assert.assertEquals("CHC", ls3.getReference());
		Assert.assertEquals(Status.ACTIVE, ls3.getStatus());
		Assert.assertEquals(ls3, crmServices().enableLocation(l3.getLocationId()));
		Assert.assertEquals(ls3, crmServices().findLocationSummary(l3.getLocationId()));

		/* count locations */
		Assert.assertEquals(4, crmServices().countLocations(new LocationsFilter(null, null, null, null)));
		Assert.assertEquals(3, crmServices().countLocations(new LocationsFilter(mlbId, null, null, null)));
		Assert.assertEquals(3, crmServices().countLocations(new LocationsFilter(mlbId, null, null, Status.ACTIVE)));
		Assert.assertEquals(1, crmServices().countLocations(new LocationsFilter(mlbId, "Yankee Stadium", null, Status.ACTIVE)));
		Assert.assertEquals(0, crmServices().countLocations(new LocationsFilter(mlbId, "Rogers Centre", null, Status.ACTIVE)));
		Assert.assertEquals(0, crmServices().countLocations(new LocationsFilter(mlbId, "Yankee Stadium", null, Status.INACTIVE)));
		Assert.assertEquals(0, crmServices().countLocations(new LocationsFilter(new OrganizationIdentifier("MLS"), "TD Place", null, Status.ACTIVE)));
		
		/* find locations details */
		Page<LocationDetails> detailsPage = crmServices().findLocationDetails(
				new LocationsFilter(null, null, null, null), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5, detailsPage.getSize());
		Assert.assertEquals(4, detailsPage.getNumberOfElements());		
		Assert.assertEquals(1, detailsPage.getTotalPages());
		Assert.assertEquals(4, detailsPage.getTotalElements());
		
		detailsPage = crmServices().findLocationDetails(
				new LocationsFilter(mlbId, null, null, null), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5, detailsPage.getSize());
		Assert.assertEquals(3, detailsPage.getNumberOfElements());		
		Assert.assertEquals(1, detailsPage.getTotalPages());
		Assert.assertEquals(3, detailsPage.getTotalElements());
		
		detailsPage = crmServices().findLocationDetails(
				new LocationsFilter(mlbId, null, null, Status.ACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5, detailsPage.getSize());
		Assert.assertEquals(3, detailsPage.getNumberOfElements());		
		Assert.assertEquals(1, detailsPage.getTotalPages());
		Assert.assertEquals(3, detailsPage.getTotalElements());
		
		detailsPage = crmServices().findLocationDetails(
				new LocationsFilter(mlbId, "Yankee Stadium", null, Status.ACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5, detailsPage.getSize());
		Assert.assertEquals(1, detailsPage.getNumberOfElements());		
		Assert.assertEquals(1, detailsPage.getTotalPages());
		Assert.assertEquals(1, detailsPage.getTotalElements());
		
		detailsPage = crmServices().findLocationDetails(
				new LocationsFilter(mlbId, "Rogers Centre", null, Status.ACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5, detailsPage.getSize());
		Assert.assertEquals(0, detailsPage.getNumberOfElements());		
		Assert.assertEquals(0, detailsPage.getTotalPages());
		Assert.assertEquals(0, detailsPage.getTotalElements());
		
		detailsPage = crmServices().findLocationDetails(
				new LocationsFilter(mlbId, "Yankee Stadium", null, Status.INACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5, detailsPage.getSize());
		Assert.assertEquals(0, detailsPage.getNumberOfElements());		
		Assert.assertEquals(0, detailsPage.getTotalPages());
		Assert.assertEquals(0, detailsPage.getTotalElements());
		
		detailsPage = crmServices().findLocationDetails(
				new LocationsFilter(new OrganizationIdentifier("MLS"), "TD Place", null, Status.ACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5, detailsPage.getSize());
		Assert.assertEquals(0, detailsPage.getNumberOfElements());		
		Assert.assertEquals(0, detailsPage.getTotalPages());
		Assert.assertEquals(0, detailsPage.getTotalElements());
		
		/* find locations summaries */
		Page<LocationSummary> summariesPage = crmServices().findLocationSummaries(
				new LocationsFilter(null, null, null, null), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(5,  summariesPage.getSize());
		Assert.assertEquals(4, summariesPage.getNumberOfElements());		
		Assert.assertEquals(1, summariesPage.getTotalPages());
		Assert.assertEquals(4, summariesPage.getTotalElements());
		
		summariesPage = crmServices().findLocationSummaries(
				new LocationsFilter(mlbId, null, null, null), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(5,  summariesPage.getSize());
		Assert.assertEquals(3, summariesPage.getNumberOfElements());		
		Assert.assertEquals(1, summariesPage.getTotalPages());
		Assert.assertEquals(3, summariesPage.getTotalElements());
		
		summariesPage = crmServices().findLocationSummaries(
				new LocationsFilter(mlbId, null, null, Status.ACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(5,  summariesPage.getSize());
		Assert.assertEquals(3, summariesPage.getNumberOfElements());		
		Assert.assertEquals(1, summariesPage.getTotalPages());
		Assert.assertEquals(3, summariesPage.getTotalElements());
		
		summariesPage = crmServices().findLocationSummaries(
				new LocationsFilter(mlbId, "Yankee Stadium", null, Status.ACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(5,  summariesPage.getSize());
		Assert.assertEquals(1, summariesPage.getNumberOfElements());		
		Assert.assertEquals(1, summariesPage.getTotalPages());
		Assert.assertEquals(1, summariesPage.getTotalElements());
		
		summariesPage = crmServices().findLocationSummaries(
				new LocationsFilter(mlbId, "Rogers Centre", null, Status.ACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(5,  summariesPage.getSize());
		Assert.assertEquals(0, summariesPage.getNumberOfElements());		
		Assert.assertEquals(0, summariesPage.getTotalPages());
		Assert.assertEquals(0, summariesPage.getTotalElements());
		
		summariesPage = crmServices().findLocationSummaries(
				new LocationsFilter(mlbId, "Yankee Stadium", null, Status.INACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(5,  summariesPage.getSize());
		Assert.assertEquals(0, summariesPage.getNumberOfElements());		
		Assert.assertEquals(0, summariesPage.getTotalPages());
		Assert.assertEquals(0, summariesPage.getTotalElements());
		
		summariesPage = crmServices().findLocationSummaries(
				new LocationsFilter(new OrganizationIdentifier("MLS"), "TD Place", null, Status.ACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(5,  summariesPage.getSize());
		Assert.assertEquals(0, summariesPage.getNumberOfElements());		
		Assert.assertEquals(0, summariesPage.getTotalPages());
		Assert.assertEquals(0, summariesPage.getTotalElements());
	}

	@Test
	public void testInvalidLocId() {
		try {
			crmServices().findLocationDetails(new LocationIdentifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Location ID '/locations/abc'", e.getMessage());
		}

		try {
			crmServices().findLocationSummary(new LocationIdentifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Location ID '/locations/abc'", e.getMessage());
		}

		try {
			crmServices().updateLocationName(new LocationIdentifier("abc"), "name");
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Location ID '/locations/abc'", e.getMessage());
		}
		
		try {
			crmServices().updateLocationAddress(new LocationIdentifier("abc"), CrmAsserts.MAILING_ADDRESS);
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Location ID '/locations/abc'", e.getMessage());
		}
		
		try {
			crmServices().disableLocation(new LocationIdentifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Location ID '/locations/abc'", e.getMessage());
		}
		
		try {
			crmServices().enableLocation(new LocationIdentifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Location ID '/locations/abc'", e.getMessage());
		}
	}
	
	@Test
	public void testWrongIdentifiers() throws Exception {
		try {
			crmServices().findLocationDetails(new LocationIdentifier("ABC"));
			fail("Not a valid identifier");
		} catch (ItemNotFoundException e) { }
	}

}
