package ca.magex.crm.ld.lookup;

import ca.magex.crm.api.lookup.BusinessClassification;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.ld.AbstractLinkedDataTransformer;
import ca.magex.crm.ld.data.DataObject;

public class BusinessClassificationTransformer extends AbstractLinkedDataTransformer<BusinessClassification> {

	public BusinessClassificationTransformer(CrmServices crm) {

	}

	@Override
	public Class<?> getType() {
		return BusinessClassification.class;
	}
	
	@Override
	public DataObject format(BusinessClassification sector) {
		return base()
			.with("@value", sector.getCode())
			.with("name", sector.getName());
	}

	@Override
	public BusinessClassification parse(DataObject data, String parentContext) {
		validateContext(data, parentContext);
		validateType(data);
		Integer code = data.getInt("@value");
		String name = data.getString("name");
		return new BusinessClassification(code, name);
	}
			
}
