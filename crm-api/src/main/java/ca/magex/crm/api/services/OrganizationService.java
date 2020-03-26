package ca.magex.crm.api.services;

import java.util.List;

import org.springframework.data.domain.Page;

import ca.magex.crm.api.common.BusinessUnit;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.crm.Location;
import ca.magex.crm.api.crm.Organization;
import ca.magex.crm.api.crm.Person;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.system.Role;

public interface OrganizationService {
	
    Organization createOrganization(String organizationName);
    Organization enableOrganization(Identifier organizationId);
    Organization disableOrganization(Identifier organizationId);
    Organization updateOrganizationName(Identifier organizationId, String name);
    Organization updateOrganizationMainLocation(Identifier organizationId, Identifier locationId);
    Organization findOrganization(Identifier organizationId);
    long countOrganizations(OrganizationsFilter filter);
    Page<Organization> findOrganizations(OrganizationsFilter filter);

    Location createLocation(Identifier organizationId, String locationName, String locationReference, MailingAddress address);
    Location updateLocationName(Identifier locationId, String locationName);
    Location updateLocationAddress(Identifier locationId, MailingAddress address);
    Location enableLocation(Identifier locationId);
    Location disableLocation(Identifier locationId);
    Location findLocation(Identifier locationId);
    long countLocations(LocationsFilter filter);
    Page<Location> findLocations(LocationsFilter filter);

    Person createPerson(Identifier organizationId, PersonName name, MailingAddress address, Communication communication, BusinessUnit unit);
    Person updatePersonName(Identifier personId, PersonName name);
    Person updatePersonAddress(Identifier personId, MailingAddress address);
    Person updatePersonCommunication(Identifier personId, Communication communication);
    Person updatePersonBusinessUnit(Identifier personId, BusinessUnit unit);
    Person enablePerson(Identifier personId);
    Person disablePerson(Identifier personId);
    Person addUserRole(Identifier personId, Role role);
    Person removeUserRole(Identifier personId, Role role);
    Person findPerson(Identifier personId);
    long countPersons(PersonsFilter filter);
    Page<Person> findPersons(PersonsFilter filter);

    List<Message> validate(Organization organization);
    List<Message> validate(Location location);
    List<Message> validate(Person person);
    List<Message> validate(List<Role> roles);
    
}
