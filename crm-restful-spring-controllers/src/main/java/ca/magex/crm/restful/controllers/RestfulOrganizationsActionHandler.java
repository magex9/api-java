package ca.magex.crm.restful.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonElement;

public class RestfulOrganizationsActionHandler<T extends OrganizationSummary> implements RestfulActionHandler<T> {
	
	public JsonArray buildActions(OrganizationSummary organization, Crm crm, Locale locale) {
		OrganizationIdentifier organizationId = organization.getOrganizationId();
		List<JsonElement> elements = new ArrayList<>();
		if (crm.canViewOrganization(organizationId))
			elements.add(buildAction(crm, locale, "view", new Localized("VIEW", "View", "Vue"), "get", organizationId));
		if (crm.canUpdateOrganization(organizationId))
			elements.add(buildAction(crm, locale, "edit", new Localized("EDIT", "Edit", "Éditer"), "patch", organizationId));
		if (crm.canDisableOrganization(organizationId))
			elements.add(buildAction(crm, locale, "disable", new Localized("INACTIVATE", "Inactivate", "Inactivate"), "put", organizationId + "/disable"));
		if (crm.canEnableOrganization(organizationId))
			elements.add(buildAction(crm, locale, "enable", new Localized("ACTIVATE", "Activate", "Activate"), "put", organizationId + "/enable"));
		return new JsonArray(elements);
	}
	
}
