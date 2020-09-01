package ca.magex.crm.restful.controllers;

import java.util.ArrayList;
import java.util.List;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.restful.models.RestfulAction;

public class RestfulPersonsActionHandler<T extends PersonSummary> implements RestfulActionHandler<T> {
	
	public List<RestfulAction> buildActions(PersonSummary person, Crm crm) {
		PersonIdentifier personId = person.getPersonId();
		List<RestfulAction> actions = new ArrayList<>();
		if (crm.canViewPerson(personId))
			actions.add(buildAction("view", new Localized("VIEW", "View", "Vue"), "get", personId));
		if (crm.canUpdatePerson(personId))
			actions.add(buildAction("edit", new Localized("EDIT", "Edit", "Éditer"), "patch", personId));
		if (crm.canDisablePerson(personId))
			actions.add(buildAction("disable", new Localized("INACTIVATE", "Inactivate", "Inactivate"), "put", personId + "/disable"));
		if (crm.canEnablePerson(personId))
			actions.add(buildAction("enable", new Localized("ACTIVATE", "Activate", "Activate"), "put", personId + "/enable"));
		return actions;
	}
	
}
