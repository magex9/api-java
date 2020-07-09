package ca.magex.crm.caching.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.transaction.TransactionAwareCacheManagerProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CachingConfig {
	
	public static interface Caches {
		public static final String Organizations 	= "organizations";
		public static final String Locations 		= "locations";
		public static final String Persons 			= "persons";
		public static final String Users			= "users";
		public static final String Options 			= "options";
	}

	@Bean
    public CacheManager cacheManager() {		
		return new TransactionAwareCacheManagerProxy(
				// TOOD switch to Caffeine Cache Manager
				new ConcurrentMapCacheManager(
						Caches.Organizations, 
						Caches.Locations,
						Caches.Persons,
						Caches.Users,
						Caches.Options));
    }	
}
