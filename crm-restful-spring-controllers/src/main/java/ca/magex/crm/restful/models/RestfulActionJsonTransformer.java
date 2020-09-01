package ca.magex.crm.restful.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ca.magex.crm.api.services.CrmOptionService;
import ca.magex.crm.transform.json.AbstractJsonTransformer;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;

public class RestfulActionJsonTransformer extends AbstractJsonTransformer<RestfulAction> {

	public RestfulActionJsonTransformer(CrmOptionService crm) {
		super(crm);
	}

	@Override
	public Class<RestfulAction> getSourceType() {
		return RestfulAction.class;
	}
	
	@Override
	public JsonObject formatRoot(RestfulAction action) {
		return formatLocalized(action, null);
	}
	
	@Override
	public JsonObject formatLocalized(RestfulAction action, Locale locale) {
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		formatType(pairs, locale);
		formatText(pairs, "action", action);
		formatLocalized(action, locale);
		formatText(pairs, "method", action);
		formatText(pairs, "link", action);
		return new JsonObject(pairs);
	}
	
}
