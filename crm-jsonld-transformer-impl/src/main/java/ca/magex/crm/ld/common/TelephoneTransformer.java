package ca.magex.crm.ld.common;

import ca.magex.crm.api.common.Telephone;
import ca.magex.crm.ld.AbstractLinkedDataTransformer;
import ca.magex.crm.ld.data.DataObject;

public class TelephoneTransformer extends AbstractLinkedDataTransformer<Telephone> {

	@Override
	public Class<?> getType() {
		return Telephone.class;
	}
	
	@Override
	public DataObject format(Telephone country) {
		return base()
			.with("number", country.getNumber())
			.with("extension", country.getExtension());
	}

	@Override
	public Telephone parse(DataObject data, String parentContext) {
		validateContext(data, parentContext);
		validateType(data);
		Long number = data.contains("number") ? data.getLong("number") : null;
		Long extension = data.contains("extension") ? data.getLong("extension") : null;
		return new Telephone(number, extension);
	}
			
}
