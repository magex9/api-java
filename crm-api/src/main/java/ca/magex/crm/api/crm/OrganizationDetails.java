package ca.magex.crm.api.crm;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public class OrganizationDetails extends OrganizationSummary {
		
	private Identifier mainLocationId;

	public OrganizationDetails(Identifier organizationId, Status status, String displayName, Identifier mainLocationId) {
		super(organizationId, status, displayName);	
		this.mainLocationId = mainLocationId;
	}

	@Override
	public OrganizationDetails withStatus(Status status) {
		return new OrganizationDetails(organizationId, status, displayName, mainLocationId);
	}
	
	@Override
	public OrganizationDetails withDisplayName(String displayName) {
		return new OrganizationDetails(organizationId, status, displayName, mainLocationId);
	}

	public Identifier getMainLocationId() {
		return mainLocationId;
	}

	public OrganizationDetails withMainLocationId(Identifier mainLocationId) {
		return new OrganizationDetails(organizationId, status, displayName, mainLocationId);
	}
	
	public OrganizationSummary toSummary() {
		return new OrganizationSummary(organizationId, status, displayName);
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