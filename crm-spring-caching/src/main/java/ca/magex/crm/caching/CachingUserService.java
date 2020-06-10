package ca.magex.crm.caching;

import java.util.List;

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

@Service("CachingUserService")
public class CachingUserService implements CrmUserService {

	private CrmUserService delegate;
	private CacheManager cacheManager;
	
	/**
	 * Wraps the delegate service using the given cacheManager
	 * 
	 * @param delegate
	 * @param cacheManager
	 */
	public CachingUserService(CrmUserService delegate, CacheManager cacheManager) {
		this.delegate = delegate;
		this.cacheManager = cacheManager;
	}

	@Override
	@Caching(put = {
			@CachePut(cacheNames = "users", key = "'Id_'.concat(#result.userId)", unless = "#result == null"),
			@CachePut(cacheNames = "users", key = "'Username_'.concat(#result.username)", unless = "#result == null")
	})
	public User createUser(Identifier personId, String username, List<String> roles) {
		return delegate.createUser(personId, username, roles);
	}

	@Override
	@Caching(put = {
			@CachePut(cacheNames = "users", key = "'Id_'.concat(#userId)", unless = "#result == null"),
			@CachePut(cacheNames = "users", key = "'Username_'.concat(#result.username)", unless = "#result == null")
	})
	public User enableUser(Identifier userId) {
		return delegate.enableUser(userId);
	}

	@Override
	@Caching(put = {
			@CachePut(cacheNames = "users", key = "'Id_'.concat(#userId)", unless = "#result == null"),
			@CachePut(cacheNames = "users", key = "'Username_'.concat(#result.username)", unless = "#result == null")
	})
	public User disableUser(Identifier userId) {
		return delegate.disableUser(userId);
	}

	@Override
	@Caching(put = {
			@CachePut(cacheNames = "users", key = "'Id_'.concat(#userId)", unless = "#result == null"),
			@CachePut(cacheNames = "users", key = "'Username_'.concat(#result.username)", unless = "#result == null")
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
	@Cacheable(cacheNames = "users", key = "'Id_'.concat(#userId)")
	public User findUser(Identifier userId) {
		return delegate.findUser(userId);
	}

	@Override
	@Cacheable(cacheNames = "users", key = "'Username_'.concat(#username)")
	public User findUserByUsername(String username) {
		return delegate.findUserByUsername(username);
	}

	@Override
	public long countUsers(UsersFilter filter) {
		return delegate.countUsers(filter);
	}

	@Override
	public FilteredPage<User> findUsers(UsersFilter filter, Paging paging) {
		FilteredPage<User> page = delegate.findUsers(filter, paging);
		Cache usersCache = cacheManager.getCache("users");
		page.forEach((user) -> {
			usersCache.putIfAbsent("Id_" + user.getUserId(), user);
			usersCache.putIfAbsent("Username_" + user.getUsername(), user);
		});
		return page;
	}
}