package ca.magex.crm.caching.util;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;

public class CacheTemplate {

	private CacheManager cacheManager;
	private String cacheName;
	
	/**
	 * Constructs a new cache template with the given cache manager and cache name
	 * @param cacheManager
	 * @param cacheName
	 */
	public CacheTemplate(CacheManager cacheManager, String cacheName) {
		this.cacheManager = cacheManager;
		this.cacheName = cacheName;
	}
	
	/**
	 * returns the backing cache manager
	 * @return
	 */
	public CacheManager getCacheManager() {
		return cacheManager;
	}
	
	/**
	 * returns the cache name being interacted with
	 * @return
	 */
	public String getCacheName() {
		return cacheName;
	}

	/**
	 * puts the pairs into the cache regardless of whether they already exists in the cache
	 * @param cachingPairs
	 * @return
	 */
	public void put(List<Pair<String, Object>> cachingPairs) {
		Cache cache = cacheManager.getCache(cacheName);
		for (Pair<String, Object> cachePair : cachingPairs) {
			cache.put(cachePair.getKey(), cachePair.getValue());
		}
	}
	
	/**
	 * puts the pairs into the cache only if it is already absent
	 * @param cachingPairs
	 * @return
	 */
	public void putIfAbsent(List<Pair<String, Object>> cachingPairs) {
		Cache cache = cacheManager.getCache(cacheName);
		for (Pair<String, Object> cachePair : cachingPairs) {
			cache.putIfAbsent(cachePair.getKey(), cachePair.getValue());
		}
	}

	/**
	 * evicts all of the keys from the given cache
	 * @param keys
	 */
	public void evict(String... keys) {
		Cache cache = cacheManager.getCache(cacheName);
		for (String key : keys) {
			cache.evictIfPresent(key);
		}
	}

	/**
	 * delays the execution of the supplier and key generator until after a cache miss has occurred
	 * @param <R>
	 * @param <I>
	 * @param supplier
	 * @param cacheKey
	 * @param keyGenerator
	 * @param cachingPairsGenerator
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <R, I> R get(
			Supplier<R> supplier, 
			I key, 
			Function<I, String> keyGenerator,
			BiFunction<R, I, List<Pair<String, Object>>> cachingPairsGenerator) {
		Cache cache = cacheManager.getCache(cacheName);
		String cacheKey = keyGenerator.apply(key);
		Object cachedValue = cache.get(cacheKey);
		if (cachedValue instanceof ValueWrapper) {
			return (R) ((ValueWrapper)cachedValue).get();
		}
		R value = supplier.get();
		List<Pair<String, Object>> cachingPairs = cachingPairsGenerator.apply(value, key);
		for (Pair<String, Object> cachePair : cachingPairs) {
			cache.put(cachePair.getKey(), cachePair.getValue());
		}		
		return value;
	}
}