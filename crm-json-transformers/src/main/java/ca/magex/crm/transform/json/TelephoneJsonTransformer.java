package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.common.Telephone;
import ca.magex.crm.api.services.CrmOptionService;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;

@Component
public class TelephoneJsonTransformer extends AbstractJsonTransformer<Telephone> {

	public TelephoneJsonTransformer(CrmOptionService crm) {
		super(crm);
	}

	@Override
	public Class<Telephone> getSourceType() {
		return Telephone.class;
	}
	
	@Override
	public JsonObject formatRoot(Telephone name) {
		return formatLocalized(name, null);
	}
	
	@Override
	public JsonObject formatLocalized(Telephone name, Locale locale) {
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		formatType(pairs, locale);
		formatText(pairs, "number", name);
		formatText(pairs, "extension", name);
		return new JsonObject(pairs);
	}

	@Override
	public Telephone parseJsonObject(JsonObject json, Locale locale) {
		String number = parseText("number", json);
		String extension = parseText("extension", json);
		return new Telephone(number, extension);
	}

}
