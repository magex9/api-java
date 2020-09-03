package ca.magex.crm.restful.controllers;

import java.util.ArrayList;
import java.util.List;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.restful.models.RestfulAction;

public class RestfulLocationsActionHandler<T extends LocationSummary> implements RestfulActionHandler<T> {
	
	public List<RestfulAction> buildActions(LocationSummary location, Crm crm) {
		LocationIdentifier locationId = location.getLocationId();
		List<RestfulAction> actions = new ArrayList<>();
		if (crm.canViewLocation(locationId))
			actions.add(buildAction("view", new Localized("VIEW", "View", "Vue"), "get", locationId));
		if (crm.canUpdateLocation(locationId))
			actions.add(buildAction("update", new Localized("UPDATE", "Edit", "Éditer"), "patch", locationId + "/details"));
		if (crm.canDisableLocation(locationId))
			actions.add(buildAction("disable", new Localized("INACTIVATE", "Inactivate", "Inactivate"), "put", locationId + "/disable"));
		if (crm.canEnableLocation(locationId))
			actions.add(buildAction("enable", new Localized("ACTIVATE", "Activate", "Activate"), "put", locationId + "/enable"));
		return actions;
	}
	
}
