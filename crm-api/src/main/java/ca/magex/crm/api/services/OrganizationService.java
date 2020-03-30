package ca.magex.crm.api.services;

import java.util.List;

import org.springframework.data.domain.Page;

import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.system.Role;

public interface OrganizationService {
	
    OrganizationDetails createOrganization(String organizationName);
    OrganizationSummary enableOrganization(Identifier organizationId);
    OrganizationSummary disableOrganization(Identifier organizationId);
    OrganizationDetails updateOrganizationName(Identifier organizationId, String name);
    OrganizationDetails updateOrganizationMainLocation(Identifier organizationId, Identifier locationId);
    OrganizationDetails findOrganization(Identifier organizationId);
    long countOrganizations(OrganizationsFilter filter);
    Page<OrganizationDetails> findOrganizationDetails(OrganizationsFilter filter, Paging paging);
    Page<OrganizationSummary> findOrganizationSummaries(OrganizationsFilter filter, Paging paging);

    LocationDetails createLocation(Identifier organizationId, String locationName, String locationReference, MailingAddress address);
    LocationSummary enableLocation(Identifier locationId);
    LocationSummary disableLocation(Identifier locationId);
    LocationDetails updateLocationName(Identifier locationId, String locationName);
    LocationDetails updateLocationAddress(Identifier locationId, MailingAddress address);
    LocationDetails findLocation(Identifier locationId);
    long countLocations(LocationsFilter filter);
    Page<LocationDetails> findLocationDetails(LocationsFilter filter, Paging paging);
    Page<LocationSummary> findLocationSummaries(LocationsFilter filter, Paging paging);

    PersonDetails createPerson(Identifier organizationId, PersonName name, MailingAddress address, Communication communication, BusinessPosition unit);
    PersonSummary enablePerson(Identifier personId);
    PersonSummary disablePerson(Identifier personId);
    PersonDetails updatePersonName(Identifier personId, PersonName name);
    PersonDetails updatePersonAddress(Identifier personId, MailingAddress address);
    PersonDetails updatePersonCommunication(Identifier personId, Communication communication);
    PersonDetails updatePersonBusinessUnit(Identifier personId, BusinessPosition unit);
    PersonDetails addUserRole(Identifier personId, Role role);
    PersonDetails removeUserRole(Identifier personId, Role role);
    PersonDetails findPerson(Identifier personId);
    long countPersons(PersonsFilter filter);
    Page<PersonDetails> findPersonDetails(PersonsFilter filter, Paging paging);
    Page<PersonSummary> findPersonSummaries(PersonsFilter filter, Paging paging);

    List<Message> validate(OrganizationDetails organization);
    List<Message> validate(LocationDetails location);
    List<Message> validate(PersonDetails person);
    List<Message> validate(List<Role> roles);
    
}
