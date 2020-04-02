package ca.magex.crm.ld.lookup;

import ca.magex.crm.api.lookup.Salutation;
import ca.magex.crm.api.services.SecuredCrmServices;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.ld.AbstractLinkedDataTransformer;
import ca.magex.crm.ld.data.DataObject;

public class SalutationTransformer extends AbstractLinkedDataTransformer<Salutation> {

	public SecuredCrmServices crm;
	
	public SalutationTransformer(SecuredCrmServices crm) {
		this.crm = crm;
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
		return crm.findSalutationByCode(code);
	}
			
}
