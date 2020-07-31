package ca.magex.crm.api.common;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.lang.Nullable;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.Choice;
import ca.magex.crm.api.system.id.CountryIdentifier;
import ca.magex.crm.api.system.id.ProvinceIdentifier;

/**
 * The mailing address a method used to describe a location for which a person
 * or organization receives lives or works.
 * 
 * @author magex
 *
 */
public class MailingAddress implements Serializable {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	// The street number, name and type including an appartment or unit number if there is one
	@NotNull
	private String street;

	// The name of the city
	@NotNull
	private String city;

	// The identifier of the province or district of the city in the country.  Use the other code if the province isnt codified.
	@NotEmpty
	private Choice<ProvinceIdentifier> province;

	// The identifier of the country of the mailing address.  This is an ISO list of countries but use the other name if its not in the list.
	@NotEmpty
	private Choice<CountryIdentifier> country;

	// The postal address of the mailing address if available
	@Nullable
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
