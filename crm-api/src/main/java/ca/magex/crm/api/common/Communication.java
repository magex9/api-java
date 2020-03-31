package ca.magex.crm.api.common;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.magex.crm.api.lookup.Language;

public class Communication {

	private String jobTitle;
	
	private Language language;
	
	private String email;
	
	private Telephone homePhone;
	
	private Long faxNumber;

	public Communication(String jobTitle, Language language, String email, Telephone homePhone, Long faxNumber) {
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

	public Language getLanguage() {
		return language;
	}

	public Communication withLanguage(Language language) {
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

	public Long getFaxNumber() {
		return faxNumber;
	}

	public Communication withFaxNumber(Long faxNumber) {
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
