package ca.magex.crm.caching;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.services.CrmGroupService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.caching.config.CachingConfig;
import ca.magex.crm.caching.config.CachingTestConfig;
import ca.magex.crm.caching.util.CacheTemplate;
import ca.magex.crm.test.CrmAsserts;
import ca.magex.crm.test.config.MockTestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CachingTestConfig.class, MockTestConfig.class })
public class CrmGroupServiceCachingDelegateTests {

	@Autowired private CrmGroupService delegate;
	@Autowired private CacheManager cacheManager;
	
	private CacheTemplate cacheTemplate;
	private CrmGroupServiceCachingDelegate groupService;

	@Before
	public void reset() {
		Mockito.reset(delegate);
		/* clear our caches */
		cacheManager.getCacheNames().forEach((cacheName) -> {
			cacheManager.getCache(cacheName).clear();
		});
		cacheTemplate = new CacheTemplate(cacheManager, CachingConfig.Caches.Locations);
		groupService = new CrmGroupServiceCachingDelegate(delegate, cacheTemplate);
	}
	
	@Test
	public void testCacheNewLoc() {
		final AtomicInteger locIndex = new AtomicInteger();
		BDDMockito.willAnswer((invocation) -> {
			return new Group(new Identifier(Integer.toString(locIndex.getAndIncrement())), Status.ACTIVE, invocation.getArgument(0));
		}).given(delegate).createGroup(Mockito.any(Localized.class));
		Group group = groupService.createGroup(new Localized("CMS", "CMS-EN", "CMS-FR"));
		BDDMockito.verify(delegate, Mockito.times(1)).createGroup(Mockito.any(Localized.class));

//		/* should have added the details to the cache */
//		Assert.assertEquals(locDetails, locationService.findLocationDetails(locDetails.getLocationId()));
//		BDDMockito.verify(delegate, Mockito.times(0)).findLocationDetails(Mockito.any(Identifier.class));
//
//		/* should have added the summary to the cache */
//		Assert.assertEquals(locDetails.asSummary(), locationService.findLocationSummary(locDetails.getLocationId()));
//		BDDMockito.verify(delegate, Mockito.times(0)).findLocationSummary(Mockito.any(Identifier.class));
	}
}
