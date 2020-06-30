package ca.magex.crm.api.common;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.Choice;
import ca.magex.crm.api.system.id.CountryIdentifier;
import ca.magex.crm.api.system.id.ProvinceIdentifier;

public class MailingAddress implements Serializable {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	private String street;

	private String city;

	private Choice<ProvinceIdentifier> province;

	private Choice<CountryIdentifier> country;

	private String postalCode;

	public MailingAddress(String street, String city, Choice<ProvinceIdentifier> province, Choice<CountryIdentifier> country, String postalCode) {
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
	
	public MailingAddress withStreet(String street) {
		return new MailingAddress(street, city, province, country, postalCode);
	}

	public String getCity() {
		return city;
	}
	
	public MailingAddress withCity(String city) {
		return new MailingAddress(street, city, province, country, postalCode);
	}

	public Choice<ProvinceIdentifier> getProvince() {
		return province;
	}
	
	public MailingAddress withProvince(Choice<ProvinceIdentifier> province) {
		return new MailingAddress(street, city, province, country, postalCode);
	}

	public Choice<CountryIdentifier> getCountry() {
		return country;
	}
	
	public MailingAddress withCountry(Choice<CountryIdentifier> country) {
		return new MailingAddress(street, city, province, country, postalCode);
	}

	public String getPostalCode() {
		return postalCode;
	}
	
	public MailingAddress withPostalCode(String postalCode) {
		return new MailingAddress(street, city, province, country, postalCode);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}

}
