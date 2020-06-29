package ca.magex.crm.caching;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.tuple.Pair;

import ca.magex.crm.api.filters.OptionsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.services.CrmOptionService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.caching.util.CacheTemplate;
import ca.magex.crm.caching.util.CrmCacheKeyGenerator;

/**
 * Delegate that intercepts calls and caches the results
 * 
 * @author Jonny
 */
public class CrmOptionServiceCachingDelegate implements CrmOptionService {

	private CrmOptionService delegate;
	private CacheTemplate cacheTemplate;
	
	/**
	 * Wraps the delegate service using the given cacheManager
	 * 
	 * @param delegate
	 * @param cacheTemplate
	 */
	public CrmOptionServiceCachingDelegate(CrmOptionService delegate, CacheTemplate cacheTemplate) {
		this.delegate = delegate;
		this.cacheTemplate = cacheTemplate;
	}
	
	/**
	 * Provides the list of pairs for caching group details
	 * @param option
	 * @param key
	 * @return
	 */
	private List<Pair<String, Object>> optionCacheSupplier(Option option, Identifier key) {
		if (option == null) {
			return List.of(
					Pair.of(CrmCacheKeyGenerator.generateDetailsKey(key), option));
		} else {
			return List.of(
					Pair.of(CrmCacheKeyGenerator.generateDetailsKey(key), option),
					Pair.of(CrmCacheKeyGenerator.generateCodeKey(option.getCode()), option));
		}
	}

	/**
	 * Provides the list of pairs for caching group details
	 * @param details
	 * @return
	 */
	private List<Pair<String, Object>> optionCacheSupplier(Option option, String code) {
		if (option == null) {
			return List.of(
					Pair.of(CrmCacheKeyGenerator.generateCodeKey(code), option));
		} else {
			return List.of(
					Pair.of(CrmCacheKeyGenerator.generateDetailsKey(option.getOptionId()), option),
					Pair.of(CrmCacheKeyGenerator.generateCodeKey(code), option));
		}
	}

	@Override
	public Option createOption(Identifier lookupId, Localized name) {
		Option option = delegate.createOption(lookupId, name);
		cacheTemplate.put(optionCacheSupplier(option, option.getOptionId()));
		return option;
	}
	
	@Override
	public Option createOption(Option prototype) {
		Option option = delegate.createOption(prototype);
		cacheTemplate.put(optionCacheSupplier(option, option.getOptionId()));
		return option;
	}
	
	@Override
	public Option enableOption(Identifier optionId) {
		Option option = delegate.enableOption(optionId);
		cacheTemplate.put(optionCacheSupplier(option, option.getOptionId()));
		return option;
	}

	@Override
	public Option disableOption(Identifier optionId) {
		Option option = delegate.disableOption(optionId);
		cacheTemplate.put(optionCacheSupplier(option, option.getOptionId()));
		return option;
	}
	
	@Override
	public Option updateOptionName(Identifier optionId, Localized name) {
		Option option = delegate.updateOptionName(optionId, name);
		cacheTemplate.put(optionCacheSupplier(option, option.getOptionId()));
		return option;
	}

	@Override
	public Option findOption(Identifier optionId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Option findOptionByCode(Identifier lookupId, String optionCode) {
		// TODO Auto-generated method stub
		return CrmOptionService.super.findOptionByCode(lookupId, optionCode);
	}
	
	@Override
	public Option findOptionByLocalizedName(Identifier lookupId, Locale locale, String name) {
		// TODO Auto-generated method stub
		return CrmOptionService.super.findOptionByLocalizedName(lookupId, locale, name);
	}
	

	@Override
	public Option findOption(String lookupCode, String optionCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FilteredPage<Option> findOptions(OptionsFilter filter, Paging paging) {
		// TODO Auto-generated method stub
		return null;
	}

}
