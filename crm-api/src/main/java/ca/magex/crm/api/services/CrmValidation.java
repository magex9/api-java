package ca.magex.crm.api.services;

import java.util.List;

import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.system.Identifier;

public interface CrmValidation {

    Group validate(Group group) throws BadRequestException;
    Role validate(Role role) throws BadRequestException;
    OrganizationDetails validate(OrganizationDetails organization) throws BadRequestException;
    LocationDetails validate(LocationDetails location) throws BadRequestException;
    PersonDetails validate(PersonDetails person) throws BadRequestException;
    List<String> validate(List<String> roles, Identifier personId) throws BadRequestException;
    
}
