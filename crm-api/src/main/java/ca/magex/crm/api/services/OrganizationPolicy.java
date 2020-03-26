package ca.magex.crm.api.services;

import ca.magex.crm.api.system.Identifier;

public interface OrganizationPolicy {
	
    boolean canCreateOrganization();
    boolean canViewOrganization(Identifier organizationId);
    boolean canUpdateOrganization(Identifier organizationId);
    boolean canEnableOrganization(Identifier organizationId);
    boolean canDisableOrganization(Identifier organizationId);

    boolean canCreateLocationForOrganization(Identifier organizationId);
    boolean canViewLocation(Identifier locationId);
    boolean canUpdateLocation(Identifier locationId);
    boolean canEnableLocation(Identifier locationId);
    boolean canDisableLocation(Identifier locationId);

    boolean canCreatePersonForOrganization(Identifier organizationId);
    boolean canViewPerson(Identifier personId);
    boolean canUpdatePerson(Identifier personId);
    boolean canEnablePerson(Identifier personId);
    boolean canDisablePerson(Identifier personId);
    boolean canUpdateUserRole(Identifier personId);
    
}