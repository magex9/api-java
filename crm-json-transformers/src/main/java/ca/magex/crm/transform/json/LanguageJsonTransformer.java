package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ca.magex.crm.api.lookup.Language;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Lang;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;
import ca.magex.json.model.JsonText;

public class LanguageJsonTransformer extends AbstractJsonTransformer<Language> {

	public LanguageJsonTransformer(CrmServices crm) {
		super(crm);
	}

	@Override
	public Class<Language> getType() {
		return Language.class;
	}

	@Override
	public JsonElement formatRoot(Language country) {
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		pairs.add(new JsonPair("@type", getType(Language.class)));
		pairs.add(new JsonPair("@value", country.getCode()));
		pairs.add(new JsonPair("@en", country.getName(Lang.ENGLISH)));
		pairs.add(new JsonPair("@fr", country.getName(Lang.FRENCH)));
		return new JsonObject(pairs);
	}
	
	@Override
	public JsonElement formatLocalized(Language country, Locale locale) {
		return new JsonText(country.get(locale));
	}

	@Override
	public Language parseJsonText(JsonText json, Locale locale) {
		return crm.findLanguageByLocalizedName(locale, ((JsonText)json).value());
	}

	@Override
	public Language parseJsonObject(JsonObject json, Locale locale) {
		return crm.findLanguageByCode(json.getString("@value")); 
	}

}
