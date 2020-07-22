package ca.magex.crm.caching;

import org.springframework.cache.CacheManager;

import ca.magex.crm.api.adapters.CrmServicesAdapter;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.caching.config.CachingConfig;
import ca.magex.crm.caching.util.CacheTemplate;

/**
 * Implementation of the Crm Services Adapter that uses a Caching Delegate
 * 
 * @author Jonny
 */
public class CrmCachingServices extends CrmServicesAdapter implements CrmServices {

	/**
	 * Creates our Caching Services Layer
	 * @param cacheManager
	 * @param services
	 */
	public CrmCachingServices(CacheManager cacheManager, CrmServices services) {
		super(
				services, 
				new CrmOptionServiceCachingDelegate(services, new CacheTemplate(cacheManager, CachingConfig.Caches.Options)),
				new CrmOrganizationServiceCachingDelegate(services, new CacheTemplate(cacheManager, CachingConfig.Caches.Organizations)),
				new CrmLocationServiceCachingDelegate(services, new CacheTemplate(cacheManager, CachingConfig.Caches.Locations)),
				new CrmPersonServiceCachingDelegate(services, new CacheTemplate(cacheManager, CachingConfig.Caches.Persons)),
				new CrmUserServiceCachingDelegate(services, new CacheTemplate(cacheManager, CachingConfig.Caches.Users))
		);
	}
}
