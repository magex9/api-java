package ca.magex.crm.api.policies.authenticated;

import static ca.magex.crm.api.authentication.CrmAuthenticationService.SYS_ADMIN;

import ca.magex.crm.api.authentication.CrmAuthenticationService;
import ca.magex.crm.api.policies.CrmOptionPolicy;
import ca.magex.crm.api.policies.basic.BasicOptionPolicy;
import ca.magex.crm.api.services.CrmOptionService;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.OptionIdentifier;

public class AuthenticatedOptionPolicy implements CrmOptionPolicy {
	
	private CrmAuthenticationService auth;

	private CrmOptionPolicy delegate;
	
	/**
	 * Authenticated Permission Policy handles options and association checks required for policy approval
	 * 
	 * @param auth
	 * @param permissions
	 * @param users
	 */
	public AuthenticatedOptionPolicy(
			CrmAuthenticationService auth,
			CrmOptionService options) {
		this.auth = auth;
		this.delegate = new BasicOptionPolicy(options);
	}

	@Override
	public boolean canCreateOption(Type type) {
		if (!delegate.canCreateOption(type)) {
			return false;
		}
		/* only a CRM Admin can create a Option */
		return auth.isUserInRole(SYS_ADMIN);
	}

	@Override
	public boolean canViewOptions(Type type) {
		if (!delegate.canViewOptions(type)) {
			return false;
		}
		/* anybody can view options */
		return true;
	}
	
	@Override
	public boolean canViewOption(Type type, String optionCode) {
		if (!delegate.canViewOption(type, optionCode)) {
			return false;
		}
		/* anybody can view options */
		return true;
	}

	@Override
	public boolean canViewOption(OptionIdentifier optionId) {
		if (!delegate.canViewOption(optionId)) {
			return false;
		}
		/* anybody can view a option */
		return true;
	}

	@Override
	public boolean canUpdateOption(OptionIdentifier optionId) {
		if (!delegate.canUpdateOption(optionId)) {
			return false;
		}
		/* only a CRM Admin can update a Option */
		return auth.isUserInRole(SYS_ADMIN);
	}

	@Override
	public boolean canEnableOption(OptionIdentifier optionId) {
		if (!delegate.canEnableOption(optionId)) {
			return false;
		}
		/* only a CRM Admin can enable a Option */
		return auth.isUserInRole(SYS_ADMIN);
	}

	@Override
	public boolean canDisableOption(OptionIdentifier optionId) {
		if (!delegate.canDisableOption(optionId)) {
			return false;
		}
		/* only a CRM Admin can disable a Option */
		return auth.isUserInRole(SYS_ADMIN);
	}
}