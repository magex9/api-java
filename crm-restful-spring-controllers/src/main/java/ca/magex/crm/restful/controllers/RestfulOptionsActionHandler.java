package ca.magex.crm.restful.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.id.OptionIdentifier;
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonElement;

public class RestfulOptionsActionHandler<T extends Option> implements RestfulActionHandler<T> {
	
	public JsonArray buildActions(Option option, Crm crm, Locale locale) {
		OptionIdentifier optionId = option.getOptionId();
		List<JsonElement> elements = new ArrayList<>();
		if (crm.canViewOption(optionId))
			elements.add(buildAction(crm, locale, "view", new Localized("VIEW", "View", "Vue"), "get", optionId));
		if (crm.canUpdateOption(optionId))
			elements.add(buildAction(crm, locale, "edit", new Localized("EDIT", "Edit", "Éditer"), "patch", optionId));
		return new JsonArray(elements);
	}
	
}
