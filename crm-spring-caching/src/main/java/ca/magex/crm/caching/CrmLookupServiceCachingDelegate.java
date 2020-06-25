package ca.magex.crm.caching;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.tuple.Pair;

import ca.magex.crm.api.filters.LookupsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Lookup;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.caching.util.CacheTemplate;
import ca.magex.crm.caching.util.CrmCacheKeyGenerator;

/**
 * Delegate that intercepts calls and caches the results
 * 
 * @author Jonny
 */
public class CrmLookupServiceCachingDelegate implements CrmLookupService {

	private CrmLookupService delegate;
	private CacheTemplate cacheTemplate;
	
	/**
	 * Wraps the delegate service using the given cacheManager
	 * @param delegate
	 * @param cacheTemplate
	 */
	public CrmLookupServiceCachingDelegate(CrmLookupService delegate, CacheTemplate cacheTemplate) {
		this.delegate = delegate;
		this.cacheTemplate = cacheTemplate;
	}
	
	/**
	 * Provides the list of pairs for caching group details
	 * @param lookup
	 * @param key
	 * @return
	 */
	private List<Pair<String, Object>> lookupCacheSupplier(Lookup lookup, Identifier key) {
		if (lookup == null) {
			return List.of(
					Pair.of(CrmCacheKeyGenerator.generateDetailsKey(key), lookup));
		} else if (lookup.getParent() == null) {
			return List.of(
					Pair.of(CrmCacheKeyGenerator.generateDetailsKey(key), lookup),
					Pair.of(CrmCacheKeyGenerator.generateCodeKey(lookup.getCode()), lookup),
					Pair.of(CrmCacheKeyGenerator.generateLocalizedNameKey(Lang.ENGLISH + "::" + lookup.getName(Lang.ENGLISH)), lookup),
					Pair.of(CrmCacheKeyGenerator.generateLocalizedNameKey(Lang.FRENCH + "::" + lookup.getName(Lang.FRENCH)), lookup));
		} else {
			return List.of(
					Pair.of(CrmCacheKeyGenerator.generateDetailsKey(key), lookup),
					Pair.of(CrmCacheKeyGenerator.generateCodeKey(lookup.getCode() + "::" + lookup.getParent().getCode()), lookup),
					Pair.of(CrmCacheKeyGenerator.generateLocalizedNameKey(Lang.ENGLISH + "::" + lookup.getName(Lang.ENGLISH) + "::" + lookup.getParent().getCode()), lookup),
					Pair.of(CrmCacheKeyGenerator.generateLocalizedNameKey(Lang.FRENCH + "::" + lookup.getName(Lang.FRENCH) + "::" + lookup.getParent().getCode()), lookup));
		}
	}
	
	/**
	 * Provides the list of pairs for caching group details
	 * @param lookup
	 * @param code
	 * @return
	 */
	private List<Pair<String, Object>> lookupCacheSupplier(Lookup lookup, String code) {
		if (lookup == null) {
			return List.of(
					Pair.of(CrmCacheKeyGenerator.generateCodeKey(code), lookup));
		} else {
			return List.of(
					Pair.of(CrmCacheKeyGenerator.generateDetailsKey(lookup.getLookupId()), lookup),
					Pair.of(CrmCacheKeyGenerator.generateCodeKey(code), lookup));
		}
	}
	
	@Override
	public Lookup createLookup(Localized name, Option parent) {
		Lookup lookup = delegate.createLookup(name, parent);
		cacheTemplate.put(lookupCacheSupplier(lookup, lookup.getLookupId()));
		return lookup;
	}

	@Override
	public Lookup createLookup(Lookup prototype) {
		Lookup lookup = delegate.createLookup(prototype);
		cacheTemplate.put(lookupCacheSupplier(lookup, lookup.getLookupId()));
		return lookup;
	}

	@Override
	public Lookup enableLookup(Identifier lookupId) {
		Lookup lookup = delegate.enableLookup(lookupId);
		cacheTemplate.put(lookupCacheSupplier(lookup, lookupId));
		return lookup;
	}

	@Override
	public Lookup disableLookup(Identifier lookupId) {
		Lookup lookup = delegate.disableLookup(lookupId);
		cacheTemplate.put(lookupCacheSupplier(lookup, lookupId));
		return lookup;
	}
	
	@Override
	public Lookup updateLookupName(Identifier lookupId, Localized name) {
		Lookup lookup = delegate.updateLookupName(lookupId, name);
		cacheTemplate.put(lookupCacheSupplier(lookup, lookupId));
		return lookup;
	}
	
	@Override
	public Lookup findLookup(Identifier lookupId) {
		return cacheTemplate.get(
				() -> delegate.findLookup(lookupId),
				lookupId,
				CrmCacheKeyGenerator::generateDetailsKey,
				this::lookupCacheSupplier);
	}
	
	@Override
	public Lookup findLookupByCode(String lookupCode) {
		return cacheTemplate.get(
				() -> delegate.findLookupByCode(lookupCode),
				lookupCode,
				CrmCacheKeyGenerator::generateCodeKey,
				this::lookupCacheSupplier);
	}
	
	@Override
	public Lookup findLookupByCode(String lookupCode, String parentCode) {
		return cacheTemplate.get(
				() -> delegate.findLookupByCode(lookupCode, parentCode),
				lookupCode + "::" + parentCode,
				CrmCacheKeyGenerator::generateCodeKey,
				this::lookupCacheSupplier);
	}
	
	@Override
	public Lookup findLookupByLocalizedName(Locale locale, String name) {
		return cacheTemplate.get(
				() -> delegate.findLookupByLocalizedName(locale, name),
				locale + "::" + name,
				CrmCacheKeyGenerator::generateLocalizedNameKey,
				this::lookupCacheSupplier);
	}
	
	@Override
	public Lookup findLookupByLocalizedName(Locale locale, String name, String parentCode) {
		return cacheTemplate.get(
				() -> delegate.findLookupByLocalizedName(locale, name),
				locale + "::" + name + "::" + parentCode,
				CrmCacheKeyGenerator::generateLocalizedNameKey,
				this::lookupCacheSupplier);
	}
	
	@Override
	public Lookup findLookupByTypeWithParent(String lookupCode, Option parent) {
		return cacheTemplate.get(
				() -> delegate.findLookupByTypeWithParent(lookupCode, parent),
				lookupCode + "::" + parent.getCode(),
				CrmCacheKeyGenerator::generateCodeKey,
				this::lookupCacheSupplier);
	}

	@Override
	public FilteredPage<Lookup> findLookups(LookupsFilter filter, Paging paging) {
		FilteredPage<Lookup> page = delegate.findLookups(filter, paging);
		page.forEach((lookup) -> {
			cacheTemplate.putIfAbsent(lookupCacheSupplier(lookup, lookup.getLookupId()));
		});
		return page;
	}
	
	@Override
	public FilteredPage<Lookup> findLookups(LookupsFilter filter) {
		FilteredPage<Lookup> page = delegate.findLookups(filter);
		page.forEach((lookup) -> {
			cacheTemplate.putIfAbsent(lookupCacheSupplier(lookup, lookup.getLookupId()));
		});
		return page;
	}
}