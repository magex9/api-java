package ca.magex.crm.api.common;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Communication implements Serializable {

	private static final long serialVersionUID = 1L;

	private String jobTitle;
	
	private String language;
	
	private String email;
	
	private Telephone homePhone;
	
	private String faxNumber;

	public Communication(String jobTitle, String language, String email, Telephone homePhone, String faxNumber) {
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

	public String getLanguage() {
		return language;
	}

	public Communication withLanguage(String language) {
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
