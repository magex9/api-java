package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.lookup.BusinessClassification;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Lang;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;
import ca.magex.json.model.JsonText;

@Component
public class BusinessClassificationJsonTransformer extends AbstractJsonTransformer<BusinessClassification> {

	public BusinessClassificationJsonTransformer(CrmServices crm) {
		super(crm);
	}

	@Override
	public Class<BusinessClassification> getSourceType() {
		return BusinessClassification.class;
	}

	@Override
	public JsonElement formatRoot(BusinessClassification classification) {
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		pairs.add(new JsonPair("@type", getType(BusinessClassification.class)));
		pairs.add(new JsonPair("@value", classification.getCode()));
		pairs.add(new JsonPair("@en", classification.getName(Lang.ENGLISH)));
		pairs.add(new JsonPair("@fr", classification.getName(Lang.FRENCH)));
		return new JsonObject(pairs);
	}
	
	@Override
	public JsonElement formatLocalized(BusinessClassification classification, Locale locale) {
		return new JsonText(classification.getName(locale));
	}

	@Override
	public BusinessClassification parseJsonText(JsonText json, Locale locale) {
		return crm.findBusinessClassificationByLocalizedName(locale, ((JsonText)json).value());
	}

	@Override
	public BusinessClassification parseJsonObject(JsonObject json, Locale locale) {
		return crm.findBusinessClassificationByCode(json.getString("@value")); 
	}

}
