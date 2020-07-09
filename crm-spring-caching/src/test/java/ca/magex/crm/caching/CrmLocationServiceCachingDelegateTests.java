package ca.magex.crm.caching;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.caching.config.CachingConfig;
import ca.magex.crm.caching.config.CachingTestConfig;
import ca.magex.crm.caching.util.CacheTemplate;
import ca.magex.crm.test.CrmAsserts;
import ca.magex.crm.test.config.MockTestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CachingTestConfig.class, MockTestConfig.class })
public class CrmLocationServiceCachingDelegateTests {

	@Autowired private CrmLocationService delegate;
	@Autowired private CacheManager cacheManager;

	private CacheTemplate cacheTemplate;
	private CrmLocationServiceCachingDelegate locationService;

	@Before
	public void reset() {
		Mockito.reset(delegate);
		/* clear our caches */
		cacheManager.getCacheNames().forEach((cacheName) -> {
			cacheManager.getCache(cacheName).clear();
		});
		cacheTemplate = new CacheTemplate(cacheManager, CachingConfig.Caches.Locations);
		locationService = new CrmLocationServiceCachingDelegate(delegate, cacheTemplate);
	}

	@Test
	public void testCacheNewLoc() {
		final AtomicInteger locIndex = new AtomicInteger();
		BDDMockito.willAnswer((invocation) -> {
			return new LocationDetails(new LocationIdentifier(Integer.toString(locIndex.getAndIncrement())), invocation.getArgument(0), Status.ACTIVE, invocation.getArgument(2), invocation.getArgument(1), invocation.getArgument(3));
		}).given(delegate).createLocation(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.any(MailingAddress.class));
		LocationDetails locDetails = locationService.createLocation(new OrganizationIdentifier("ABC"), "Head Quarters", "HQ", CrmAsserts.MAILING_ADDRESS);
		BDDMockito.verify(delegate, Mockito.times(1)).createLocation(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.any(MailingAddress.class));

		/* should have added the details to the cache */
		Assert.assertEquals(locDetails, locationService.findLocationDetails(locDetails.getLocationId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findLocationDetails(Mockito.any(LocationIdentifier.class));

		/* should have added the summary to the cache */
		Assert.assertEquals(locDetails.asSummary(), locationService.findLocationSummary(locDetails.getLocationId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findLocationSummary(Mockito.any());
	}

	@Test
	public void testCacheNewLocFromPrototype() {
		final AtomicInteger locIndex = new AtomicInteger();
		BDDMockito.willAnswer((invocation) -> {
			LocationDetails locDetails = invocation.getArgument(0);
			return new LocationDetails(new LocationIdentifier(Integer.toString(locIndex.getAndIncrement())), locDetails.getOrganizationId(), locDetails.getStatus(), locDetails.getReference(), locDetails.getDisplayName(), locDetails.getAddress());
		}).given(delegate).createLocation(Mockito.any(LocationDetails.class));
		LocationDetails locDetails = locationService.createLocation(new LocationDetails(null, new OrganizationIdentifier("ABC"), Status.ACTIVE, "Head Quarters", "HQ", CrmAsserts.MAILING_ADDRESS));
		BDDMockito.verify(delegate, Mockito.times(1)).createLocation(Mockito.any(LocationDetails.class));

		/* should have added the details to the cache */
		Assert.assertEquals(locDetails, locationService.findLocationDetails(locDetails.getLocationId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findLocationDetails(Mockito.any(LocationIdentifier.class));

		/* should have added the summary to the cache */
		Assert.assertEquals(locDetails.asSummary(), locationService.findLocationSummary(locDetails.getLocationId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findLocationSummary(Mockito.any());
	}

	@Test
	public void testCacheLoc() {
		BDDMockito.willAnswer((invocation) -> {
			return new LocationDetails(invocation.getArgument(0), new OrganizationIdentifier("ABC"), Status.ACTIVE, "HQ", "Head Quarters", CrmAsserts.MAILING_ADDRESS);
		}).given(delegate).findLocationDetails(Mockito.any(LocationIdentifier.class));

		/* this should also cache the result, so the second find doesn't hit the delegate */
		LocationDetails locDetails = locationService.findLocationDetails(new LocationIdentifier("1"));
		Assert.assertEquals(locDetails, locationService.findLocationDetails(locDetails.getLocationId()));
		BDDMockito.verify(delegate, Mockito.times(1)).findLocationDetails(Mockito.any(LocationIdentifier.class));

		/* this should also cache the result, so the second find doesn't hit the delegate */
		LocationSummary locSummary = locationService.findLocationSummary(new LocationIdentifier("1"));
		Assert.assertEquals(locSummary, locationService.findLocationSummary(locDetails.getLocationId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findLocationSummary(Mockito.any());
	}

	@Test
	public void testCacheNonExistantLoc() {
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).findLocationDetails(Mockito.any(LocationIdentifier.class));
		
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).findLocationSummary(Mockito.any());

		/* this should also cache the result, so the second find doesn't hit the delegate */
		Assert.assertNull(locationService.findLocationDetails(new LocationIdentifier("1")));
		Assert.assertNull(locationService.findLocationDetails(new LocationIdentifier("1")));
		BDDMockito.verify(delegate, Mockito.times(1)).findLocationDetails(Mockito.any(LocationIdentifier.class));
		
		/* this should also cache the result, so the second find doesn't hit the delegate */
		Assert.assertNull(locationService.findLocationSummary(new LocationIdentifier("2")));
		Assert.assertNull(locationService.findLocationSummary(new LocationIdentifier("2")));
		BDDMockito.verify(delegate, Mockito.times(1)).findLocationSummary(Mockito.any());
	}

	@Test
	public void testDisableCachedLoc() {
		final AtomicInteger locIndex = new AtomicInteger();
		final AtomicReference<LocationDetails> reference = new AtomicReference<>();
		BDDMockito.willAnswer((invocation) -> {
			reference.set(new LocationDetails(new LocationIdentifier(Integer.toString(locIndex.getAndIncrement())), invocation.getArgument(0), Status.ACTIVE, invocation.getArgument(2), invocation.getArgument(1), invocation.getArgument(3)));
			return reference.get();
		}).given(delegate).createLocation(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.any(MailingAddress.class));
		BDDMockito.willAnswer((invocation) -> {
			reference.set(reference.get().withStatus(Status.INACTIVE));
			return reference.get();
		}).given(delegate).disableLocation(Mockito.any());
		BDDMockito.willAnswer((invocation) -> {
			return reference.get();
		}).given(delegate).findLocationDetails(Mockito.any(LocationIdentifier.class));

		LocationDetails locDetails = locationService.createLocation(new OrganizationIdentifier("ABC"), "Head Quarters", "HQ", CrmAsserts.MAILING_ADDRESS);
		Assert.assertEquals(reference.get(), locDetails);
		/* ensure the details and summary are cached */
		Assert.assertEquals(locDetails, locationService.findLocationDetails(locDetails.getLocationId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findLocationDetails(Mockito.any(LocationIdentifier.class));
		Assert.assertEquals(locDetails.asSummary(), locationService.findLocationSummary(locDetails.getLocationId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findLocationSummary(Mockito.any());

		/* make sure the summary is cached after the disable */
		LocationSummary locSummary = locationService.disableLocation(locDetails.getLocationId());
		Assert.assertEquals(locSummary, locationService.findLocationSummary(locDetails.getLocationId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findLocationSummary(Mockito.any());

		/* details should have been evicted from the cache */
		BDDMockito.verify(delegate, Mockito.times(0)).findLocationDetails(Mockito.any(LocationIdentifier.class));
		locDetails = locationService.findLocationDetails(locDetails.getLocationId());
		BDDMockito.verify(delegate, Mockito.times(1)).findLocationDetails(Mockito.any(LocationIdentifier.class));
		locDetails = locationService.findLocationDetails(locDetails.getLocationId());
		BDDMockito.verify(delegate, Mockito.times(1)).findLocationDetails(Mockito.any(LocationIdentifier.class));

		/* disable non existent location (should cache the fact that it doesn't exist for the summary) */
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).disableLocation(new LocationIdentifier("JJ"));
		Assert.assertNull(locationService.disableLocation(new LocationIdentifier("JJ")));
		Assert.assertNull(locationService.findLocationSummary(new LocationIdentifier("JJ")));
		BDDMockito.verify(delegate, Mockito.times(0)).findLocationSummary(new LocationIdentifier("JJ"));
	}

	@Test
	public void testEnableCachedLoc() {
		final AtomicInteger locIndex = new AtomicInteger();
		final AtomicReference<LocationDetails> reference = new AtomicReference<>();
		BDDMockito.willAnswer((invocation) -> {
			reference.set(new LocationDetails(new LocationIdentifier(Integer.toString(locIndex.getAndIncrement())), invocation.getArgument(0), Status.INACTIVE, invocation.getArgument(2), invocation.getArgument(1), invocation.getArgument(3)));
			return reference.get();
		}).given(delegate).createLocation(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.any(MailingAddress.class));
		BDDMockito.willAnswer((invocation) -> {
			reference.set(reference.get().withStatus(Status.ACTIVE));
			return reference.get();
		}).given(delegate).enableLocation(Mockito.any());
		BDDMockito.willAnswer((invocation) -> {
			return reference.get();
		}).given(delegate).findLocationDetails(Mockito.any(LocationIdentifier.class));

		LocationDetails locDetails = locationService.createLocation(new OrganizationIdentifier("ABC"), "Head Quarters", "HQ", CrmAsserts.MAILING_ADDRESS);
		Assert.assertEquals(reference.get(), locDetails);
		/* ensure the details and summary are cached */
		Assert.assertEquals(locDetails, locationService.findLocationDetails(locDetails.getLocationId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findLocationDetails(Mockito.any(LocationIdentifier.class));
		Assert.assertEquals(locDetails.asSummary(), locationService.findLocationSummary(locDetails.getLocationId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findLocationSummary(Mockito.any());

		/* make sure the summary is cached after the disable */
		LocationSummary locSummary = locationService.enableLocation(locDetails.getLocationId());
		Assert.assertEquals(locSummary, locationService.findLocationSummary(locDetails.getLocationId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findLocationSummary(Mockito.any());

		/* details should have been evicted from the cache */
		BDDMockito.verify(delegate, Mockito.times(0)).findLocationDetails(Mockito.any(LocationIdentifier.class));
		locDetails = locationService.findLocationDetails(locDetails.getLocationId());
		BDDMockito.verify(delegate, Mockito.times(1)).findLocationDetails(Mockito.any(LocationIdentifier.class));
		locDetails = locationService.findLocationDetails(locDetails.getLocationId());
		BDDMockito.verify(delegate, Mockito.times(1)).findLocationDetails(Mockito.any(LocationIdentifier.class));

		/* enable non existent location (should cache the fact that it doesn't exist for the summary) */
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).enableLocation(new LocationIdentifier("JJ"));
		Assert.assertNull(locationService.enableLocation(new LocationIdentifier("JJ")));
		Assert.assertNull(locationService.findLocationSummary(new LocationIdentifier("JJ")));
		BDDMockito.verify(delegate, Mockito.times(0)).findLocationSummary(new LocationIdentifier("JJ"));
	}

	@Test
	public void testUpdateExistingLoc() {
		final AtomicInteger locIndex = new AtomicInteger();
		final AtomicReference<LocationDetails> reference = new AtomicReference<LocationDetails>();
		BDDMockito.willAnswer((invocation) -> {
			reference.set(new LocationDetails(new LocationIdentifier(Integer.toString(locIndex.getAndIncrement())), invocation.getArgument(0), Status.INACTIVE, invocation.getArgument(2), invocation.getArgument(1), invocation.getArgument(3)));
			return reference.get();
		}).given(delegate).createLocation(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.any(MailingAddress.class));
		BDDMockito.willAnswer((invocation) -> {
			reference.set(reference.get().withDisplayName(invocation.getArgument(1)));
			return reference.get();
		}).given(delegate).updateLocationName(Mockito.any(), Mockito.anyString());
		BDDMockito.willAnswer((invocation) -> {
			reference.set(reference.get().withAddress(invocation.getArgument(1)));
			return reference.get();
		}).given(delegate).updateLocationAddress(Mockito.any(), Mockito.any(MailingAddress.class));

		/* create and ensure cached */
		LocationDetails locDetails = locationService.createLocation(new OrganizationIdentifier("ABC"), "Head Quarters", "HQ", CrmAsserts.MAILING_ADDRESS);
		Assert.assertEquals(reference.get(), locDetails);
		/* ensure the details and summary are cached */
		Assert.assertEquals(locDetails, locationService.findLocationDetails(locDetails.getLocationId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findLocationDetails(Mockito.any(LocationIdentifier.class));
		Assert.assertEquals(locDetails.asSummary(), locationService.findLocationSummary(locDetails.getLocationId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findLocationSummary(Mockito.any());

		/* clear cache, update name, and ensure cached */
		cacheManager.getCache(CachingConfig.Caches.Locations).clear();
		locDetails = locationService.updateLocationName(locDetails.getLocationId(), "Bye");
		Assert.assertEquals(locDetails, locationService.findLocationDetails(locDetails.getLocationId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findLocationDetails(Mockito.any(LocationIdentifier.class));
		Assert.assertEquals(locDetails.asSummary(), locationService.findLocationSummary(locDetails.getLocationId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findLocationSummary(Mockito.any());

		/* clear cache, update address, and ensure cached */
		cacheManager.getCache(CachingConfig.Caches.Locations).clear();
		locDetails = locationService.updateLocationAddress(locDetails.getLocationId(), CrmAsserts.CA_ADDRESS);
		Assert.assertEquals(locDetails, locationService.findLocationDetails(locDetails.getLocationId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findLocationDetails(Mockito.any(LocationIdentifier.class));
		Assert.assertEquals(locDetails.asSummary(), locationService.findLocationSummary(locDetails.getLocationId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findLocationSummary(Mockito.any());

		/* update non existent location (should cache the fact that it doesn't exist for the summary) */
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).updateLocationName(Mockito.eq(new LocationIdentifier("JJ")), Mockito.anyString());
		Assert.assertNull(locationService.updateLocationName(new LocationIdentifier("JJ"), "hello"));
		Assert.assertNull(locationService.findLocationSummary(new LocationIdentifier("JJ")));
		BDDMockito.verify(delegate, Mockito.times(0)).findLocationSummary(new LocationIdentifier("JJ"));

		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).updateLocationAddress(Mockito.eq(new LocationIdentifier("KK")), Mockito.any());
		Assert.assertNull(locationService.updateLocationAddress(new LocationIdentifier("KK"), CrmAsserts.DE_ADDRESS));
		Assert.assertNull(locationService.findLocationSummary(new LocationIdentifier("KK")));
		BDDMockito.verify(delegate, Mockito.times(0)).findLocationSummary(new LocationIdentifier("KK"));
	}

	@Test
	public void testCachingFindDetailsResults() {
		LocationDetails details1 = new LocationDetails(new LocationIdentifier("A"), new OrganizationIdentifier("O"), Status.ACTIVE, "A", "a", CrmAsserts.CA_ADDRESS);
		LocationDetails details2 = new LocationDetails(new LocationIdentifier("B"), new OrganizationIdentifier("O"), Status.ACTIVE, "B", "b", CrmAsserts.US_ADDRESS);
		LocationDetails details3 = new LocationDetails(new LocationIdentifier("C"), new OrganizationIdentifier("O"), Status.ACTIVE, "C", "c", CrmAsserts.DE_ADDRESS);

		BDDMockito.willAnswer((invocation) -> {
			return new FilteredPage<>(invocation.getArgument(0), invocation.getArgument(1), List.of(details1, details2, details3), 3);
		}).given(delegate).findLocationDetails(Mockito.any(LocationsFilter.class), Mockito.any(Paging.class));

		BDDMockito.willAnswer((invocation) -> {
			return new FilteredPage<>(invocation.getArgument(0), LocationsFilter.getDefaultPaging(), List.of(details1, details2, details3), 3);
		}).given(delegate).findLocationDetails(Mockito.any(LocationsFilter.class));

		BDDMockito.willAnswer((invocation) -> {
			return 3l;
		}).given(delegate).countLocations(Mockito.any(LocationsFilter.class));

		Assert.assertEquals(3l, locationService.countLocations(new LocationsFilter()));

		/* find locations summaries with paging and ensure cached results */
		cacheManager.getCache(CachingConfig.Caches.Locations).clear();
		Assert.assertEquals(3, locationService.findLocationDetails(new LocationsFilter(), new Paging(1, 5, Sort.unsorted())).getNumberOfElements());

		Assert.assertEquals(details1, locationService.findLocationDetails(details1.getLocationId()));
		Assert.assertEquals(details1.asSummary(), locationService.findLocationSummary(details1.getLocationId()));

		Assert.assertEquals(details2, locationService.findLocationDetails(details2.getLocationId()));
		Assert.assertEquals(details2.asSummary(), locationService.findLocationSummary(details2.getLocationId()));

		Assert.assertEquals(details3, locationService.findLocationDetails(details3.getLocationId()));
		Assert.assertEquals(details3.asSummary(), locationService.findLocationSummary(details3.getLocationId()));

		BDDMockito.verify(delegate, Mockito.times(0)).findLocationDetails(Mockito.any(LocationIdentifier.class));
		BDDMockito.verify(delegate, Mockito.times(0)).findLocationSummary(Mockito.any());

		/* find locations summaries with default paging and ensure cached results */
		cacheManager.getCache(CachingConfig.Caches.Locations).clear();
		Assert.assertEquals(3, locationService.findLocationDetails(new LocationsFilter()).getNumberOfElements());

		Assert.assertEquals(details1, locationService.findLocationDetails(details1.getLocationId()));
		Assert.assertEquals(details1.asSummary(), locationService.findLocationSummary(details1.getLocationId()));

		Assert.assertEquals(details2, locationService.findLocationDetails(details2.getLocationId()));
		Assert.assertEquals(details2.asSummary(), locationService.findLocationSummary(details2.getLocationId()));

		Assert.assertEquals(details3, locationService.findLocationDetails(details3.getLocationId()));
		Assert.assertEquals(details3.asSummary(), locationService.findLocationSummary(details3.getLocationId()));

		BDDMockito.verify(delegate, Mockito.times(0)).findLocationDetails(Mockito.any(LocationIdentifier.class));
		BDDMockito.verify(delegate, Mockito.times(0)).findLocationSummary(Mockito.any());
	}

	@Test
	public void testCachingFindSummariesResults() {
		LocationDetails details1 = new LocationDetails(new LocationIdentifier("A"), new OrganizationIdentifier("O"), Status.ACTIVE, "A", "a", CrmAsserts.CA_ADDRESS);
		LocationDetails details2 = new LocationDetails(new LocationIdentifier("B"), new OrganizationIdentifier("O"), Status.ACTIVE, "B", "b", CrmAsserts.US_ADDRESS);
		LocationDetails details3 = new LocationDetails(new LocationIdentifier("C"), new OrganizationIdentifier("O"), Status.ACTIVE, "C", "c", CrmAsserts.DE_ADDRESS);

		BDDMockito.willAnswer((invocation) -> {
			return new FilteredPage<>(invocation.getArgument(0), invocation.getArgument(1), List.of(details1, details2, details3), 3);
		}).given(delegate).findLocationSummaries(Mockito.any(LocationsFilter.class), Mockito.any(Paging.class));

		BDDMockito.willAnswer((invocation) -> {
			return new FilteredPage<>(invocation.getArgument(0), LocationsFilter.getDefaultPaging(), List.of(details1, details2, details3), 3);
		}).given(delegate).findLocationSummaries(Mockito.any(LocationsFilter.class));

		BDDMockito.willAnswer((invocation) -> {
			return new FilteredPage<>(invocation.getArgument(0), LocationsFilter.getDefaultPaging(), List.of(details1, details2, details3), 3);
		}).given(delegate).findActiveLocationSummariesForOrg(Mockito.any());

		BDDMockito.willAnswer((invocation) -> {
			return 3l;
		}).given(delegate).countLocations(Mockito.any(LocationsFilter.class));

		Assert.assertEquals(3l, locationService.countLocations(new LocationsFilter()));

		/* find locations summaries with paging and ensure cached results */
		cacheManager.getCache(CachingConfig.Caches.Locations).clear();
		Assert.assertEquals(3, locationService.findLocationSummaries(new LocationsFilter(), new Paging(1, 5, Sort.unsorted())).getNumberOfElements());

		Assert.assertEquals(details1, locationService.findLocationSummary(details1.getLocationId()));

		Assert.assertEquals(details2, locationService.findLocationSummary(details2.getLocationId()));

		Assert.assertEquals(details3, locationService.findLocationSummary(details3.getLocationId()));

		BDDMockito.verify(delegate, Mockito.times(0)).findLocationSummary(Mockito.any());

		/* find locations summaries with default paging and ensure cached results */
		cacheManager.getCache(CachingConfig.Caches.Locations).clear();
		Assert.assertEquals(3, locationService.findLocationSummaries(new LocationsFilter()).getNumberOfElements());

		Assert.assertEquals(details1, locationService.findLocationSummary(details1.getLocationId()));

		Assert.assertEquals(details2, locationService.findLocationSummary(details2.getLocationId()));

		Assert.assertEquals(details3, locationService.findLocationSummary(details3.getLocationId()));

		BDDMockito.verify(delegate, Mockito.times(0)).findLocationSummary(Mockito.any());

		/* find locations summaries with default paging and ensure cached results */
		cacheManager.getCache(CachingConfig.Caches.Locations).clear();
		Assert.assertEquals(3, locationService.findLocationSummaries(new LocationsFilter()).getNumberOfElements());

		Assert.assertEquals(details1, locationService.findLocationSummary(details1.getLocationId()));

		Assert.assertEquals(details2, locationService.findLocationSummary(details2.getLocationId()));

		Assert.assertEquals(details3, locationService.findLocationSummary(details3.getLocationId()));

		BDDMockito.verify(delegate, Mockito.times(0)).findLocationSummary(Mockito.any());

		/* find active locations summaries */
		cacheManager.getCache(CachingConfig.Caches.Locations).clear();
		Assert.assertEquals(3, locationService.findActiveLocationSummariesForOrg(details1.getOrganizationId()).getNumberOfElements());

		Assert.assertEquals(details1, locationService.findLocationSummary(details1.getLocationId()));

		Assert.assertEquals(details2, locationService.findLocationSummary(details2.getLocationId()));

		Assert.assertEquals(details3, locationService.findLocationSummary(details3.getLocationId()));

		BDDMockito.verify(delegate, Mockito.times(0)).findLocationSummary(Mockito.any());
	}
}