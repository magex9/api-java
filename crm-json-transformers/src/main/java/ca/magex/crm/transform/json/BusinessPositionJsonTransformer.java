package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;

@Component
public class BusinessPositionJsonTransformer extends AbstractJsonTransformer<BusinessPosition> {

	private BusinessSectorJsonTransformer businessSectorJsonTransformer;
	
	private BusinessUnitJsonTransformer businessUnitJsonTransformer;
	
	private BusinessClassificationJsonTransformer businessClassificationJsonTransformer;
	
	public BusinessPositionJsonTransformer(CrmServices crm, BusinessSectorJsonTransformer businessSectorJsonTransformer, 
			BusinessUnitJsonTransformer businessUnitJsonTransformer, BusinessClassificationJsonTransformer businessClassificationJsonTransformer) {
		super(crm);
		this.businessSectorJsonTransformer = businessSectorJsonTransformer;
		this.businessUnitJsonTransformer = businessUnitJsonTransformer;
		this.businessClassificationJsonTransformer = businessClassificationJsonTransformer;
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
		if (name.getSector() != null) {
			pairs.add(new JsonPair("sector", businessSectorJsonTransformer
				.format(crm.findBusinessSectorByCode(name.getSector()), locale)));
		}
		if (name.getUnit() != null) {
			pairs.add(new JsonPair("unit", businessUnitJsonTransformer
				.format(crm.findBusinessUnitByCode(name.getUnit()), locale)));
		}
		if (name.getClassification() != null) {
			pairs.add(new JsonPair("classification", businessClassificationJsonTransformer
				.format(crm.findBusinessClassificationByCode(name.getClassification()), locale)));
		}
		return new JsonObject(pairs);
	}

	@Override
	public BusinessPosition parseJsonObject(JsonObject json, Locale locale) {
		String sector = parseObject("sector", json, businessSectorJsonTransformer, locale).getCode();
		String unit = parseObject("unit", json, businessUnitJsonTransformer, locale).getCode();
		String classification = parseObject("classification", json, businessClassificationJsonTransformer, locale).getCode();
		return new BusinessPosition(sector, unit, classification);
	}

}
