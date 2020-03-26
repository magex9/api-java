package ca.magex.crm.ld.lookup;

import ca.magex.crm.api.lookup.Salutation;
import ca.magex.crm.ld.AbstractLinkedDataTransformer;
import ca.magex.crm.ld.data.DataObject;

public class SalutationTransformer extends AbstractLinkedDataTransformer<Salutation> {

	@Override
	public String getType() {
		return "salutation";
	}
	
	@Override
	public DataObject format(Salutation salutation) {
		return base()
			.with("code", salutation.getCode())
			.with("name", salutation.getName());
	}

	@Override
	public Salutation parse(DataObject data) {
		validateContext(data);
		validateType(data);
		Integer code = data.getInt("code");
		String name = data.getString("name");
		return new Salutation(code, name);
	}
			
}
