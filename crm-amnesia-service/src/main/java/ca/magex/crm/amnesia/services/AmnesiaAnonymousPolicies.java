package ca.magex.crm.amnesia.services;

import ca.magex.crm.api.policies.CrmPolicies;
import ca.magex.crm.api.system.Identifier;

public class AmnesiaAnonymousPolicies implements CrmPolicies {
	
	@Override
	public boolean canCreateOrganization() {
		return true;
	}

	@Override
	public boolean canViewOrganization(Identifier organizationId) {
		return true;
	}

	@Override
	public boolean canUpdateOrganization(Identifier organizationId) {
		return true;
	}

	@Override
	public boolean canEnableOrganization(Identifier organizationId) {
		return true;
	}

	@Override
	public boolean canDisableOrganization(Identifier organizationId) {
		return true;
	}

	@Override
	public boolean canCreateLocationForOrganization(Identifier organizationId) {
		return true;
	}
	
	@Override
	public boolean canViewLocation(Identifier locationId) {
		return true;
	}

	@Override
	public boolean canUpdateLocation(Identifier locationId) {
		return true;
	}

	@Override
	public boolean canEnableLocation(Identifier locationId) {
		return true;
	}

	@Override
	public boolean canDisableLocation(Identifier locationId) {
		return true;
	}

	@Override
	public boolean canCreatePersonForOrganization(Identifier organizationId) {
		return true;
	}

	@Override
	public boolean canViewPerson(Identifier personId) {
		return true;
	}

	@Override
	public boolean canUpdatePerson(Identifier personId) {
		return true;
	}

	@Override
	public boolean canEnablePerson(Identifier personId) {
		return true;
	}

	@Override
	public boolean canDisablePerson(Identifier personId) {
		return true;
	}
	
	@Override
	public boolean canCreateUserForPerson(Identifier personId) {
		return true;
	}
	
	@Override
	public boolean canViewUser(Identifier userId) {
		return true;
	}

	@Override
	public boolean canUpdateUserRole(Identifier personId) {
		return true;
	}

	@Override
	public boolean canUpdateUserPassword(Identifier personId) {
		return true;
	}
}
