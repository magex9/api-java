package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.lookup.BusinessUnit;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Lang;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;
import ca.magex.json.model.JsonText;

@Component
public class BusinessUnitJsonTransformer extends AbstractJsonTransformer<BusinessUnit> {

	public BusinessUnitJsonTransformer(CrmServices crm) {
		super(crm);
	}

	@Override
	public Class<BusinessUnit> getSourceType() {
		return BusinessUnit.class;
	}

	@Override
	public JsonElement formatRoot(BusinessUnit unit) {
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		pairs.add(new JsonPair("@type", getType(BusinessUnit.class)));
		pairs.add(new JsonPair("@value", unit.getCode()));
		pairs.add(new JsonPair("@en", unit.getName(Lang.ENGLISH)));
		pairs.add(new JsonPair("@fr", unit.getName(Lang.FRENCH)));
		return new JsonObject(pairs);
	}
	
	@Override
	public JsonElement formatLocalized(BusinessUnit unit, Locale locale) {
		return new JsonText(unit.getName(locale));
	}

	@Override
	public BusinessUnit parseJsonText(JsonText json, Locale locale) {
		return crm.findBusinessUnitByLocalizedName(locale, ((JsonText)json).value());
	}

	@Override
	public BusinessUnit parseJsonObject(JsonObject json, Locale locale) {
		return crm.findBusinessUnitByCode(json.getString("@value")); 
	}

}
