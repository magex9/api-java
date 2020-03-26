package ca.magex.crm.ld.common;

import ca.magex.crm.api.common.Telephone;
import ca.magex.crm.ld.AbstractLinkedDataTransformer;
import ca.magex.crm.ld.data.DataObject;

public class TelephoneTransformer extends AbstractLinkedDataTransformer<Telephone> {

	@Override
	public String getType() {
		return "telephone";
	}
	
	@Override
	public DataObject format(Telephone country) {
		return base()
			.with("number", country.getNumber())
			.with("extension", country.getExtension());
	}

	@Override
	public Telephone parse(DataObject data) {
		validateContext(data);
		validateType(data);
		Integer number = data.getInt("number");
		Integer extension = data.getInt("extension");
		return new Telephone(number, extension);
	}
			
}
