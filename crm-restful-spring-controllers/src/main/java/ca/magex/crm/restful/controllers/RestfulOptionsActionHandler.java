package ca.magex.crm.restful.controllers;

import java.util.ArrayList;
import java.util.List;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.id.OptionIdentifier;
import ca.magex.crm.restful.models.RestfulAction;

public class RestfulOptionsActionHandler<T extends Option> implements RestfulActionHandler<T> {
	
	public List<RestfulAction> buildActions(Option option, Crm crm) {
		OptionIdentifier optionId = option.getOptionId();
		List<RestfulAction> actions = new ArrayList<>();
		if (crm.canViewOption(optionId))
			actions.add(buildAction("view", new Localized("VIEW", "View", "Vue"), "get", optionId));
		if (crm.canUpdateOption(optionId))
			actions.add(buildAction("edit", new Localized("EDIT", "Edit", "Éditer"), "patch", optionId));
		return actions;
	}
	
}
