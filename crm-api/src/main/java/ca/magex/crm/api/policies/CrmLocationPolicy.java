package ca.magex.crm.api.policies;

import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;

public interface CrmLocationPolicy {

    boolean canCreateLocationForOrganization(OrganizationIdentifier organizationId);
    boolean canViewLocation(LocationIdentifier locationId);
    boolean canUpdateLocation(LocationIdentifier locationId);
    boolean canEnableLocation(LocationIdentifier locationId);
    boolean canDisableLocation(LocationIdentifier locationId);
    
}