package ca.magex.crm.api.repositories;

import ca.magex.crm.api.filters.LookupsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lookup;

public interface CrmLookupRepository {
	
	public FilteredPage<Lookup> findLookups(LookupsFilter filter, Paging paging);
	
	public long countLookups(LookupsFilter filter);
	
	public Lookup findLookup(Identifier lookupId);

	public Lookup saveLookup(Lookup lookup);

}
