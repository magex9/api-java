package ca.magex.crm.caching.config;

import java.util.concurrent.TimeUnit;

import javax.transaction.TransactionManager;

import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.transaction.TransactionAwareCacheManagerProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

import com.arjuna.ats.internal.jta.transaction.arjunacore.TransactionManagerImple;
import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
public class CachingTestConfig {

	@Bean
	@Primary
	public PlatformTransactionManager jtaTransactionManager() {
		JtaTransactionManager tm = new JtaTransactionManager();
		tm.setTransactionManager(transactionManager());
		return tm;
	}

	@Bean
	public TransactionManager transactionManager() {
		TransactionManager tm = new TransactionManagerImple();
		return tm;
	}

	@Bean
	public CacheManager cacheManager() {

		CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
		caffeineCacheManager.setCaffeine(Caffeine
				.newBuilder()
				.expireAfterWrite(5, TimeUnit.MINUTES)
				.expireAfterAccess(10, TimeUnit.MINUTES)
				.maximumSize(1000L));

		return new TransactionAwareCacheManagerProxy(caffeineCacheManager);

		//						// TOOD switch to Caffeine Cache Manager
		//						new ConcurrentMapCacheManager(
		//								Caches.Organizations, 
		//								Caches.Locations,
		//								Caches.Persons,
		//								Caches.Users,
		//								Caches.Options));
	}
}
