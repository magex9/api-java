package ca.magex.crm.api.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.OptionsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.PhraseIdentifier;
import ca.magex.crm.api.system.id.MessageTypeIdentifier;
import ca.magex.crm.api.system.id.OptionIdentifier;

public interface CrmOptionService {

	default Option prototypeOption(OptionIdentifier parentId, Type type, Localized name) {
		return new Option(null, parentId, type, Status.PENDING, true, name);
	}

	default Option createOption(Option prototype) {
		return createOption(prototype.getParentId(), prototype.getType(), prototype.getName());
	}

	Option createOption(OptionIdentifier parentId, Type type, Localized name);

	Option findOption(OptionIdentifier optionId);

	Option findOptionByCode(Type type, String optionCode);
	
	Option updateOptionName(OptionIdentifier optionId, Localized name);

	Option enableOption(OptionIdentifier optionId);

	Option disableOption(OptionIdentifier optionId);

	FilteredPage<Option> findOptions(OptionsFilter filter, Paging paging);
	
	default PhraseIdentifier findMessageId(String key) {
		return findOptionByCode(Type.PHRASE, key.replaceAll("\\.", "/").toUpperCase()).getOptionId();
	}

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
		
		MessageTypeIdentifier error = crm.findOptionByCode(Type.MESSAGE_TYPE, "ERROR").getOptionId();

		// Status
		if (option.getStatus() == null) {
			messages.add(new Message(option.getOptionId(), error, "status", crm.findMessageId("validation.field.required")));
		} else if (option.getStatus() == Status.PENDING && option.getOptionId() != null) {
			messages.add(new Message(option.getOptionId(), error, "status", crm.findMessageId("validation.status.pending")));
		}

		// Must be a valid option code
		if (StringUtils.isBlank(option.getCode())) {
			messages.add(new Message(option.getOptionId(), error, "code", crm.findMessageId("validation.field.required")));
		} else if (!option.getCode().matches("([A-Z0-9/]{1,20})(/[A-Z0-9/]{1,20})*")) {
			messages.add(new Message(option.getOptionId(), error, "code", crm.findMessageId("validation.field.format")));
		}

		// Make sure the existing code didn't change
		if (option.getOptionId() != null) {
			try {
				if (!crm.findOption(option.getOptionId()).getCode().equals(option.getCode()))
					messages.add(new Message(option.getOptionId(), error, "code", crm.findMessageId("validation.option.immutable")));
			} catch (ItemNotFoundException e) {
				/* no existing option, so don't care */
			}
		}

		// Make sure the code is unique
		for (Option existing : crm.findOptions(crm.defaultOptionsFilter().withType(option.getType()).withOptionCode(option.getCode()), OptionsFilter.getDefaultPaging().allItems()).getContent()) {
			if (!existing.getOptionId().equals(option.getOptionId())) {
				messages.add(new Message(option.getOptionId(), error, "code", crm.findMessageId("validation.option.duplicate")));
			}
		}

		// Make sure the english name is unique
		for (Option existing : crm.findOptions(crm.defaultOptionsFilter().withType(option.getType()).withName(Lang.ENGLISH, option.getName(Lang.ENGLISH)), OptionsFilter.getDefaultPaging().allItems()).getContent()) {
			if (!existing.getOptionId().equals(option.getOptionId())) {
				messages.add(new Message(option.getOptionId(), error, "englishName", crm.findMessageId("validation.option.duplicate")));
			}
		}

		// Make sure the french name is unique
		for (Option existing : crm.findOptions(crm.defaultOptionsFilter().withType(option.getType()).withName(Lang.FRENCH, option.getName(Lang.FRENCH)), OptionsFilter.getDefaultPaging().allItems()).getContent()) {
			if (!existing.getOptionId().equals(option.getOptionId())) {
				messages.add(new Message(option.getOptionId(), error, "frenchName", crm.findMessageId("validation.option.duplicate")));
			}
		}

		// Make sure there is an English description
		if (StringUtils.isBlank(option.getName(Lang.ENGLISH))) {
			messages.add(new Message(option.getOptionId(), error, "englishName", crm.findMessageId("validation.field.required")));
		} else if (option.getName(Lang.ENGLISH).length() > 50) {
			messages.add(new Message(option.getOptionId(), error, "englishName", crm.findMessageId("validation.field.maxlength")));
		}

		// Make sure there is a French description
		if (StringUtils.isBlank(option.getName(Lang.FRENCH))) {
			messages.add(new Message(option.getOptionId(), error, "frenchName", crm.findMessageId("validation.field.required")));
		} else if (option.getName(Lang.FRENCH).length() > 50) {
			messages.add(new Message(option.getOptionId(), error, "frenchName", crm.findMessageId("validation.field.maxlength")));
		}

		return messages;
	}

}