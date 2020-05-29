package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.lookup.BusinessSector;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Lang;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;
import ca.magex.json.model.JsonText;

@Component
public class BusinessSectorJsonTransformer extends AbstractJsonTransformer<BusinessSector> {

	public BusinessSectorJsonTransformer(CrmServices crm) {
		super(crm);
	}

	@Override
	public Class<BusinessSector> getSourceType() {
		return BusinessSector.class;
	}

	@Override
	public JsonElement formatRoot(BusinessSector sector) {
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		pairs.add(new JsonPair("@type", getType(BusinessSector.class)));
		pairs.add(new JsonPair("@value", sector.getCode()));
		pairs.add(new JsonPair("@en", sector.getName(Lang.ENGLISH)));
		pairs.add(new JsonPair("@fr", sector.getName(Lang.FRENCH)));
		return new JsonObject(pairs);
	}
	
	@Override
	public JsonElement formatLocalized(BusinessSector sector, Locale locale) {
		return new JsonText(sector.getName(locale));
	}

	@Override
	public BusinessSector parseJsonText(JsonText json, Locale locale) {
		return crm.findBusinessSectorByLocalizedName(locale, ((JsonText)json).value());
	}

	@Override
	public BusinessSector parseJsonObject(JsonObject json, Locale locale) {
		return crm.findBusinessSectorByCode(json.getString("@value")); 
	}

}
