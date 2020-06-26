package ca.magex.crm.caching;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.caching.util.CacheTemplate;
import ca.magex.crm.caching.util.CrmCacheKeyGenerator;

/**
 * Delegate that intercepts calls and caches the results
 * 
 * @author Jonny
 */
public class CrmUserServiceCachingDelegate implements CrmUserService {

	private CrmUserService delegate;
	private CacheTemplate cacheTemplate;

	/**
	 * Wraps the delegate service using the given cacheManager
	 * 
	 * @param delegate
	 * @param cacheTemplate
	 */
	public CrmUserServiceCachingDelegate(CrmUserService delegate, CacheTemplate cacheTemplate) {
		this.delegate = delegate;
		this.cacheTemplate = cacheTemplate;
	}

	/**
	 * Provides the list of pairs for caching user details
	 * @param details
	 * @return
	 */
	private List<Pair<String, Object>> userCacheSupplier(User user, Identifier key) {
		if (user == null) {
			return List.of(
					Pair.of(CrmCacheKeyGenerator.generateDetailsKey(key), user));
		} else {
			return List.of(
					Pair.of(CrmCacheKeyGenerator.generateDetailsKey(key), user),
					Pair.of(CrmCacheKeyGenerator.generateUsernameKey(user.getUsername()), user));
		}
	}
	
	/**
	 * Provides the list of pairs for caching user details
	 * @param details
	 * @return
	 */
	private List<Pair<String, Object>> userCacheSupplier(User user, String username) {
		if (user == null) {
			return List.of(
					Pair.of(CrmCacheKeyGenerator.generateUsernameKey(username), user));
		} else {
			return List.of(
					Pair.of(CrmCacheKeyGenerator.generateUsernameKey(username), user),
					Pair.of(CrmCacheKeyGenerator.generateDetailsKey(user.getUserId()), user));
		}
	}

	@Override
	public User createUser(Identifier personId, String username, List<String> roles) {
		User user = delegate.createUser(personId, username, roles);
		cacheTemplate.put(userCacheSupplier(user, user.getUserId()));
		return user;
	}

	@Override
	public User createUser(User prototype) {
		User user = delegate.createUser(prototype);
		cacheTemplate.put(userCacheSupplier(user, user.getUserId()));
		return user;
	}

	@Override
	public User enableUser(Identifier userId) {
		User user = delegate.enableUser(userId);
		cacheTemplate.put(userCacheSupplier(user, userId));
		return user;
	}

	@Override
	public User disableUser(Identifier userId) {
		User user = delegate.disableUser(userId);
		cacheTemplate.put(userCacheSupplier(user, userId));
		return user;
	}

	@Override
	public User updateUserRoles(Identifier userId, List<String> roles) {
		User user = delegate.updateUserRoles(userId, roles);
		cacheTemplate.put(userCacheSupplier(user, userId));
		return user;
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
	public User findUser(Identifier userId) {
		return cacheTemplate.get(
				() -> delegate.findUser(userId),
				userId,
				CrmCacheKeyGenerator::generateDetailsKey,
				this::userCacheSupplier);
	}

	@Override
	public User findUserByUsername(String username) {
		return cacheTemplate.get(
				() -> delegate.findUserByUsername(username),
				username,
				CrmCacheKeyGenerator::generateUsernameKey,
				this::userCacheSupplier);
	}

	@Override
	public long countUsers(UsersFilter filter) {
		return delegate.countUsers(filter);
	}

	@Override
	public FilteredPage<User> findUsers(UsersFilter filter, Paging paging) {
		FilteredPage<User> page = delegate.findUsers(filter, paging);
		page.forEach((details) -> {
			cacheTemplate.putIfAbsent(userCacheSupplier(details, details.getUserId()));
		});
		return page;
	}
	
	@Override
	public FilteredPage<User> findUsers(UsersFilter filter) {
		FilteredPage<User> page = delegate.findUsers(filter);
		page.forEach((details) -> {
			cacheTemplate.putIfAbsent(userCacheSupplier(details, details.getUserId()));
		});
		return page;
	}

	@Override
	public FilteredPage<User> findActiveUserForOrg(Identifier organizationId) {
		FilteredPage<User> page = delegate.findActiveUserForOrg(organizationId);
		page.forEach((details) -> {
			cacheTemplate.putIfAbsent(userCacheSupplier(details, details.getUserId()));
		});
		return page;
	}
}