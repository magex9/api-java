package ca.magex.crm.api.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.OptionsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;

public interface CrmOptionService {

	default Option prototypeOption(Identifier parentId, String typeCode, Localized name) {
		return new Option(null, parentId, Type.of(typeCode), Status.PENDING, true, name);
	}

	default Option createOption(Option option) {
		return createOption(option.getParentId(), option.getType().getCode(), option.getName());
	}

	Option createOption(Identifier parentId, String typeCode, Localized name);

	Option findOption(Identifier optionId);

	Option findOptionByCode(String typeCode, String optionCode);
	
	default Option findOptionByCode(Type type, String optionCode) {
		return findOptionByCode(type.getCode(), optionCode);
	}
	
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

	static List<Message> validateOption(Crm crm, Option option) {
		List<Message> messages = new ArrayList<Message>();

		// Status
		if (option.getStatus() == null) {
			messages.add(new Message(option.getOptionId(), "error", "status", new Localized(Lang.ENGLISH, "Status is mandatory for a option")));
		} else if (option.getStatus() == Status.PENDING && option.getOptionId() != null) {
			messages.add(new Message(option.getOptionId(), "error", "status", new Localized(Lang.ENGLISH, "Pending statuses should not have identifiers")));
		}

		// Must be a valid option code
		if (StringUtils.isBlank(option.getCode())) {
			messages.add(new Message(option.getOptionId(), "error", "code", new Localized(Lang.ENGLISH, "Option code must not be blank")));
		} else if (!option.getCode().matches("[A-Z0-9_]{1,20}")) {
			messages.add(new Message(option.getOptionId(), "error", "code", new Localized(Lang.ENGLISH, "Option code must match: [A-Z0-9_]{1,20}")));
		}

		// Make sure the existing code didn't change
		if (option.getOptionId() != null) {
			try {
				if (!crm.findOption(option.getOptionId()).getCode().equals(option.getCode())) {
					messages.add(new Message(option.getOptionId(), "error", "code", new Localized(Lang.ENGLISH, "Option code must not change during updates")));
				}
			} catch (ItemNotFoundException e) {
				/* no existing option, so don't care */
			}
		}

		// Make sure the code is unique
		FilteredPage<Option> options = crm.findOptions(crm.defaultOptionsFilter().withOptionCode(option.getCode()), OptionsFilter.getDefaultPaging().allItems());
		for (Option existing : options.getContent()) {
			if (!existing.getOptionId().equals(option.getOptionId())) {
				messages.add(new Message(option.getOptionId(), "error", "code", new Localized(Lang.ENGLISH, "Duplicate code found in another option: " + existing.getOptionId())));
			}
		}

		// Make sure there is an English description
		if (StringUtils.isBlank(option.getName(Lang.ENGLISH))) {
			messages.add(new Message(option.getOptionId(), "error", "englishName", new Localized(Lang.ENGLISH, "An English description is required")));
		} else if (option.getName(Lang.ENGLISH).length() > 50) {
			messages.add(new Message(option.getOptionId(), "error", "englishName", new Localized(Lang.ENGLISH, "English name must be 50 characters or less")));
		}

		// Make sure there is a French description
		if (StringUtils.isBlank(option.getName(Lang.FRENCH))) {
			messages.add(new Message(option.getOptionId(), "error", "frenchName", new Localized(Lang.ENGLISH, "An French description is required")));
		} else if (option.getName(Lang.FRENCH).length() > 50) {
			messages.add(new Message(option.getOptionId(), "error", "frenchName", new Localized(Lang.ENGLISH, "French name must be 50 characters or less")));
		}

		return messages;
	}

}