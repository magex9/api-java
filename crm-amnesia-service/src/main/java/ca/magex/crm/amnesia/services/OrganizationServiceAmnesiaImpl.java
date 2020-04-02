package ca.magex.crm.amnesia.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;

import ca.magex.crm.amnesia.generator.AmnesiaBase58IdGenerator;
import ca.magex.crm.amnesia.generator.IdGenerator;
import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.common.User;
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
import ca.magex.crm.api.filters.PageBuilder;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.lookup.Salutation;
import ca.magex.crm.api.services.OrganizationService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.system.Role;
import ca.magex.crm.api.system.Status;

public class OrganizationServiceAmnesiaImpl implements OrganizationService {

	private IdGenerator idGenerator;
	
	private Map<Identifier, Serializable> data;
	
	private Lookups<Status, String> statuses;
	
	private Lookups<Role, String> roles;
	
	private Lookups<Country, String> countries;
	
	private Lookups<Salutation, Integer> salutations;
	
	public OrganizationServiceAmnesiaImpl() {
		idGenerator = new AmnesiaBase58IdGenerator();
		data = new HashMap<Identifier, Serializable>();
		statuses = new Lookups<Status, String>(Arrays.asList(Status.values()), Status.class, String.class);
		roles = new Lookups<Role, String>(Role.class, String.class);
		countries = new Lookups<Country, String>(Country.class, String.class);
		salutations = new Lookups<Salutation, Integer>(Salutation.class, Integer.class);
	}
	
	public Identifier generateId() {
		return idGenerator.generate();
	}

	public List<Status> findStatuses() {
		return statuses.getOptions();
	}
	
	public Status findStatusByCode(String code) throws ItemNotFoundException {
		return statuses.findByCode(code);
	}
	
	public Status findStatusByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return statuses.findByName(locale, name);
	}

	public List<Role> findRoles() {
		return roles.getOptions();
	}
	
	public Role findRoleByCode(String code) throws ItemNotFoundException {
		return roles.findByCode(code);
	}
	
	public Role findRoleByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return roles.findByName(locale, name);
	}

	public List<Country> findCountries() {
		return countries.getOptions();
	}
	
	public Country findCountryByCode(String code) throws ItemNotFoundException {
		return countries.findByCode(code);
	}
	
	public Country findCountryByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return countries.findByName(locale, name);
	}

	public List<Salutation> findSalutations() {
		return salutations.getOptions();
	}
	
	public Salutation findSalutationByCode(Integer code) throws ItemNotFoundException {
		return salutations.findByCode(code);
	}
	
	public Salutation findSalutationByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return salutations.findByName(locale, name);
	}

	public OrganizationDetails createOrganization(String organizationName) {
		Identifier organizationId = generateId();
		OrganizationDetails organization = new OrganizationDetails(organizationId, Status.ACTIVE, organizationName, null);
		data.put(organizationId, validate(organization));
		return organization;
	}

	public OrganizationSummary enableOrganization(Identifier organizationId) {
		OrganizationDetails updated = findOrganizationDetails(organizationId).withStatus(Status.ACTIVE);
		data.put(organizationId, updated);
		return updated;
	}

	public OrganizationSummary disableOrganization(Identifier organizationId) {
		OrganizationDetails updated = findOrganizationDetails(organizationId).withStatus(Status.INACTIVE);
		data.put(organizationId, updated);
		return updated;
	}

	public OrganizationDetails updateOrganizationName(Identifier organizationId, String name) {
		OrganizationDetails updated = findOrganizationDetails(organizationId).withDisplayName(name);
		data.put(organizationId, updated);
		return updated;
	}

	public OrganizationDetails updateOrganizationMainLocation(Identifier organizationId, Identifier locationId) {
		OrganizationDetails updated = findOrganizationDetails(organizationId).withMainLocationId(findLocationDetails(locationId).getLocationId());
		data.put(organizationId, updated);
		return updated;
	}
	
	@Override
	public OrganizationSummary findOrganizationSummary(Identifier organizationId) {
		return findOrganizationDetails(organizationId);
	}
	
	public OrganizationDetails findOrganizationDetails(Identifier organizationId) {
		if (!data.containsKey(organizationId))
			throw new ItemNotFoundException(organizationId.toString());
		if (!(data.get(organizationId) instanceof OrganizationDetails))
			throw new BadRequestException(organizationId, "fatal", "class", "Class is not Organization: " + organizationId);
		return ((OrganizationDetails)SerializationUtils.clone(data.get(organizationId)));
	}
	
	@Override
	public long countOrganizations(OrganizationsFilter filter) {
		return data.values().stream()
			.filter(i -> i instanceof OrganizationDetails)
			.map(i -> (OrganizationDetails)i)
			.filter(p -> StringUtils.isNotBlank(filter.getDisplayName()) ? p.getDisplayName().contains(filter.getDisplayName()) : true)
			.count();
	}
	
	public Page<OrganizationSummary> findOrganizationSummaries(OrganizationsFilter filter, Paging paging) {
		List<OrganizationSummary> allMatchingOrgs = data
			.values()
			.stream()
			.filter(i -> i instanceof OrganizationDetails)
			.map(i -> (OrganizationSummary)i)
			.filter(i -> StringUtils.isNotBlank(filter.getDisplayName()) ? i.getDisplayName().contains(filter.getDisplayName()) : true)
			.filter(i -> filter.getStatus() != null ? i.getStatus().equals(filter.getStatus()) : true)
			.map(i -> SerializationUtils.clone(i))
			.sorted(filter.getComparator(paging))
			.collect(Collectors.toList());		
		return PageBuilder.buildPageFor(allMatchingOrgs, paging);
	}
	
	public Page<OrganizationDetails> findOrganizationDetails(OrganizationsFilter filter, Paging paging) {
		List<OrganizationDetails> allMatchingOrgs = data
			.values()
			.stream()
			.filter(i -> i instanceof OrganizationDetails)
			.map(i -> (OrganizationDetails)i)
			.filter(p -> StringUtils.isNotBlank(filter.getDisplayName()) ? p.getDisplayName().contains(filter.getDisplayName()) : true)
			.filter(i -> filter.getStatus() != null ? i.getStatus().equals(filter.getStatus()) : true)
			.map(i -> SerializationUtils.clone(i))
			.sorted(filter.getComparator(paging))
			.collect(Collectors.toList());
		return PageBuilder.buildPageFor(allMatchingOrgs, paging);
	}

	public LocationDetails createLocation(Identifier organizationId, String locationName, String locationReference, MailingAddress address) {
		Identifier locationId = generateId();
		LocationDetails location = new LocationDetails(locationId, findOrganizationDetails(organizationId).getOrganizationId(), Status.ACTIVE, locationReference, locationName, address);
		data.put(locationId, location);
		return location;
	}

	public LocationDetails updateLocationName(Identifier locationId, String locationName) {
		LocationDetails updated = findLocationDetails(locationId).withDisplayName(locationName);
		data.put(locationId, updated);
		return updated;
	}

	public LocationDetails updateLocationAddress(Identifier locationId, MailingAddress address) {
		LocationDetails updated = findLocationDetails(locationId).withAddress(address);
		data.put(locationId, updated);
		return updated;
	}

	public LocationSummary enableLocation(Identifier locationId) {		
		LocationDetails updated = findLocationDetails(locationId).withStatus(Status.ACTIVE);
		data.put(locationId, updated);
		return updated;
	}

	public LocationSummary disableLocation(Identifier locationId) {		
		LocationDetails updated = findLocationDetails(locationId).withStatus(Status.INACTIVE);
		data.put(locationId, updated);
		return updated;
	}
	
	@Override
	public LocationSummary findLocationSummary(Identifier locationId) {
		return findLocationDetails(locationId);
	}
	
	public LocationDetails findLocationDetails(Identifier locationId) {
		if (!data.containsKey(locationId))
			throw new ItemNotFoundException(locationId.toString());
		if (!(data.get(locationId) instanceof LocationDetails))
			throw new BadRequestException(locationId, "fatal", "class", "Class is not Location: " + locationId);
		return ((LocationDetails)SerializationUtils.clone(data.get(locationId)));
	}
	
	public long countLocations(LocationsFilter filter) {
		return data.values().stream()
			.filter(i -> i instanceof LocationDetails)
			.map(i -> (LocationDetails)i)
			.filter(p -> StringUtils.isNotBlank(filter.getDisplayName()) ? p.getDisplayName().contains(filter.getDisplayName()) : true)
			.count();
	}
	
	public Page<LocationSummary> findLocationSummaries(LocationsFilter filter, Paging paging) {
		List<LocationSummary> allMatchingLocations = data
			.values()
			.stream()
			.filter(i -> i instanceof LocationDetails)
			.map(i -> (LocationSummary) i)
			.filter(p -> StringUtils.isNotBlank(filter.getDisplayName()) ? p.getDisplayName().contains(filter.getDisplayName()) : true)
			.filter(i -> filter.getStatus() != null ? i.getStatus().equals(filter.getStatus()) : true)
			.map(i -> SerializationUtils.clone(i))
			.sorted(filter.getComparator(paging))
			.collect(Collectors.toList());		
		return PageBuilder.buildPageFor(allMatchingLocations, paging);
	}
	
	public Page<LocationDetails> findLocationDetails(LocationsFilter filter, Paging paging) {
		List<LocationDetails> allMatchingLocations = data
			.values()
			.stream()
			.filter(i -> i instanceof LocationDetails)
			.map(i -> (LocationDetails)i)
			.filter(p -> StringUtils.isNotBlank(filter.getDisplayName()) ? p.getDisplayName().contains(filter.getDisplayName()) : true)
			.filter(i -> filter.getStatus() != null ? i.getStatus().equals(filter.getStatus()) : true)
			.map(i -> SerializationUtils.clone(i))
			.sorted(filter.getComparator(paging))
			.collect(Collectors.toList());		
		return PageBuilder.buildPageFor(allMatchingLocations, paging);
	}

	public PersonDetails createPerson(Identifier organizationId, PersonName legalName, MailingAddress address, Communication communication, BusinessPosition unit) {
		Identifier personId = generateId();
		StringBuilder displayName = new StringBuilder();
		if (StringUtils.isNotBlank(legalName.getLastName()))
			displayName.append(legalName.getLastName());
		if (StringUtils.isNotBlank(legalName.getFirstName()) && displayName.length() > 0)
			displayName.append(", ");
		if (StringUtils.isNotBlank(legalName.getFirstName()))
			displayName.append(legalName.getFirstName());
		if (StringUtils.isNotBlank(legalName.getMiddleName()) && displayName.length() > 0)
			displayName.append(" ");
		if (StringUtils.isNotBlank(legalName.getMiddleName()))
			displayName.append(legalName.getMiddleName());
		PersonDetails person = new PersonDetails(personId, organizationId, Status.ACTIVE, displayName.toString(), legalName, address, communication, unit, null);
		data.put(personId, person);
		return person;
	}

	public PersonDetails updatePersonName(Identifier personId, PersonName legalName) {
		PersonDetails updated = findPersonDetails(personId).withLegalName(legalName);
		data.put(personId, updated);
		return updated;
	}

	public PersonDetails updatePersonAddress(Identifier personId, MailingAddress address) {		
		PersonDetails updated = findPersonDetails(personId).withAddress(address);
		data.put(personId, updated);
		return updated;
	}

	public PersonDetails updatePersonCommunication(Identifier personId, Communication communication) {		
		PersonDetails updated = findPersonDetails(personId).withCommunication(communication);
		data.put(personId, updated);
		return updated;
	}
	
	@Override
	public PersonDetails updatePersonBusinessUnit(Identifier personId, BusinessPosition position) {		
		PersonDetails updated = findPersonDetails(personId).withPosition(position);
		data.put(personId, updated);
		return updated;
	}

	public PersonSummary enablePerson(Identifier personId) {		
		PersonDetails updated = findPersonDetails(personId).withStatus(Status.ACTIVE);
		data.put(personId, updated);
		return summary(updated);
	}

	public PersonSummary disablePerson(Identifier personId) {		
		PersonDetails updated = findPersonDetails(personId).withStatus(Status.INACTIVE);
		data.put(personId, updated);
		return summary(updated);
	}
	
	public PersonSummary summary(PersonDetails details) {
		return new PersonSummary(details.getPersonId(), details.getOrganizationId(), details.getStatus(), details.getDisplayName());
	}
	
	@Override
	public PersonSummary findPersonSummary(Identifier personId) {
		return findPersonDetails(personId);
	}
	
	public PersonDetails findPersonDetails(Identifier personId) {
		if (!data.containsKey(personId))
			throw new ItemNotFoundException(personId.toString());
		if (!(data.get(personId) instanceof PersonDetails))
			throw new BadRequestException(personId, "fatal", "class", "Class is not Person: " + personId);
		return ((PersonDetails)data.get(personId));
	}
	
	@Override
	public long countPersons(PersonsFilter filter) {
		return data.values().stream()
			.filter(i -> i instanceof PersonDetails)
			.map(i -> (PersonDetails)i)
			.filter(p -> StringUtils.isNotBlank(filter.getDisplayName()) ? p.getDisplayName().contains(filter.getDisplayName()) : true)
			.count();
	}
	
	public Page<PersonSummary> findPersonSummaries(PersonsFilter filter, Paging paging) {
		List<PersonSummary> allMatchingPersons = data
			.values()
			.stream()
			.filter(i -> i instanceof PersonDetails)
			.map(i -> summary((PersonDetails)i))
			.filter(p -> StringUtils.isNotBlank(filter.getDisplayName()) ? p.getDisplayName().contains(filter.getDisplayName()) : true)
			.filter(i -> filter.getStatus() != null ? i.getStatus().equals(filter.getStatus()) : true)
			.map(i -> SerializationUtils.clone(i))
			.sorted(filter.getComparator(paging))
			.collect(Collectors.toList());
		return PageBuilder.buildPageFor(allMatchingPersons, paging);
	}
	
	public Page<PersonDetails> findPersonDetails(PersonsFilter filter, Paging paging) {
		List<PersonDetails> allMatchingPersons = data
			.values()
			.stream()
			.filter(i -> i instanceof PersonDetails)
			.map(i -> (PersonDetails)i)
			.filter(p -> StringUtils.isNotBlank(filter.getDisplayName()) ? p.getDisplayName().contains(filter.getDisplayName()) : true)
			.filter(i -> filter.getStatus() != null ? i.getStatus().equals(filter.getStatus()) : true)
			.map(i -> SerializationUtils.clone(i))
			.sorted(filter.getComparator(paging))
			.collect(Collectors.toList());
		return PageBuilder.buildPageFor(allMatchingPersons, paging);
	}

	public PersonDetails addUserRole(Identifier personId, Role role) {
		
		PersonDetails person = findPersonDetails(personId);		
		/* first time we add a role we need to generate the user name */
		if (person.getUser() == null) {
			String userName = person.getLegalName().getFirstName().substring(0, 1) + "X" + person.getLegalName().getLastName().substring(0, 1) + countPersons(new PersonsFilter());
			person = person.withUser(new User(userName, Arrays.asList(role)));
		}
		else {
			List<Role> roles = new ArrayList<Role>(person.getUser().getRoles());
			roles.add(role);
			person = person.withUser(person.getUser().withRoles(roles));
		}
		data.put(personId, person);
		return person;
	}

	public PersonDetails removeUserRole(Identifier personId, Role role) {		
		List<Role> roles = new ArrayList<Role>(findPersonDetails(personId).getUser().getRoles());
		roles.remove(role);
		PersonDetails updated = ((PersonDetails)data.get(personId)).withUser(((PersonDetails)data.get(personId)).getUser().withRoles(roles));
		data.put(personId, updated);
		return updated;
	}

	public OrganizationDetails validate(OrganizationDetails organization) throws BadRequestException {
		List<Message> messages = new ArrayList<Message>();
		
		// Display Name
		if (StringUtils.isBlank(organization.getDisplayName())) {
			messages.add(new Message(organization.getOrganizationId(), "error", "displayName", "Display name is mandatory for an organization"));
		} else if (organization.getDisplayName().length() > 60) {
			messages.add(new Message(organization.getOrganizationId(), "error", "displayName", "Display name must be 60 characters or less"));
		}
		
		// Status
		if (organization.getStatus() == null)
			messages.add(new Message(organization.getOrganizationId(), "error", "status", "Status is mandatory for an organization"));
		
		// Main location reference
		if (organization.getMainLocationId() != null && !findLocationSummary(organization.getMainLocationId()).getOrganizationId().equals(organization.getOrganizationId())) {
			messages.add(new Message(organization.getOrganizationId(), "error", "mainLocation", "Main location organization has invalid referential integrity"));
		}
		
		if (!messages.isEmpty())
			throw new BadRequestException(messages);
		return organization;
	}

	public LocationDetails validate(LocationDetails location) throws BadRequestException {
		List<Message> messages = new ArrayList<Message>();
		
		// Organization
		if (location.getOrganizationId() == null) {
			messages.add(new Message(location.getLocationId(), "error", "organizationId", "Organization cannot be null"));
		} else {
			try {
				findOrganizationSummary(location.getOrganizationId());
			} catch (ItemNotFoundException e) {
				messages.add(new Message(location.getLocationId(), "error", "organizationId", "Organization does not exist"));
			}
		}
		
		// Status
		if (location.getStatus() == null)
			messages.add(new Message(location.getLocationId(), "error", "status", "Status is mandatory for a location"));
		
		// Reference
		if (StringUtils.isBlank(location.getReference())) {
			messages.add(new Message(location.getLocationId(), "error", "reference", "Reference is mandatory for aa location"));
		} else if (location.getReference().matches("[A-Z0-9-]{0,60}")) {
			messages.add(new Message(location.getLocationId(), "error", "reference", "Reference is not in the correct format"));
		}
		
		// Display Name
		if (StringUtils.isBlank(location.getDisplayName())) {
			messages.add(new Message(location.getLocationId(), "error", "displayName", "Display name is mandatory for a location"));
		} else if (location.getDisplayName().length() > 60) {
			messages.add(new Message(location.getLocationId(), "error", "displayName", "Display name must be 60 characters or less"));
		}
		
		// Address
		if (location.getAddress() == null) {
			messages.add(new Message(location.getLocationId(), "error", "address", "Mailing address is mandatory for a location"));
		} else {
			validateMailingAddress(location.getAddress(), messages, location.getLocationId(), "address");
		}
		
		if (!messages.isEmpty())
			throw new BadRequestException(messages);
		return location;
	}
	
	private void validateMailingAddress(MailingAddress address, List<Message> messages, Identifier identifier, String prefix) {
		// Street
		if (StringUtils.isBlank(address.getStreet())) {
			messages.add(new Message(identifier, "error", prefix + ".street", "Street address is mandatory"));
		} else if (address.getStreet().length() > 60) {
			messages.add(new Message(identifier, "error", prefix + ".street", "Street must be 60 characters or less"));
		}
		
		// City
		if (StringUtils.isBlank(address.getCity())) {
			messages.add(new Message(identifier, "error", prefix + ".city", "City is mandatory"));
		} else if (address.getCity().length() > 60) {
			messages.add(new Message(identifier, "error", prefix + ".city", "City must be 60 characters or less"));
		}
		
		// Province
		if (StringUtils.isBlank(address.getProvince())) {
			messages.add(new Message(identifier, "error", prefix + ".province", "Province is mandatory"));
		} else if (address.getCountry() == null) {
			messages.add(new Message(identifier, "error", prefix + ".province", "Province is forbidden unless there is a country"));
		}
		
		// Country
		if (address.getCountry() == null || address.getCountry().getCode() == null) {
			messages.add(new Message(identifier, "error", prefix + ".country", "Country is mandatory"));
		} else {
			try {
				findCountryByCode(address.getCountry().getCode());
			} catch (ItemNotFoundException e) {
				messages.add(new Message(identifier, "error", prefix + ".country", "Country code is not in the lookup"));
			}
		}
		
		// Postal Code
		if (StringUtils.isNotBlank(address.getPostalCode())) {
			if (address.getCountry() != null || address.getCountry().getCode() != null && address.getCountry().getCode().equals("CA")) {
				if (!address.getPostalCode().matches("[A-Z][0-9][A-Z][0-9][A-Z][0-9]")) {
					messages.add(new Message(identifier, "error", prefix + ".provinceCode", "Canadian province format is invalid"));
				}
			}
		}
	}
	
	private void validatePersonName(PersonName name, List<Message> messages, Identifier identifier, String prefix) {

		// Salutation
		if (name.getSalutation() == null) {
			messages.add(new Message(identifier, "error", prefix + ".salutation", "Salutation is mandatory"));
		} else {
			try {
				findSalutationByCode(name.getSalutation().getCode());
			} catch (ItemNotFoundException e) {
				messages.add(new Message(identifier, "error", prefix + ".salutation", "Salutation code is not in the lookup"));
			}
		}
		
		// First Name
		if (StringUtils.isBlank(name.getFirstName())) {
			messages.add(new Message(identifier, "error", prefix + ".firstName", "First name is required"));
		} else if (name.getFirstName().length() > 60) {
			messages.add(new Message(identifier, "error", prefix + ".firstName", "First name must be 60 characters or less"));
		}
		
		// Middle Name
		if (StringUtils.isBlank(name.getFirstName())) {
			messages.add(new Message(identifier, "error", prefix + ".middleName", "Middle name is required"));
		} else if (name.getFirstName().length() > 30) {
			messages.add(new Message(identifier, "error", prefix + ".middleName", "Middle name must be 60 characters or less"));
		}
		
		// Last Name
		if (StringUtils.isBlank(name.getFirstName())) {
			messages.add(new Message(identifier, "error", prefix + ".lastName", "Last name is required"));
		} else if (name.getFirstName().length() > 60) {
			messages.add(new Message(identifier, "error", prefix + ".lastName", "Last name must be 60 characters or less"));
		}
		
	}

	public PersonDetails validate(PersonDetails person) throws BadRequestException {
		List<Message> messages = new ArrayList<Message>();
		
		// Organization
		if (person.getOrganizationId() == null) {
			messages.add(new Message(person.getPersonId(), "error", "organizationId", "Organization cannot be null"));
		} else {
			try {
				findOrganizationSummary(person.getOrganizationId());
			} catch (ItemNotFoundException e) {
				messages.add(new Message(person.getPersonId(), "error", "organizationId", "Organization does not exist"));
			}
		}
		
		// Status
		if (person.getStatus() == null)
			messages.add(new Message(person.getPersonId(), "error", "status", "Status is mandatory for a person"));
		
		// Display Name
		if (StringUtils.isBlank(person.getDisplayName())) {
			messages.add(new Message(person.getPersonId(), "error", "displayName", "Display name is mandatory for a person"));
		} else if (person.getDisplayName().length() > 60) {
			messages.add(new Message(person.getPersonId(), "error", "displayName", "Display name must be 60 characters or less"));
		}
		
		// Legal Name
		if (person.getLegalName() == null) {
			messages.add(new Message(person.getPersonId(), "error", "legalName", "Legal name is mandatory for a person"));
		} else {
			validatePersonName(person.getLegalName(), messages, person.getPersonId(), "legalName");
		}
		
		// Address
		if (person.getAddress() != null) {
			validateMailingAddress(person.getAddress(), messages, person.getPersonId(), "address");
		}

		if (!messages.isEmpty())
			throw new BadRequestException(messages);
		return person;
	}

	public List<Role> validate(List<Role> roles, Identifier personId) throws BadRequestException {
		List<Message> messages = new ArrayList<Message>();
		
		if (!messages.isEmpty())
			throw new BadRequestException(messages);
		return roles;
	}

}