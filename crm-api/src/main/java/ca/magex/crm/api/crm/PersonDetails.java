package ca.magex.crm.api.crm;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.BusinessRoleIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;

/**
 * An extension to the Person Summary with additional details associated to the Person
 * 
 * @author Jonny
 */
public class PersonDetails extends PersonSummary {
	
	private static final long serialVersionUID = 1L;
	
	/** the full name of the person */
	private PersonName legalName;

	/** the full mailing address of the person */
	private MailingAddress address;

	/** the full communication details for the person */
	private Communication communication;

	/** the business role within the organization associated with this person */
	private List<BusinessRoleIdentifier> roleIds;

	/**
	 * Constructs a full person details from the given information
	 * 
	 * @param personId
	 * @param organizationId
	 * @param status
	 * @param displayName
	 * @param legalName
	 * @param address
	 * @param communication
	 * @param roleIds
	 */
	public PersonDetails(
			PersonIdentifier personId, 
			OrganizationIdentifier organizationId, 
			Status status, 
			String displayName, 
			PersonName legalName, 
			MailingAddress address, 
			Communication communication, 
			List<BusinessRoleIdentifier> roleIds) {
		super(personId, organizationId, status, displayName);		
		this.legalName = legalName;
		this.address = address;
		this.communication = communication;
		this.roleIds = roleIds;
	}

	/**
	 * returns the legal name for the person
	 * @return
	 */
	public PersonName getLegalName() {
		return legalName;
	}
	
	/**
	 * returns the address for the person
	 * @return
	 */
	public MailingAddress getAddress() {
		return address;
	}
	
	/**
	 * returns the communications for the person
	 * @return
	 */
	public Communication getCommunication() {
		return communication;
	}
	
	/**
	 * returns the business roles associated to the person
	 * @return
	 */
	public List<BusinessRoleIdentifier> getRoleIds() {
		return roleIds;
	}
	
	@Override
	public PersonDetails withStatus(Status status) {
		return new PersonDetails(personId, organizationId, status, displayName, legalName, address, communication, roleIds);
	}

	@Override
	public PersonDetails withDisplayName(String displayName) {
		return new PersonDetails(personId, organizationId, status, displayName, legalName, address, communication, roleIds);
	}

	/**
	 * returns a copy of the person with the given legal name
	 * @param legalName
	 * @return
	 */
	public PersonDetails withLegalName(PersonName legalName) {
		return new PersonDetails(personId, organizationId, status, displayName, legalName, address, communication, roleIds);
	}	

	/**
	 * returns a copy of the person with the given address
	 * @param address
	 * @return
	 */
	public PersonDetails withAddress(MailingAddress address) {
		return new PersonDetails(personId, organizationId, status, displayName, legalName, address, communication, roleIds);
	}
	
	/**
	 * returns a copy of the person with the given communication
	 * @param communication
	 * @return
	 */
	public PersonDetails withCommunication(Communication communication) {
		return new PersonDetails(personId, organizationId, status, displayName, legalName, address, communication, roleIds);
	}	
	
	/**
	 * returns a copy of the person with the given business roles
	 * @param roleIds
	 * @return
	 */
	public PersonDetails withRoleIds(List<BusinessRoleIdentifier> roleIds) {
		return new PersonDetails(personId, organizationId, status, displayName, legalName, address, communication, roleIds);
	}	

	/**
	 * returns the summary information for the person
	 * @return
	 */
	public PersonSummary asSummary() {
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