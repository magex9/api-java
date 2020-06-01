package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.lookup.Province;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Lang;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;
import ca.magex.json.model.JsonText;

@Component
public class ProvinceJsonTransformer extends AbstractJsonTransformer<Province> {

	public ProvinceJsonTransformer(CrmServices crm) {
		super(crm);
	}

	@Override
	public Class<Province> getSourceType() {
		return Province.class;
	}

	@Override
	public JsonElement formatRoot(Province province) {
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		pairs.add(new JsonPair("@type", getType(Province.class)));
		pairs.add(new JsonPair("@value", province.getCode()));
		pairs.add(new JsonPair("@en", province.getName(Lang.ENGLISH)));
		pairs.add(new JsonPair("@fr", province.getName(Lang.FRENCH)));
		pairs.add(new JsonPair("country", province.getParent().getCode()));
		return new JsonObject(pairs);
	}
	
	@Override
	public JsonElement formatLocalized(Province province, Locale locale) {
		return new JsonText(province.get(locale));
	}

	@Override
	public Province parseJsonObject(JsonObject json, Locale locale) {
		return crm.findProvinceByCode(json.getString("@value"), json.getString("country")); 
	}

}
