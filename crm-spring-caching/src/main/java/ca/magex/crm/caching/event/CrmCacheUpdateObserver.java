package ca.magex.crm.caching.event;

import ca.magex.crm.api.observer.CrmUpdateObserver;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OptionIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.system.id.UserIdentifier;
import ca.magex.crm.caching.util.CacheTemplate;
import ca.magex.crm.caching.util.CrmCacheKeyGenerator;

/**
 * Update Observer used to evict an item from the cache if it changes
 * 
 * @author Jonny
 */
public class CrmCacheUpdateObserver implements CrmUpdateObserver {
		
	private CrmCacheKeyGenerator keyGenerator = CrmCacheKeyGenerator.getInstance();
	
	private CacheTemplate cacheTemplate;
	
	@Override
	public CrmUpdateObserver optionUpdated(Long timestamp, OptionIdentifier optionId) {
		String optionKey = keyGenerator.generateOptionKey(optionId);
		Option option = cacheTemplate.getIfPresent(optionKey);
		/* not cached, so nothing to do */
		if (option == null) {
			return this;
		}
		/* if our current cached version has the same timestamp as the updated one then nothing to do */
		if (option.getLastModified().equals(timestamp)) {
			return this;
		}
		/* evict the key, it will get loaded back next time this option is requested */
		cacheTemplate.evict(optionKey);
		return this;
	}
	
	@Override
	public CrmUpdateObserver organizationUpdated(Long timestamp, OrganizationIdentifier organizationId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public CrmUpdateObserver locationUpdated(Long timestamp, LocationIdentifier locationId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	@Override
	public CrmUpdateObserver personUpdated(Long timestamp, PersonIdentifier personId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public CrmUpdateObserver userUpdated(Long timestamp, UserIdentifier userId) {
		// TODO Auto-generated method stub
		return null;
	}
}