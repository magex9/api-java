package ca.magex.crm.caching;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import ca.magex.crm.api.authentication.CrmPasswordService;
import ca.magex.crm.api.crm.UserDetails;
import ca.magex.crm.caching.util.CacheTemplate;
import ca.magex.crm.caching.util.CrmCacheKeyGenerator;

/**
 * Delegate that intercepts calls and caches the results
 * 
 * @author Jonny
 */
public class CrmPasswordServiceCachingDelegate implements CrmPasswordService {

	private CrmPasswordService delegate;
	private CacheTemplate cacheTemplate;
	
	/**
	 * Wraps the delegate service using the given cacheManager
	 */
	public CrmPasswordServiceCachingDelegate(CrmPasswordService delegate, CacheTemplate cacheTemplate) {
		this.delegate = delegate;
		this.cacheTemplate = cacheTemplate;
	}
	
	/**
	 * Provides the list of pairs for caching user summaries
	 * @param details
	 * @return
	 */
	private List<Pair<String, Object>> detailsCacheSupplier(UserDetails details, String key) {		
		if (details == null) {
			return List.of(
					Pair.of(CrmCacheKeyGenerator.getInstance().generateUsernameDetailsKey(key), null),
					Pair.of(CrmCacheKeyGenerator.getInstance().generateUsernameSummaryKey(key), null));
		} else {
			return List.of(
					Pair.of(CrmCacheKeyGenerator.getInstance().generateDetailsKey(details.getUserId()), details),
					Pair.of(CrmCacheKeyGenerator.getInstance().generateSummaryKey(details.getUserId()), details.asSummary()),
					Pair.of(CrmCacheKeyGenerator.getInstance().generateUsernameDetailsKey(key), details),
					Pair.of(CrmCacheKeyGenerator.getInstance().generateUsernameSummaryKey(details.getUsername()), details.asSummary()));
		}
	}
	
	/**
	 * Provides the list of pairs for caching user summaries
	 * @param details
	 * @return
	 */
	private List<Pair<String, Object>> passwordCacheSupplier(String password, String username) {		
		return List.of(Pair.of(CrmCacheKeyGenerator.getInstance().generatePasswordKey(username), password));		
	}
	
	/**
	 * Provides the list of pairs for caching user summaries
	 * @param details
	 * @return
	 */
	private List<Pair<String, Object>> temporaryPasswordCacheSupplier(Boolean temporary, String username) {		
		return List.of(Pair.of(CrmCacheKeyGenerator.getInstance().generateTemporaryPasswordKey(username), temporary));		
	}
	
	
	@Override
	public UserDetails findUser(String username) {
		return cacheTemplate.get(
				() -> delegate.findUser(username),
				username,
				CrmCacheKeyGenerator.getInstance()::generateUsernameDetailsKey,
				this::detailsCacheSupplier);
	}

	@Override
	public String getEncodedPassword(String username) {
		return cacheTemplate.get(
				() -> delegate.getEncodedPassword(username),
				username,
				CrmCacheKeyGenerator.getInstance()::generatePasswordKey,
				this::passwordCacheSupplier);
	}

	@Override
	public boolean isTempPassword(String username) {
		return cacheTemplate.get(
				() -> delegate.isTempPassword(username),
				username,
				CrmCacheKeyGenerator.getInstance()::generateTemporaryPasswordKey,
				this::temporaryPasswordCacheSupplier);
	}

	@Override
	public boolean isExpiredPassword(String username) {
		/* this needs to be calculated based on time of call, so can't be cached */
		return delegate.isExpiredPassword(username);
	}

	@Override
	public boolean verifyPassword(String username, String rawPassword) {
		/* don't need to cache this */
		return delegate.verifyPassword(username, rawPassword);
	}

	@Override
	public String generateTemporaryPassword(String username) {		
		String temporaryPassword = delegate.generateTemporaryPassword(username);		
		/* we know the current password is temporary now so cache it */
		cacheTemplate.put(passwordCacheSupplier(delegate.encodePassword(temporaryPassword), username));
		cacheTemplate.put(temporaryPasswordCacheSupplier(true, username));
		return temporaryPassword;
	}

	@Override
	public void updatePassword(String username, String encodedPassword) {
		delegate.updatePassword(username, encodedPassword);
		/* we know the current password is NOT temporary now so cache it */
		cacheTemplate.put(passwordCacheSupplier(encodedPassword, username));
		cacheTemplate.put(temporaryPasswordCacheSupplier(false, username));
	}

	@Override
	public String encodePassword(String rawPassword) {
		/* never cache this */
		return delegate.encodePassword(rawPassword);
	}
}
