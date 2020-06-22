package ca.magex.crm.api.policies.authenticated;

import static ca.magex.crm.api.authentication.CrmAuthenticationService.SYS_ADMIN;

import ca.magex.crm.api.authentication.CrmAuthenticationService;
import ca.magex.crm.api.policies.CrmLookupPolicy;
import ca.magex.crm.api.policies.basic.BasicLookupPolicy;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.api.system.Identifier;

public class AuthenticatedLookupPolicy implements CrmLookupPolicy {

	private CrmAuthenticationService auth;
	
	private CrmLookupPolicy delegate;
	
	public AuthenticatedLookupPolicy(
			CrmAuthenticationService auth,
			CrmLookupService lookups) {
		this.auth = auth;
		this.delegate = new BasicLookupPolicy(lookups);
	}

	@Override
	public boolean canCreateLookup() {
		if (!delegate.canCreateLookup()) {
			return false;
		}
		/* only a CRM Admin can create a Lookup */
		return auth.isUserInRole(SYS_ADMIN);
	}

	@Override
	public boolean canViewLookup(String lookup) {
		if (!delegate.canViewLookup(lookup)) {
			return false;
		}
		/* anybody can view a lookup */
		return true;
	}

	@Override
	public boolean canViewLookup(Identifier lookupId) {
		if (!delegate.canViewLookup(lookupId)) {
			return false;
		}
		/* anybody can view a lookup */
		return true;
	}

	@Override
	public boolean canUpdateLookup(Identifier lookupId) {
		if (!delegate.canUpdateLookup(lookupId)) {
			return false;
		}
		/* only a CRM Admin can update a Lookup */
		return auth.isUserInRole(SYS_ADMIN);
	}

	@Override
	public boolean canEnableLookup(Identifier lookupId) {
		if (!delegate.canEnableLookup(lookupId)) {
			return false;
		}
		/* only a CRM Admin can enable a Lookup */
		return auth.isUserInRole(SYS_ADMIN);
	}

	@Override
	public boolean canDisableLookup(Identifier lookupId) {
		if (!delegate.canDisableLookup(lookupId)) {
			return false;
		}
		/* only a CRM Admin can disable a Lookup */
		return auth.isUserInRole(SYS_ADMIN);
	}

}
