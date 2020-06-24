package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;

@Component
public class BusinessPositionJsonTransformer extends AbstractJsonTransformer<BusinessPosition> {

	public BusinessPositionJsonTransformer(CrmServices crm) {
		super(crm);
	}

	@Override
	public Class<BusinessPosition> getSourceType() {
		return BusinessPosition.class;
	}
	
	@Override
	public JsonObject formatRoot(BusinessPosition name) {
		return formatLocalized(name, null);
	}
	
	@Override
	public JsonObject formatLocalized(BusinessPosition name, Locale locale) {
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		formatType(pairs);
		formatOption(pairs, "sector", name, Crm.BUSINESS_SECTOR, locale);
		formatOption(pairs, "unit", name, Crm.BUSINESS_UNIT, name.getSector(), locale);
		formatOption(pairs, "classification", name, Crm.BUSINESS_CLASSIFICATION, locale);
		return new JsonObject(pairs);
	}

	@Override
	public BusinessPosition parseJsonObject(JsonObject json, Locale locale) {
		String sector = parseOption("sector", json, Crm.BUSINESS_SECTOR, locale);
		String unit = parseOption("unit", json, Crm.BUSINESS_UNIT, Crm.BUSINESS_SECTOR, "sector", locale);
		String classification = parseOption("classification", json, Crm.BUSINESS_CLASSIFICATION, locale);
		return new BusinessPosition(sector, unit, classification);
	}

}
