package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.services.CrmOptionService;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;
import ca.magex.json.model.JsonText;

@Component
public class LocalizedJsonTransformer extends AbstractJsonTransformer<Localized> {

	public LocalizedJsonTransformer(CrmOptionService crm) {
		super(crm);
	}

	@Override
	public Class<Localized> getSourceType() {
		return Localized.class;
	}

	@Override
	public JsonElement formatRoot(Localized localized) {
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		formatType(pairs, null);
		pairs.add(new JsonPair("@id", new JsonText("http://api.magex.ca/crm/rest/dictionary/" + localized.getCode().replaceAll("\\.", "/"))));
		pairs.add(new JsonPair("@value", localized.getCode()));
		pairs.add(new JsonPair("@en", localized.get(Lang.ENGLISH)));
		pairs.add(new JsonPair("@fr", localized.get(Lang.FRENCH)));
		return new JsonObject(pairs);
	}
	
	@Override
	public JsonElement formatLocalized(Localized localized, Locale locale) {
		if (Lang.ROOT.equals(locale)) {
			return new JsonText(localized.getCode().replaceAll("\\.", "/"));
		} else {
			return new JsonText(localized.get(locale));
		}
	}
	
	@Override
	public Localized parseJsonObject(JsonObject json, Locale locale) {
		String code = json.getString("@value");
		String englishName = json.getString("@en");
		String frenchName = json.getString("@fr");
		return new Localized(code, englishName, frenchName);
	}

}
