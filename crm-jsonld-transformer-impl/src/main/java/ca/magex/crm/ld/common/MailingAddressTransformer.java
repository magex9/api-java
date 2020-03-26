package ca.magex.crm.ld.common;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.ld.AbstractLinkedDataTransformer;
import ca.magex.crm.ld.data.DataObject;
import ca.magex.crm.ld.lookup.CountryTransformer;

public class MailingAddressTransformer extends AbstractLinkedDataTransformer<MailingAddress> {

	private CountryTransformer countryTransformer;
	
	public MailingAddressTransformer() {
		this.countryTransformer = new CountryTransformer();
	}
	
	@Override
	public String getType() {
		return "mailing_address";
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
	public MailingAddress parse(DataObject data) {
		validateContext(data);
		validateType(data);
		String street = data.getString("street");
		String city = data.getString("city");
		String province = data.getString("province");
		Country country = countryTransformer.parse(data.getObject("country"));
		String postalCode = data.getString("postalCode");
		return new MailingAddress(street, city, province, country, postalCode);
	}
			
}
