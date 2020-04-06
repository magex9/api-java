package ca.magex.crm.ld.common;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.ld.AbstractLinkedDataTransformer;
import ca.magex.crm.ld.data.DataObject;
import ca.magex.crm.ld.lookup.CountryTransformer;

public class MailingAddressTransformer extends AbstractLinkedDataTransformer<MailingAddress> {

	private CountryTransformer countryTransformer;
	
	public MailingAddressTransformer(CrmServices crm) {
		this.countryTransformer = new CountryTransformer(crm);
	}
	
	@Override
	public Class<?> getType() {
		return MailingAddress.class;
	}
	
	@Override
	public DataObject format(MailingAddress mailingAddress) {
		return base()
			.with("street", mailingAddress.getStreet())
			.with("city", mailingAddress.getCity())
			.with("province", mailingAddress.getProvince())
			.with("country", countryTransformer.format(mailingAddress.getCountry()))
			.with("postalCode", mailingAddress.getPostalCode());
	}

	@Override
	public MailingAddress parse(DataObject data, String parentContext) {
		validateContext(data, parentContext);
		validateType(data);
		String street = data.getString("street");
		String city = data.getString("city");
		String province = data.getString("province");
		Country country = countryTransformer.parse(data.getObject("country"));
		String postalCode = data.getString("postalCode");
		return new MailingAddress(street, city, province, country, postalCode);
	}
			
}
