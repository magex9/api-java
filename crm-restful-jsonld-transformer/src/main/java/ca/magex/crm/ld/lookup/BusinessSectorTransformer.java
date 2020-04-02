package ca.magex.crm.ld.lookup;

import ca.magex.crm.api.lookup.BusinessSector;
import ca.magex.crm.api.services.SecuredOrganizationService;
import ca.magex.crm.ld.AbstractLinkedDataTransformer;
import ca.magex.crm.ld.data.DataObject;

public class BusinessSectorTransformer extends AbstractLinkedDataTransformer<BusinessSector> {

	public BusinessSectorTransformer(SecuredOrganizationService service) {

	}

	@Override
	public Class<?> getType() {
		return BusinessSector.class;
	}
	
	@Override
	public DataObject format(BusinessSector sector) {
		return base()
			.with("@value", sector.getCode())
			.with("name", sector.getName());
	}

	@Override
	public BusinessSector parse(DataObject data, String parentContext) {
		validateContext(data, parentContext);
		validateType(data);
		Integer code = data.getInt("@value");
		String name = data.getString("name");
		return new BusinessSector(code, name);
	}
			
}
