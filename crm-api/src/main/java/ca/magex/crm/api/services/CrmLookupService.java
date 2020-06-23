package ca.magex.crm.api.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.LookupsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Lookup;
import ca.magex.crm.api.system.Message;
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

	static List<Message> validateLookup(Crm crm, Lookup lookup) {
		List<Message> messages = new ArrayList<Message>();

		// Status
		if (lookup.getStatus() == null) {
			messages.add(new Message(lookup.getLookupId(), "error", "status", new Localized(Lang.ENGLISH, "Status is mandatory for a lookup")));
		} else if (lookup.getStatus() == Status.PENDING && lookup.getLookupId() != null) {
			messages.add(new Message(lookup.getLookupId(), "error", "status", new Localized(Lang.ENGLISH, "Pending statuses should not have identifiers")));
		}

		// Must be a valid group code
		if (StringUtils.isBlank(lookup.getCode())) {
			messages.add(new Message(lookup.getLookupId(), "error", "code", new Localized(Lang.ENGLISH, "Lookup code must not be blank")));
		} else if (!lookup.getCode().matches("[A-Z0-9_]{1,20}")) {
			messages.add(new Message(lookup.getLookupId(), "error", "code", new Localized(Lang.ENGLISH, "Lookup code must match: [A-Z0-9_]{1,20}")));
		}

		// Make sure the existing code didn't change
		if (lookup.getLookupId() != null) {
			try {			
				if (!crm.findLookup(lookup.getLookupId()).getCode().equals(lookup.getCode())) {
					messages.add(new Message(lookup.getLookupId(), "error", "code", new Localized(Lang.ENGLISH, "Lookup code must not change during updates")));
				}
			} catch (ItemNotFoundException e) {
				/* no existing group, so don't care */
			}
		}

		// Make sure the code is unique
		FilteredPage<Lookup> lookups = crm.findLookups(crm.defaultLookupsFilter().withLookupCode(lookup.getCode()), LookupsFilter.getDefaultPaging().allItems());
		for (Lookup existing : lookups.getContent()) {
			if (!existing.getLookupId().equals(lookup.getLookupId())) {
				messages.add(new Message(lookup.getLookupId(), "error", "code", new Localized(Lang.ENGLISH, "Duplicate code found in another lookup: " + existing.getLookupId())));
			}
		}

		// Make sure there is an English description
		if (StringUtils.isBlank(lookup.getName(Lang.ENGLISH))) {
			messages.add(new Message(lookup.getLookupId(), "error", "englishName", new Localized(Lang.ENGLISH, "An English description is required")));
		} else if (lookup.getName(Lang.ENGLISH).length() > 50) {
			messages.add(new Message(lookup.getLookupId(), "error", "englishName", new Localized(Lang.ENGLISH, "English name must be 50 characters or less")));
		}

		// Make sure there is a French description
		if (StringUtils.isBlank(lookup.getName(Lang.FRENCH))) {
			messages.add(new Message(lookup.getLookupId(), "error", "frenchName", new Localized(Lang.ENGLISH, "An French description is required")));
		} else if (lookup.getName(Lang.FRENCH).length() > 50) {
			messages.add(new Message(lookup.getLookupId(), "error", "frenchName", new Localized(Lang.ENGLISH, "French name must be 50 characters or less")));
		}

		return messages;
	}
    
}
