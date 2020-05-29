package ca.magex.crm.api.validation;

import java.util.List;

import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.system.Identifier;

public interface CrmValidation {

	public Group validate(Group group) throws BadRequestException;

	public Role validate(Role role) throws BadRequestException;

	public OrganizationDetails validate(OrganizationDetails organization) throws BadRequestException;

	public LocationDetails validate(LocationDetails location) throws BadRequestException;

	public PersonDetails validate(PersonDetails person) throws BadRequestException;
	
	public User validate(User user) throws BadRequestException;

	public List<String> validate(List<String> roles, Identifier personId) throws BadRequestException;
}