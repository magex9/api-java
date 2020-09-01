package ca.magex.crm.restful.controllers;

import java.util.Locale;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.transform.json.LocalizedJsonTransformer;
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonObject;

public interface RestfulActionHandler<T> {

	public JsonArray buildActions(T instance, Crm crm, Locale locale);
	
	public default JsonObject buildAction(Crm crm, Locale locale, String action, Localized label, String method, Object link) {
		return new JsonObject()
			.with("action", action)
			.with("label", locale == null ? label.getCode() : new LocalizedJsonTransformer(crm).formatLocalized(label, locale))
			.with("method", method)
			.with("link", Crm.REST_BASE + link.toString());
	}
	
}