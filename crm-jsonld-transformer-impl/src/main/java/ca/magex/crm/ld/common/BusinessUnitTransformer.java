package ca.magex.crm.ld.common;

import ca.magex.crm.api.common.BusinessUnit;
import ca.magex.crm.ld.AbstractLinkedDataTransformer;
import ca.magex.crm.ld.data.DataObject;

public class BusinessUnitTransformer extends AbstractLinkedDataTransformer<BusinessUnit> {

	@Override
	public Class<?> getType() {
		return BusinessUnit.class;
	}
	
	@Override
	public DataObject format(BusinessUnit unit) {
		return base()
			.with("sector", unit.getSector())
			.with("unit", unit.getUnit())
			.with("level", unit.getLevel());
	}

	@Override
	public BusinessUnit parse(DataObject data) {
		validateContext(data);
		validateType(data);
		Integer sector = data.getInt("sector");
		Integer unit = data.getInt("unit");
		Integer level = data.getInt("level");
		return new BusinessUnit(sector, unit, level);
	}
			
}
