package ca.magex.crm.ld.lookup;

import ca.magex.crm.api.lookup.BusinessUnit;
import ca.magex.crm.api.services.SecuredOrganizationService;
import ca.magex.crm.ld.AbstractLinkedDataTransformer;
import ca.magex.crm.ld.data.DataObject;

public class BusinessUnitTransformer extends AbstractLinkedDataTransformer<BusinessUnit> {

	public BusinessUnitTransformer(SecuredOrganizationService service) {

	}

	@Override
	public Class<?> getType() {
		return BusinessUnit.class;
	}
	
	@Override
	public DataObject format(BusinessUnit sector) {
		return base()
			.with("@value", sector.getCode())
			.with("name", sector.getName());
	}

	@Override
	public BusinessUnit parse(DataObject data, String parentContext) {
		validateContext(data, parentContext);
		validateType(data);
		Integer code = data.getInt("@value");
		String name = data.getString("name");
		return new BusinessUnit(code, name);
	}
			
}
