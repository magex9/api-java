package ca.magex.crm.api.crm;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;

/**
 * Contains the tombstone information for a Person within the System
 * 
 * @author Jonny
 */
public class PersonSummary implements Serializable {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	/** a unique identifier for the person within the system */
	protected PersonIdentifier personId;

	/** the unique organization this person belongs to */
	protected OrganizationIdentifier organizationId;

	/** current status of the person */
	protected Status status;

	/** name of the person for display purposes */
	protected String displayName;

	/**
	 * Constructs a new Person Summary from the provided information
	 * 
	 * @param personId
	 * @param organizationId
	 * @param status
	 * @param displayName
	 */
	public PersonSummary(PersonIdentifier personId, OrganizationIdentifier organizationId, Status status, String displayName) {
		super();
		this.personId = personId;
		this.organizationId = organizationId;
		this.status = status;
		this.displayName = displayName;
	}

	/**
	 * returns the unique identifier associated with this person
	 * @return
	 */
	public PersonIdentifier getPersonId() {
		return personId;
	}

	/**
	 * returns the unique identifier of the organization this person is associated to
	 * @return
	 */
	public OrganizationIdentifier getOrganizationId() {
		return organizationId;
	}

	/**
	 * returns the current status of the person
	 * @return
	 */
	public Status getStatus() {
		return status;
	}
	
	/**
	 * returns the display name associated with the person
	 * @return
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * returns a copy of the person with the given status
	 * @param status
	 * @return
	 */
	public PersonSummary withStatus(Status status) {
		return new PersonSummary(personId, organizationId, status, displayName);
	}

	/**
	 * returns a copy of the person with the given display name
	 * @param displayName
	 * @return
	 */
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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}
}