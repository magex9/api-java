package ca.magex.crm.hazelcast.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.SerializationUtils;

import com.hazelcast.core.TransactionalMap;

import ca.magex.crm.api.filters.OptionsFilter;
import ca.magex.crm.api.filters.PageBuilder;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.repositories.CrmOptionRepository;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.id.OptionIdentifier;
import ca.magex.crm.hazelcast.predicate.CrmFilterPredicate;
import ca.magex.crm.hazelcast.xa.XATransactionAwareHazelcastInstance;

/**
 * An implementation of the Option Repository that uses the Hazelcast in memory data grid
 * for persisting instances across multiple nodes
 * 
 * @author Jonny
 */
public class HazelcastOptionRepository implements CrmOptionRepository {

	private XATransactionAwareHazelcastInstance hzInstance;

	/**
	 * Creates the new repository using the backing Transaction Aware Hazelcast Instance
	 * 
	 * @param hzInstance
	 */
	public HazelcastOptionRepository(XATransactionAwareHazelcastInstance hzInstance) {
		this.hzInstance = hzInstance;
	}
	
	@Override
	public Option saveOption(Option option) {
		TransactionalMap<OptionIdentifier, Option> organizations = hzInstance.getOptionsMap();
		/* persist a clone of this organization, and return the original */
		organizations.put(option.getOptionId(), SerializationUtils.clone(option));
		return option;
	}

	@Override
	public Option findOption(OptionIdentifier optionId) {
		TransactionalMap<OptionIdentifier, Option> options = hzInstance.getOptionsMap();
		Option option = options.get(optionId);
		if (option == null) {
			return null;
		}
		return SerializationUtils.clone(option);
	}
	
	@Override
	public FilteredPage<Option> findOptions(OptionsFilter filter, Paging paging) {
		TransactionalMap<OptionIdentifier, Option> options = hzInstance.getOptionsMap();
		List<Option> allMatchingOptions = options.values(new CrmFilterPredicate<Option>(filter))
				.stream()				
				.sorted(filter.getComparator(paging))
				.map(i -> SerializationUtils.clone(i))
				.collect(Collectors.toList());
		return PageBuilder.buildPageFor(filter, allMatchingOptions, paging);
	}

	@Override
	public long countOptions(OptionsFilter filter) {
		TransactionalMap<OptionIdentifier, Option> options = hzInstance.getOptionsMap();
		return options.values(new CrmFilterPredicate<Option>(filter)).size();
	}
}