package ca.magex.crm.caching.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
		"ca.magex.crm.caching"
		})
public class CachingTestConfig {

	@Autowired CacheManager cacheManager;
	
}
