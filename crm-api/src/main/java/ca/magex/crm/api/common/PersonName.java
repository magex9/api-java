package ca.magex.crm.api.common;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class PersonName implements Serializable {

	private static final long serialVersionUID = 1L;

	private String salutation;

	private String firstName;

	private String middleName;

	private String lastName;

	private String displayName;

	public PersonName(String salutation, String firstName, String middleName, String lastName) {
		super();
		this.salutation = salutation;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.displayName = "";
		if (StringUtils.isNotBlank(lastName))
			displayName += lastName;
		if (StringUtils.isNotBlank(firstName) && displayName.length() > 0)
			displayName += ", ";
		if (StringUtils.isNotBlank(firstName))
			displayName += firstName;
		if (StringUtils.isNotBlank(middleName) && displayName.length() > 0)
			displayName += " ";
		if (StringUtils.isNotBlank(middleName))
			displayName += middleName;
	}

	public String getSalutation() {
		return salutation;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getDisplayName() {
		return displayName;
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