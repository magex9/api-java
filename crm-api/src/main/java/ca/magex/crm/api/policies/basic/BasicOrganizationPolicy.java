package ca.magex.crm.api.policies.basic;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.policies.CrmOrganizationPolicy;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

@Component
@Primary
@Profile(MagexCrmProfiles.CRM_NO_AUTH)
public class BasicOrganizationPolicy implements CrmOrganizationPolicy {

	private CrmOrganizationService organizationService;

	/**
	 * Basic Organization Policy handles presence and status checks require for policy approval
	 * 
	 * @param organizationService
	 */
	public BasicOrganizationPolicy(
			CrmOrganizationService organizationService) {
		this.organizationService = organizationService;
	}
	
	@Override
	public boolean canCreateOrganization() {
		/* always can create an organization */
		return true;
	}

	@Override
	public boolean canViewOrganization(Identifier organizationId) {
		try {
			/* can only view an organization if it exists */
			organizationService.findOrganizationSummary(organizationId);
			return true;
		} catch (ItemNotFoundException e) {
			return false;
		}
	}

	@Override
	public boolean canUpdateOrganization(Identifier organizationId) {
		try {
			/* can only update an organization if it exists, and is active */
			return organizationService.findOrganizationSummary(organizationId).getStatus() == Status.ACTIVE;
		} catch (ItemNotFoundException e) {
			return false;
		}
	}

	@Override
	public boolean canEnableOrganization(Identifier organizationId) {
		try {
			/* can only enable an organization if it exists */
			organizationService.findOrganizationSummary(organizationId);
			return true;
		} catch (ItemNotFoundException e) {
			return false;
		}
	}

	@Override
	public boolean canDisableOrganization(Identifier organizationId) {
		try {
			/* can only disable an organization if it exists */
			organizationService.findOrganizationSummary(organizationId);
			return true;
		} catch (ItemNotFoundException e) {
			return false;
		}
	}
}