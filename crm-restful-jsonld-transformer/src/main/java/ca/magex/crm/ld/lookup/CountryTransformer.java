package ca.magex.crm.ld.lookup;

import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.ld.AbstractLinkedDataTransformer;
import ca.magex.crm.ld.data.DataObject;

public class CountryTransformer extends AbstractLinkedDataTransformer<Country> {

	@Override
	public Class<?> getType() {
		return Country.class;
	}
	
	@Override
	public DataObject format(Country country) {
		return base()
			.with("@value", country.getCode())
			.with("name", country.getName());
	}

	@Override
	public Country parse(DataObject data, String parentContext) {
		validateContext(data, parentContext);
		validateType(data);
		String code = data.getString("@value");
		String name = data.getString("name");
		return new Country(code, name);
	}
			
}
