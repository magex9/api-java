package ca.magex.crm.restful.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonElement;

public class RestfulPersonsActionHandler<T extends PersonSummary> implements RestfulActionHandler<T> {
	
	public JsonArray buildActions(PersonSummary person, Crm crm, Locale locale) {
		PersonIdentifier personId = person.getPersonId();
		List<JsonElement> elements = new ArrayList<>();
		if (crm.canViewPerson(personId))
			elements.add(buildAction(crm, locale, "view", new Localized("VIEW", "View", "Vue"), "get", personId));
		if (crm.canUpdatePerson(personId))
			elements.add(buildAction(crm, locale, "edit", new Localized("EDIT", "Edit", "Éditer"), "patch", personId));
		if (crm.canDisablePerson(personId))
			elements.add(buildAction(crm, locale, "disable", new Localized("INACTIVATE", "Inactivate", "Inactivate"), "put", personId + "/disable"));
		if (crm.canEnablePerson(personId))
			elements.add(buildAction(crm, locale, "enable", new Localized("ACTIVATE", "Activate", "Activate"), "put", personId + "/enable"));
		return new JsonArray(elements);
	}
	
}
