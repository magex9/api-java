package ca.magex.crm.caching;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import ca.magex.crm.api.filters.GroupsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.services.CrmGroupService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.caching.util.CacheTemplate;
import ca.magex.crm.caching.util.CrmCacheKeyGenerator;

/**
 * Delegate that intercepts calls and caches the results
 * 
 * @author Jonny
 */
public class CrmGroupServiceCachingDelegate implements CrmGroupService {

	private CrmGroupService delegate;
	private CacheTemplate cacheTemplate;
	
	/**
	 * Wraps the delegate service using the given cacheManager
	 * 
	 * @param delegate
	 * @param cacheTemplate
	 */
	public CrmGroupServiceCachingDelegate(CrmGroupService delegate, CacheTemplate cacheTemplate) {
		this.delegate = delegate;
		this.cacheTemplate = cacheTemplate;
	}
	
	/**
	 * Provides the list of pairs for caching group details
	 * @param details
	 * @return
	 */
	private List<Pair<String, Object>> groupCacheSupplier(Group group, Identifier key) {
		if (group == null) {
			return List.of(
					Pair.of(CrmCacheKeyGenerator.generateDetailsKey(key), group));
		} else {
			return List.of(
					Pair.of(CrmCacheKeyGenerator.generateDetailsKey(key), group),
					Pair.of(CrmCacheKeyGenerator.generateCodeKey(group.getCode()), group));
		}
	}
	
	/**
	 * Provides the list of pairs for caching group details
	 * @param details
	 * @return
	 */
	private List<Pair<String, Object>> groupCacheSupplier(Group group, String code) {
		if (group == null) {
			return List.of(
					Pair.of(CrmCacheKeyGenerator.generateCodeKey(code), group));
		} else {
			return List.of(
					Pair.of(CrmCacheKeyGenerator.generateDetailsKey(group.getGroupId()), group),
					Pair.of(CrmCacheKeyGenerator.generateCodeKey(code), group));
		}
	}
		
	@Override
	public Group createGroup(Localized name) {
		Group group = delegate.createGroup(name);
		cacheTemplate.put(groupCacheSupplier(group, group.getGroupId()));
		return group;
	}
	
	@Override
	public Group createGroup(Group prototype) {
		Group group = delegate.createGroup(prototype);
		cacheTemplate.put(groupCacheSupplier(group, group.getGroupId()));
		return group;
	}
	
	@Override
	public Group enableGroup(Identifier groupId) {
		Group group = delegate.enableGroup(groupId);
		cacheTemplate.put(groupCacheSupplier(group, groupId));
		return group;
	}

	@Override
	public Group disableGroup(Identifier groupId) {
		Group group = delegate.disableGroup(groupId);
		cacheTemplate.put(groupCacheSupplier(group, groupId));
		return group;
	}
	
	@Override
	public Group updateGroupName(Identifier groupId, Localized name) {
		Group group = delegate.updateGroupName(groupId, name);
		cacheTemplate.put(groupCacheSupplier(group, groupId));
		return group;
	}
	
	@Override
	public Group findGroup(Identifier groupId) {
		return cacheTemplate.get(
				() -> delegate.findGroup(groupId),
				groupId,
				CrmCacheKeyGenerator::generateDetailsKey,
				this::groupCacheSupplier);
	}
	
	@Override
	public Group findGroupByCode(String code) {
		return cacheTemplate.get(
				() -> delegate.findGroupByCode(code),
				code,
				CrmCacheKeyGenerator::generateCodeKey,
				this::groupCacheSupplier);
	}

	@Override
	public FilteredPage<Group> findGroups(GroupsFilter filter, Paging paging) {
		FilteredPage<Group> page = delegate.findGroups(filter, paging);
		page.forEach((group) -> {
			cacheTemplate.putIfAbsent(groupCacheSupplier(group, group.getGroupId()));
		});
		return page;
	}
	
	@Override
	public FilteredPage<Group> findGroups(GroupsFilter filter) {
		FilteredPage<Group> page = delegate.findGroups(filter);
		page.forEach((details) -> {
			cacheTemplate.putIfAbsent(groupCacheSupplier(details, details.getGroupId()));
		});
		return page;
	}
}