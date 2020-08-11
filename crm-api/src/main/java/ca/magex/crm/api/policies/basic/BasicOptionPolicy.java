package ca.magex.crm.api.policies.basic;

import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.policies.CrmOptionPolicy;
import ca.magex.crm.api.services.CrmOptionService;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.OptionIdentifier;

public class BasicOptionPolicy implements CrmOptionPolicy {

	private CrmOptionService options;

	/**
	 * Basic Option Policy handles presence and status checks require for policy approval
	 * 
	 * @param options
	 */
	public BasicOptionPolicy(CrmOptionService options) {
		this.options = options;
	}

	@Override
	public boolean canCreateOption(Type type) {
		return type != null && type.isExtendable();
	}

	@Override
	public boolean canViewOptions(Type type) {
		/* can always view options */
		return type != null;
	}
	
	@Override
	public boolean canViewOption(Type type, String optionCode) {
		/* can view a specific option if it exists */
		try {
			return options.findOptionByCode(type, optionCode) != null;
		} catch (ItemNotFoundException e) {
			throw new ItemNotFoundException("Option Code '" + optionCode + "'");
		}
	}

	@Override
	public boolean canViewOption(OptionIdentifier optionId) {
		/* can view a specific option if it exists */
		if (options.findOption(optionId) == null) {
			throw new ItemNotFoundException("Option ID '" + optionId + "'");
		}
		return true;
	}

	@Override
	public boolean canUpdateOption(OptionIdentifier optionId) {
		/* can view a specific option if it exists and is active */
		Option option = options.findOption(optionId);
		if (option == null) {
			throw new ItemNotFoundException("Option ID '" + optionId + "'");
		}
		return options.findOption(optionId).getStatus() == Status.ACTIVE;
	}

	@Override
	public boolean canEnableOption(OptionIdentifier optionId) {
		/* can enable a specific option if it exists */
		if (options.findOption(optionId) == null) {
			throw new ItemNotFoundException("Option ID '" + optionId + "'");
		}
		return true;
	}

	@Override
	public boolean canDisableOption(OptionIdentifier optionId) {
		/* can disable a specific option if it exists */
		if (options.findOption(optionId) == null) {
			throw new ItemNotFoundException("Option ID '" + optionId + "'");
		}
		return true;
	}

}