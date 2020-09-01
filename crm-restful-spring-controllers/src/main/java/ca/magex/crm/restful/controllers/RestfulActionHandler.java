package ca.magex.crm.restful.controllers;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.restful.models.RestfulAction;
import ca.magex.crm.transform.json.LocalizedJsonTransformer;
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonObject;

public interface RestfulActionHandler<T> {

	public List<RestfulAction> buildActions(T instance, Crm crm);
	
	public default RestfulAction buildAction(String action, Localized label, String method, Object link) {
		return new RestfulAction(action, label, method, Crm.REST_BASE + link.toString());
	}
	
	public default JsonArray transformAction(T instance, Crm crm, Locale locale) {
		return new JsonArray(buildActions(instance, crm).stream()
			.map(action -> new JsonObject()
				.with("action", action.getAction())
				.with("label", locale == null ? action.getLabel().getCode() : new LocalizedJsonTransformer(crm).formatLocalized(action.getLabel(), locale))
				.with("method", action.getMethod())
				.with("link", action.getLink()))
			.collect(Collectors.toList()));
	}
	
}