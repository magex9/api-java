package ca.magex.crm.amnesia.services;

import ca.magex.crm.amnesia.AmnesiaDB;
import ca.magex.crm.api.policies.CrmPolicies;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public class AmnesiaAnonymousPolicies implements CrmPolicies {
	
	private AmnesiaDB db;
	
	public AmnesiaAnonymousPolicies(AmnesiaDB db) {
		this.db = db;
	}
	
	@Override
	public boolean canCreateOrganization() {
		return true;
	}

	@Override
	public boolean canViewOrganization(Identifier organizationId) {
		try {
			db.findOrganization(organizationId);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean canUpdateOrganization(Identifier organizationId) {
		return canViewOrganization(organizationId);
	}

	@Override
	public boolean canEnableOrganization(Identifier organizationId) {
		try {
			return !db.findOrganization(organizationId).getStatus().equals(Status.ACTIVE);
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean canDisableOrganization(Identifier organizationId) {
		try {
			return db.findOrganization(organizationId).getStatus().equals(Status.ACTIVE);
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean canCreateLocationForOrganization(Identifier organizationId) {
		return true;
	}
	
	@Override
	public boolean canViewLocation(Identifier locationId) {
		try {
			db.findLocation(locationId);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean canUpdateLocation(Identifier locationId) {
		return canViewLocation(locationId);
	}

	@Override
	public boolean canEnableLocation(Identifier locationId) {
		try {
			return !db.findLocation(locationId).getStatus().equals(Status.ACTIVE);
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean canDisableLocation(Identifier locationId) {
		try {
			return db.findLocation(locationId).getStatus().equals(Status.ACTIVE);
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean canCreatePersonForOrganization(Identifier organizationId) {
		return true;
	}

	@Override
	public boolean canViewPerson(Identifier personId) {
		try {
			db.findPerson(personId);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean canUpdatePerson(Identifier personId) {
		return canViewPerson(personId);
	}

	@Override
	public boolean canEnablePerson(Identifier personId) {
		try {
			return !db.findPerson(personId).getStatus().equals(Status.ACTIVE);
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean canDisablePerson(Identifier personId) {
		try {
			return db.findPerson(personId).getStatus().equals(Status.ACTIVE);
		} catch (Exception e) {
			return false;
		}
	}
	
	@Override
	public boolean canCreateUserForPerson(Identifier personId) {
		return true;
	}
	
	@Override
	public boolean canViewUser(Identifier userId) {
		try {
			db.findUser(userId);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean canUpdateUserRole(Identifier userId) {
		return canViewUser(userId);
	}

	@Override
	public boolean canUpdateUserPassword(Identifier userId) {
		return canViewUser(userId);
	}
}
