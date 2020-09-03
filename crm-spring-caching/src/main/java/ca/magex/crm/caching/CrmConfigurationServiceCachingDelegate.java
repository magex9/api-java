package ca.magex.crm.caching;

import java.io.OutputStream;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.services.CrmConfigurationService;
import ca.magex.crm.caching.util.CacheTemplate;
import ca.magex.crm.caching.util.CrmCacheKeyGenerator;

/**
 * Delegate that intercepts calls and caches the results
 * 
 * @author Jonny
 */
public class CrmConfigurationServiceCachingDelegate implements CrmConfigurationService {

	private CrmConfigurationService delegate;
	private CacheTemplate cacheTemplate;
	
	/**
	 * 
	 * @param delegate
	 * @param cacheTemplate
	 */
	public CrmConfigurationServiceCachingDelegate(CrmConfigurationService delegate, CacheTemplate cacheTemplate) {
		this.delegate = delegate;
		this.cacheTemplate = cacheTemplate;
	}
	
	/**
	 * Provides the list of pairs for caching location details
	 * @param details
	 * @return
	 */
	private List<Pair<String, Object>> initializedCacheSupplier(Boolean response, String key) {
		return List.of(Pair.of(CrmCacheKeyGenerator.getInstance().generateInitKey(key), response));
	}
	
	@Override
	public boolean isInitialized() {
		return cacheTemplate.get(
				() -> delegate.isInitialized(), 
				"system", 
				CrmCacheKeyGenerator.getInstance()::generateInitKey, 
				this::initializedCacheSupplier);
	}

	@Override
	public boolean initializeSystem(String organization, PersonName name, String email, String username, String password) {
		cacheTemplate.evict(CrmCacheKeyGenerator.getInstance().generateInitKey("system"));
		return delegate.initializeSystem(organization, name, email, username, password);
	}

	@Override
	public boolean reset() {
		cacheTemplate.evict(CrmCacheKeyGenerator.getInstance().generateInitKey("system"));
		return delegate.reset();
	}

	@Override
	public void dump(OutputStream os) {
		delegate.dump(os);
	}
}