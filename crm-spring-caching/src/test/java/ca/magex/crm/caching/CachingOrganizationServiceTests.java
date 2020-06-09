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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.test.config.MockConfig;
import ca.magex.crm.test.config.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CachingTestConfig.class, TestConfig.class, MockConfig.class })
@ActiveProfiles(profiles = { MagexCrmProfiles.CRM_NO_AUTH })
public class CachingOrganizationServiceTests {

	@Autowired private CrmOrganizationService delegate;
	@Autowired private CacheManager cacheManager;
	@Autowired @Qualifier("CachingOrganizationService") private CrmOrganizationService organizationService;

	@Before
	public void reset() {
		Mockito.reset(delegate);
		/* clear our caches */
		cacheManager.getCacheNames().forEach((cacheName) -> {
			cacheManager.getCache(cacheName).clear();
		});
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
		BDDMockito.verify(delegate, Mockito.times(1)).findOrganizationSummary(Mockito.any(Identifier.class));
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
		BDDMockito.willAnswer((invocation) -> {
			return new OrganizationDetails(new Identifier(Integer.toString(orgIndex.incrementAndGet())), Status.ACTIVE, invocation.getArgument(0), null, null, invocation.getArgument(1));
		}).given(delegate).createOrganization(Mockito.anyString(), Mockito.anyList());
		BDDMockito.willAnswer((invocation) -> {
			return new OrganizationDetails(invocation.getArgument(0), Status.INACTIVE, "hello", null, null, List.of("ORG"));
		}).given(delegate).disableOrganization(Mockito.any(Identifier.class));
		BDDMockito.willAnswer((invocation) -> {
			return new OrganizationDetails(invocation.getArgument(0), Status.INACTIVE, "hello", null, null, List.of("ORG"));
		}).given(delegate).findOrganizationDetails(Mockito.any(Identifier.class));

		OrganizationDetails orgDetails = organizationService.createOrganization("hello", List.of("ORG"));

		OrganizationSummary orgSummary = organizationService.disableOrganization(orgDetails.getOrganizationId());
		Assert.assertEquals(orgSummary, organizationService.findOrganizationSummary(orgDetails.getOrganizationId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationSummary(Mockito.any(Identifier.class));

		/* details should have been evicted from the cache */
		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationDetails(Mockito.any(Identifier.class));
		orgDetails = organizationService.findOrganizationDetails(orgDetails.getOrganizationId());
		BDDMockito.verify(delegate, Mockito.times(1)).findOrganizationDetails(Mockito.any(Identifier.class));
		orgDetails = organizationService.findOrganizationDetails(orgDetails.getOrganizationId());
		BDDMockito.verify(delegate, Mockito.times(1)).findOrganizationDetails(Mockito.any(Identifier.class));
	}

	@Test
	public void testEnableCachedOrg() {
		final AtomicInteger orgIndex = new AtomicInteger();
		BDDMockito.willAnswer((invocation) -> {
			return new OrganizationDetails(new Identifier(Integer.toString(orgIndex.incrementAndGet())), Status.INACTIVE, invocation.getArgument(0), null, null, invocation.getArgument(1));
		}).given(delegate).createOrganization(Mockito.anyString(), Mockito.anyList());
		BDDMockito.willAnswer((invocation) -> {
			return new OrganizationDetails(invocation.getArgument(0), Status.ACTIVE, "hello", null, null, List.of("ORG"));
		}).given(delegate).enableOrganization(Mockito.any(Identifier.class));
		BDDMockito.willAnswer((invocation) -> {
			return new OrganizationDetails(invocation.getArgument(0), Status.ACTIVE, "hello", null, null, List.of("ORG"));
		}).given(delegate).findOrganizationDetails(Mockito.any(Identifier.class));

		OrganizationDetails orgDetails = organizationService.createOrganization("hello", List.of("ORG"));

		OrganizationSummary orgSummary = organizationService.enableOrganization(orgDetails.getOrganizationId());
		Assert.assertEquals(orgSummary, organizationService.findOrganizationSummary(orgDetails.getOrganizationId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationSummary(Mockito.any(Identifier.class));

		/* details should have been evicted from the cache */
		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationDetails(Mockito.any(Identifier.class));
		orgDetails = organizationService.findOrganizationDetails(orgDetails.getOrganizationId());
		BDDMockito.verify(delegate, Mockito.times(1)).findOrganizationDetails(Mockito.any(Identifier.class));
		orgDetails = organizationService.findOrganizationDetails(orgDetails.getOrganizationId());
		BDDMockito.verify(delegate, Mockito.times(1)).findOrganizationDetails(Mockito.any(Identifier.class));
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
		Assert.assertEquals(orgDetails, organizationService.findOrganizationSummary(orgDetails.getOrganizationId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationDetails(Mockito.any(Identifier.class));
		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationSummary(Mockito.any(Identifier.class));
		
		/* clear cache, update name, and ensure cached */
		cacheManager.getCache("organizations").clear();
		orgDetails = organizationService.updateOrganizationDisplayName(orgDetails.getOrganizationId(), "Bye");
		Assert.assertEquals(orgDetails, organizationService.findOrganizationDetails(orgDetails.getOrganizationId()));
		Assert.assertEquals(orgDetails, organizationService.findOrganizationSummary(orgDetails.getOrganizationId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationDetails(Mockito.any(Identifier.class));
		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationSummary(Mockito.any(Identifier.class));
		
		/* clear cache, update main location, and ensure cached */
		cacheManager.getCache("organizations").clear();
		orgDetails = organizationService.updateOrganizationMainLocation(orgDetails.getOrganizationId(), new Identifier("LOC"));
		Assert.assertEquals(orgDetails, organizationService.findOrganizationDetails(orgDetails.getOrganizationId()));
		Assert.assertEquals(orgDetails, organizationService.findOrganizationSummary(orgDetails.getOrganizationId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationDetails(Mockito.any(Identifier.class));
		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationSummary(Mockito.any(Identifier.class));
		
		/* clear cache, update main contact, and ensure cached */
		cacheManager.getCache("organizations").clear();
		orgDetails = organizationService.updateOrganizationMainContact(orgDetails.getOrganizationId(), new Identifier("WOMAN"));
		Assert.assertEquals(orgDetails, organizationService.findOrganizationDetails(orgDetails.getOrganizationId()));
		Assert.assertEquals(orgDetails, organizationService.findOrganizationSummary(orgDetails.getOrganizationId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationDetails(Mockito.any(Identifier.class));
		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationSummary(Mockito.any(Identifier.class));
		
		/* clear cache, update groups, and ensure cached */
		cacheManager.getCache("organizations").clear();
		orgDetails = organizationService.updateOrganizationGroups(orgDetails.getOrganizationId(), List.of("ADM"));
		Assert.assertEquals(orgDetails, organizationService.findOrganizationDetails(orgDetails.getOrganizationId()));
		Assert.assertEquals(orgDetails, organizationService.findOrganizationSummary(orgDetails.getOrganizationId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationDetails(Mockito.any(Identifier.class));
		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationSummary(Mockito.any(Identifier.class));
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
			return 3l;
		}).given(delegate).countOrganizations(Mockito.any(OrganizationsFilter.class));
		
		
		organizationService.findOrganizationDetails(new OrganizationsFilter(), new Paging(1, 5, Sort.unsorted()));
		Assert.assertEquals(3l, organizationService.countOrganizations(new OrganizationsFilter()));
		
		Assert.assertEquals(details1, organizationService.findOrganizationDetails(details1.getOrganizationId()));
		Assert.assertEquals(details1, organizationService.findOrganizationSummary(details1.getOrganizationId()));
		
		Assert.assertEquals(details2, organizationService.findOrganizationDetails(details2.getOrganizationId()));
		Assert.assertEquals(details2, organizationService.findOrganizationSummary(details2.getOrganizationId()));
		
		Assert.assertEquals(details3, organizationService.findOrganizationDetails(details3.getOrganizationId()));
		Assert.assertEquals(details3, organizationService.findOrganizationSummary(details3.getOrganizationId()));
		
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
			return 3l;
		}).given(delegate).countOrganizations(Mockito.any(OrganizationsFilter.class));
		
		organizationService.findOrganizationSummaries(new OrganizationsFilter(), new Paging(1, 5, Sort.unsorted()));
		Assert.assertEquals(3l, organizationService.countOrganizations(new OrganizationsFilter()));
		
		Assert.assertEquals(details1, organizationService.findOrganizationSummary(details1.getOrganizationId()));
		
		Assert.assertEquals(details2, organizationService.findOrganizationSummary(details2.getOrganizationId()));
		
		Assert.assertEquals(details3, organizationService.findOrganizationSummary(details3.getOrganizationId()));
		
		BDDMockito.verify(delegate, Mockito.times(0)).findOrganizationSummary(Mockito.any(Identifier.class));
	}
}
