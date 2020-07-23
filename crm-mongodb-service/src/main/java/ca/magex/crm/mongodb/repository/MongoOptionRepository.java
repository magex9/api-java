package ca.magex.crm.mongodb.repository;

import ca.magex.crm.api.filters.OptionsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.repositories.CrmOptionRepository;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.id.OptionIdentifier;

/**
 * Implementation of the Crm Option Repository backed by a MongoDB
 * 
 * @author Jonny
 */
public class MongoOptionRepository implements CrmOptionRepository {


	@Override
	public FilteredPage<Option> findOptions(OptionsFilter filter, Paging paging) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long countOptions(OptionsFilter filter) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Option findOption(OptionIdentifier optionId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Option saveOption(Option option) {
		// TODO Auto-generated method stub
		return null;
	}

}
