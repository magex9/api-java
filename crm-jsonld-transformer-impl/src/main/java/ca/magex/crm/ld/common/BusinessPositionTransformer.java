package ca.magex.crm.ld.common;

import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.lookup.BusinessClassification;
import ca.magex.crm.api.lookup.BusinessSector;
import ca.magex.crm.api.lookup.BusinessUnit;
import ca.magex.crm.ld.AbstractLinkedDataTransformer;
import ca.magex.crm.ld.data.DataObject;
import ca.magex.crm.ld.lookup.BusinessClassificationTransformer;
import ca.magex.crm.ld.lookup.BusinessSectorTransformer;
import ca.magex.crm.ld.lookup.BusinessUnitTransformer;

public class BusinessPositionTransformer extends AbstractLinkedDataTransformer<BusinessPosition> {

	private BusinessSectorTransformer businessSectorTransformer;
	
	private BusinessUnitTransformer businessUnitTransformer;
	
	private BusinessClassificationTransformer businessClassificationTransformer;
	
	public BusinessPositionTransformer() {
		this.businessSectorTransformer = new BusinessSectorTransformer();
		this.businessUnitTransformer = new BusinessUnitTransformer();
		this.businessClassificationTransformer = new BusinessClassificationTransformer();
	}
	
	@Override
	public Class<?> getType() {
		return BusinessPosition.class;
	}
	
	@Override
	public DataObject format(BusinessPosition position) {
		return base()
			.with("sector", position.getSector())
			.with("unit", position.getUnit())
			.with("classification", position.getClassification());
	}

	@Override
	public BusinessPosition parse(DataObject data, String parentContext) {
		validateContext(data, parentContext);
		validateType(data);
		BusinessSector sector = data.contains("sector") ? businessSectorTransformer.parse(data.getInt("sector")) : null;
		BusinessUnit unit = data.contains("unit") ? businessUnitTransformer.parse(data.getInt("unit")) : null;
		BusinessClassification classification = data.contains("classification") ? businessClassificationTransformer.parse(data.getInt("classification")) : null;
		return new BusinessPosition(sector, unit, classification);
	}
			
}
