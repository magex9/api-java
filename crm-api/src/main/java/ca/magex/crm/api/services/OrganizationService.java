package ca.magex.crm.api.services;

import java.util.List;
import java.util.Locale;

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
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.lookup.Salutation;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Role;
import ca.magex.crm.api.system.Status;

public interface OrganizationService {
	
	List<Status> findStatuses();
	Status findStatusByCode(String code) throws ItemNotFoundException;
	Status findStatusByLocalizedName(Locale locale, String name) throws ItemNotFoundException;
	
	List<Role> findRoles();
	Role findRoleByCode(String code) throws ItemNotFoundException;
	Role findRoleByLocalizedName(Locale locale, String name) throws ItemNotFoundException;
	
	List<Country> findCountries();
	Country findCountryByCode(String code) throws ItemNotFoundException;
	Country findCountryByLocalizedName(Locale locale, String name) throws ItemNotFoundException;
	
	List<Salutation> findSalutations();
	Salutation findSalutationByCode(Integer code) throws ItemNotFoundException;
	Salutation findSalutationByLocalizedName(Locale locale, String name) throws ItemNotFoundException;
	
    OrganizationDetails createOrganization(String organizationName);
    OrganizationSummary enableOrganization(Identifier organizationId);
    OrganizationSummary disableOrganization(Identifier organizationId);
    OrganizationDetails updateOrganizationName(Identifier organizationId, String name);
    OrganizationDetails updateOrganizationMainLocation(Identifier organizationId, Identifier locationId);
    OrganizationSummary findOrganizationSummary(Identifier organizationId);
    OrganizationDetails findOrganizationDetails(Identifier organizationId);
    long countOrganizations(OrganizationsFilter filter);
    Page<OrganizationDetails> findOrganizationDetails(OrganizationsFilter filter, Paging paging);
    Page<OrganizationSummary> findOrganizationSummaries(OrganizationsFilter filter, Paging paging);

    LocationDetails createLocation(Identifier organizationId, String locationName, String locationReference, MailingAddress address);
    LocationSummary enableLocation(Identifier locationId);
    LocationSummary disableLocation(Identifier locationId);
    LocationDetails updateLocationName(Identifier locationId, String locationName);
    LocationDetails updateLocationAddress(Identifier locationId, MailingAddress address);
    LocationSummary findLocationSummary(Identifier locationId);
    LocationDetails findLocationDetails(Identifier locationId);
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
    PersonSummary findPersonSummary(Identifier personId);
    PersonDetails findPersonDetails(Identifier personId);
    long countPersons(PersonsFilter filter);
    Page<PersonDetails> findPersonDetails(PersonsFilter filter, Paging paging);
    Page<PersonSummary> findPersonSummaries(PersonsFilter filter, Paging paging);

    OrganizationDetails validate(OrganizationDetails organization) throws BadRequestException;
    LocationDetails validate(LocationDetails location) throws BadRequestException;
    PersonDetails validate(PersonDetails person) throws BadRequestException;
    List<Role> validate(List<Role> roles, Identifier personId) throws BadRequestException;
    
}
