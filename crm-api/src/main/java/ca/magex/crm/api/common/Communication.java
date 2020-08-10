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
import ca.magex.crm.api.system.id.LanguageIdentifier;

public class Communication implements Serializable {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;

	@Nullable
	private String jobTitle;
	
	@NotEmpty
	private Choice<LanguageIdentifier> language;
	
	@NotNull
	private String email;
	
	@Nullable
	private Telephone homePhone;
	
	@Nullable
	private String faxNumber;

	public Communication(String jobTitle, Choice<LanguageIdentifier> language, String email, Telephone homePhone, String faxNumber) {
		super();
		this.jobTitle = jobTitle;
		this.language = language;
		this.email = email;
		this.homePhone = homePhone;
		this.faxNumber = faxNumber;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public Communication withJobTitle(String jobTitle) {
		return new Communication(jobTitle, language, jobTitle, homePhone, faxNumber);
	}

	public Choice<LanguageIdentifier> getLanguage() {
		return language;
	}

	public Communication withLanguage(Choice<LanguageIdentifier> language) {
		return new Communication(jobTitle, language, jobTitle, homePhone, faxNumber);
	}

	public String getEmail() {
		return email;
	}

	public Communication withEmail(String email) {
		return new Communication(jobTitle, language, jobTitle, homePhone, faxNumber);
	}

	public Telephone getHomePhone() {
		return homePhone;
	}

	public Communication withHomePhone(Telephone homePhone) {
		return new Communication(jobTitle, language, jobTitle, homePhone, faxNumber);
	}

	public String getFaxNumber() {
		return faxNumber;
	}

	public Communication withFaxNumber(String faxNumber) {
		return new Communication(jobTitle, language, jobTitle, homePhone, faxNumber);
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
