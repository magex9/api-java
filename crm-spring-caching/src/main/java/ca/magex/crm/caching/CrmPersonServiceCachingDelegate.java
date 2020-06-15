package ca.magex.crm.caching;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.caching.config.CachingConfig;

@Service("CrmPersonServiceCachingDelegate")
public class CrmPersonServiceCachingDelegate implements CrmPersonService {

	private CrmPersonService delegate;
	private CacheManager cacheManager;

	/**
	 * Wraps the delegate service using the given cacheManager
	 * 
	 * @param delegate
	 * @param cacheManager
	 */
	public CrmPersonServiceCachingDelegate(@Qualifier("PrincipalPersonService") CrmPersonService delegate, CacheManager cacheManager) {
		this.delegate = delegate;
		this.cacheManager = cacheManager;
	}

	@Override
	@Caching(put = {
			@CachePut(cacheNames = CachingConfig.Caches.Persons, key = "'Details_'.concat(#result == null ? '' : #result.personId)", unless = "#result == null"),
			@CachePut(cacheNames = CachingConfig.Caches.Persons, key = "'Summary_'.concat(#result == null ? '' : #result.personId)", unless = "#result == null")
	})
	public PersonDetails createPerson(Identifier organizationId, PersonName name, MailingAddress address, Communication communication, BusinessPosition position) {
		return delegate.createPerson(organizationId, name, address, communication, position);
	}
	
	@Override
	@Caching(put = {
			@CachePut(cacheNames = CachingConfig.Caches.Persons, key = "'Details_'.concat(#result == null ? '' : #result.personId)", unless = "#result == null"),
			@CachePut(cacheNames = CachingConfig.Caches.Persons, key = "'Summary_'.concat(#result == null ? '' : #result.personId)", unless = "#result == null")
	})
	public PersonDetails createPerson(PersonDetails prototype) {
		return delegate.createPerson(prototype);
	}

	@Override
	@CachePut(cacheNames = CachingConfig.Caches.Persons, key = "'Summary_'.concat(#personId)")
	@CacheEvict(cacheNames = CachingConfig.Caches.Persons, key = "'Details_'.concat(#personId)")
	public PersonSummary enablePerson(Identifier personId) {
		return delegate.enablePerson(personId);
	}

	@Override
	@CachePut(cacheNames = CachingConfig.Caches.Persons, key = "'Summary_'.concat(#personId)")
	@CacheEvict(cacheNames = CachingConfig.Caches.Persons, key = "'Details_'.concat(#personId)")
	public PersonSummary disablePerson(Identifier personId) {
		return delegate.disablePerson(personId);
	}

	@Override
	@Caching(put = {
			@CachePut(cacheNames = CachingConfig.Caches.Persons, key = "'Details_'.concat(#personId)"),
			@CachePut(cacheNames = CachingConfig.Caches.Persons, key = "'Summary_'.concat(#personId)")
	})
	public PersonDetails updatePersonName(Identifier personId, PersonName name) {
		return delegate.updatePersonName(personId, name);
	}

	@Override
	@Caching(put = {
			@CachePut(cacheNames = CachingConfig.Caches.Persons, key = "'Details_'.concat(#personId)"),
			@CachePut(cacheNames = CachingConfig.Caches.Persons, key = "'Summary_'.concat(#personId)")
	})
	public PersonDetails updatePersonAddress(Identifier personId, MailingAddress address) {
		return delegate.updatePersonAddress(personId, address);
	}

	@Override
	@Caching(put = {
			@CachePut(cacheNames = CachingConfig.Caches.Persons, key = "'Details_'.concat(#personId)"),
			@CachePut(cacheNames = CachingConfig.Caches.Persons, key = "'Summary_'.concat(#personId)")
	})
	public PersonDetails updatePersonCommunication(Identifier personId, Communication communication) {
		return delegate.updatePersonCommunication(personId, communication);
	}

	@Override
	@Caching(put = {
			@CachePut(cacheNames = CachingConfig.Caches.Persons, key = "'Details_'.concat(#personId)"),
			@CachePut(cacheNames = CachingConfig.Caches.Persons, key = "'Summary_'.concat(#personId)")
	})
	public PersonDetails updatePersonBusinessPosition(Identifier personId, BusinessPosition position) {
		return delegate.updatePersonBusinessPosition(personId, position);
	}

	@Override
	@Cacheable(cacheNames = CachingConfig.Caches.Persons, key = "'Summary_'.concat(#personId)")
	public PersonSummary findPersonSummary(Identifier personId) {
		return delegate.findPersonSummary(personId);
	}

	@Override
	@Cacheable(cacheNames = CachingConfig.Caches.Persons, key = "'Details_'.concat(#personId)")
	public PersonDetails findPersonDetails(Identifier personId) {
		return delegate.findPersonDetails(personId);
	}

	@Override
	public long countPersons(PersonsFilter filter) {
		return delegate.countPersons(filter);
	}

	@Override
	public FilteredPage<PersonDetails> findPersonDetails(PersonsFilter filter, Paging paging) {
		FilteredPage<PersonDetails> page = delegate.findPersonDetails(filter, paging);
		Cache personsCache = cacheManager.getCache(CachingConfig.Caches.Persons);
		page.forEach((details) -> {
			personsCache.putIfAbsent("Details_" + details.getPersonId(), details);
			personsCache.putIfAbsent("Summary_" + details.getPersonId(), details);
		});
		return page;
	}
	
	@Override
	public FilteredPage<PersonDetails> findPersonDetails(PersonsFilter filter) {
		FilteredPage<PersonDetails> page = delegate.findPersonDetails(filter);
		Cache personsCache = cacheManager.getCache(CachingConfig.Caches.Persons);
		page.forEach((details) -> {
			personsCache.putIfAbsent("Details_" + details.getPersonId(), details);
			personsCache.putIfAbsent("Summary_" + details.getPersonId(), details);
		});
		return page;
	}

	@Override
	public FilteredPage<PersonSummary> findPersonSummaries(PersonsFilter filter, Paging paging) {
		FilteredPage<PersonSummary> page = delegate.findPersonSummaries(filter, paging);
		Cache personsCache = cacheManager.getCache(CachingConfig.Caches.Persons);
		page.forEach((summary) -> {
			personsCache.putIfAbsent("Summary_" + summary.getPersonId(), summary);
		});
		return page;
	}
	
	@Override
	public FilteredPage<PersonSummary> findPersonSummaries(PersonsFilter filter) {
		FilteredPage<PersonSummary> page = delegate.findPersonSummaries(filter);
		Cache personsCache = cacheManager.getCache(CachingConfig.Caches.Persons);
		page.forEach((summary) -> {
			personsCache.putIfAbsent("Summary_" + summary.getPersonId(), summary);
		});
		return page;
	}
	
	@Override
	public FilteredPage<PersonSummary> findActivePersonSummariesForOrg(Identifier organizationId) {
		FilteredPage<PersonSummary> page = delegate.findActivePersonSummariesForOrg(organizationId);
		Cache personsCache = cacheManager.getCache(CachingConfig.Caches.Persons);
		page.forEach((summary) -> {
			personsCache.putIfAbsent("Summary_" + summary.getPersonId(), summary);
		});
		return page;
	}
}