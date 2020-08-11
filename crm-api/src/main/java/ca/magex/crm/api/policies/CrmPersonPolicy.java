package ca.magex.crm.api.policies;

import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;

public interface CrmPersonPolicy {

    boolean canCreatePersonForOrganization(OrganizationIdentifier organizationId);
    boolean canViewPerson(PersonIdentifier personId);
    boolean canUpdatePerson(PersonIdentifier personId);
    boolean canEnablePerson(PersonIdentifier personId);
    boolean canDisablePerson(PersonIdentifier personId);        
}