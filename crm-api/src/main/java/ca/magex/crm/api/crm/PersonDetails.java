package ca.magex.crm.api.crm;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public class PersonDetails extends PersonSummary {
	
	private static final long serialVersionUID = 1L;
	
	private PersonName legalName;

	private MailingAddress address;

	private Communication communication;

	private List<Identifier> roleIds;

	public PersonDetails(Identifier personId, Identifier organizationId, Status status, String displayName,
			PersonName legalName, MailingAddress address, Communication communication, List<Identifier> roleIds) {
		super(personId, organizationId, status, displayName);		
		this.legalName = legalName;
		this.address = address;
		this.communication = communication;
		this.roleIds = roleIds;
	}

	@Override
	public PersonDetails withStatus(Status status) {
		return new PersonDetails(personId, organizationId, status, displayName, legalName, address, communication, roleIds);
	}

	@Override
	public PersonDetails withDisplayName(String displayName) {
		return new PersonDetails(personId, organizationId, status, displayName, legalName, address, communication, roleIds);
	}

	public PersonName getLegalName() {
		return legalName;
	}
	
	public PersonDetails withLegalName(PersonName legalName) {
		return new PersonDetails(personId, organizationId, status, displayName, legalName, address, communication, roleIds);
	}

	public MailingAddress getAddress() {
		return address;
	}

	public PersonDetails withAddress(MailingAddress address) {
		return new PersonDetails(personId, organizationId, status, displayName, legalName, address, communication, roleIds);
	}

	public Communication getCommunication() {
		return communication;
	}
	
	public PersonDetails withCommunication(Communication communication) {
		return new PersonDetails(personId, organizationId, status, displayName, legalName, address, communication, roleIds);
	}

	public List<Identifier> getRoleIds() {
		return roleIds;
	}
	
	public PersonDetails withRoleIds(List<Identifier> roleIds) {
		return new PersonDetails(personId, organizationId, status, displayName, legalName, address, communication, roleIds);
	}	

	public PersonSummary toSummary() {
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
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}
}