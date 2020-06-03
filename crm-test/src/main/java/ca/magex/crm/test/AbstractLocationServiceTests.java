package ca.magex.crm.test;

import static ca.magex.crm.test.CrmAsserts.GROUP;
import static ca.magex.crm.test.CrmAsserts.ILLINOIS;
import static ca.magex.crm.test.CrmAsserts.MASSACHUSETTS;
import static ca.magex.crm.test.CrmAsserts.NEW_YORK;
import static ca.magex.crm.test.CrmAsserts.UNITED_STATES;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.Crm;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public abstract class AbstractLocationServiceTests {

	protected Crm crm;
	
	protected AbstractLocationServiceTests() {}
	
	public AbstractLocationServiceTests(Crm crm) {
		this.crm = crm;
	}
	
	@Before
	public void setup() {
		crm.reset();
		crm.initializeSystem("Magex", CrmAsserts.PERSON_NAME, "admin@magex.ca", "admin", "admin");
	}
	
	@Test
	public void testLocations() {
		
		Identifier mlbId = crm.createOrganization("MLB", List.of("ORG")).getOrganizationId();
		
		/* create */
		MailingAddress newyork = new MailingAddress("1 E 161 St", "The Bronx", NEW_YORK.getCode(), UNITED_STATES.getCode(), "10451");
		LocationDetails l1 = crm.createLocation(mlbId, "New York", "NYY", newyork);
		Assert.assertEquals("New York", l1.getDisplayName());
		Assert.assertEquals("NYY", l1.getReference());
		Assert.assertEquals(Status.ACTIVE, l1.getStatus());
		Assert.assertEquals(newyork, l1.getAddress());
		Assert.assertEquals(l1, crm.findLocationDetails(l1.getLocationId()));

		MailingAddress boston = new MailingAddress("4 Jersey St", "Boston", MASSACHUSETTS.getCode(), UNITED_STATES.getCode(), "02215");
		LocationDetails l2 = crm.createLocation(mlbId, "Boston", "BOS", boston);
		Assert.assertEquals("Boston", l2.getDisplayName());
		Assert.assertEquals("BOS", l2.getReference());
		Assert.assertEquals(Status.ACTIVE, l2.getStatus());
		Assert.assertEquals(boston, l2.getAddress());
		Assert.assertEquals(l2, crm.findLocationDetails(l2.getLocationId()));

		MailingAddress chicago = new MailingAddress("1060 W Addison St", "Chicago", ILLINOIS.getCode(), UNITED_STATES.getCode(), "60613");
		LocationDetails l3 = crm.createLocation(mlbId, "Chicago", "CHC", chicago);
		Assert.assertEquals("Chicago", l3.getDisplayName());
		Assert.assertEquals("CHC", l3.getReference());
		Assert.assertEquals(Status.ACTIVE, l3.getStatus());
		Assert.assertEquals(chicago, l3.getAddress());
		Assert.assertEquals(l3, crm.findLocationDetails(l3.getLocationId()));

		/* update */
		l1 = crm.updateLocationName(l1.getLocationId(), "Yankee Stadium");
		Assert.assertEquals("Yankee Stadium", l1.getDisplayName());
		Assert.assertEquals("NYY", l1.getReference());
		Assert.assertEquals(Status.ACTIVE, l1.getStatus());
		Assert.assertEquals(newyork, l1.getAddress());
		Assert.assertEquals(l1, crm.updateLocationName(l1.getLocationId(), "Yankee Stadium"));

		l2 = crm.updateLocationName(l2.getLocationId(), "Fenway Park");
		Assert.assertEquals("Fenway Park", l2.getDisplayName());
		Assert.assertEquals("BOS", l2.getReference());
		Assert.assertEquals(Status.ACTIVE, l2.getStatus());
		Assert.assertEquals(boston, l2.getAddress());
		Assert.assertEquals(l2, crm.updateLocationName(l2.getLocationId(), "Fenway Park"));

		l3 = crm.updateLocationName(l3.getLocationId(), "Wrigley Field");
		Assert.assertEquals("Wrigley Field", l3.getDisplayName());
		Assert.assertEquals("CHC", l3.getReference());
		Assert.assertEquals(Status.ACTIVE, l3.getStatus());
		Assert.assertEquals(chicago, l3.getAddress());
		Assert.assertEquals(l3, crm.updateLocationName(l3.getLocationId(), "Wrigley Field"));

		l1 = crm.updateLocationAddress(l1.getLocationId(), boston);
		Assert.assertEquals("Yankee Stadium", l1.getDisplayName());
		Assert.assertEquals("NYY", l1.getReference());
		Assert.assertEquals(Status.ACTIVE, l1.getStatus());
		Assert.assertEquals(boston, l1.getAddress());
		Assert.assertEquals(l1, crm.updateLocationAddress(l1.getLocationId(), boston));

		l2 = crm.updateLocationAddress(l2.getLocationId(), chicago);
		Assert.assertEquals("Fenway Park", l2.getDisplayName());
		Assert.assertEquals("BOS", l2.getReference());
		Assert.assertEquals(Status.ACTIVE, l2.getStatus());
		Assert.assertEquals(chicago, l2.getAddress());
		Assert.assertEquals(l2, crm.updateLocationAddress(l2.getLocationId(), chicago));

		l3 = crm.updateLocationAddress(l3.getLocationId(), newyork);
		Assert.assertEquals("Wrigley Field", l3.getDisplayName());
		Assert.assertEquals("CHC", l3.getReference());
		Assert.assertEquals(Status.ACTIVE, l3.getStatus());
		Assert.assertEquals(newyork, l3.getAddress());
		Assert.assertEquals(l3, crm.updateLocationAddress(l3.getLocationId(), newyork));

		/* disable */
		LocationSummary ls1 = crm.disableLocation(l1.getLocationId());
		Assert.assertEquals("Yankee Stadium", ls1.getDisplayName());
		Assert.assertEquals("NYY", ls1.getReference());
		Assert.assertEquals(Status.INACTIVE, ls1.getStatus());
		Assert.assertEquals(ls1, crm.disableLocation(l1.getLocationId()));
		Assert.assertEquals(ls1, crm.findLocationSummary(l1.getLocationId()));

		LocationSummary ls2 = crm.disableLocation(l2.getLocationId());
		Assert.assertEquals("Fenway Park", ls2.getDisplayName());
		Assert.assertEquals("BOS", ls2.getReference());
		Assert.assertEquals(Status.INACTIVE, ls2.getStatus());
		Assert.assertEquals(ls2, crm.disableLocation(l2.getLocationId()));
		Assert.assertEquals(ls2, crm.findLocationSummary(l2.getLocationId()));

		LocationSummary ls3 = crm.disableLocation(l3.getLocationId());
		Assert.assertEquals("Wrigley Field", ls3.getDisplayName());
		Assert.assertEquals("CHC", ls3.getReference());
		Assert.assertEquals(Status.INACTIVE, ls3.getStatus());
		Assert.assertEquals(ls3, crm.disableLocation(l3.getLocationId()));
		Assert.assertEquals(ls3, crm.findLocationSummary(l3.getLocationId()));
		
		/* enable */
		ls1 = crm.enableLocation(l1.getLocationId());
		Assert.assertEquals("Yankee Stadium", ls1.getDisplayName());
		Assert.assertEquals("NYY", ls1.getReference());
		Assert.assertEquals(Status.ACTIVE, ls1.getStatus());
		Assert.assertEquals(ls1, crm.enableLocation(l1.getLocationId()));
		Assert.assertEquals(ls1, crm.findLocationSummary(l1.getLocationId()));

		ls2 = crm.enableLocation(l2.getLocationId());
		Assert.assertEquals("Fenway Park", ls2.getDisplayName());
		Assert.assertEquals("BOS", ls2.getReference());
		Assert.assertEquals(Status.ACTIVE, ls2.getStatus());
		Assert.assertEquals(ls2, crm.enableLocation(l2.getLocationId()));
		Assert.assertEquals(ls2, crm.findLocationSummary(l2.getLocationId()));

		ls3 = crm.enableLocation(l3.getLocationId());
		Assert.assertEquals("Wrigley Field", ls3.getDisplayName());
		Assert.assertEquals("CHC", ls3.getReference());
		Assert.assertEquals(Status.ACTIVE, ls3.getStatus());
		Assert.assertEquals(ls3, crm.enableLocation(l3.getLocationId()));
		Assert.assertEquals(ls3, crm.findLocationSummary(l3.getLocationId()));

		/* count locations */
		Assert.assertEquals(3, crm.countLocations(new LocationsFilter(null, null, null, null)));
		Assert.assertEquals(3, crm.countLocations(new LocationsFilter(mlbId, null, null, null)));
		Assert.assertEquals(3, crm.countLocations(new LocationsFilter(mlbId, null, null, Status.ACTIVE)));
		Assert.assertEquals(1, crm.countLocations(new LocationsFilter(mlbId, "Yankee Stadium", null, Status.ACTIVE)));
		Assert.assertEquals(0, crm.countLocations(new LocationsFilter(mlbId, "Rogers Centre", null, Status.ACTIVE)));
		Assert.assertEquals(0, crm.countLocations(new LocationsFilter(mlbId, "Yankee Stadium", null, Status.INACTIVE)));
		Assert.assertEquals(0, crm.countLocations(new LocationsFilter(new Identifier("MLS"), "TD Place", null, Status.ACTIVE)));
		
		/* find locations details */
		Page<LocationDetails> detailsPage = crm.findLocationDetails(
				new LocationsFilter(null, null, null, null), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5,  detailsPage.getSize());
		Assert.assertEquals(3, detailsPage.getNumberOfElements());		
		Assert.assertEquals(1, detailsPage.getTotalPages());
		Assert.assertEquals(3, detailsPage.getTotalElements());
		
		detailsPage = crm.findLocationDetails(
				new LocationsFilter(mlbId, null, null, null), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5,  detailsPage.getSize());
		Assert.assertEquals(3, detailsPage.getNumberOfElements());		
		Assert.assertEquals(1, detailsPage.getTotalPages());
		Assert.assertEquals(3, detailsPage.getTotalElements());
		
		detailsPage = crm.findLocationDetails(
				new LocationsFilter(mlbId, null, null, Status.ACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5,  detailsPage.getSize());
		Assert.assertEquals(3, detailsPage.getNumberOfElements());		
		Assert.assertEquals(1, detailsPage.getTotalPages());
		Assert.assertEquals(3, detailsPage.getTotalElements());
		
		detailsPage = crm.findLocationDetails(
				new LocationsFilter(mlbId, "Yankee Stadium", null, Status.ACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5,  detailsPage.getSize());
		Assert.assertEquals(1, detailsPage.getNumberOfElements());		
		Assert.assertEquals(1, detailsPage.getTotalPages());
		Assert.assertEquals(1, detailsPage.getTotalElements());
		
		detailsPage = crm.findLocationDetails(
				new LocationsFilter(mlbId, "Rogers Centre", null, Status.ACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5,  detailsPage.getSize());
		Assert.assertEquals(0, detailsPage.getNumberOfElements());		
		Assert.assertEquals(0, detailsPage.getTotalPages());
		Assert.assertEquals(0, detailsPage.getTotalElements());
		
		detailsPage = crm.findLocationDetails(
				new LocationsFilter(mlbId, "Yankee Stadium", null, Status.INACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5,  detailsPage.getSize());
		Assert.assertEquals(0, detailsPage.getNumberOfElements());		
		Assert.assertEquals(0, detailsPage.getTotalPages());
		Assert.assertEquals(0, detailsPage.getTotalElements());
		
		detailsPage = crm.findLocationDetails(
				new LocationsFilter(new Identifier("MLS"), "TD Place", null, Status.ACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5,  detailsPage.getSize());
		Assert.assertEquals(0, detailsPage.getNumberOfElements());		
		Assert.assertEquals(0, detailsPage.getTotalPages());
		Assert.assertEquals(0, detailsPage.getTotalElements());
		
		/* find locations summaries */
		Page<LocationSummary> summariesPage = crm.findLocationSummaries(
				new LocationsFilter(null, null, null, null), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(5,  summariesPage.getSize());
		Assert.assertEquals(3, summariesPage.getNumberOfElements());		
		Assert.assertEquals(1, summariesPage.getTotalPages());
		Assert.assertEquals(3, summariesPage.getTotalElements());
		
		summariesPage = crm.findLocationSummaries(
				new LocationsFilter(mlbId, null, null, null), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(5,  summariesPage.getSize());
		Assert.assertEquals(3, summariesPage.getNumberOfElements());		
		Assert.assertEquals(1, summariesPage.getTotalPages());
		Assert.assertEquals(3, summariesPage.getTotalElements());
		
		summariesPage = crm.findLocationSummaries(
				new LocationsFilter(mlbId, null, null, Status.ACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(5,  summariesPage.getSize());
		Assert.assertEquals(3, summariesPage.getNumberOfElements());		
		Assert.assertEquals(1, summariesPage.getTotalPages());
		Assert.assertEquals(3, summariesPage.getTotalElements());
		
		summariesPage = crm.findLocationSummaries(
				new LocationsFilter(mlbId, "Yankee Stadium", null, Status.ACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(5,  summariesPage.getSize());
		Assert.assertEquals(1, summariesPage.getNumberOfElements());		
		Assert.assertEquals(1, summariesPage.getTotalPages());
		Assert.assertEquals(1, summariesPage.getTotalElements());
		
		summariesPage = crm.findLocationSummaries(
				new LocationsFilter(mlbId, "Rogers Centre", null, Status.ACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(5,  summariesPage.getSize());
		Assert.assertEquals(0, summariesPage.getNumberOfElements());		
		Assert.assertEquals(0, summariesPage.getTotalPages());
		Assert.assertEquals(0, summariesPage.getTotalElements());
		
		summariesPage = crm.findLocationSummaries(
				new LocationsFilter(mlbId, "Yankee Stadium", null, Status.INACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(5,  summariesPage.getSize());
		Assert.assertEquals(0, summariesPage.getNumberOfElements());		
		Assert.assertEquals(0, summariesPage.getTotalPages());
		Assert.assertEquals(0, summariesPage.getTotalElements());
		
		summariesPage = crm.findLocationSummaries(
				new LocationsFilter(new Identifier("MLS"), "TD Place", null, Status.ACTIVE), 
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
			crm.findLocationDetails(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Location ID 'abc'", e.getMessage());
		}

		try {
			crm.findLocationSummary(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Location ID 'abc'", e.getMessage());
		}

		try {
			crm.updateLocationName(new Identifier("abc"), "name");
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Location ID 'abc'", e.getMessage());
		}
		
		try {
			crm.updateLocationAddress(new Identifier("abc"), CrmAsserts.MAILING_ADDRESS);
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Location ID 'abc'", e.getMessage());
		}
		
		try {
			crm.disableLocation(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Location ID 'abc'", e.getMessage());
		}
		
		try {
			crm.enableLocation(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Location ID 'abc'", e.getMessage());
		}
	}
	
	@Test
	public void testWrongIdentifiers() throws Exception {
		Identifier groupId = crm.createGroup(GROUP).getGroupId();
		Identifier organizationId = crm.createOrganization("Org Name", List.of("GRP")).getOrganizationId();
		Identifier locationId = crm.createLocation(organizationId, "Location", "LOC", CrmAsserts.MAILING_ADDRESS).getLocationId();

		assertEquals("LOC", crm.findLocationDetails(locationId).getReference());
		assertEquals("LOC", crm.findLocationSummary(locationId).getReference());
		try {
			crm.findLocationDetails(groupId);
			fail("Not a valid identifier");
		} catch (ItemNotFoundException e) { }
	}

}
