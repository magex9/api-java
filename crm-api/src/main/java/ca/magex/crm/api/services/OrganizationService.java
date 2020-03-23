package ca.magex.crm.api.services;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.common.Telephone;
import ca.magex.crm.api.crm.Location;
import ca.magex.crm.api.crm.Organization;
import ca.magex.crm.api.crm.Person;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.lookup.Language;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Role;

public interface OrganizationService {
	
    Organization createOrganization(String organizationName);
    Organization enableOrganization(Identifier organizationId);
    Organization disableOrganization(Identifier organizationId);
    Organization updateOrganizationName(Identifier organizationId, String name);
    Organization updateMainLocation(Identifier organizationId, Identifier locationId);
    Organization findOrganization(Identifier organizationId);
    Organization findOrganizations(OrganizationsFilter filter);

    Location createLocation(Identifier organizationId, String locationName, String locationReference, MailingAddress address);
    Location updateLocationName(Identifier locationId, String locationName);
    Location updateLocationAddress(Identifier locationId, MailingAddress address);
    Location enableLocation(Identifier locationId);
    Location disableLocation(Identifier locationId);
    Location findLocation(Identifier organizationId);
    Location findLocations(LocationsFilter filter);

    Person createPerson(Identifier organizationId, PersonName name, MailingAddress address, String email, String jobTitle, Language language, Telephone homePhone, Integer faxNumber);
    Person updatePersonName(Identifier personId, PersonName name);
    Person updatePersonAddress(Identifier personId, MailingAddress address);
    Person updatePersonCommunication(Identifier personId, String email, String jobTitle, Language language, Telephone homePhone, Integer faxNumber);
    Person enablePerson(Identifier personId);
    Person disablePerson(Identifier personId);
    Person findPerson(Identifier organizationId);
    Person findPersons(PersonsFilter filter);
    Person addUserRole(Identifier personId, Role role);
    Person removeUserRole(Identifier personId, Role role);
    
}