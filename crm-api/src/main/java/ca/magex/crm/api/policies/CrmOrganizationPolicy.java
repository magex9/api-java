package ca.magex.crm.api.policies;

import ca.magex.crm.api.system.id.OrganizationIdentifier;

public interface CrmOrganizationPolicy {
	
    boolean canCreateOrganization();
    boolean canViewOrganization(OrganizationIdentifier organizationId);
    boolean canUpdateOrganization(OrganizationIdentifier organizationId);
    boolean canEnableOrganization(OrganizationIdentifier organizationId);
    boolean canDisableOrganization(OrganizationIdentifier organizationId);
    
}