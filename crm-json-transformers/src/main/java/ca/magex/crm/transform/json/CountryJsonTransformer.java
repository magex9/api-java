package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Lang;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;
import ca.magex.json.model.JsonText;

@Component
public class CountryJsonTransformer extends AbstractJsonTransformer<Country> {

	public CountryJsonTransformer(CrmServices crm) {
		super(crm);
	}

	@Override
	public Class<Country> getSourceType() {
		return Country.class;
	}

	@Override
	public JsonElement formatRoot(Country country) {
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		pairs.add(new JsonPair("@type", getType(Country.class)));
		pairs.add(new JsonPair("@value", country.getCode()));
		pairs.add(new JsonPair("@en", country.getName(Lang.ENGLISH)));
		pairs.add(new JsonPair("@fr", country.getName(Lang.FRENCH)));
		return new JsonObject(pairs);
	}
	
	@Override
	public JsonElement formatLocalized(Country country, Locale locale) {
		return new JsonText(country.get(locale));
	}

	@Override
	public Country parseJsonText(JsonText json, Locale locale) {
		return crm.findCountryByLocalizedName(locale, ((JsonText)json).value());
	}

	@Override
	public Country parseJsonObject(JsonObject json, Locale locale) {
		return crm.findCountryByCode(json.getString("@value")); 
	}

}
