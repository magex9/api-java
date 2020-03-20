package ca.magex.crm.api.common;

import ca.magex.crm.api.lookup.Country;

public class MailingAddress {

	private String street;

	private String city;

	private String province;

	private Country country;

	private String postalCode;

	public MailingAddress(String street, String city, String province, Country country, String postalCode) {
		super();
		this.street = street;
		this.city = city;
		this.province = province;
		this.country = country;
		this.postalCode = postalCode;
	}

	public String getStreet() {
		return street;
	}

	public String getCity() {
		return city;
	}

	public String getProvince() {
		return province;
	}

	public Country getCountry() {
		return country;
	}

	public String getPostalCode() {
		return postalCode;
	}

}
