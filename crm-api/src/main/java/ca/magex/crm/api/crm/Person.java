package ca.magex.crm.api.crm;

import java.util.List;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.common.Telephone;
import ca.magex.crm.api.lookup.Language;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Role;
import ca.magex.crm.api.system.Status;

public class Person {

	private Identifier personId;
	
	private Identifier organizationId;
	
	private Status status;
	
	private String displayName;
	
	private PersonName legalName;
	
	private MailingAddress address;
	
	private String email;
	
	private String jobTitle;
	
	private Language language;
	
	private Telephone homePhone;
	
	private Integer faxNumber;
	
	private String userName;
	
	private List<Role> roles;

	public Person(Identifier personId, Identifier organizationId, Status status, String displayName,
			PersonName legalName, MailingAddress address, String email, String jobTitle, Language language,
			Telephone homePhone, Integer faxNumber, String userName, List<Role> roles) {
		super();
		this.personId = personId;
		this.organizationId = organizationId;
		this.status = status;
		this.displayName = displayName;
		this.legalName = legalName;
		this.address = address;
		this.email = email;
		this.jobTitle = jobTitle;
		this.language = language;
		this.homePhone = homePhone;
		this.faxNumber = faxNumber;
		this.userName = userName;
		this.roles = roles;
	}

	public Identifier getPersonId() {
		return personId;
	}

	public Identifier getOrganizationId() {
		return organizationId;
	}

	public Status getStatus() {
		return status;
	}
	
	public Person withStatus(Status status) {
		return new Person(personId, organizationId, status, displayName, legalName, address, email, jobTitle, language, homePhone, faxNumber, userName, roles);
	}

	public String getDisplayName() {
		return displayName;
	}

	public Person withDisplayName(String displayName) {
		return new Person(personId, organizationId, status, displayName, legalName, address, email, jobTitle, language, homePhone, faxNumber, userName, roles);
	}

	public PersonName getLegalName() {
		return legalName;
	}

	public Person withLegalName(PersonName legalName) {
		return new Person(personId, organizationId, status, displayName, legalName, address, email, jobTitle, language, homePhone, faxNumber, userName, roles);
	}

	public MailingAddress getAddress() {
		return address;
	}

	public Person withAddress(MailingAddress address) {
		return new Person(personId, organizationId, status, displayName, legalName, address, email, jobTitle, language, homePhone, faxNumber, userName, roles);
	}

	public String getEmail() {
		return email;
	}

	public Person withEmail(String email) {
		return new Person(personId, organizationId, status, displayName, legalName, address, email, jobTitle, language, homePhone, faxNumber, userName, roles);
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public Person withJobTitle(String jobTitle) {
		return new Person(personId, organizationId, status, displayName, legalName, address, email, jobTitle, language, homePhone, faxNumber, userName, roles);
	}

	public Language getLanguage() {
		return language;
	}

	public Person withLanguage(Language language) {
		return new Person(personId, organizationId, status, displayName, legalName, address, email, jobTitle, language, homePhone, faxNumber, userName, roles);
	}

	public Telephone getHomePhone() {
		return homePhone;
	}

	public Person withHomePhone(Telephone homePhone) {
		return new Person(personId, organizationId, status, displayName, legalName, address, email, jobTitle, language, homePhone, faxNumber, userName, roles);
	}

	public Integer getFaxNumber() {
		return faxNumber;
	}
	
	public Person withFaxNumber(Integer faxNumber) {
		return new Person(personId, organizationId, status, displayName, legalName, address, email, jobTitle, language, homePhone, faxNumber, userName, roles);
	}
	
	public String getUserName() {
		return userName;
	}
	
	public Person withUserName(String userName) {
		return new Person(personId, organizationId, status, userName, legalName, address, userName, userName, language, homePhone, faxNumber, userName, roles);
	}
	
	public List<Role> getRoles() {
		return roles;
	}
	
	public Person withRoles(List<Role> roles) {
		return new Person(personId, organizationId, status, displayName, legalName, address, email, jobTitle, language, homePhone, faxNumber, userName, roles);
	}

}
