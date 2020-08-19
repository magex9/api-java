package ca.magex.crm.caching.event;

import org.springframework.cache.CacheManager;

import ca.magex.crm.api.crm.OrganizationSummary;
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
	
	public CrmCacheUpdateObserver(CacheManager cacheManager) {
		optionsCacheTemplate = new CacheTemplate(cacheManager, CachingConfig.Caches.Options);
		organizationCacheTemplate = new CacheTemplate(cacheManager, CachingConfig.Caches.Organizations);
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
		OrganizationSummary details = organizationCacheTemplate.getIfPresent(detailsKey);
		/* evict if cached and last modified has changed */
		if (details != null && !details.getLastModified().equals(timestamp)) {
			organizationCacheTemplate.evict(detailsKey);
		}		
		return this;
	}
	
	@Override
	public CrmEventObserver locationUpdated(Long timestamp, LocationIdentifier locationId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	@Override
	public CrmEventObserver personUpdated(Long timestamp, PersonIdentifier personId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public CrmEventObserver userUpdated(Long timestamp, UserIdentifier userId) {
		// TODO Auto-generated method stub
		return null;
	}
}