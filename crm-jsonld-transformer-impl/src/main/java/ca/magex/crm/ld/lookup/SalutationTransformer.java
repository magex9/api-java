package ca.magex.crm.ld.lookup;

import ca.magex.crm.api.lookup.Salutation;
import ca.magex.crm.ld.AbstractLinkedDataTransformer;
import ca.magex.crm.ld.data.DataObject;

public class SalutationTransformer extends AbstractLinkedDataTransformer<Salutation> {

	@Override
	public Class<?> getType() {
		return Salutation.class;
	}
	
	@Override
	public DataObject format(Salutation salutation) {
		return base()
			.with("@value", salutation.getCode())
			.with("name", salutation.getName());
	}

	@Override
	public Salutation parse(DataObject data, String parentContext) {
		validateContext(data, parentContext);
		validateType(data);
		Integer code = data.getInt("@value");
		String name = data.getString("name");
		return new Salutation(code, name);
	}
			
}
