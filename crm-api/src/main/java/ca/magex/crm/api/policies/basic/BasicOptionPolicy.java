package ca.magex.crm.api.policies.basic;

import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.policies.CrmOptionPolicy;
import ca.magex.crm.api.services.CrmOptionService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;

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
	public boolean canCreateOption(String typeCode) {
		return Type.of(typeCode).isExtendable();
	}

	@Override
	public boolean canViewOptions(String typeCode) {
		/* can always view options */
		try {
			return Type.of(typeCode) != null;
		} catch (ItemNotFoundException e) {
			return false;
		}
	}
	
	@Override
	public boolean canViewOption(String typeCode, String optionCode) {
		/* can view a specific option if it exists */
		try {
			return options.findOptionByCode(typeCode, optionCode) != null;
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