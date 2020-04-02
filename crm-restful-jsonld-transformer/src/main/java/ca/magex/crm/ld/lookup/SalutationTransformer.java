package ca.magex.crm.ld.lookup;

import ca.magex.crm.api.lookup.Salutation;
import ca.magex.crm.api.services.SecuredOrganizationService;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.ld.AbstractLinkedDataTransformer;
import ca.magex.crm.ld.data.DataObject;

public class SalutationTransformer extends AbstractLinkedDataTransformer<Salutation> {

	public SecuredOrganizationService service;
	
	public SalutationTransformer(SecuredOrganizationService service) {
		this.service = service;
	}

	@Override
	public Class<?> getType() {
		return Salutation.class;
	}
	
	@Override
	public DataObject format(Salutation salutation) {
		return base()
			.with("@value", salutation.getCode())
			.with("@en", salutation.getName(Lang.ENGLISH))
			.with("@fr", salutation.getName(Lang.FRENCH));
	}

	@Override
	public Salutation parse(DataObject data, String parentContext) {
		validateContext(data, parentContext);
		validateType(data);
		Integer code = data.getInt("@value");
		return service.findSalutationByCode(code);
	}
			
}
