package ca.magex.crm.ld.lookup;

import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.ld.AbstractLinkedDataTransformer;
import ca.magex.crm.ld.data.DataObject;

public class CountryTransformer extends AbstractLinkedDataTransformer<Country> {

	@Override
	public String getType() {
		return "country";
	}
	
	@Override
	public DataObject format(Country country) {
		return base()
			.with("code", country.getCode())
			.with("name", country.getName());
	}

	@Override
	public Country parse(DataObject data) {
		validateContext(data);
		validateType(data);
		String code = data.getString("code");
		String name = data.getString("name");
		return new Country(code, name);
	}
			
}
