package ca.magex.crm.caching;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.RolesFilter;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.services.CrmRoleService;
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
public class CrmRoleServiceCachingDelegate implements CrmRoleService {

	private CrmRoleService delegate;
	private CacheTemplate cacheTemplate;

	/**
	 * Wraps the delegate service using the given cacheManager
	 * 
	 * @param delegate
	 * @param cacheTemplate
	 */
	public CrmRoleServiceCachingDelegate(CrmRoleService delegate, CacheTemplate cacheTemplate) {
		this.delegate = delegate;
		this.cacheTemplate = cacheTemplate;
	}

	/**
	 * Provides the list of pairs for caching group details
	 * @param details
	 * @return
	 */
	private List<Pair<String, Object>> roleCacheSupplier(Role role, Identifier key) {
		if (role == null) {
			return List.of(
					Pair.of(CrmCacheKeyGenerator.generateDetailsKey(key), role));
		} else {
			return List.of(
					Pair.of(CrmCacheKeyGenerator.generateDetailsKey(key), role),
					Pair.of(CrmCacheKeyGenerator.generateCodeKey(role.getCode()), role));
		}
	}

	/**
	 * Provides the list of pairs for caching group details
	 * @param details
	 * @return
	 */
	private List<Pair<String, Object>> roleCacheSupplier(Role role, String code) {
		if (role == null) {
			return List.of(
					Pair.of(CrmCacheKeyGenerator.generateCodeKey(code), role));
		} else {
			return List.of(
					Pair.of(CrmCacheKeyGenerator.generateDetailsKey(role.getRoleId()), role),
					Pair.of(CrmCacheKeyGenerator.generateCodeKey(code), role));
		}
	}

	@Override
	public Role createRole(Identifier groupId, Localized name) {
		Role role = delegate.createRole(groupId, name);
		cacheTemplate.put(roleCacheSupplier(role, role.getRoleId()));
		return role;
	}

	@Override
	public Role createRole(Role prototype) {
		Role role = delegate.createRole(prototype);
		cacheTemplate.put(roleCacheSupplier(role, role.getRoleId()));
		return role;
	}

	@Override
	public Role enableRole(Identifier roleId) {
		Role role = delegate.enableRole(roleId);
		cacheTemplate.put(roleCacheSupplier(role, roleId));
		return role;
	}

	@Override
	public Role disableRole(Identifier roleId) {
		Role role = delegate.disableRole(roleId);
		cacheTemplate.put(roleCacheSupplier(role, roleId));
		return role;
	}

	@Override
	public Role updateRoleName(Identifier roleId, Localized name) {
		Role role = delegate.updateRoleName(roleId, name);
		cacheTemplate.put(roleCacheSupplier(role, roleId));
		return role;
	}

	@Override
	public Role findRole(Identifier roleId) {
		return cacheTemplate.get(
				() -> delegate.findRole(roleId),
				roleId,
				CrmCacheKeyGenerator::generateDetailsKey,
				this::roleCacheSupplier);
	}
	
	@Override
	public Role findRoleByCode(String code) {
		return cacheTemplate.get(
				() -> delegate.findRoleByCode(code),
				code,
				CrmCacheKeyGenerator::generateCodeKey,
				this::roleCacheSupplier);
	}

	@Override
	public FilteredPage<Role> findRoles(RolesFilter filter, Paging paging) {
		FilteredPage<Role> page = delegate.findRoles(filter, paging);
		page.forEach((role) -> {
			cacheTemplate.putIfAbsent(roleCacheSupplier(role, role.getRoleId()));
		});
		return page;
	}

	@Override
	public FilteredPage<Role> findRoles(RolesFilter filter) {
		FilteredPage<Role> page = delegate.findRoles(filter);
		page.forEach((role) -> {
			cacheTemplate.putIfAbsent(roleCacheSupplier(role, role.getRoleId()));
		});
		return page;
	}

	@Override
	public List<Role> findRoles() {
		List<Role> page = delegate.findRoles();
		page.forEach((role) -> {
			cacheTemplate.putIfAbsent(roleCacheSupplier(role, role.getRoleId()));
		});
		return page;
	}	
}