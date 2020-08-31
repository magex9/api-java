package ca.magex.crm.caching.event;

import org.springframework.cache.CacheManager;

import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.crm.UserDetails;
import ca.magex.crm.api.crm.UserSummary;
import ca.magex.crm.api.event.CrmEventObserver;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OptionIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.system.id.UserIdentifier;
import ca.magex.crm.caching.config.CachingConfig;
import ca.magex.crm.caching.util.CacheTemplate;
import ca.magex.crm.caching.util.CrmCacheKeyGenerator;

/**
 * Update Observer used to evict an item from the cache if it changes
 * 
 * @author Jonny
 */
public class CrmCacheUpdateObserver implements CrmEventObserver {
		
	private CrmCacheKeyGenerator keyGenerator = CrmCacheKeyGenerator.getInstance();
	
	private CacheTemplate optionsCacheTemplate;
	private CacheTemplate organizationCacheTemplate;
	private CacheTemplate locationCacheTemplate;
	private CacheTemplate personCacheTemplate;
	private CacheTemplate userCacheTemplate;
	
	public CrmCacheUpdateObserver(CacheManager cacheManager) {
		optionsCacheTemplate = new CacheTemplate(cacheManager, CachingConfig.Caches.Options);
		organizationCacheTemplate = new CacheTemplate(cacheManager, CachingConfig.Caches.Organizations);
		locationCacheTemplate = new CacheTemplate(cacheManager, CachingConfig.Caches.Locations);
		personCacheTemplate = new CacheTemplate(cacheManager, CachingConfig.Caches.Persons);
		userCacheTemplate = new CacheTemplate(cacheManager, CachingConfig.Caches.Users);
	}
	
	@Override
	public CrmEventObserver optionUpdated(Long timestamp, OptionIdentifier optionId) {
		String key = keyGenerator.generateOptionKey(optionId);
		Option option = optionsCacheTemplate.getIfPresent(key);
		/* evict if cached and last modified has changed */
		if (option != null && !option.getLastModified().equals(timestamp)) {			
			optionsCacheTemplate.evict(key);
		}		
		return this;
	}
	
	@Override
	public CrmEventObserver organizationUpdated(Long timestamp, OrganizationIdentifier organizationId) {
		String summaryKey = keyGenerator.generateSummaryKey(organizationId);
		OrganizationSummary summary = organizationCacheTemplate.getIfPresent(summaryKey);
		/* evict if cached and last modified has changed */
		if (summary != null && !summary.getLastModified().equals(timestamp)) {
			organizationCacheTemplate.evict(summaryKey);
		}
		
		String detailsKey = keyGenerator.generateDetailsKey(organizationId);
		OrganizationDetails details = organizationCacheTemplate.getIfPresent(detailsKey);
		/* evict if cached and last modified has changed */
		if (details != null && !details.getLastModified().equals(timestamp)) {
			organizationCacheTemplate.evict(detailsKey);
		}		
		return this;
	}
	
	@Override
	public CrmEventObserver locationUpdated(Long timestamp, LocationIdentifier locationId) {
		String summaryKey = keyGenerator.generateSummaryKey(locationId);
		LocationSummary summary = locationCacheTemplate.getIfPresent(summaryKey);
		/* evict if cached and last modified has changed */
		if (summary != null && !summary.getLastModified().equals(timestamp)) {
			locationCacheTemplate.evict(summaryKey);
		}
		
		String detailsKey = keyGenerator.generateDetailsKey(locationId);
		LocationDetails details = locationCacheTemplate.getIfPresent(detailsKey);
		/* evict if cached and last modified has changed */
		if (details != null && !details.getLastModified().equals(timestamp)) {
			locationCacheTemplate.evict(detailsKey);
		}		
		return this;
	}
	
	@Override
	public CrmEventObserver personUpdated(Long timestamp, PersonIdentifier personId) {
		String summaryKey = keyGenerator.generateSummaryKey(personId);
		PersonSummary summary = personCacheTemplate.getIfPresent(summaryKey);
		/* evict if cached and last modified has changed */
		if (summary != null && !summary.getLastModified().equals(timestamp)) {
			personCacheTemplate.evict(summaryKey);
		}
		
		String detailsKey = keyGenerator.generateDetailsKey(personId);
		PersonDetails details = personCacheTemplate.getIfPresent(detailsKey);
		/* evict if cached and last modified has changed */
		if (details != null && !details.getLastModified().equals(timestamp)) {
			personCacheTemplate.evict(detailsKey);
		}		
		return this;
	}
	
	@Override
	public CrmEventObserver userUpdated(Long timestamp, UserIdentifier userId, String username) {
		String summaryKey = keyGenerator.generateSummaryKey(userId);
		UserSummary summary = userCacheTemplate.getIfPresent(summaryKey);
		/* evict if cached and last modified has changed */
		if (summary != null && !summary.getLastModified().equals(timestamp)) {
			userCacheTemplate.evict(summaryKey);
		}
		
		summaryKey = keyGenerator.generateUsernameSummaryKey(username);
		summary = userCacheTemplate.getIfPresent(summaryKey);
		/* evict if cached and last modified has changed */
		if (summary != null && !summary.getLastModified().equals(timestamp)) {
			userCacheTemplate.evict(summaryKey);
		}
		
		
		String detailsKey = keyGenerator.generateDetailsKey(userId);
		UserDetails details = userCacheTemplate.getIfPresent(detailsKey);
		/* evict if cached and last modified has changed */
		if (details != null && !details.getLastModified().equals(timestamp)) {
			userCacheTemplate.evict(detailsKey);
		}		
		
		detailsKey = keyGenerator.generateUsernameDetailsKey(username);
		details = userCacheTemplate.getIfPresent(detailsKey);
		/* evict if cached and last modified has changed */
		if (details != null && !details.getLastModified().equals(timestamp)) {
			userCacheTemplate.evict(detailsKey);
		}		
		
		return this;
	}
}