package ca.magex.crm.caching;

import java.util.List;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import ca.magex.crm.api.filters.GroupsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.RolesFilter;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.services.CrmPermissionService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.caching.config.CachingConfig;

@Service("CrmPermissionServiceCachingDelegate")
public class CrmPermissionServiceCachingDelegate implements CrmPermissionService {

	private CrmPermissionService delegate;
	private CacheManager cacheManager;
	
	public CrmPermissionServiceCachingDelegate(CrmPermissionService delegate, CacheManager cacheManager) {
		this.delegate = delegate;
		this.cacheManager = cacheManager;
	}
	
	@Override
	@Caching(put = {
			@CachePut(cacheNames = CachingConfig.Caches.Groups, key = "'Id_'.concat(#result == null ? '' : #result.groupId)", unless = "#result == null"),
			@CachePut(cacheNames = CachingConfig.Caches.Groups, key = "'Code_'.concat(#result == null ? '' : #result.getCode())", unless = "#result == null")
	})
	public Group createGroup(Localized name) {
		return delegate.createGroup(name);
	}
	
	@Override
	@Caching(put = {
			@CachePut(cacheNames = CachingConfig.Caches.Groups, key = "'Id_'.concat(#result == null ? '' : #result.groupId)", unless = "#result == null"),
			@CachePut(cacheNames = CachingConfig.Caches.Groups, key = "'Code_'.concat(#result == null ? '' : #result.getCode())", unless = "#result == null")
	})
	public Group createGroup(Group group) {
		return delegate.createGroup(group);
	}
	
	@Override
	@Caching(put = {
			@CachePut(cacheNames = CachingConfig.Caches.Groups, key = "'Id_'.concat(#groupId)"),
			@CachePut(cacheNames = CachingConfig.Caches.Groups, key = "'Code_'.concat(#result == null ? '' : #result.getCode())", unless = "#result == null")
	})
	public Group updateGroupName(Identifier groupId, Localized name) {
		return delegate.updateGroupName(groupId, name);
	}

	@Override
	@Caching(put = {
			@CachePut(cacheNames = CachingConfig.Caches.Groups, key = "'Id_'.concat(#groupId)"),
			@CachePut(cacheNames = CachingConfig.Caches.Groups, key = "'Code_'.concat(#result == null ? '' : #result.getCode())", unless = "#result == null")
	})
	public Group enableGroup(Identifier groupId) {
		return delegate.enableGroup(groupId);
	}

	@Override
	@Caching(put = {
			@CachePut(cacheNames = CachingConfig.Caches.Groups, key = "'Id_'.concat(#groupId)"),
			@CachePut(cacheNames = CachingConfig.Caches.Groups, key = "'Code_'.concat(#result == null ? '' : #result.getCode())", unless = "#result == null")
	})
	public Group disableGroup(Identifier groupId) {
		return delegate.disableGroup(groupId);
	}

	@Override
	@Cacheable(cacheNames = CachingConfig.Caches.Groups, key = "'Id_'.concat(#groupId)")
	public Group findGroup(Identifier groupId) {
		Group group = delegate.findGroup(groupId);
		if (group != null) {
			cacheManager.getCache(CachingConfig.Caches.Groups).putIfAbsent("Code_" + group.getCode(), group);
		}
		return group;
	}
	
	@Override
	@Cacheable(cacheNames = CachingConfig.Caches.Groups, key = "'Code_'.concat(#code)")
	public Group findGroupByCode(String code) {	
		Group group = delegate.findGroupByCode(code);
		if (group != null) {
			cacheManager.getCache(CachingConfig.Caches.Groups).putIfAbsent("Id_" + group.getGroupId(), group);
		}
		return group;
	}

	@Override
	public FilteredPage<Group> findGroups(GroupsFilter filter, Paging paging) {
		FilteredPage<Group> page = delegate.findGroups(filter, paging);
		Cache groupsCache = cacheManager.getCache(CachingConfig.Caches.Groups);
		page.forEach((group) -> {
			groupsCache.putIfAbsent("Id_" + group.getGroupId(), group);
			groupsCache.putIfAbsent("Code_" + group.getCode(), group);
		});
		return page;
	}
	
	@Override
	public FilteredPage<Group> findGroups(GroupsFilter filter) {
		FilteredPage<Group> page = delegate.findGroups(filter);
		Cache groupsCache = cacheManager.getCache(CachingConfig.Caches.Groups);
		page.forEach((group) -> {
			groupsCache.putIfAbsent("Id_" + group.getGroupId(), group);
			groupsCache.putIfAbsent("Code_" + group.getCode(), group);
		});
		return page;
	}
	
	@Override
	public List<String> findActiveGroupCodes() {
		return delegate.findActiveGroupCodes();
	}

	@Override
	@Caching(put = {
			@CachePut(cacheNames = CachingConfig.Caches.Roles, key = "'Id_'.concat(#result == null ? '' : #result.roleId)", unless = "#result == null"),
			@CachePut(cacheNames = CachingConfig.Caches.Roles, key = "'Code_'.concat(#result == null ? '' : #result.getCode())", unless = "#result == null")
	})
	public Role createRole(Identifier groupId, Localized name) {
		return delegate.createRole(groupId, name);
	}
	
	@Override
	@Caching(put = {
			@CachePut(cacheNames = CachingConfig.Caches.Roles, key = "'Id_'.concat(#roleId)"),
			@CachePut(cacheNames = CachingConfig.Caches.Roles, key = "'Code_'.concat(#result == null ? '' : #result.getCode())", unless = "#result == null")
	})
	public Role updateRoleName(Identifier roleId, Localized name) {
		return delegate.updateRoleName(roleId, name);
	}

	@Override
	@Caching(put = {
			@CachePut(cacheNames = CachingConfig.Caches.Roles, key = "'Id_'.concat(#roleId)"),
			@CachePut(cacheNames = CachingConfig.Caches.Roles, key = "'Code_'.concat(#result == null ? '' : #result.getCode())", unless = "#result == null")
	})
	public Role enableRole(Identifier roleId) {
		return delegate.enableRole(roleId);
	}

	@Override
	@Caching(put = {
			@CachePut(cacheNames = CachingConfig.Caches.Roles, key = "'Id_'.concat(#roleId)"),
			@CachePut(cacheNames = CachingConfig.Caches.Roles, key = "'Code_'.concat(#result == null ? '' : #result.getCode())", unless = "#result == null")
	})
	public Role disableRole(Identifier roleId) {
		return delegate.disableRole(roleId);
	}
	
	@Override
	@Caching(put = {
			@CachePut(cacheNames = CachingConfig.Caches.Roles, key = "'Id_'.concat(#result == null ? '' : #result.roleId)", unless = "#result == null"),
			@CachePut(cacheNames = CachingConfig.Caches.Roles, key = "'Code_'.concat(#result == null ? '' : #result.getCode())", unless = "#result == null")
	})
	public Role createRole(Role role) {
		return delegate.createRole(role);
	}

	@Override
	@Cacheable(cacheNames = CachingConfig.Caches.Roles, key = "'Id_'.concat(#roleId)")
	public Role findRole(Identifier roleId) {
		Role role = delegate.findRole(roleId);
		if (role != null) {
			cacheManager.getCache(CachingConfig.Caches.Roles).putIfAbsent("Code_" + role.getCode(), role);
		}
		return role;
	}
	
	@Override
	@Cacheable(cacheNames = CachingConfig.Caches.Roles, key = "'Code_'.concat(#code)")
	public Role findRoleByCode(String code) {
		Role role = delegate.findRoleByCode(code);		
		if (role != null) {
			cacheManager.getCache(CachingConfig.Caches.Roles).putIfAbsent("Id_" + role.getRoleId(), role);
		}
		return role;
	}

	@Override
	public FilteredPage<Role> findRoles(RolesFilter filter, Paging paging) {
		FilteredPage<Role> page = delegate.findRoles(filter, paging);
		Cache rolesCache = cacheManager.getCache(CachingConfig.Caches.Roles);
		page.forEach((role) -> {
			rolesCache.putIfAbsent("Id_" + role.getRoleId(), role);
			rolesCache.putIfAbsent("Code_" + role.getCode(), role);
		});
		return page;
	}
	
	@Override
	public FilteredPage<Role> findRoles(RolesFilter filter) {
		FilteredPage<Role> page = delegate.findRoles(filter);
		Cache rolesCache = cacheManager.getCache(CachingConfig.Caches.Roles);
		page.forEach((role) -> {
			rolesCache.putIfAbsent("Id_" + role.getRoleId(), role);
			rolesCache.putIfAbsent("Code_" + role.getCode(), role);
		});
		return page;
	}
	
	@Override
	public List<String> findActiveRoleCodesForGroup(String group) {
		return delegate.findActiveRoleCodesForGroup(group);
	}
	
	@Override
	public List<Role> findRoles() {
		List<Role> roles = delegate.findRoles();
		Cache rolesCache = cacheManager.getCache(CachingConfig.Caches.Roles);
		roles.forEach((role) -> {
			rolesCache.putIfAbsent("Id_" + role.getRoleId(), role);
			rolesCache.putIfAbsent("Code_" + role.getCode(), role);
		});
		return roles;
	}
}