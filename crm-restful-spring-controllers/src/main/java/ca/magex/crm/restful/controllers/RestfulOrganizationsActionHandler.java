package ca.magex.crm.restful.controllers;

import java.util.ArrayList;
import java.util.List;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.restful.models.RestfulAction;

public class RestfulOrganizationsActionHandler<T extends OrganizationSummary> implements RestfulActionHandler<T> {
	
	public List<RestfulAction> buildActions(OrganizationSummary organization, Crm crm) {
		OrganizationIdentifier organizationId = organization.getOrganizationId();
		List<RestfulAction> actions = new ArrayList<>();
		if (crm.canViewOrganization(organizationId))
			actions.add(buildAction("view", new Localized("VIEW", "View", "Vue"), "get", organizationId));
		if (crm.canUpdateOrganization(organizationId))
			actions.add(buildAction("edit", new Localized("EDIT", "Edit", "Éditer"), "patch", organizationId));
		if (crm.canDisableOrganization(organizationId))
			actions.add(buildAction("disable", new Localized("INACTIVATE", "Inactivate", "Inactivate"), "put", organizationId + "/disable"));
		if (crm.canEnableOrganization(organizationId))
			actions.add(buildAction("enable", new Localized("ACTIVATE", "Activate", "Activate"), "put", organizationId + "/enable"));
		return actions;
	}
	
}
