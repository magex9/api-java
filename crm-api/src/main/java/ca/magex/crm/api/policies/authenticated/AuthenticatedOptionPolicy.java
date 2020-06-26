package ca.magex.crm.api.policies.authenticated;

import static ca.magex.crm.api.authentication.CrmAuthenticationService.SYS_ADMIN;

import ca.magex.crm.api.authentication.CrmAuthenticationService;
import ca.magex.crm.api.policies.CrmOptionPolicy;
import ca.magex.crm.api.policies.basic.BasicOptionPolicy;
import ca.magex.crm.api.services.CrmOptionService;
import ca.magex.crm.api.system.Identifier;

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
	public boolean canCreateOption(String typeCode) {
		if (!delegate.canCreateOption(typeCode)) {
			return false;
		}
		/* only a CRM Admin can create a Option */
		return auth.isUserInRole(SYS_ADMIN);
	}

	@Override
	public boolean canViewOptions(String typeCode) {
		if (!delegate.canViewOptions(typeCode)) {
			return false;
		}
		/* anybody can view options */
		return true;
	}
	
	@Override
	public boolean canViewOption(String typeCode, String optionCode) {
		if (!delegate.canViewOption(typeCode, optionCode)) {
			return false;
		}
		/* anybody can view options */
		return true;
	}

	@Override
	public boolean canViewOption(Identifier optionId) {
		if (!delegate.canViewOption(optionId)) {
			return false;
		}
		/* anybody can view a option */
		return true;
	}

	@Override
	public boolean canUpdateOption(Identifier optionId) {
		if (!delegate.canUpdateOption(optionId)) {
			return false;
		}
		/* only a CRM Admin can update a Option */
		return auth.isUserInRole(SYS_ADMIN);
	}

	@Override
	public boolean canEnableOption(Identifier optionId) {
		if (!delegate.canEnableOption(optionId)) {
			return false;
		}
		/* only a CRM Admin can enable a Option */
		return auth.isUserInRole(SYS_ADMIN);
	}

	@Override
	public boolean canDisableOption(Identifier optionId) {
		if (!delegate.canDisableOption(optionId)) {
			return false;
		}
		/* only a CRM Admin can disable a Option */
		return auth.isUserInRole(SYS_ADMIN);
	}
}