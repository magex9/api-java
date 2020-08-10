package ca.magex.crm.api.common;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.lang.Nullable;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.Choice;
import ca.magex.crm.api.system.id.SalutationIdentifier;

public class PersonName implements Serializable {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	@Nullable
	private Choice<SalutationIdentifier> salutation;

	@NotNull
	private String firstName;

	@Nullable
	private String middleName;

	@NotNull
	private String lastName;

	public PersonName(Choice<SalutationIdentifier> salutation, String firstName, String middleName, String lastName) {
		super();
		this.salutation = salutation;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
	}

	public Choice<SalutationIdentifier> getSalutation() {
		return salutation;
	}
	
	public PersonName withSalutation(Choice<SalutationIdentifier> salutation) {
		return new PersonName(salutation, firstName, middleName, lastName);
	}

	public String getFirstName() {
		return firstName;
	}
	
	public PersonName withFirstName(String firstName) {
		return new PersonName(salutation, firstName, middleName, lastName);
	}

	public String getMiddleName() {
		return middleName;
	}
	
	public PersonName withMiddleName(String middleName) {
		return new PersonName(salutation, firstName, middleName, lastName);
	}

	public String getLastName() {
		return lastName;
	}
	
	public PersonName withLastName(String lastName) {
		return new PersonName(salutation, firstName, middleName, lastName);
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