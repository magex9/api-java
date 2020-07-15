package ca.magex.crm.caching;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import ca.magex.crm.api.crm.UserDetails;
import ca.magex.crm.api.crm.UserSummary;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.id.AuthenticationRoleIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.system.id.UserIdentifier;
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
	private List<Pair<String, Object>> detailsCacheSupplier(UserDetails details, Identifier key) {
		if (details == null) {
			return List.of(
					Pair.of(CrmCacheKeyGenerator.getInstance().generateDetailsKey(key), null),
					Pair.of(CrmCacheKeyGenerator.getInstance().generateSummaryKey(key), null));
		} else {
			return List.of(
					Pair.of(CrmCacheKeyGenerator.getInstance().generateDetailsKey(key), details),
					Pair.of(CrmCacheKeyGenerator.getInstance().generateSummaryKey(key), details.asSummary()),
					Pair.of(CrmCacheKeyGenerator.getInstance().generateUsernameKey(details.getUsername()), details));
		}
	}

	/**
	 * Provides the list of pairs for caching user summaries
	 * @param details
	 * @return
	 */
	private List<Pair<String, Object>> summaryCacheSupplier(UserSummary summary, Identifier key) {
		if (summary == null) {
			return List.of(
					Pair.of(CrmCacheKeyGenerator.getInstance().generateDetailsKey(key), null),
					Pair.of(CrmCacheKeyGenerator.getInstance().generateSummaryKey(key), null));
		} else {
			return List.of(
					Pair.of(CrmCacheKeyGenerator.getInstance().generateSummaryKey(key), summary));
		}
	}

	/**
	 * Provides the list of pairs for caching user summaries
	 * @param details
	 * @return
	 */
	private List<Pair<String, Object>> summaryCacheSupplier(UserDetails details, String key) {		
		if (details == null) {
			return List.of(
					Pair.of(CrmCacheKeyGenerator.getInstance().generateUsernameKey(key), null));
		} else {
			return List.of(
					Pair.of(CrmCacheKeyGenerator.getInstance().generateDetailsKey(details.getUserId()), details),
					Pair.of(CrmCacheKeyGenerator.getInstance().generateSummaryKey(details.getUserId()), details.asSummary()),
					Pair.of(CrmCacheKeyGenerator.getInstance().generateUsernameKey(key), details));
		}
	}

	@Override
	public UserDetails createUser(PersonIdentifier personId, String username, List<AuthenticationRoleIdentifier> authenticationRoleIds) {
		UserDetails user = delegate.createUser(personId, username, authenticationRoleIds);
		cacheTemplate.put(detailsCacheSupplier(user, user.getUserId()));
		return user;
	}

	@Override
	public UserDetails createUser(UserDetails prototype) {
		UserDetails user = delegate.createUser(prototype);
		cacheTemplate.put(detailsCacheSupplier(user, user.getUserId()));
		return user;
	}

	@Override
	public UserSummary enableUser(UserIdentifier userId) {
		UserSummary user = delegate.enableUser(userId);
		cacheTemplate.evict(CrmCacheKeyGenerator.getInstance().generateDetailsKey(userId));
		if (user != null) {
			cacheTemplate.evict(CrmCacheKeyGenerator.getInstance().generateUsernameKey(user.getUsername()));
		}
		cacheTemplate.put(summaryCacheSupplier(user, userId));
		return user;
	}

	@Override
	public UserSummary disableUser(UserIdentifier userId) {
		UserSummary user = delegate.disableUser(userId);
		cacheTemplate.evict(CrmCacheKeyGenerator.getInstance().generateDetailsKey(userId));
		if (user != null) {
			cacheTemplate.evict(CrmCacheKeyGenerator.getInstance().generateUsernameKey(user.getUsername()));
		}
		cacheTemplate.put(summaryCacheSupplier(user, userId));
		return user;
	}

	@Override
	public UserDetails updateUserRoles(UserIdentifier userId, List<AuthenticationRoleIdentifier> roleIds) {
		UserDetails user = delegate.updateUserRoles(userId, roleIds);
		cacheTemplate.put(detailsCacheSupplier(user, userId));
		return user;
	}

	@Override
	public boolean changePassword(UserIdentifier userId, String currentPassword, String newPassword) {
		return delegate.changePassword(userId, currentPassword, newPassword);
	}

	@Override
	public String resetPassword(UserIdentifier userId) {
		return delegate.resetPassword(userId);
	}

	@Override
	public UserSummary findUserSummary(UserIdentifier userId) {
		return cacheTemplate.get(
				() -> delegate.findUserSummary(userId),
				userId,
				CrmCacheKeyGenerator.getInstance()::generateSummaryKey,
				this::summaryCacheSupplier);
	}

	@Override
	public UserDetails findUserDetails(UserIdentifier userId) {
		return cacheTemplate.get(
				() -> delegate.findUserDetails(userId),
				userId,
				CrmCacheKeyGenerator.getInstance()::generateDetailsKey,
				this::detailsCacheSupplier);
	}

	@Override
	public UserDetails findUserByUsername(String username) {
		return cacheTemplate.get(
				() -> delegate.findUserByUsername(username),
				username,
				CrmCacheKeyGenerator.getInstance()::generateUsernameKey,
				this::summaryCacheSupplier);
	}

	@Override
	public long countUsers(UsersFilter filter) {
		return delegate.countUsers(filter);
	}

	@Override
	public FilteredPage<UserSummary> findUserSummaries(UsersFilter filter, Paging paging) {
		FilteredPage<UserSummary> page = delegate.findUserSummaries(filter, paging);
		page.forEach((summary) -> {
			cacheTemplate.putIfAbsent(summaryCacheSupplier(summary, summary.getUserId()));
		});
		return page;
	}

	@Override
	public FilteredPage<UserDetails> findUserDetails(UsersFilter filter, Paging paging) {
		FilteredPage<UserDetails> page = delegate.findUserDetails(filter, paging);
		page.forEach((details) -> {
			cacheTemplate.putIfAbsent(detailsCacheSupplier(details, details.getUserId()));
		});
		return page;
	}
}