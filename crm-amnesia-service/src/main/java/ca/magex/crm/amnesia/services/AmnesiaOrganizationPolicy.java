package ca.magex.crm.amnesia.services;

import static ca.magex.crm.amnesia.AmnesiaDB.CRM_ADMIN;
import static ca.magex.crm.amnesia.AmnesiaDB.RE_ADMIN;

import ca.magex.crm.amnesia.AmnesiaDB;
import ca.magex.crm.api.services.CrmOrganizationPolicy;
import ca.magex.crm.api.system.Identifier;

public class AmnesiaOrganizationPolicy implements CrmOrganizationPolicy {
	
	private AmnesiaDB db;

	public AmnesiaOrganizationPolicy(AmnesiaDB db) {
		this.db = db;
	}
	
	public boolean canCreateOrganization() {
		if (!db.isAuthenticated())
			return false;
		if (db.userHasRole(CRM_ADMIN))
			return true;
		return false;
	}

	public boolean canViewOrganization(Identifier organizationId) {
		if (!db.isAuthenticated())
			return false;
		if (db.userHasRole(CRM_ADMIN))
			return true;
		if (db.userBelongsToOrg(organizationId))
			return true;
		return false;
	}

	public boolean canUpdateOrganization(Identifier organizationId) {
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

	public boolean canEnableOrganization(Identifier organizationId) {
		return canCreateOrganization();
	}

	public boolean canDisableOrganization(Identifier organizationId) {
		return canCreateOrganization();
	}

}
