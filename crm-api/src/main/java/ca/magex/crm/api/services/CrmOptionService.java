package ca.magex.crm.api.services;

import java.util.List;

import ca.magex.crm.api.filters.OptionsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Status;

public interface CrmOptionService {

	default Option prototypeOption(Identifier lookupId, Localized name) {
		return new Option(null, lookupId, Status.PENDING, name);
	}

	default Option createOption(Option option) {
		return createOption(option.getLookupId(), option.getName());
	}

	Option createOption(Identifier lookupId, Localized name);

	Option findOption(Identifier optionId);

	default Option findOptionByCode(Identifier lookupId, String optionCode) {
		return findOptions(
			defaultOptionsFilter().withLookupId(lookupId).withOptionCode(optionCode), 
			OptionsFilter.getDefaultPaging()
		).getSingleItem();
	};

	Option updateOptionName(Identifier optionId, Localized name);

	Option enableOption(Identifier optionId);

	Option disableOption(Identifier optionId);

	FilteredPage<Option> findOptions(OptionsFilter filter, Paging paging);

	default FilteredPage<Option> findOptions(OptionsFilter filter) {
		return findOptions(filter, defaultOptionPaging());
	}
	
	default List<Option> findOptions() {
		return findOptions(
			defaultOptionsFilter(), 
			OptionsFilter.getDefaultPaging().allItems()
		).getContent();
	}
	
	default OptionsFilter defaultOptionsFilter() {
		return new OptionsFilter();
	};
	
	default Paging defaultOptionPaging() {
		return new Paging(OptionsFilter.getSortOptions().get(0));
	}

}