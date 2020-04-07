package ca.magex.crm.amnesia.services;

import static ca.magex.crm.amnesia.AmnesiaDB.CRM_ADMIN;
import static ca.magex.crm.amnesia.AmnesiaDB.RE_ADMIN;

import org.springframework.stereotype.Component;

import ca.magex.crm.amnesia.AmnesiaDB;
import ca.magex.crm.api.services.CrmLocationPolicy;
import ca.magex.crm.api.system.Identifier;

@Component
public class AmnesiaLocationPolicy implements CrmLocationPolicy {
	
	private AmnesiaDB db;

	public AmnesiaLocationPolicy(AmnesiaDB db) {
		this.db = db;
	}
	
	public boolean canCreateLocationForOrganization(Identifier organizationId) {
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

	public boolean canViewLocation(Identifier locationId) {
		if (!db.isAuthenticated())
			return false;
		if (db.userHasRole(CRM_ADMIN))
			return true;
		if (db.userBelongsToOrg(db.findLocation(locationId).getOrganizationId()))
			return true;
		return false;
	}

	public boolean canUpdateLocation(Identifier locationId) {
		if (!db.isAuthenticated())
			return false;
		if (db.userHasRole(CRM_ADMIN))
			return true;
		if (db.userBelongsToOrg(db.findLocation(locationId).getOrganizationId()))
			return false;
		if (db.userHasRole(RE_ADMIN))
			return true;
		return false;
	}

	public boolean canEnableLocation(Identifier locationId) {
		return canUpdateLocation(locationId);
	}

	public boolean canDisableLocation(Identifier locationId) {
		return canUpdateLocation(locationId);
	}

}
