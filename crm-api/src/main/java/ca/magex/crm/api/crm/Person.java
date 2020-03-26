package ca.magex.crm.api.crm;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ca.magex.crm.api.common.BusinessUnit;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.common.User;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public class Person {

	private Identifier personId;

	private Identifier organizationId;

	private Status status;

	private String displayName;

	private PersonName legalName;

	private MailingAddress address;

	private Communication communication;

	private BusinessUnit unit;

	private User user;

	public Person(Identifier personId, Identifier organizationId, Status status, String displayName,
			PersonName legalName, MailingAddress address, Communication communication, BusinessUnit unit, User user) {
		super();
		this.personId = personId;
		this.organizationId = organizationId;
		this.status = status;
		this.displayName = displayName;
		this.legalName = legalName;
		this.address = address;
		this.communication = communication;
		this.unit = unit;
		this.user = user;
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
		return new Person(personId, organizationId, status, displayName, legalName, address, communication, unit, user);
	}

	public String getDisplayName() {
		return displayName;
	}

	public Person withDisplayName(String displayName) {
		return new Person(personId, organizationId, status, displayName, legalName, address, communication, unit, user);
	}

	public PersonName getLegalName() {
		return legalName;
	}

	public Person withLegalName(PersonName legalName) {
		return new Person(personId, organizationId, status, displayName, legalName, address, communication, unit, user);
	}

	public MailingAddress getAddress() {
		return address;
	}

	public Person withAddress(MailingAddress address) {
		return new Person(personId, organizationId, status, displayName, legalName, address, communication, unit, user);
	}

	public Communication getCommunication() {
		return communication;
	}
	
	public Person withCommunication(Communication communication) {
		return new Person(personId, organizationId, status, displayName, legalName, address, communication, unit, user);
	}

	public BusinessUnit getUnit() {
		return unit;
	}
	
	public Person withUnit(BusinessUnit unit) {
		return new Person(personId, organizationId, status, displayName, legalName, address, communication, unit, user);
	}
	
	public User getUser() {
		return user;
	}

	public Person withUser(User user) {
		return new Person(personId, organizationId, status, displayName, legalName, address, communication, unit, user);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

}
