package ca.magex.crm.api.repositories;

import ca.magex.crm.api.filters.OptionsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.store.CrmStore;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Option;

public interface CrmOptionRepository {

	public static final String CONTEXT = "/options";
	
	default Identifier generateOptionId() {
		return CrmStore.generateId(CONTEXT);
	}
	
	public FilteredPage<Option> findOptions(OptionsFilter filter, Paging paging);
	
	public long countOptions(OptionsFilter filter);
	
	public Option findOption(Identifier lookupId);

	public Option saveOption(Option lookup);

}
