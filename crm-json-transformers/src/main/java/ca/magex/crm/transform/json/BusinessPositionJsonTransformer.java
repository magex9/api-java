package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.lookup.BusinessClassification;
import ca.magex.crm.api.lookup.BusinessSector;
import ca.magex.crm.api.lookup.BusinessUnit;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;

public class BusinessPositionJsonTransformer extends AbstractJsonTransformer<BusinessPosition> {

	public BusinessPositionJsonTransformer(CrmServices crm) {
		super(crm);
	}

	@Override
	public Class<BusinessPosition> getType() {
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
		if (name.getSector() != null) {
			pairs.add(new JsonPair("sector", new BusinessSectorJsonTransformer(crm)
				.format(crm.findBusinessSectorByCode(name.getSector()), locale)));
		}
		if (name.getUnit() != null) {
			pairs.add(new JsonPair("unit", new BusinessUnitJsonTransformer(crm)
				.format(crm.findBusinessUnitByCode(name.getUnit()), locale)));
		}
		if (name.getClassification() != null) {
			pairs.add(new JsonPair("classification", new BusinessClassificationJsonTransformer(crm)
				.format(crm.findBusinessClassificationByCode(name.getClassification()), locale)));
		}
		return new JsonObject(pairs);
	}

	@Override
	public BusinessPosition parseJsonObject(JsonObject json, Locale locale) {
		String sector = parseObject("sector", json, BusinessSector.class, BusinessSectorJsonTransformer.class, locale).getCode();
		String unit = parseObject("unit", json, BusinessUnit.class, BusinessUnitJsonTransformer.class, locale).getCode();
		String classification = parseObject("classification", json, BusinessClassification.class, BusinessClassificationJsonTransformer.class, locale).getCode();
		return new BusinessPosition(sector, unit, classification);
	}

}
