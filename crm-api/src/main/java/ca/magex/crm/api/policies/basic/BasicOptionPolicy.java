package ca.magex.crm.api.policies.basic;

import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.policies.CrmOptionPolicy;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.api.services.CrmOptionService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lookup;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Status;

public class BasicOptionPolicy implements CrmOptionPolicy {

	private CrmLookupService lookups;
	
	private CrmOptionService options;

	/**
	 * Basic Option Policy handles presence and status checks require for policy approval
	 * 
	 * @param options
	 */
	public BasicOptionPolicy(CrmLookupService lookups, CrmOptionService options) {
		this.lookups = lookups;
		this.options = options;
	}

	@Override
	public boolean canCreateOption(Identifier lookupId) {
		Lookup lookup = lookups.findLookup(lookupId);
		/* can create a option for this lookup if it exists */
		if (lookup == null) {
			throw new ItemNotFoundException("Lookup ID '" + lookupId + "'");
		}
		return lookup.isMutable();
	}

	@Override
	public boolean canViewOptions(Identifier lookupId) {
		/* can always view options */
		return true;
	}
	
	@Override
	public boolean canViewOption(Identifier lookupId, String optionCode) {
		/* can view a specific option if it exists */
		try {
			return options.findOptionByCode(lookupId, optionCode) != null;
		} catch (ItemNotFoundException e) {
			throw new ItemNotFoundException("Option Code '" + optionCode + "'");
		}
	}

	@Override
	public boolean canViewOption(Identifier optionId) {
		/* can view a specific option if it exists */
		if (options.findOption(optionId) == null) {
			throw new ItemNotFoundException("Option ID '" + optionId + "'");
		}
		return true;
	}

	@Override
	public boolean canUpdateOption(Identifier optionId) {
		/* can view a specific option if it exists and is active */
		Option option = options.findOption(optionId);
		if (option == null) {
			throw new ItemNotFoundException("Option ID '" + optionId + "'");
		}
		return options.findOption(optionId).getStatus() == Status.ACTIVE;
	}

	@Override
	public boolean canEnableOption(Identifier optionId) {
		/* can enable a specific option if it exists */
		if (options.findOption(optionId) == null) {
			throw new ItemNotFoundException("Option ID '" + optionId + "'");
		}
		return true;
	}

	@Override
	public boolean canDisableOption(Identifier optionId) {
		/* can disable a specific option if it exists */
		if (options.findOption(optionId) == null) {
			throw new ItemNotFoundException("Option ID '" + optionId + "'");
		}
		return true;
	}

}