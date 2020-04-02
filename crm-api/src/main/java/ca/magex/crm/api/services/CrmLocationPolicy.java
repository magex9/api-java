package ca.magex.crm.api.services;

import ca.magex.crm.api.system.Identifier;

public interface CrmLocationPolicy {

    boolean canCreateLocationForOrganization(Identifier organizationId);
    boolean canViewLocation(Identifier locationId);
    boolean canUpdateLocation(Identifier locationId);
    boolean canEnableLocation(Identifier locationId);
    boolean canDisableLocation(Identifier locationId);
    
}