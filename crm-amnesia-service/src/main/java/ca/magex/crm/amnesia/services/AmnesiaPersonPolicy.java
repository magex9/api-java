package ca.magex.crm.amnesia.services;

import static ca.magex.crm.amnesia.AmnesiaDB.CRM_ADMIN;
import static ca.magex.crm.amnesia.AmnesiaDB.RE_ADMIN;

import ca.magex.crm.amnesia.AmnesiaDB;
import ca.magex.crm.api.services.CrmPersonPolicy;
import ca.magex.crm.api.system.Identifier;

public class AmnesiaPersonPolicy implements CrmPersonPolicy {
	
	private AmnesiaDB db;

	public AmnesiaPersonPolicy(AmnesiaDB db) {
		this.db = db;
	}

	public boolean canCreatePersonForOrganization(Identifier organizationId) {
		if (!db.isAuthenticated())
			return false;
		if (db.userHasRole(CRM_ADMIN))
			return true;
		if (!db.userBelongsToOrg(organizationId))
			return false;
		if (db.userHasRole(RE_ADMIN))
			return true;
		return false;
	}

	public boolean canViewPerson(Identifier personId) {
		if (!db.isAuthenticated())
			return false;
		if (db.userHasRole(CRM_ADMIN))
			return true;
		if (db.userBelongsToOrg(db.findPerson(personId).getOrganizationId()))
			return true;
		return false;
	}

	public boolean canUpdatePerson(Identifier personId) {
		if (!db.isAuthenticated())
			return false;
		if (db.userHasRole(CRM_ADMIN))
			return true;
		if (db.userBelongsToOrg(db.findPerson(personId).getOrganizationId()))
			return false;
		if (db.userHasRole(RE_ADMIN))
			return true;
		return false;
	}

	public boolean canEnablePerson(Identifier personId) {
		return canUpdatePerson(personId);
	}

	public boolean canDisablePerson(Identifier personId) {
		return canUpdatePerson(personId);
	}

	public boolean canUpdateUserRole(Identifier personId) {
		return canUpdatePerson(personId);
	}

}
