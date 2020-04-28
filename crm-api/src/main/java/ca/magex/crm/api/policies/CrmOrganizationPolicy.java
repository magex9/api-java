package ca.magex.crm.api.policies;

import ca.magex.crm.api.system.Identifier;

public interface CrmOrganizationPolicy {
	
    boolean canCreateOrganization();
    boolean canViewOrganization(Identifier organizationId);
    boolean canUpdateOrganization(Identifier organizationId);
    boolean canEnableOrganization(Identifier organizationId);
    boolean canDisableOrganization(Identifier organizationId);
    
}