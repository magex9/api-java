package ca.magex.crm.api.crm;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public class PersonSummary {

	private Identifier personId;

	private Identifier organizationId;

	private Status status;

	private String displayName;

	public PersonSummary(Identifier personId, Identifier organizationId, Status status, String displayName) {
		super();
		this.personId = personId;
		this.organizationId = organizationId;
		this.status = status;
		this.displayName = displayName;
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

	public PersonSummary withStatus(Status status) {
		return new PersonSummary(personId, organizationId, status, displayName);
	}

	public String getDisplayName() {
		return displayName;
	}

	public PersonSummary withDisplayName(String displayName) {
		return new PersonSummary(personId, organizationId, status, displayName);
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
