package ca.magex.crm.hazelcast.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.hazelcast.core.HazelcastInstance;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.test.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
@ActiveProfiles(MagexCrmProfiles.CRM_DATASTORE_DECENTRALIZED)
public class HazelcastLocationServiceTests {

	@Autowired private CrmLocationService hzLocationService;
	@Autowired private HazelcastInstance hzInstance;

	@MockBean private CrmOrganizationService organizationService;

	@Before
	public void reset() {
		hzInstance.getMap(HazelcastLocationService.HZ_LOCATION_KEY).clear();
		Mockito.when(organizationService.findOrganizationSummary(new Identifier("MLB"))).thenReturn(Mockito.mock(OrganizationSummary.class));
	}

	@Test
	public void testLocations() {
		/* create */
		MailingAddress newyork = new MailingAddress("1 E 161 St", "The Bronx", "NY", "US", "10451");
		LocationDetails l1 = hzLocationService.createLocation(new Identifier("MLB"), "New York", "NYY", newyork);
		Assert.assertEquals("New York", l1.getDisplayName());
		Assert.assertEquals("NYY", l1.getReference());
		Assert.assertEquals(Status.ACTIVE, l1.getStatus());
		Assert.assertEquals(newyork, l1.getAddress());
		Assert.assertEquals(l1, hzLocationService.findLocationDetails(l1.getLocationId()));

		MailingAddress boston = new MailingAddress("4 Jersey St", "Boston", "MA", "US", "02215");
		LocationDetails l2 = hzLocationService.createLocation(new Identifier("MLB"), "Boston", "BOS", boston);
		Assert.assertEquals("Boston", l2.getDisplayName());
		Assert.assertEquals("BOS", l2.getReference());
		Assert.assertEquals(Status.ACTIVE, l2.getStatus());
		Assert.assertEquals(boston, l2.getAddress());
		Assert.assertEquals(l2, hzLocationService.findLocationDetails(l2.getLocationId()));

		MailingAddress chicago = new MailingAddress("1060 W Addison St", "Chicago", "IL", "US", "60613");
		LocationDetails l3 = hzLocationService.createLocation(new Identifier("MLB"), "Chicago", "CHC", chicago);
		Assert.assertEquals("Chicago", l3.getDisplayName());
		Assert.assertEquals("CHC", l3.getReference());
		Assert.assertEquals(Status.ACTIVE, l3.getStatus());
		Assert.assertEquals(chicago, l3.getAddress());
		Assert.assertEquals(l3, hzLocationService.findLocationDetails(l3.getLocationId()));

		/* update */
		l1 = hzLocationService.updateLocationName(l1.getLocationId(), "Yankee Stadium");
		Assert.assertEquals("Yankee Stadium", l1.getDisplayName());
		Assert.assertEquals("NYY", l1.getReference());
		Assert.assertEquals(Status.ACTIVE, l1.getStatus());
		Assert.assertEquals(newyork, l1.getAddress());
		Assert.assertEquals(l1, hzLocationService.updateLocationName(l1.getLocationId(), "Yankee Stadium"));

		l2 = hzLocationService.updateLocationName(l2.getLocationId(), "Fenway Park");
		Assert.assertEquals("Fenway Park", l2.getDisplayName());
		Assert.assertEquals("BOS", l2.getReference());
		Assert.assertEquals(Status.ACTIVE, l2.getStatus());
		Assert.assertEquals(boston, l2.getAddress());
		Assert.assertEquals(l2, hzLocationService.updateLocationName(l2.getLocationId(), "Fenway Park"));

		l3 = hzLocationService.updateLocationName(l3.getLocationId(), "Wrigley Field");
		Assert.assertEquals("Wrigley Field", l3.getDisplayName());
		Assert.assertEquals("CHC", l3.getReference());
		Assert.assertEquals(Status.ACTIVE, l3.getStatus());
		Assert.assertEquals(chicago, l3.getAddress());
		Assert.assertEquals(l3, hzLocationService.updateLocationName(l3.getLocationId(), "Wrigley Field"));

		l1 = hzLocationService.updateLocationAddress(l1.getLocationId(), boston);
		Assert.assertEquals("Yankee Stadium", l1.getDisplayName());
		Assert.assertEquals("NYY", l1.getReference());
		Assert.assertEquals(Status.ACTIVE, l1.getStatus());
		Assert.assertEquals(boston, l1.getAddress());
		Assert.assertEquals(l1, hzLocationService.updateLocationAddress(l1.getLocationId(), boston));

		l2 = hzLocationService.updateLocationAddress(l2.getLocationId(), chicago);
		Assert.assertEquals("Fenway Park", l2.getDisplayName());
		Assert.assertEquals("BOS", l2.getReference());
		Assert.assertEquals(Status.ACTIVE, l2.getStatus());
		Assert.assertEquals(chicago, l2.getAddress());
		Assert.assertEquals(l2, hzLocationService.updateLocationAddress(l2.getLocationId(), chicago));

		l3 = hzLocationService.updateLocationAddress(l3.getLocationId(), newyork);
		Assert.assertEquals("Wrigley Field", l3.getDisplayName());
		Assert.assertEquals("CHC", l3.getReference());
		Assert.assertEquals(Status.ACTIVE, l3.getStatus());
		Assert.assertEquals(newyork, l3.getAddress());
		Assert.assertEquals(l3, hzLocationService.updateLocationAddress(l3.getLocationId(), newyork));

		/* disable */
		LocationSummary ls1 = hzLocationService.disableLocation(l1.getLocationId());
		Assert.assertEquals("Yankee Stadium", ls1.getDisplayName());
		Assert.assertEquals("NYY", ls1.getReference());
		Assert.assertEquals(Status.INACTIVE, ls1.getStatus());
		Assert.assertEquals(ls1, hzLocationService.disableLocation(l1.getLocationId()));
		Assert.assertEquals(ls1, hzLocationService.findLocationSummary(l1.getLocationId()));

		LocationSummary ls2 = hzLocationService.disableLocation(l2.getLocationId());
		Assert.assertEquals("Fenway Park", ls2.getDisplayName());
		Assert.assertEquals("BOS", ls2.getReference());
		Assert.assertEquals(Status.INACTIVE, ls2.getStatus());
		Assert.assertEquals(ls2, hzLocationService.disableLocation(l2.getLocationId()));
		Assert.assertEquals(ls2, hzLocationService.findLocationSummary(l2.getLocationId()));

		LocationSummary ls3 = hzLocationService.disableLocation(l3.getLocationId());
		Assert.assertEquals("Wrigley Field", ls3.getDisplayName());
		Assert.assertEquals("CHC", ls3.getReference());
		Assert.assertEquals(Status.INACTIVE, ls3.getStatus());
		Assert.assertEquals(ls3, hzLocationService.disableLocation(l3.getLocationId()));
		Assert.assertEquals(ls3, hzLocationService.findLocationSummary(l3.getLocationId()));
		
		/* enable */
		ls1 = hzLocationService.enableLocation(l1.getLocationId());
		Assert.assertEquals("Yankee Stadium", ls1.getDisplayName());
		Assert.assertEquals("NYY", ls1.getReference());
		Assert.assertEquals(Status.ACTIVE, ls1.getStatus());
		Assert.assertEquals(ls1, hzLocationService.enableLocation(l1.getLocationId()));
		Assert.assertEquals(ls1, hzLocationService.findLocationSummary(l1.getLocationId()));

		ls2 = hzLocationService.enableLocation(l2.getLocationId());
		Assert.assertEquals("Fenway Park", ls2.getDisplayName());
		Assert.assertEquals("BOS", ls2.getReference());
		Assert.assertEquals(Status.ACTIVE, ls2.getStatus());
		Assert.assertEquals(ls2, hzLocationService.enableLocation(l2.getLocationId()));
		Assert.assertEquals(ls2, hzLocationService.findLocationSummary(l2.getLocationId()));

		ls3 = hzLocationService.enableLocation(l3.getLocationId());
		Assert.assertEquals("Wrigley Field", ls3.getDisplayName());
		Assert.assertEquals("CHC", ls3.getReference());
		Assert.assertEquals(Status.ACTIVE, ls3.getStatus());
		Assert.assertEquals(ls3, hzLocationService.enableLocation(l3.getLocationId()));
		Assert.assertEquals(ls3, hzLocationService.findLocationSummary(l3.getLocationId()));

		/* count locations */
		Assert.assertEquals(3, hzLocationService.countLocations(new LocationsFilter(null, null, null, null)));
		Assert.assertEquals(3, hzLocationService.countLocations(new LocationsFilter(new Identifier("MLB"), null, null, null)));
		Assert.assertEquals(3, hzLocationService.countLocations(new LocationsFilter(new Identifier("MLB"), null, null, Status.ACTIVE)));
		Assert.assertEquals(1, hzLocationService.countLocations(new LocationsFilter(new Identifier("MLB"), "Yankee Stadium", null, Status.ACTIVE)));
		Assert.assertEquals(0, hzLocationService.countLocations(new LocationsFilter(new Identifier("MLB"), "Rogers Centre", null, Status.ACTIVE)));
		Assert.assertEquals(0, hzLocationService.countLocations(new LocationsFilter(new Identifier("MLB"), "Yankee Stadium", null, Status.INACTIVE)));
		Assert.assertEquals(0, hzLocationService.countLocations(new LocationsFilter(new Identifier("MLS"), "TD Place", null, Status.ACTIVE)));
		
		/* find locations details */
		Page<LocationDetails> detailsPage = hzLocationService.findLocationDetails(
				new LocationsFilter(null, null, null, null), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5,  detailsPage.getSize());
		Assert.assertEquals(3, detailsPage.getNumberOfElements());		
		Assert.assertEquals(1, detailsPage.getTotalPages());
		Assert.assertEquals(3, detailsPage.getTotalElements());
		
		detailsPage = hzLocationService.findLocationDetails(
				new LocationsFilter(new Identifier("MLB"), null, null, null), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5,  detailsPage.getSize());
		Assert.assertEquals(3, detailsPage.getNumberOfElements());		
		Assert.assertEquals(1, detailsPage.getTotalPages());
		Assert.assertEquals(3, detailsPage.getTotalElements());
		
		detailsPage = hzLocationService.findLocationDetails(
				new LocationsFilter(new Identifier("MLB"), null, null, Status.ACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5,  detailsPage.getSize());
		Assert.assertEquals(3, detailsPage.getNumberOfElements());		
		Assert.assertEquals(1, detailsPage.getTotalPages());
		Assert.assertEquals(3, detailsPage.getTotalElements());
		
		detailsPage = hzLocationService.findLocationDetails(
				new LocationsFilter(new Identifier("MLB"), "Yankee Stadium", null, Status.ACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5,  detailsPage.getSize());
		Assert.assertEquals(1, detailsPage.getNumberOfElements());		
		Assert.assertEquals(1, detailsPage.getTotalPages());
		Assert.assertEquals(1, detailsPage.getTotalElements());
		
		detailsPage = hzLocationService.findLocationDetails(
				new LocationsFilter(new Identifier("MLB"), "Rogers Centre", null, Status.ACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5,  detailsPage.getSize());
		Assert.assertEquals(0, detailsPage.getNumberOfElements());		
		Assert.assertEquals(0, detailsPage.getTotalPages());
		Assert.assertEquals(0, detailsPage.getTotalElements());
		
		detailsPage = hzLocationService.findLocationDetails(
				new LocationsFilter(new Identifier("MLB"), "Yankee Stadium", null, Status.INACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5,  detailsPage.getSize());
		Assert.assertEquals(0, detailsPage.getNumberOfElements());		
		Assert.assertEquals(0, detailsPage.getTotalPages());
		Assert.assertEquals(0, detailsPage.getTotalElements());
		
		detailsPage = hzLocationService.findLocationDetails(
				new LocationsFilter(new Identifier("MLS"), "TD Place", null, Status.ACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5,  detailsPage.getSize());
		Assert.assertEquals(0, detailsPage.getNumberOfElements());		
		Assert.assertEquals(0, detailsPage.getTotalPages());
		Assert.assertEquals(0, detailsPage.getTotalElements());
		
		/* find locations summaries */
		Page<LocationSummary> summariesPage = hzLocationService.findLocationSummaries(
				new LocationsFilter(null, null, null, null), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(5,  summariesPage.getSize());
		Assert.assertEquals(3, summariesPage.getNumberOfElements());		
		Assert.assertEquals(1, summariesPage.getTotalPages());
		Assert.assertEquals(3, summariesPage.getTotalElements());
		
		summariesPage = hzLocationService.findLocationSummaries(
				new LocationsFilter(new Identifier("MLB"), null, null, null), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(5,  summariesPage.getSize());
		Assert.assertEquals(3, summariesPage.getNumberOfElements());		
		Assert.assertEquals(1, summariesPage.getTotalPages());
		Assert.assertEquals(3, summariesPage.getTotalElements());
		
		summariesPage = hzLocationService.findLocationSummaries(
				new LocationsFilter(new Identifier("MLB"), null, null, Status.ACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(5,  summariesPage.getSize());
		Assert.assertEquals(3, summariesPage.getNumberOfElements());		
		Assert.assertEquals(1, summariesPage.getTotalPages());
		Assert.assertEquals(3, summariesPage.getTotalElements());
		
		summariesPage = hzLocationService.findLocationSummaries(
				new LocationsFilter(new Identifier("MLB"), "Yankee Stadium", null, Status.ACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(5,  summariesPage.getSize());
		Assert.assertEquals(1, summariesPage.getNumberOfElements());		
		Assert.assertEquals(1, summariesPage.getTotalPages());
		Assert.assertEquals(1, summariesPage.getTotalElements());
		
		summariesPage = hzLocationService.findLocationSummaries(
				new LocationsFilter(new Identifier("MLB"), "Rogers Centre", null, Status.ACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(5,  summariesPage.getSize());
		Assert.assertEquals(0, summariesPage.getNumberOfElements());		
		Assert.assertEquals(0, summariesPage.getTotalPages());
		Assert.assertEquals(0, summariesPage.getTotalElements());
		
		summariesPage = hzLocationService.findLocationSummaries(
				new LocationsFilter(new Identifier("MLB"), "Yankee Stadium", null, Status.INACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(5,  summariesPage.getSize());
		Assert.assertEquals(0, summariesPage.getNumberOfElements());		
		Assert.assertEquals(0, summariesPage.getTotalPages());
		Assert.assertEquals(0, summariesPage.getTotalElements());
		
		summariesPage = hzLocationService.findLocationSummaries(
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
			hzLocationService.findLocationDetails(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Location ID 'abc'", e.getMessage());
		}

		try {
			hzLocationService.findLocationSummary(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Location ID 'abc'", e.getMessage());
		}

		try {
			hzLocationService.updateLocationName(new Identifier("abc"), "name");
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Location ID 'abc'", e.getMessage());
		}
		
		try {
			hzLocationService.updateLocationAddress(new Identifier("abc"), new MailingAddress("", "", "", "", ""));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Location ID 'abc'", e.getMessage());
		}
		
		try {
			hzLocationService.disableLocation(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Location ID 'abc'", e.getMessage());
		}
		
		try {
			hzLocationService.enableLocation(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Location ID 'abc'", e.getMessage());
		}
	}

}
