package ca.magex.crm.api.services;

import java.util.List;

import ca.magex.crm.api.crm.Location;
import ca.magex.crm.api.crm.Organization;
import ca.magex.crm.api.crm.Person;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.system.Role;

interface OrganizationPolicy {
	
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

    boolean canUpdateUserRole(String username);

    List<Message> validate(Organization organization);
    List<Message> validate(Location location);
    List<Message> validate(Person person);
    List<Message> validate(List<Role> roles);
    
}