package ca.magex.crm.caching.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.transaction.TransactionAwareCacheManagerProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CachingConfig {

	@Bean
    public CacheManager cacheManager() {
		ConcurrentMapCacheManager cm = new ConcurrentMapCacheManager("organizations", "locations");
		return new TransactionAwareCacheManagerProxy(cm);
    }	
}
