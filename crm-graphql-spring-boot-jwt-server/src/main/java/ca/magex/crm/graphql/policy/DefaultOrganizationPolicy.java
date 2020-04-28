package ca.magex.crm.graphql.policy;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.policies.CrmOrganizationPolicy;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

@Component
public class DefaultOrganizationPolicy implements CrmOrganizationPolicy {

	private CrmOrganizationService organizationService;

	@Override
	public boolean canCreateOrganization() {
		return true;
	}

	@Override
	public boolean canViewOrganization(Identifier organizationId) {
		try {
			organizationService.findOrganizationDetails(organizationId);
			return true;
		} catch (ItemNotFoundException e) {
			return false;
		}
	}

	@Override
	public boolean canUpdateOrganization(Identifier organizationId) {
		try {
			organizationService.findOrganizationDetails(organizationId);
			return true;
		} catch (ItemNotFoundException e) {
			return false;
		}
	}

	@Override
	public boolean canEnableOrganization(Identifier organizationId) {
		try {
			return organizationService.findOrganizationDetails(organizationId).getStatus() != Status.ACTIVE;
		} catch (ItemNotFoundException e) {
			return false;
		}
	}

	@Override
	public boolean canDisableOrganization(Identifier organizationId) {
		try {
			return organizationService.findOrganizationDetails(organizationId).getStatus() != Status.INACTIVE;
		} catch (ItemNotFoundException e) {
			return false;
		}
	}
}