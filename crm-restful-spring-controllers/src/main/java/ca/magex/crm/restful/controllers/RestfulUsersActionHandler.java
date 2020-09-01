package ca.magex.crm.restful.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.crm.UserSummary;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.id.UserIdentifier;
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonElement;

public class RestfulUsersActionHandler<T extends UserSummary> implements RestfulActionHandler<T> {
	
	public JsonArray buildActions(UserSummary user, Crm crm, Locale locale) {
		UserIdentifier userId = user.getUserId();
		List<JsonElement> elements = new ArrayList<>();
		if (crm.canViewUser(userId))
			elements.add(buildAction(crm, locale, "view", new Localized("VIEW", "View", "Vue"), "get", userId));
		if (crm.canDisableUser(userId))
			elements.add(buildAction(crm, locale, "disable", new Localized("INACTIVATE", "Inactivate", "Inactivate"), "put", userId + "/disable"));
		if (crm.canEnableUser(userId))
			elements.add(buildAction(crm, locale, "enable", new Localized("ACTIVATE", "Activate", "Activate"), "put", userId + "/enable"));
		return new JsonArray(elements);
	}
	
}
