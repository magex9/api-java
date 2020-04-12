package ca.magex.crm.api.services;

import ca.magex.crm.api.system.Identifier;

public interface CrmPersonPolicy {

    boolean canCreatePersonForOrganization(Identifier organizationId);
    boolean canViewPerson(Identifier personId);
    boolean canUpdatePerson(Identifier personId);
    boolean canEnablePerson(Identifier personId);
    boolean canDisablePerson(Identifier personId);
    boolean canUpdateUserRole(Identifier personId);
    boolean canUpdateUserPassword(Identifier personId);
    
}