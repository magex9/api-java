package ca.magex.crm.ld.lookup;

import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.services.SecuredCrmServices;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.ld.AbstractLinkedDataTransformer;
import ca.magex.crm.ld.data.DataObject;

public class CountryTransformer extends AbstractLinkedDataTransformer<Country> {

	public SecuredCrmServices crm;
	
	public CountryTransformer(SecuredCrmServices crm) {
		this.crm = crm;
	}
	
	@Override
	public Class<?> getType() {
		return Country.class;
	}
	
	@Override
	public DataObject format(Country country) {
		return base()
			.with("@value", country.getCode())
			.with("@en", country.getName(Lang.ENGLISH))
			.with("@fr", country.getName(Lang.FRENCH));
	}

	@Override
	public Country parse(DataObject data, String parentContext) {
		validateContext(data, parentContext);
		validateType(data);
		String code = data.getString("@value");
		return crm.findCountryByCode(code);
	}
			
}
