package ca.magex.crm.api.crm;

import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.common.Telephone;
import ca.magex.crm.api.lookup.Language;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public class Person {

	private Identifier personId;
	
	private Identifier organizationId;
	
	private Status status;
	
	private String displayName;
	
	private PersonName legalName;
	
	private Location address;
	
	private String email;
	
	private String jobTitle;
	
	private Language langauge;
	
	private Telephone homePhone;
	
	private Integer faxNumber;

	public Person(Identifier personId, Identifier organizationId, Status status, String displayName,
			PersonName legalName, Location address, String email, String jobTitle, Language langauge,
			Telephone homePhone, Integer faxNumber) {
		super();
		this.personId = personId;
		this.organizationId = organizationId;
		this.status = status;
		this.displayName = displayName;
		this.legalName = legalName;
		this.address = address;
		this.email = email;
		this.jobTitle = jobTitle;
		this.langauge = langauge;
		this.homePhone = homePhone;
		this.faxNumber = faxNumber;
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

	public String getDisplayName() {
		return displayName;
	}

	public PersonName getLegalName() {
		return legalName;
	}

	public Location getAddress() {
		return address;
	}

	public String getEmail() {
		return email;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public Language getLangauge() {
		return langauge;
	}

	public Telephone getHomePhone() {
		return homePhone;
	}

	public Integer getFaxNumber() {
		return faxNumber;
	}
	
}
