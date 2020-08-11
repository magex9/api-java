package ca.magex.crm.api.policies;

import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.system.id.OrganizationIdentifier;

public interface CrmOrganizationPolicy {
	
    boolean canCreateOrganization();
    boolean canViewOrganization(OrganizationIdentifier organizationId);
    boolean canUpdateOrganization(OrganizationIdentifier organizationId);
    boolean canEnableOrganization(OrganizationIdentifier organizationId);
    boolean canDisableOrganization(OrganizationIdentifier organizationId);

	/**
	 * The default organization filter information for the current user
	 * @return
	 */
	default OrganizationsFilter defaultOrganizationsFilter() {
		return new OrganizationsFilter();
	};
    
}