package ca.magex.crm.ld.lookup;

import ca.magex.crm.api.lookup.BusinessClassification;
import ca.magex.crm.ld.AbstractLinkedDataTransformer;
import ca.magex.crm.ld.data.DataObject;

public class BusinessClassificationTransformer extends AbstractLinkedDataTransformer<BusinessClassification> {

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
	public BusinessClassification parse(DataObject data) {
		validateContext(data);
		validateType(data);
		Integer code = data.getInt("@value");
		String name = data.getString("name");
		return new BusinessClassification(code, name);
	}
			
}
