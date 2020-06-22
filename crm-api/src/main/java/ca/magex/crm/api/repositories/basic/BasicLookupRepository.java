package ca.magex.crm.api.repositories.basic;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.SerializationUtils;

import ca.magex.crm.api.filters.LookupsFilter;
import ca.magex.crm.api.filters.PageBuilder;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.observer.CrmUpdateNotifier;
import ca.magex.crm.api.repositories.CrmLookupRepository;
import ca.magex.crm.api.store.CrmStore;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lookup;

public class BasicLookupRepository implements CrmLookupRepository {

	private CrmStore store;
	
	private CrmUpdateNotifier notifier;
	
	public BasicLookupRepository(CrmStore store, CrmUpdateNotifier notifier) {
		this.store = store;
		this.notifier = notifier;
	}
	
	@Override
	public Identifier generateLookupId() {
		return CrmStore.generateId(Lookup.class);
	}
	
	private Stream<Lookup> apply(LookupsFilter filter) {
		return store.getLookups().values().stream().filter(p -> filter.apply(p));
	}

	@Override
	public FilteredPage<Lookup> findLookups(LookupsFilter filter, Paging paging) {
		return PageBuilder.buildPageFor(filter, apply(filter)
			.map(i -> SerializationUtils.clone(i))
			.sorted(filter.getComparator(paging))
			.collect(Collectors.toList()), paging);
	}

	@Override
	public long countLookups(LookupsFilter filter) {
		return apply(filter).count();
	}

	@Override
	public synchronized Lookup findLookup(Identifier lookupId) {
		return store.getLookups().get(lookupId);
	}

	@Override
	public Lookup saveLookup(Lookup lookup) {
		notifier.lookupUpdated(System.nanoTime(), lookup.getLookupId());
		store.getLookups().put(lookup.getLookupId(), lookup);
		return lookup;
	}
	
}