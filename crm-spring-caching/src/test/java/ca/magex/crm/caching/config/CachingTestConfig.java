package ca.magex.crm.caching.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.transaction.TransactionAwareCacheManagerProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ca.magex.crm.caching.config.CachingConfig.Caches;

@Configuration
public class CachingTestConfig {
	
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
