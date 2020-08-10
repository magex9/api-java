package ca.magex.crm.springboot.config.crm;

import java.util.concurrent.TimeUnit;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.transaction.TransactionAwareCacheManagerProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.github.benmanes.caffeine.cache.Caffeine;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.CrmProfiles;
import ca.magex.crm.api.authentication.basic.BasicPasswordService;
import ca.magex.crm.api.config.CrmConfigurer;
import ca.magex.crm.api.observer.basic.BasicUpdateObserver;
import ca.magex.crm.api.policies.authenticated.AuthenticatedPolicies;
import ca.magex.crm.api.repositories.basic.BasicPasswordRepository;
import ca.magex.crm.api.repositories.basic.BasicRepositories;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.services.basic.BasicConfigurationService;
import ca.magex.crm.api.services.basic.BasicServices;
import ca.magex.crm.api.store.basic.BasicPasswordStore;
import ca.magex.crm.api.store.basic.BasicStore;
import ca.magex.crm.caching.CrmCachingServices;
import ca.magex.crm.spring.security.auth.SpringSecurityAuthenticationService;
import ca.magex.crm.transform.json.JsonTransformerFactory;

@Configuration
@Profile(CrmProfiles.BASIC)
@Description("Configures the CRM by adding caching support, and using the Authenticated Policies for CRM Processing")
public class BasicCrmConfig implements CrmConfigurer {
	
	@Value("${crm.caching.services.enabled:false}") private Boolean enableCachedServices;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean 
	public JsonTransformerFactory jsonTransformerFactory() {
		return new JsonTransformerFactory(services());
	}
	
	@Bean
	public BasicStore store() {
		return new BasicStore();
	}

	@Bean
	public BasicPasswordStore passwordStore() {
		return new BasicPasswordStore();
	}

	@Bean
	public BasicUpdateObserver observer() {
		return new BasicUpdateObserver();
	}

	@Bean
	public BasicRepositories repos() {
		return new BasicRepositories(store(), observer());
	}

	@Bean
	public BasicPasswordRepository passwordRepo() {
		return new BasicPasswordRepository(passwordStore());
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
	}

	@Bean(autowireCandidate = false) // ensure this bean doesn't conflict with our CRM for autowiring
	public CrmServices services() {
		CrmServices services = new BasicServices(repos(), passwords()); 
		if (enableCachedServices) {
			LoggerFactory.getLogger(BasicCrmConfig.class).info("CRM Caching Services Enabled");
			// clear caches
			cacheManager().getCacheNames().forEach((cache) -> cacheManager().getCache(cache).clear());
			return new CrmCachingServices(cacheManager(), services);
		}
		else {
			LoggerFactory.getLogger(getClass()).info("CRM Caching Services Disabled");
			return services;
		}
	}

	@Bean
	public AuthenticatedPolicies policies() {
		return new AuthenticatedPolicies(auth(), services());
	}

	@Bean
	public SpringSecurityAuthenticationService auth() {
		return new SpringSecurityAuthenticationService(services());
	}

	@Bean
	public BasicPasswordService passwords() {
		return new BasicPasswordService(repos(), passwordRepo(), passwordEncoder());
	}
	
	@Bean 
	public BasicConfigurationService config() {
		return new BasicConfigurationService(repos(), passwords());
	}

	@Bean
	@Override
	public Crm crm() {
		return new Crm(services(), policies());
	}
}