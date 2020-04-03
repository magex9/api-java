package ca.magex.crm.api.services;

import java.util.List;

import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Role;

public interface CrmValidation {

    OrganizationDetails validate(OrganizationDetails organization) throws BadRequestException;
    LocationDetails validate(LocationDetails location) throws BadRequestException;
    PersonDetails validate(PersonDetails person) throws BadRequestException;
    List<Role> validate(List<Role> roles, Identifier personId) throws BadRequestException;
    
}
