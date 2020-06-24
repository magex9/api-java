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

import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.caching.config.CachingConfig;
import ca.magex.crm.caching.config.CachingTestConfig;
import ca.magex.crm.caching.util.CacheTemplate;
import ca.magex.crm.test.config.MockTestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CachingTestConfig.class, MockTestConfig.class })
public class CrmOrganizationServiceCachingDelegateTests {

	@Autowired private CrmOrganizationService delegate;
	@Autowired private CacheManager cacheManager;

	private CacheTemplate cacheTemplate;
	private CrmOrganizationServiceCachingDelegate organizationService;

	@Before
	public void reset() {
		Mockito.reset(delegate);
		/* clear our caches */
		cacheManager.getCacheNames().forEach((cacheName) -> {
			cacheManager.getCache(cacheName).clear();
		});
		cacheTemplate = new CacheTemplate(cacheManager, CachingConfig.Caches.Organizations);
		organizationService = new CrmOrganizationServiceCachingDelegate(delegate, cacheTemplate);
	}

	@Test
	public void testCacheNewOrg() {
		final AtomicInteger orgIndex = new AtomicInteger();
		BDDMockito.willAnswer((invocation) -> {
			return new OrganizationDetails(new Identifier(Integer.toString(orgIndex.incrementAndGet())), Status.ACTIVE, invocation.getArgument(0), null, null, invocation.getArgument(1));
		}).given(delegate).createOrganization(Mockito.anyString(), Mockito.anyList());
		OrganizationDetails orgDetails = organizationService.createOrganization("hello", List.of("ORG"));
		BDDMockito.verify(delegate, Mockito.times(1)).createOrganization(Mockito.anyString(), Mockito.anyList());

		/* should have added the details to the cache */
		Assert.assertEquals(orgDetails, organizationService.findOrganizationDetails(orgDetails.getOrganizationId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationDetails(Mockito.any(Identifier.class));

		/* should have added the summary to the cache */
		Assert.assertNotNull(organizationService.findOrganizationSummary(orgDetails.getOrganizationId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationSummary(Mockito.any(Identifier.class));
	}

	@Test
	public void testCacheNewOrgFromPrototype() {
		final AtomicInteger orgIndex = new AtomicInteger();
		BDDMockito.willAnswer((invocation) -> {
			OrganizationDetails orgDetails = invocation.getArgument(0);
			return new OrganizationDetails(new Identifier(Integer.toString(orgIndex.incrementAndGet())), orgDetails.getStatus(), orgDetails.getDisplayName(), orgDetails.getMainLocationId(), orgDetails.getMainContactId(), orgDetails.getGroups());
		}).given(delegate).createOrganization(Mockito.any(OrganizationDetails.class));
		OrganizationDetails orgDetails = organizationService.createOrganization(new OrganizationDetails(null, Status.ACTIVE, "Hello", null, null, List.of("ORG")));
		BDDMockito.verify(delegate, Mockito.times(1)).createOrganization(Mockito.any(OrganizationDetails.class));

		/* should have added the details to the cache */
		Assert.assertEquals(orgDetails, organizationService.findOrganizationDetails(orgDetails.getOrganizationId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationDetails(Mockito.any(Identifier.class));

		/* should have added the summary to the cache */
		Assert.assertNotNull(organizationService.findOrganizationSummary(orgDetails.getOrganizationId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationSummary(Mockito.any(Identifier.class));
	}

	@Test
	public void testCacheExistingOrg() {
		BDDMockito.willAnswer((invocation) -> {
			return new OrganizationDetails(invocation.getArgument(0), Status.ACTIVE, "Org1", null, null, List.of("ORG"));
		}).given(delegate).findOrganizationDetails(Mockito.any(Identifier.class));

		BDDMockito.willAnswer((invocation) -> {
			return new OrganizationSummary(invocation.getArgument(0), Status.ACTIVE, "Org1");
		}).given(delegate).findOrganizationSummary(Mockito.any(Identifier.class));

		/* this should also cache the result, so the second find doesn't hit the delegate */
		OrganizationDetails orgDetails = organizationService.findOrganizationDetails(new Identifier("ABC"));
		Assert.assertEquals(orgDetails, organizationService.findOrganizationDetails(orgDetails.getOrganizationId()));
		BDDMockito.verify(delegate, Mockito.times(1)).findOrganizationDetails(Mockito.any(Identifier.class));

		/* this should also cache the result, so the second find doesn't hit the delegate */
		OrganizationSummary orgSummary = organizationService.findOrganizationSummary(new Identifier("ABC"));
		Assert.assertEquals(orgSummary, organizationService.findOrganizationSummary(orgSummary.getOrganizationId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationSummary(Mockito.any(Identifier.class));
	}

	@Test
	public void testCacheNonExistantOrg() {
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).findOrganizationDetails(Mockito.any(Identifier.class));
		/* this should also cache the result, so the second find doesn't hit the delegate */
		Assert.assertNull(organizationService.findOrganizationDetails(new Identifier("ABC")));
		Assert.assertNull(organizationService.findOrganizationDetails(new Identifier("ABC")));

		BDDMockito.verify(delegate, Mockito.times(1)).findOrganizationDetails(Mockito.any(Identifier.class));
	}

	@Test
	public void testDisableCachedOrg() {
		final AtomicInteger orgIndex = new AtomicInteger();
		final AtomicReference<OrganizationDetails> reference = new AtomicReference<>();
		BDDMockito.willAnswer((invocation) -> {
			reference.set(new OrganizationDetails(new Identifier(Integer.toString(orgIndex.incrementAndGet())), Status.ACTIVE, invocation.getArgument(0), null, null, invocation.getArgument(1)));
			return reference.get();
		}).given(delegate).createOrganization(Mockito.anyString(), Mockito.anyList());
		BDDMockito.willAnswer((invocation) -> {
			reference.set(reference.get().withStatus(Status.INACTIVE));
			return reference.get();
		}).given(delegate).disableOrganization(Mockito.any(Identifier.class));
		BDDMockito.willAnswer((invocation) -> {
			return reference.get();
		}).given(delegate).findOrganizationDetails(Mockito.any(Identifier.class));

		OrganizationDetails orgDetails = organizationService.createOrganization("hello", List.of("ORG"));
		Assert.assertEquals(reference.get(), orgDetails);
		/* ensure the details and summary are cached */
		Assert.assertEquals(orgDetails, organizationService.findOrganizationDetails(orgDetails.getOrganizationId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationDetails(Mockito.any(Identifier.class));
		Assert.assertEquals(orgDetails.asSummary(), organizationService.findOrganizationSummary(orgDetails.getOrganizationId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationSummary(Mockito.any(Identifier.class));

		/* make sure the summary is cached after the disable */
		OrganizationSummary orgSummary = organizationService.disableOrganization(orgDetails.getOrganizationId());
		Assert.assertEquals(orgSummary, organizationService.findOrganizationSummary(orgDetails.getOrganizationId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationSummary(Mockito.any(Identifier.class));

		/* details should have been evicted from the cache */
		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationDetails(Mockito.any(Identifier.class));
		orgDetails = organizationService.findOrganizationDetails(orgDetails.getOrganizationId());
		BDDMockito.verify(delegate, Mockito.times(1)).findOrganizationDetails(Mockito.any(Identifier.class));
		orgDetails = organizationService.findOrganizationDetails(orgDetails.getOrganizationId());
		BDDMockito.verify(delegate, Mockito.times(1)).findOrganizationDetails(Mockito.any(Identifier.class));

		/* disable non existent organization (should cache the fact that it doesn't exist for the summary) */
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).disableOrganization(new Identifier("JJ"));
		Assert.assertNull(organizationService.disableOrganization(new Identifier("JJ")));
		Assert.assertNull(organizationService.findOrganizationSummary(new Identifier("JJ")));
		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationSummary(new Identifier("JJ"));
	}

	@Test
	public void testEnableCachedOrg() {
		final AtomicInteger orgIndex = new AtomicInteger();
		final AtomicReference<OrganizationDetails> reference = new AtomicReference<>();
		BDDMockito.willAnswer((invocation) -> {
			reference.set(new OrganizationDetails(new Identifier(Integer.toString(orgIndex.incrementAndGet())), Status.INACTIVE, invocation.getArgument(0), null, null, invocation.getArgument(1)));
			return reference.get();
		}).given(delegate).createOrganization(Mockito.anyString(), Mockito.anyList());
		BDDMockito.willAnswer((invocation) -> {
			reference.set(reference.get().withStatus(Status.ACTIVE));
			return reference.get();
		}).given(delegate).enableOrganization(Mockito.any(Identifier.class));
		BDDMockito.willAnswer((invocation) -> {
			return reference.get();
		}).given(delegate).findOrganizationDetails(Mockito.any(Identifier.class));

		OrganizationDetails orgDetails = organizationService.createOrganization("hello", List.of("ORG"));
		Assert.assertEquals(reference.get(), orgDetails);
		/* ensure the details and summary are cached */
		Assert.assertEquals(orgDetails, organizationService.findOrganizationDetails(orgDetails.getOrganizationId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationDetails(Mockito.any(Identifier.class));
		Assert.assertEquals(orgDetails.asSummary(), organizationService.findOrganizationSummary(orgDetails.getOrganizationId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationSummary(Mockito.any(Identifier.class));

		/* make sure the summary is cached after the enable */
		OrganizationSummary orgSummary = organizationService.enableOrganization(orgDetails.getOrganizationId());
		Assert.assertEquals(orgSummary, organizationService.findOrganizationSummary(orgDetails.getOrganizationId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationSummary(Mockito.any(Identifier.class));

		/* details should have been evicted from the cache */
		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationDetails(Mockito.any(Identifier.class));
		orgDetails = organizationService.findOrganizationDetails(orgDetails.getOrganizationId());
		BDDMockito.verify(delegate, Mockito.times(1)).findOrganizationDetails(Mockito.any(Identifier.class));
		orgDetails = organizationService.findOrganizationDetails(orgDetails.getOrganizationId());
		BDDMockito.verify(delegate, Mockito.times(1)).findOrganizationDetails(Mockito.any(Identifier.class));

		/* enable non existent organization (should cache the fact that it doesn't exist for the summary) */
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).enableOrganization(new Identifier("JJ"));
		Assert.assertNull(organizationService.enableOrganization(new Identifier("JJ")));
		Assert.assertNull(organizationService.findOrganizationSummary(new Identifier("JJ")));
		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationSummary(new Identifier("JJ"));
	}

	@Test
	public void testUpdateExistingOrg() {
		final AtomicInteger orgIndex = new AtomicInteger();
		final AtomicReference<OrganizationDetails> reference = new AtomicReference<OrganizationDetails>();
		BDDMockito.willAnswer((invocation) -> {
			reference.set(new OrganizationDetails(new Identifier(Integer.toString(orgIndex.incrementAndGet())), Status.INACTIVE, invocation.getArgument(0), null, null, invocation.getArgument(1)));
			return reference.get();
		}).given(delegate).createOrganization(Mockito.anyString(), Mockito.anyList());
		BDDMockito.willAnswer((invocation) -> {
			reference.set(reference.get().withDisplayName(invocation.getArgument(1)));
			return reference.get();
		}).given(delegate).updateOrganizationDisplayName(Mockito.any(Identifier.class), Mockito.anyString());
		BDDMockito.willAnswer((invocation) -> {
			reference.set(reference.get().withMainLocationId(invocation.getArgument(1)));
			return reference.get();
		}).given(delegate).updateOrganizationMainLocation(Mockito.any(Identifier.class), Mockito.any(Identifier.class));
		BDDMockito.willAnswer((invocation) -> {
			reference.set(reference.get().withMainContactId(invocation.getArgument(1)));
			return reference.get();
		}).given(delegate).updateOrganizationMainContact(Mockito.any(Identifier.class), Mockito.any(Identifier.class));
		BDDMockito.willAnswer((invocation) -> {
			reference.set(reference.get().withGroups(invocation.getArgument(1)));
			return reference.get();
		}).given(delegate).updateOrganizationGroups(Mockito.any(Identifier.class), Mockito.anyList());

		/* create and ensure cached */
		OrganizationDetails orgDetails = organizationService.createOrganization("hello", List.of("ORG"));
		Assert.assertEquals(orgDetails, organizationService.findOrganizationDetails(orgDetails.getOrganizationId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationDetails(Mockito.any(Identifier.class));
		Assert.assertEquals(orgDetails.asSummary(), organizationService.findOrganizationSummary(orgDetails.getOrganizationId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationSummary(Mockito.any(Identifier.class));

		/* clear cache, update name, and ensure cached */
		cacheManager.getCache(CachingConfig.Caches.Organizations).clear();
		orgDetails = organizationService.updateOrganizationDisplayName(orgDetails.getOrganizationId(), "Bye");
		Assert.assertEquals(orgDetails, organizationService.findOrganizationDetails(orgDetails.getOrganizationId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationDetails(Mockito.any(Identifier.class));
		Assert.assertEquals(orgDetails.asSummary(), organizationService.findOrganizationSummary(orgDetails.getOrganizationId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationSummary(Mockito.any(Identifier.class));

		/* clear cache, update main location, and ensure cached */
		cacheManager.getCache(CachingConfig.Caches.Organizations).clear();
		orgDetails = organizationService.updateOrganizationMainLocation(orgDetails.getOrganizationId(), new Identifier("LOC"));
		Assert.assertEquals(orgDetails, organizationService.findOrganizationDetails(orgDetails.getOrganizationId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationDetails(Mockito.any(Identifier.class));
		Assert.assertEquals(orgDetails.asSummary(), organizationService.findOrganizationSummary(orgDetails.getOrganizationId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationSummary(Mockito.any(Identifier.class));

		/* clear cache, update main contact, and ensure cached */
		cacheManager.getCache(CachingConfig.Caches.Organizations).clear();
		orgDetails = organizationService.updateOrganizationMainContact(orgDetails.getOrganizationId(), new Identifier("WOMAN"));
		Assert.assertEquals(orgDetails, organizationService.findOrganizationDetails(orgDetails.getOrganizationId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationDetails(Mockito.any(Identifier.class));
		Assert.assertEquals(orgDetails.asSummary(), organizationService.findOrganizationSummary(orgDetails.getOrganizationId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationSummary(Mockito.any(Identifier.class));

		/* clear cache, update groups, and ensure cached */
		cacheManager.getCache(CachingConfig.Caches.Organizations).clear();
		orgDetails = organizationService.updateOrganizationGroups(orgDetails.getOrganizationId(), List.of("ADM"));
		Assert.assertEquals(orgDetails, organizationService.findOrganizationDetails(orgDetails.getOrganizationId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationDetails(Mockito.any(Identifier.class));
		Assert.assertEquals(orgDetails.asSummary(), organizationService.findOrganizationSummary(orgDetails.getOrganizationId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationSummary(Mockito.any(Identifier.class));

		/* update non existent organization (should cache the fact that it doesn't exist for the summary) */
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).updateOrganizationDisplayName(Mockito.eq(new Identifier("JJ")), Mockito.any());
		Assert.assertNull(organizationService.updateOrganizationDisplayName(new Identifier("JJ"), "Hello"));
		Assert.assertNull(organizationService.findOrganizationSummary(new Identifier("JJ")));
		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationSummary(new Identifier("JJ"));

		/* update non existent organization (should cache the fact that it doesn't exist for the summary) */
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).updateOrganizationMainLocation(Mockito.eq(new Identifier("KK")), Mockito.any());
		Assert.assertNull(organizationService.updateOrganizationMainLocation(new Identifier("KK"), new Identifier("123")));
		Assert.assertNull(organizationService.findOrganizationSummary(new Identifier("KK")));
		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationSummary(new Identifier("KK"));

		/* update non existent organization (should cache the fact that it doesn't exist for the summary) */
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).updateOrganizationMainContact(Mockito.eq(new Identifier("LL")), Mockito.any());
		Assert.assertNull(organizationService.updateOrganizationMainContact(new Identifier("LL"), new Identifier("123")));
		Assert.assertNull(organizationService.findOrganizationSummary(new Identifier("LL")));
		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationSummary(new Identifier("LL"));

		/* update non existent organization (should cache the fact that it doesn't exist for the summary) */
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).updateOrganizationGroups(Mockito.eq(new Identifier("MM")), Mockito.any());
		Assert.assertNull(organizationService.updateOrganizationGroups(new Identifier("MM"), List.of("A")));
		Assert.assertNull(organizationService.findOrganizationSummary(new Identifier("MM")));
		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationSummary(new Identifier("MM"));
	}

	@Test
	public void testCachingFindDetailsResults() {
		OrganizationDetails details1 = new OrganizationDetails(new Identifier("A"), Status.ACTIVE, "A", null, null, List.of());
		OrganizationDetails details2 = new OrganizationDetails(new Identifier("B"), Status.ACTIVE, "B", null, null, List.of());
		OrganizationDetails details3 = new OrganizationDetails(new Identifier("C"), Status.ACTIVE, "C", null, null, List.of());

		BDDMockito.willAnswer((invocation) -> {
			return new FilteredPage<>(invocation.getArgument(0), invocation.getArgument(1), List.of(details1, details2, details3), 3);
		}).given(delegate).findOrganizationDetails(Mockito.any(OrganizationsFilter.class), Mockito.any(Paging.class));

		BDDMockito.willAnswer((invocation) -> {
			return new FilteredPage<>(invocation.getArgument(0), OrganizationsFilter.getDefaultPaging(), List.of(details1, details2, details3), 3);
		}).given(delegate).findOrganizationDetails(Mockito.any(OrganizationsFilter.class));

		BDDMockito.willAnswer((invocation) -> {
			return 3l;
		}).given(delegate).countOrganizations(Mockito.any(OrganizationsFilter.class));

		Assert.assertEquals(3l, organizationService.countOrganizations(new OrganizationsFilter()));

		/* find organization details with paging and ensure cached results */
		cacheManager.getCache(CachingConfig.Caches.Organizations).clear();
		Assert.assertEquals(3, organizationService.findOrganizationDetails(new OrganizationsFilter(), new Paging(1, 5, Sort.unsorted())).getNumberOfElements());

		Assert.assertEquals(details1, organizationService.findOrganizationDetails(details1.getOrganizationId()));
		Assert.assertEquals(details1.asSummary(), organizationService.findOrganizationSummary(details1.getOrganizationId()));

		Assert.assertEquals(details2, organizationService.findOrganizationDetails(details2.getOrganizationId()));
		Assert.assertEquals(details2.asSummary(), organizationService.findOrganizationSummary(details2.getOrganizationId()));

		Assert.assertEquals(details3, organizationService.findOrganizationDetails(details3.getOrganizationId()));
		Assert.assertEquals(details3.asSummary(), organizationService.findOrganizationSummary(details3.getOrganizationId()));

		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationDetails(Mockito.any(Identifier.class));
		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationSummary(Mockito.any(Identifier.class));

		/* find organization details with default paging and ensure cached results */
		cacheManager.getCache(CachingConfig.Caches.Organizations).clear();
		Assert.assertEquals(3, organizationService.findOrganizationDetails(new OrganizationsFilter()).getNumberOfElements());

		Assert.assertEquals(details1, organizationService.findOrganizationDetails(details1.getOrganizationId()));
		Assert.assertEquals(details1.asSummary(), organizationService.findOrganizationSummary(details1.getOrganizationId()));

		Assert.assertEquals(details2, organizationService.findOrganizationDetails(details2.getOrganizationId()));
		Assert.assertEquals(details2.asSummary(), organizationService.findOrganizationSummary(details2.getOrganizationId()));

		Assert.assertEquals(details3, organizationService.findOrganizationDetails(details3.getOrganizationId()));
		Assert.assertEquals(details3.asSummary(), organizationService.findOrganizationSummary(details3.getOrganizationId()));

		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationDetails(Mockito.any(Identifier.class));
		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationSummary(Mockito.any(Identifier.class));
	}

	@Test
	public void testCachingFindSummaryResults() {
		OrganizationDetails details1 = new OrganizationDetails(new Identifier("A"), Status.ACTIVE, "A", null, null, List.of());
		OrganizationDetails details2 = new OrganizationDetails(new Identifier("B"), Status.ACTIVE, "B", null, null, List.of());
		OrganizationDetails details3 = new OrganizationDetails(new Identifier("C"), Status.ACTIVE, "C", null, null, List.of());

		BDDMockito.willAnswer((invocation) -> {
			return new FilteredPage<>(invocation.getArgument(0), invocation.getArgument(1), List.of(details1, details2, details3), 3);
		}).given(delegate).findOrganizationSummaries(Mockito.any(OrganizationsFilter.class), Mockito.any(Paging.class));

		BDDMockito.willAnswer((invocation) -> {
			return new FilteredPage<>(invocation.getArgument(0), OrganizationsFilter.getDefaultPaging(), List.of(details1, details2, details3), 3);
		}).given(delegate).findOrganizationSummaries(Mockito.any(OrganizationsFilter.class));

		BDDMockito.willAnswer((invocation) -> {
			return 3l;
		}).given(delegate).countOrganizations(Mockito.any(OrganizationsFilter.class));

		Assert.assertEquals(3l, organizationService.countOrganizations(new OrganizationsFilter()));

		/* find organization summaries with paging and ensure cached results */
		Assert.assertEquals(3, organizationService.findOrganizationSummaries(new OrganizationsFilter(), new Paging(1, 5, Sort.unsorted())).getNumberOfElements());

		Assert.assertEquals(details1, organizationService.findOrganizationSummary(details1.getOrganizationId()));

		Assert.assertEquals(details2, organizationService.findOrganizationSummary(details2.getOrganizationId()));

		Assert.assertEquals(details3, organizationService.findOrganizationSummary(details3.getOrganizationId()));

		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationSummary(Mockito.any(Identifier.class));

		/* find organization details with default paging and ensure cached results */
		cacheManager.getCache(CachingConfig.Caches.Organizations).clear();
		Assert.assertEquals(3, organizationService.findOrganizationSummaries(new OrganizationsFilter()).getNumberOfElements());

		Assert.assertEquals(details1, organizationService.findOrganizationSummary(details1.getOrganizationId()));

		Assert.assertEquals(details2, organizationService.findOrganizationSummary(details2.getOrganizationId()));

		Assert.assertEquals(details3, organizationService.findOrganizationSummary(details3.getOrganizationId()));

		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationSummary(Mockito.any(Identifier.class));
	}
}
