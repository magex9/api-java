package ca.magex.crm.caching;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

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
import ca.magex.crm.caching.util.CacheTemplate;
import ca.magex.crm.caching.util.CrmCacheKeyGenerator;

public class CrmPersonServiceCachingDelegate implements CrmPersonService {

	private CrmPersonService delegate;
	private CacheTemplate cacheTemplate;

	/**
	 * Wraps the delegate service using the given cacheManager
	 * 
	 * @param delegate
	 * @param cacheManager
	 */
	public CrmPersonServiceCachingDelegate(CrmPersonService delegate, CacheTemplate cacheTemplate) {
		this.delegate = delegate;
		this.cacheTemplate = cacheTemplate;
	}

	/**
	 * Provides the list of pairs for caching person details
	 * @param details
	 * @return
	 */
	private List<Pair<String, Object>> detailsCacheSupplier(PersonDetails details, Identifier key) {
		return List.of(
				Pair.of(CrmCacheKeyGenerator.generateDetailsKey(key), details),
				Pair.of(CrmCacheKeyGenerator.generateSummaryKey(key), details == null ? null : details.asSummary()));
	}

	/**
	 * Provides the list of pairs for caching person summary
	 * @param summary
	 * @param key
	 * @return
	 */
	private List<Pair<String, Object>> summaryCacheSupplier(PersonSummary summary, Identifier key) {
		return List.of(
				Pair.of(CrmCacheKeyGenerator.generateSummaryKey(key), summary));
	}

	@Override
	public PersonDetails createPerson(Identifier organizationId, PersonName name, MailingAddress address, Communication communication, BusinessPosition position) {
		PersonDetails details = delegate.createPerson(organizationId, name, address, communication, position);
		cacheTemplate.put(detailsCacheSupplier(details, details.getPersonId()));
		return details;
	}

	@Override
	public PersonDetails createPerson(PersonDetails prototype) {
		PersonDetails details = delegate.createPerson(prototype);
		cacheTemplate.put(detailsCacheSupplier(details, details.getPersonId()));
		return details;
	}

	@Override
	public PersonSummary enablePerson(Identifier personId) {
		PersonSummary summary = delegate.enablePerson(personId);
		cacheTemplate.evict(CrmCacheKeyGenerator.generateDetailsKey(personId));
		cacheTemplate.put(summaryCacheSupplier(summary, personId));
		return summary;
	}

	@Override
	public PersonSummary disablePerson(Identifier personId) {
		PersonSummary summary = delegate.disablePerson(personId);
		cacheTemplate.evict(CrmCacheKeyGenerator.generateDetailsKey(personId));
		cacheTemplate.put(summaryCacheSupplier(summary, personId));
		return summary;
	}

	@Override
	public PersonDetails updatePersonName(Identifier personId, PersonName name) {
		PersonDetails details = delegate.updatePersonName(personId, name);
		cacheTemplate.put(detailsCacheSupplier(details, personId));
		return details;
	}

	@Override
	public PersonDetails updatePersonAddress(Identifier personId, MailingAddress address) {
		PersonDetails details = delegate.updatePersonAddress(personId, address);
		cacheTemplate.put(detailsCacheSupplier(details, personId));
		return details;
	}

	@Override
	public PersonDetails updatePersonCommunication(Identifier personId, Communication communication) {
		PersonDetails details = delegate.updatePersonCommunication(personId, communication);
		cacheTemplate.put(detailsCacheSupplier(details, personId));
		return details;
	}

	@Override
	public PersonDetails updatePersonBusinessPosition(Identifier personId, BusinessPosition position) {
		PersonDetails details = delegate.updatePersonBusinessPosition(personId, position);
		cacheTemplate.put(detailsCacheSupplier(details, personId));
		return details;
	}

	@Override
	public PersonSummary findPersonSummary(Identifier personId) {
		return cacheTemplate.get(
				() -> delegate.findPersonSummary(personId),
				personId,
				CrmCacheKeyGenerator::generateSummaryKey,
				this::summaryCacheSupplier);
	}

	@Override
	public PersonDetails findPersonDetails(Identifier personId) {
		return cacheTemplate.get(
				() -> delegate.findPersonDetails(personId),
				personId,
				CrmCacheKeyGenerator::generateDetailsKey,
				this::detailsCacheSupplier);
	}

	@Override
	public long countPersons(PersonsFilter filter) {
		return delegate.countPersons(filter);
	}

	@Override
	public FilteredPage<PersonSummary> findPersonSummaries(PersonsFilter filter, Paging paging) {
		FilteredPage<PersonSummary> page = delegate.findPersonSummaries(filter, paging);
		page.forEach((summary) -> {
			cacheTemplate.putIfAbsent(summaryCacheSupplier(summary, summary.getPersonId()));
		});
		return page;
	}

	@Override
	public FilteredPage<PersonSummary> findPersonSummaries(PersonsFilter filter) {
		FilteredPage<PersonSummary> page = delegate.findPersonSummaries(filter);
		page.forEach((summary) -> {
			cacheTemplate.putIfAbsent(summaryCacheSupplier(summary, summary.getPersonId()));
		});
		return page;
	}

	@Override
	public FilteredPage<PersonSummary> findActivePersonSummariesForOrg(Identifier organizationId) {
		FilteredPage<PersonSummary> page = delegate.findActivePersonSummariesForOrg(organizationId);
		page.forEach((summary) -> {
			cacheTemplate.putIfAbsent(summaryCacheSupplier(summary, summary.getPersonId()));
		});
		return page;
	}

	@Override
	public FilteredPage<PersonDetails> findPersonDetails(PersonsFilter filter, Paging paging) {
		FilteredPage<PersonDetails> page = delegate.findPersonDetails(filter, paging);
		page.forEach((details) -> {
			cacheTemplate.putIfAbsent(detailsCacheSupplier(details, details.getPersonId()));
		});
		return page;
	}

	@Override
	public FilteredPage<PersonDetails> findPersonDetails(PersonsFilter filter) {
		FilteredPage<PersonDetails> page = delegate.findPersonDetails(filter);
		page.forEach((details) -> {
			cacheTemplate.putIfAbsent(detailsCacheSupplier(details, details.getPersonId()));
		});
		return page;
	}
}