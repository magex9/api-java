package ca.magex.crm.api.services;

import ca.magex.crm.api.filters.LookupsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Lookup;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Status;

public interface CrmLookupService {
	
	public static final String STATUSES_LOOKUP = "STATUSES";
	
	public static final String LOCALES_LOOKUP = "LOCALES";
	
	public static final String SALUTATION_LOOKUP = "SALUTATION";
	
	public static final String LANGUAGE_LOOKUP = "LANGUAGE";
	
	public static final String COUNTRY_LOOKUP = "COUNTRY";
	
	public static final String CA_PROVINCE_LOOKUP = "CA_PROVINCE";
	
	public static final String US_PROVINCE_LOOKUP = "US_PROVINCE";
	
	public static final String MX_PROVINCE_LOOKUP = "MX_PROVINCE";
	
	default Lookup prototypeLookup(Localized name, Option parent) {
		return new Lookup(null, Status.PENDING, true, name, parent);
	}

	default Lookup createLookup(Lookup lookup) {
		return createLookup(lookup.getName(), lookup.getParent());
	}

	Lookup createLookup(Localized name, Option parent);
	
	Lookup findLookup(
		Identifier lookupId
	);
	
	Lookup updateLookupName(Identifier lookupId, Localized name);
	
	default Lookup findLookupByCode(String code) {
		return (Lookup)findLookups(
			defaultLookupsFilter().withLookupCode(code),
			LookupsFilter.getDefaultPaging()
		).getSingleItem();
	}
	
	default Lookup findLookupByTypeWithParent(Lookup parent) {
		return (Lookup)findLookups(
			defaultLookupsFilter().withParentCode(parent.getCode()),
			LookupsFilter.getDefaultPaging()
		).getSingleItem();
	}
		
	Lookup enableLookup(Identifier lookupId);

	Lookup disableLookup(Identifier lookupId);

	default LookupsFilter defaultLookupsFilter() {
		return new LookupsFilter();
	};

	FilteredPage<Lookup> findLookups(LookupsFilter filter, Paging paging);

	default FilteredPage<Lookup> findLookups(LookupsFilter filter) {
		return findLookups(filter, defaultLookupPaging());
	}

	default Paging defaultLookupPaging() {
		return new Paging(LookupsFilter.getSortOptions().get(0));
	}
    
}
