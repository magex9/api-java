package ca.magex.crm.api.common;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.magex.crm.api.services.Crm;

public class MailingAddress implements Serializable {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	private String street;

	private String city;

	private String province;

	private String country;

	private String postalCode;

	public MailingAddress(String street, String city, String province, String country, String postalCode) {
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
		this.street = street;
		return new MailingAddress(street, city, province, country, postalCode);
	}

	public String getCity() {
		return city;
	}
	
	public MailingAddress withCity(String city) {
		this.city = city;
		return new MailingAddress(street, city, province, country, postalCode);
	}

	public String getProvince() {
		return province;
	}
	
	public MailingAddress withProvince(String province) {
		this.province = province;
		return new MailingAddress(street, city, province, country, postalCode);
	}

	public String getCountry() {
		return country;
	}
	
	public MailingAddress withCountry(String country) {
		this.country = country;
		return new MailingAddress(street, city, province, country, postalCode);
	}

	public String getPostalCode() {
		return postalCode;
	}
	
	public MailingAddress withPostalCode(String postalCode) {
		this.postalCode = postalCode;
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
