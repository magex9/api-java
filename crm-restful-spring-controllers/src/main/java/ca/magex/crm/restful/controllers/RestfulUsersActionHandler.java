package ca.magex.crm.restful.controllers;

import java.util.ArrayList;
import java.util.List;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.crm.UserSummary;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.id.UserIdentifier;
import ca.magex.crm.restful.models.RestfulAction;

public class RestfulUsersActionHandler<T extends UserSummary> implements RestfulActionHandler<T> {
	
	public List<RestfulAction> buildActions(UserSummary user, Crm crm) {
		UserIdentifier userId = user.getUserId();
		List<RestfulAction> actions = new ArrayList<>();
		if (crm.canViewUser(userId))
			actions.add(buildAction("view", new Localized("VIEW", "View", "Vue"), "get", userId));
		if (crm.canDisableUser(userId))
			actions.add(buildAction("disable", new Localized("INACTIVATE", "Inactivate", "Inactivate"), "put", userId + "/disable"));
		if (crm.canEnableUser(userId))
			actions.add(buildAction("enable", new Localized("ACTIVATE", "Activate", "Activate"), "put", userId + "/enable"));
		return actions;
	}
	
}
