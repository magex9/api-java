package ca.magex.crm.restful.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonElement;

public class RestfulLocationsActionHandler<T extends LocationSummary> implements RestfulActionHandler<T> {
	
	public JsonArray buildActions(LocationSummary location, Crm crm, Locale locale) {
		LocationIdentifier locationId = location.getLocationId();
		List<JsonElement> elements = new ArrayList<>();
		if (crm.canViewLocation(locationId))
			elements.add(buildAction(crm, locale, "view", new Localized("VIEW", "View", "Vue"), "get", locationId));
		if (crm.canUpdateLocation(locationId))
			elements.add(buildAction(crm, locale, "edit", new Localized("EDIT", "Edit", "Éditer"), "patch", locationId));
		if (crm.canDisableLocation(locationId))
			elements.add(buildAction(crm, locale, "disable", new Localized("INACTIVATE", "Inactivate", "Inactivate"), "put", locationId + "/disable"));
		if (crm.canEnableLocation(locationId))
			elements.add(buildAction(crm, locale, "enable", new Localized("ACTIVATE", "Activate", "Activate"), "put", locationId + "/enable"));
		return new JsonArray(elements);
	}
	
}
