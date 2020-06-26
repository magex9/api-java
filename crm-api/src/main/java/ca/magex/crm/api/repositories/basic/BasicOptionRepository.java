package ca.magex.crm.api.repositories.basic;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.SerializationUtils;

import ca.magex.crm.api.filters.OptionsFilter;
import ca.magex.crm.api.filters.PageBuilder;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.repositories.CrmOptionRepository;
import ca.magex.crm.api.store.CrmStore;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Option;

public class BasicOptionRepository implements CrmOptionRepository {

	private CrmStore store;

	public BasicOptionRepository(CrmStore store) {
		this.store = store;
	}
	
	private Stream<Option> apply(OptionsFilter filter) {
		return store.getOptions().values().stream().filter(p -> filter.apply(p));
	}

	@Override
	public FilteredPage<Option> findOptions(OptionsFilter filter, Paging paging) {
		return PageBuilder.buildPageFor(filter, apply(filter)
			.map(i -> SerializationUtils.clone(i))
			.sorted(filter.getComparator(paging))
			.collect(Collectors.toList()), paging);
	}

	@Override
	public long countOptions(OptionsFilter filter) {
		return apply(filter).count();
	}

	@Override
	public Option findOption(Identifier optionId) {
		return store.getOptions().get(optionId);
	}

	@Override
	public Option saveOption(Option option) {
		store.getNotifier().optionUpdated(System.nanoTime(), option.getOptionId());
		store.getOptions().put(option.getOptionId(), option);
		return option;
	}

}