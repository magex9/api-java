package ca.magex.crm.api.crm;

import java.util.Collections;
import java.util.List;

import javax.validation.constraints.NotEmpty;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.common.IdentifierList;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.AuthenticationGroupIdentifier;
import ca.magex.crm.api.system.id.BusinessGroupIdentifier;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;

/**
 * An extension to the Organization Summary with additional details associated to an organization
 * 
 * @author Jonny
 */
public class OrganizationDetails extends OrganizationSummary {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;

	/** identifier for the main location of the organization */
	private LocationIdentifier mainLocationId;

	/** identifier for the main contact of the organization */
	private PersonIdentifier mainContactId;

	/** the list of authentication groups associated to the organization */
	@NotEmpty
	private IdentifierList<AuthenticationGroupIdentifier> authenticationGroupIds;
	
	/** the list of business groups associated to the organization */
	@NotEmpty
	private IdentifierList<BusinessGroupIdentifier> businessGroupIds;

	/**
	 * constructs a full organization details from the given information
	 * 
	 * @param organizationId
	 * @param status
	 * @param displayName
	 * @param mainLocationId
	 * @param mainContactId
	 * @param authenticationGroupIds
	 * @param lastModified
	 */
	public OrganizationDetails(OrganizationIdentifier organizationId, Status status, String displayName, LocationIdentifier mainLocationId, PersonIdentifier mainContactId, List<AuthenticationGroupIdentifier> authenticationGroupIds, List<BusinessGroupIdentifier> businessGroupIds, Long lastModified) {
		super(organizationId, status, displayName, lastModified);
		this.mainLocationId = mainLocationId;
		this.mainContactId = mainContactId;
		this.authenticationGroupIds = authenticationGroupIds == null ? new IdentifierList<>() : new IdentifierList<>(authenticationGroupIds);
		this.businessGroupIds = businessGroupIds == null ? new IdentifierList<>() : new IdentifierList<>(businessGroupIds);
	}

	/**
	 * returns the identifier for the main location associated with this organization
	 * @return
	 */
	public LocationIdentifier getMainLocationId() {
		return mainLocationId;
	}

	/**
	 * returns the identifier for the main contact associated with this organization
	 * @return
	 */
	public PersonIdentifier getMainContactId() {
		return mainContactId;
	}
	
	/**
	 * returns the authentication groups this organization is allocated 
	 * @return
	 */
	public List<AuthenticationGroupIdentifier> getAuthenticationGroupIds() {
		return Collections.unmodifiableList(authenticationGroupIds);
	}
	
	/**
	 * returns the business groups this organization is allocated
	 * @return
	 */
	public List<BusinessGroupIdentifier> getBusinessGroupIds() {
		return Collections.unmodifiableList(businessGroupIds);
	}
	
	@Override
	public OrganizationDetails withStatus(Status status) {
		return new OrganizationDetails(organizationId, status, displayName, mainLocationId, mainContactId, authenticationGroupIds, businessGroupIds, lastModified);
	}

	@Override
	public OrganizationDetails withDisplayName(String displayName) {
		return new OrganizationDetails(organizationId, status, displayName, mainLocationId, mainContactId, authenticationGroupIds, businessGroupIds, lastModified);
	}	
	
	@Override
	public OrganizationDetails withLastModified(Long lastModified) {
		return new OrganizationDetails(organizationId, status, displayName, mainLocationId, mainContactId, authenticationGroupIds, businessGroupIds, lastModified);
	}
	
	/**
	 * returns a copy of the Organization with the new main location identifier
	 * @param mainLocationId
	 * @return
	 */
	public OrganizationDetails withMainLocationId(LocationIdentifier mainLocationId) {
		return new OrganizationDetails(organizationId, status, displayName, mainLocationId, mainContactId, authenticationGroupIds, businessGroupIds, lastModified);
	}
	
	/**
	 * returns a copy of the Organization with the new main contact identifier
	 * @param mainContactId
	 * @return
	 */
	public OrganizationDetails withMainContactId(PersonIdentifier mainContactId) {
		return new OrganizationDetails(organizationId, status, displayName, mainLocationId, mainContactId, authenticationGroupIds, businessGroupIds, lastModified);
	}

	/**
	 * returns a copy of the organization with the new authentication groups allocated
	 * @param groupIds
	 * @return
	 */
	public OrganizationDetails withAuthenticationGroupIds(List<AuthenticationGroupIdentifier> authenticationGroupIds) {
		return new OrganizationDetails(organizationId, status, displayName, mainLocationId, mainContactId, authenticationGroupIds, businessGroupIds, lastModified);
	}
	
	/**
	 * returns a copy of the organization with the new authentication groups allocated
	 * @param groupIds
	 * @return
	 */
	public OrganizationDetails withBusinessGroupIds(List<BusinessGroupIdentifier> businessGroupIds) {
		return new OrganizationDetails(organizationId, status, displayName, mainLocationId, mainContactId, authenticationGroupIds, businessGroupIds, lastModified);
	}
	
	/**
	 * returns the summary associated with this location
	 * @return
	 */
	public OrganizationSummary asSummary() {
		return new OrganizationSummary(organizationId, status, displayName, lastModified);
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