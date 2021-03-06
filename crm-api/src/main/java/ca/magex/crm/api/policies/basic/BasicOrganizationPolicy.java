package ca.magex.crm.api.policies.basic;

import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.policies.CrmOrganizationPolicy;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.OrganizationIdentifier;

public class BasicOrganizationPolicy implements CrmOrganizationPolicy {

	private CrmOrganizationService organizations;

	/**
	 * Basic Organization Policy handles presence and status checks require for policy approval
	 * 
	 * @param organizations
	 */
	public BasicOrganizationPolicy(CrmOrganizationService organizations) {
		this.organizations = organizations;
	}
	
	@Override
	public boolean canCreateOrganization() {
		/* always can create an organization */
		return true;
	}

	@Override
	public boolean canViewOrganization(OrganizationIdentifier organizationId) {
		/* can only view an organization if it exists */
		if (organizations.findOrganizationSummary(organizationId) == null) {
			throw new ItemNotFoundException("Organization ID '" + organizationId + "'");
		}
		return true;
	}

	@Override
	public boolean canUpdateOrganization(OrganizationIdentifier organizationId) {
		/* can only update an organization if it exists, and is active */
		OrganizationSummary summary = organizations.findOrganizationSummary(organizationId);
		if (summary == null) {
			throw new ItemNotFoundException("Organization ID '" + organizationId + "'");
		}
		return summary.getStatus() == Status.ACTIVE;
	}

	@Override
	public boolean canEnableOrganization(OrganizationIdentifier organizationId) {
		/* can only update an organization if it exists, and is active */
		OrganizationSummary summary = organizations.findOrganizationSummary(organizationId);
		if (summary == null) {
			throw new ItemNotFoundException("Organization ID '" + organizationId + "'");
		}
		return !summary.getStatus().equals(Status.ACTIVE);
	}

	@Override
	public boolean canDisableOrganization(OrganizationIdentifier organizationId) {
		/* can only update an organization if it exists, and is active */
		OrganizationSummary summary = organizations.findOrganizationSummary(organizationId);
		if (summary == null) {
			throw new ItemNotFoundException("Organization ID '" + organizationId + "'");
		}
		return summary.getStatus().equals(Status.ACTIVE);
	}
}