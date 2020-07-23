package ca.magex.crm.caching.util;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;

public class CacheTemplate {

	private Logger logger = LoggerFactory.getLogger("ca.magex.crm.caching");
			
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
	 * puts the pairs into the cache regardless of whether they already exists in the cache
	 * @param cachingPairs
	 * @return
	 */
	public void put(List<Pair<String, Object>> cachingPairs) {
		Cache cache = cacheManager.getCache(cacheName);
		for (Pair<String, Object> cachePair : cachingPairs) {
			logger.debug("put[" + cachePair.getKey() + "] - " + cachePair.getValue());
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
			logger.debug("putIfAbsent[" + cachePair.getKey() + "] - " + cachePair.getValue());
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
			logger.debug("evict[" + key + "]");
			cache.evictIfPresent(key);
		}
	}
	
	/**
	 * returns the cached value for the given key if it's present in the cache
	 * @param <R>
	 * @param cacheKey
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <R> R getIfPresent(String cacheKey) {
		Cache cache = cacheManager.getCache(cacheName);
		Object cachedValue = cache.get(cacheKey);
		if (cachedValue instanceof ValueWrapper) {
			ValueWrapper valueWrapper = (ValueWrapper)cachedValue;
			logger.debug("get[" + cacheKey + "]::cacheHit - " + valueWrapper.get());
			return (R) valueWrapper.get();
		}
		return null;
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
			ValueWrapper valueWrapper = (ValueWrapper)cachedValue;
			logger.debug("get[" + cacheKey + "]::cacheHit - " + valueWrapper.get());
			return (R) valueWrapper.get();
		}		
		logger.debug("get[" + cacheKey + "]::cacheMiss");
		R value = supplier.get();
		List<Pair<String, Object>> cachingPairs = cachingPairsGenerator.apply(value, key);
		for (Pair<String, Object> cachePair : cachingPairs) {
			logger.debug("put[" + cachePair.getKey() + "] - " + cachePair.getValue());
			cache.put(cachePair.getKey(), cachePair.getValue());
		}		
		return value;
	}
}