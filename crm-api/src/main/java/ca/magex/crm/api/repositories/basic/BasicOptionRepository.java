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
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.id.OptionIdentifier;

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
			.map(i -> SerializationUtils.clone(i)) // return a clone of what is in the repository
			.sorted(filter.getComparator(paging))
			.collect(Collectors.toList()), paging);
	}

	@Override
	public long countOptions(OptionsFilter filter) {
		return apply(filter).count();
	}

	@Override
	public Option findOption(OptionIdentifier optionId) {
		Option option = store.getOptions().get(optionId);
		if (option == null) {
			return null;
		}
		return SerializationUtils.clone(option); // return a clone of what is in the repository
	}

	@Override
	public Option saveOption(Option optionToSave) {
		Option option = optionToSave.withLastModified(System.currentTimeMillis());		
		store.getOptions().put(option.getOptionId(), option);
		store.getNotifier().optionUpdated(option.getLastModified(), option.getOptionId());
		return SerializationUtils.clone(option);
	}

}