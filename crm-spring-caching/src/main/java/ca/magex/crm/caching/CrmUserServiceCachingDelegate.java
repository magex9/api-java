package ca.magex.crm.caching;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.caching.config.CachingConfig;

@Service("CrmUserServiceCachingDelegate")
public class CrmUserServiceCachingDelegate implements CrmUserService {

	private CrmUserService delegate;
	private CacheManager cacheManager;
	
	/**
	 * Wraps the delegate service using the given cacheManager
	 * 
	 * @param delegate
	 * @param cacheManager
	 */
	public CrmUserServiceCachingDelegate(@Qualifier("PrincipalUserService") CrmUserService delegate, CacheManager cacheManager) {
		this.delegate = delegate;
		this.cacheManager = cacheManager;
	}

	@Override
	@Caching(put = {
			@CachePut(cacheNames = CachingConfig.Caches.Users, key = "'Id_'.concat(#result == null ? '' : #result.userId)", unless = "#result == null"),
			@CachePut(cacheNames = CachingConfig.Caches.Users, key = "'Username_'.concat(#result == null ? '' : #result.username)", unless = "#result == null")
	})
	public User createUser(Identifier personId, String username, List<String> roles) {
		return delegate.createUser(personId, username, roles);
	}
	
	@Override
	@Caching(put = {
			@CachePut(cacheNames = CachingConfig.Caches.Users, key = "'Id_'.concat(#result == null ? '' : #result.userId)", unless = "#result == null"),
			@CachePut(cacheNames = CachingConfig.Caches.Users, key = "'Username_'.concat(#result == null ? '' : #result.username)", unless = "#result == null")
	})
	public User createUser(User prototype) {
		return delegate.createUser(prototype);
	}

	@Override
	@Caching(put = {
			@CachePut(cacheNames = CachingConfig.Caches.Users, key = "'Id_'.concat(#userId)"),
			@CachePut(cacheNames = CachingConfig.Caches.Users, key = "'Username_'.concat(#result == null ? '' : #result.username)", unless = "#result == null")
	})
	public User enableUser(Identifier userId) {
		return delegate.enableUser(userId);
	}

	@Override
	@Caching(put = {
			@CachePut(cacheNames = CachingConfig.Caches.Users, key = "'Id_'.concat(#userId)"),
			@CachePut(cacheNames = CachingConfig.Caches.Users, key = "'Username_'.concat(#result == null ? '' : #result.username)", unless = "#result == null")
	})
	public User disableUser(Identifier userId) {
		return delegate.disableUser(userId);
	}

	@Override
	@Caching(put = {
			@CachePut(cacheNames = CachingConfig.Caches.Users, key = "'Id_'.concat(#userId)"),
			@CachePut(cacheNames = CachingConfig.Caches.Users, key = "'Username_'.concat(#result == null ? '' : #result.username)", unless = "#result == null")
	})
	public User updateUserRoles(Identifier userId, List<String> roles) {
		return delegate.updateUserRoles(userId, roles);
	}

	@Override
	public boolean changePassword(Identifier userId, String currentPassword, String newPassword) {
		return delegate.changePassword(userId, currentPassword, newPassword);
	}

	@Override
	public String resetPassword(Identifier userId) {
		return delegate.resetPassword(userId);
	}

	@Override
	@Cacheable(cacheNames = CachingConfig.Caches.Users, key = "'Id_'.concat(#userId)")			
	public User findUser(Identifier userId) {
		User user = delegate.findUser(userId);
		/* programmatically add a username entry to the cache */
		if (user != null) {
			Cache usersCache = cacheManager.getCache(CachingConfig.Caches.Users);
			usersCache.putIfAbsent("Username_" + user.getUsername(), user);
		}
		return user;
	}

	@Override
	@Cacheable(cacheNames = CachingConfig.Caches.Users, key = "'Username_'.concat(#username)")
	public User findUserByUsername(String username) {
		/* programmatically add an id entry to the cache */
		User user = delegate.findUserByUsername(username);
		if (user != null) {
			Cache usersCache = cacheManager.getCache(CachingConfig.Caches.Users);
			usersCache.putIfAbsent("Id_" + user.getUserId(), user);
		}
		return user;
	}

	@Override
	public long countUsers(UsersFilter filter) {
		return delegate.countUsers(filter);
	}

	@Override
	public FilteredPage<User> findUsers(UsersFilter filter, Paging paging) {
		FilteredPage<User> page = delegate.findUsers(filter, paging);
		Cache usersCache = cacheManager.getCache(CachingConfig.Caches.Users);
		page.forEach((user) -> {
			usersCache.putIfAbsent("Id_" + user.getUserId(), user);
			usersCache.putIfAbsent("Username_" + user.getUsername(), user);
		});
		return page;
	}
	
	@Override
	public FilteredPage<User> findActiveUserForOrg(Identifier organizationId) {
		FilteredPage<User> page = delegate.findActiveUserForOrg(organizationId);
		Cache usersCache = cacheManager.getCache(CachingConfig.Caches.Users);
		page.forEach((user) -> {
			usersCache.putIfAbsent("Id_" + user.getUserId(), user);
			usersCache.putIfAbsent("Username_" + user.getUsername(), user);
		});
		return page;
	}
}