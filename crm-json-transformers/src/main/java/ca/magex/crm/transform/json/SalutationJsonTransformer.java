package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.lookup.Salutation;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Lang;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;
import ca.magex.json.model.JsonText;

@Component
public class SalutationJsonTransformer extends AbstractJsonTransformer<Salutation> {

	public SalutationJsonTransformer(CrmServices crm) {
		super(crm);
	}

	@Override
	public Class<Salutation> getSourceType() {
		return Salutation.class;
	}

	@Override
	public JsonElement formatRoot(Salutation salutation) {
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		pairs.add(new JsonPair("@type", getType(Salutation.class)));
		pairs.add(new JsonPair("@value", salutation.getCode()));
		pairs.add(new JsonPair("@en", salutation.getName(Lang.ENGLISH)));
		pairs.add(new JsonPair("@fr", salutation.getName(Lang.FRENCH)));
		return new JsonObject(pairs);
	}
	
	@Override
	public JsonElement formatLocalized(Salutation salutation, Locale locale) {
		return new JsonText(salutation.get(locale));
	}

	@Override
	public Salutation parseJsonText(JsonText json, Locale locale) {
		return crm.findSalutationByLocalizedName(locale, ((JsonText)json).value());
	}

	@Override
	public Salutation parseJsonObject(JsonObject json, Locale locale) {
		return crm.findSalutationByCode(json.getString("@value")); 
	}

}
