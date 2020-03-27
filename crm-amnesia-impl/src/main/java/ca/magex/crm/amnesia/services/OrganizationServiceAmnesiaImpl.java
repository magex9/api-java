package ca.magex.crm.amnesia.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;

import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.crm.Location;
import ca.magex.crm.api.crm.Organization;
import ca.magex.crm.api.crm.Person;
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.PageBuilder;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.services.OrganizationService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.system.Role;
import ca.magex.crm.api.system.Status;

public class OrganizationServiceAmnesiaImpl implements OrganizationService {
	
	private static final String BASE_58 = "123456789abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ";
	
	private Map<Identifier, Object> data;		
	
	public OrganizationServiceAmnesiaImpl() {
		data = new HashMap<Identifier, Object>();
	}
	
	public Identifier generateId() {
		return new Identifier(RandomStringUtils.random(10, BASE_58));
	}

	public Organization createOrganization(String organizationName) {
		Identifier organizationId = generateId();
		Organization organization = new Organization(organizationId, Status.ACTIVE, organizationName, null);
		data.put(organizationId, organization);
		return organization;
	}

	public Organization enableOrganization(Identifier organizationId) {
		if (!data.containsKey(organizationId))
			throw new ItemNotFoundException(organizationId.toString());
		if (!(data.get(organizationId) instanceof Organization))
			throw new BadRequestException(organizationId, "fatal", "class", "Class is not Organization: " + organizationId);
		Organization updated = ((Organization)data.get(organizationId)).withStatus(Status.ACTIVE);
		data.put(organizationId, updated);
		return updated;
	}

	public Organization disableOrganization(Identifier organizationId) {
		if (!data.containsKey(organizationId))
			throw new ItemNotFoundException(organizationId.toString());
		if (!(data.get(organizationId) instanceof Organization))
			throw new BadRequestException(organizationId, "fatal", "class", "Class is not Organization: " + organizationId);
		Organization updated = ((Organization)data.get(organizationId)).withStatus(Status.INACTIVE);
		data.put(organizationId, updated);
		return updated;
	}

	public Organization updateOrganizationName(Identifier organizationId, String name) {
		if (!data.containsKey(organizationId))
			throw new ItemNotFoundException(organizationId.toString());
		if (!(data.get(organizationId) instanceof Organization))
			throw new BadRequestException(organizationId, "fatal", "class", "Class is not Organization: " + organizationId);
		Organization updated = ((Organization)data.get(organizationId)).withDisplayName(name);
		data.put(organizationId, updated);
		return updated;
	}

	public Organization updateOrganizationMainLocation(Identifier organizationId, Identifier locationId) {
		if (!data.containsKey(organizationId))
			throw new ItemNotFoundException(organizationId.toString());
		if (!(data.get(organizationId) instanceof Organization))
			throw new BadRequestException(organizationId, "fatal", "class", "Class is not Organization: " + organizationId);
		Organization updated = ((Organization)data.get(organizationId)).withMainLocation(locationId);
		data.put(organizationId, updated);
		return updated;
	}
	
	public Organization findOrganization(Identifier organizationId) {
		if (!data.containsKey(organizationId))
			throw new ItemNotFoundException(organizationId.toString());
		if (!(data.get(organizationId) instanceof Organization))
			throw new BadRequestException(organizationId, "fatal", "class", "Class is not Organization: " + organizationId);
		return ((Organization)data.get(organizationId));
	}
	
	@Override
	public long countOrganizations(OrganizationsFilter filter) {
		return data.values().stream()
			.filter(i -> i instanceof Organization)
			.map(i -> (Organization)i)
			.filter(p -> StringUtils.isNotBlank(filter.getDisplayName()) ? p.getDisplayName().contains(filter.getDisplayName()) : true)
			.count();
	}
	
	public Page<Organization> findOrganizations(OrganizationsFilter filter) {
		List<Organization> allMatchingOrgs = data
				.values()
				.stream()
				.filter(i -> i instanceof Organization)
				.map(i -> (Organization)i)
				.filter(p -> StringUtils.isNotBlank(filter.getDisplayName()) ? p.getDisplayName().contains(filter.getDisplayName()) : true)
				.collect(Collectors.toList());		
		return PageBuilder.buildPageFor(allMatchingOrgs, filter.getPaging());
	}

	public Location createLocation(Identifier organizationId, String locationName, String locationReference,
			MailingAddress address) {
		Identifier locationId = generateId();
		Location location = new Location(locationId, organizationId, Status.ACTIVE, locationReference, locationName, address);
		data.put(locationId, location);
		return location;
	}

	public Location updateLocationName(Identifier locationId, String locationName) {
		if (!data.containsKey(locationId))
			throw new ItemNotFoundException(locationId.toString());
		if (!(data.get(locationId) instanceof Location))
			throw new BadRequestException(locationId, "fatal", "class", "Class is not Location: " + locationId);
		Location updated = ((Location)data.get(locationId)).withDisplayName(locationName);
		data.put(locationId, updated);
		return updated;
	}

	public Location updateLocationAddress(Identifier locationId, MailingAddress address) {
		if (!data.containsKey(locationId))
			throw new ItemNotFoundException(locationId.toString());
		if (!(data.get(locationId) instanceof Location))
			throw new BadRequestException(locationId, "fatal", "class", "Class is not Location: " + locationId);
		Location updated = ((Location)data.get(locationId)).withAddress(address);
		data.put(locationId, updated);
		return updated;
	}

	public Location enableLocation(Identifier locationId) {
		if (!data.containsKey(locationId))
			throw new ItemNotFoundException(locationId.toString());
		if (!(data.get(locationId) instanceof Location))
			throw new BadRequestException(locationId, "fatal", "class", "Class is not Location: " + locationId);
		Location updated = ((Location)data.get(locationId)).withStatus(Status.ACTIVE);
		data.put(locationId, updated);
		return updated;
	}

	public Location disableLocation(Identifier locationId) {
		if (!data.containsKey(locationId))
			throw new ItemNotFoundException(locationId.toString());
		if (!(data.get(locationId) instanceof Location))
			throw new BadRequestException(locationId, "fatal", "class", "Class is not Location: " + locationId);
		Location updated = ((Location)data.get(locationId)).withStatus(Status.INACTIVE);
		data.put(locationId, updated);
		return updated;
	}
	
	public Location findLocation(Identifier locationId) {
		if (!data.containsKey(locationId))
			throw new ItemNotFoundException(locationId.toString());
		if (!(data.get(locationId) instanceof Location))
			throw new BadRequestException(locationId, "fatal", "class", "Class is not Location: " + locationId);
		return ((Location)data.get(locationId));
	}
	
	public long countLocations(LocationsFilter filter) {
		return data.values().stream()
			.filter(i -> i instanceof Location)
			.map(i -> (Location)i)
			.filter(p -> StringUtils.isNotBlank(filter.getDisplayName()) ? p.getDisplayName().contains(filter.getDisplayName()) : true)
			.count();
	}
	
	public Page<Location> findLocations(LocationsFilter filter) {
		List<Location> allMatchingLocations = data
				.values()
				.stream()
				.filter(i -> i instanceof Location)
				.map(i -> (Location)i)
				.filter(p -> StringUtils.isNotBlank(filter.getDisplayName()) ? p.getDisplayName().contains(filter.getDisplayName()) : true)
				.collect(Collectors.toList());		
		return PageBuilder.buildPageFor(allMatchingLocations, filter.getPaging());
	}

	public Person createPerson(Identifier organizationId, PersonName legalName, MailingAddress address, Communication communication, BusinessPosition unit) {
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
		Person person = new Person(personId, organizationId, Status.ACTIVE, displayName.toString(), legalName, address, communication, unit, null);
		data.put(personId, person);
		return person;
	}

	public Person updatePersonName(Identifier personId, PersonName legalName) {
		if (!data.containsKey(personId))
			throw new ItemNotFoundException(personId.toString());
		if (!(data.get(personId) instanceof Person))
			throw new BadRequestException(personId, "fatal", "class", "Class is not Person: " + personId);
		Person updated = ((Person)data.get(personId)).withLegalName(legalName);
		data.put(personId, updated);
		return updated;
	}

	public Person updatePersonAddress(Identifier personId, MailingAddress address) {
		if (!data.containsKey(personId))
			throw new ItemNotFoundException(personId.toString());
		if (!(data.get(personId) instanceof Person))
			throw new BadRequestException(personId, "fatal", "class", "Class is not Person: " + personId);
		Person updated = ((Person)data.get(personId)).withAddress(address);
		data.put(personId, updated);
		return updated;
	}

	public Person updatePersonCommunication(Identifier personId, Communication communication) {
		if (!data.containsKey(personId))
			throw new ItemNotFoundException(personId.toString());
		if (!(data.get(personId) instanceof Person))
			throw new BadRequestException(personId, "fatal", "class", "Class is not Person: " + personId);
		Person updated = ((Person)data.get(personId)).withCommunication(communication);
		data.put(personId, updated);
		return updated;
	}
	
	@Override
	public Person updatePersonBusinessUnit(Identifier personId, BusinessPosition position) {
		if (!data.containsKey(personId))
			throw new ItemNotFoundException(personId.toString());
		if (!(data.get(personId) instanceof Person))
			throw new BadRequestException(personId, "fatal", "class", "Class is not Person: " + personId);
		Person updated = ((Person)data.get(personId)).withPosition(position);
		data.put(personId, updated);
		return updated;
	}

	public Person enablePerson(Identifier personId) {
		if (!data.containsKey(personId))
			throw new ItemNotFoundException(personId.toString());
		if (!(data.get(personId) instanceof Person))
			throw new BadRequestException(personId, "fatal", "class", "Class is not Person: " + personId);
		Person updated = ((Person)data.get(personId)).withStatus(Status.ACTIVE);
		data.put(personId, updated);
		return updated;
	}

	public Person disablePerson(Identifier personId) {
		if (!data.containsKey(personId))
			throw new ItemNotFoundException(personId.toString());
		if (!(data.get(personId) instanceof Person))
			throw new BadRequestException(personId, "fatal", "class", "Class is not Person: " + personId);
		Person updated = ((Person)data.get(personId)).withStatus(Status.ACTIVE);
		data.put(personId, updated);
		return updated;
	}
	
	public Person findPerson(Identifier personId) {
		if (!data.containsKey(personId))
			throw new ItemNotFoundException(personId.toString());
		if (!(data.get(personId) instanceof Person))
			throw new BadRequestException(personId, "fatal", "class", "Class is not Person: " + personId);
		return ((Person)data.get(personId));
	}
	
	@Override
	public long countPersons(PersonsFilter filter) {
		return data.values().stream()
			.filter(i -> i instanceof Person)
			.map(i -> (Person)i)
			.filter(p -> StringUtils.isNotBlank(filter.getDisplayName()) ? p.getDisplayName().contains(filter.getDisplayName()) : true)
			.count();
	}
	
	public Page<Person> findPersons(PersonsFilter filter) {
		List<Person> allMatchingPersons = data
			.values()
			.stream()
			.filter(i -> i instanceof Person)
			.map(i -> (Person)i)
			.filter(p -> StringUtils.isNotBlank(filter.getDisplayName()) ? p.getDisplayName().contains(filter.getDisplayName()) : true)
			.collect(Collectors.toList());
		return PageBuilder.buildPageFor(allMatchingPersons, filter.getPaging());
	}

	public Person addUserRole(Identifier personId, Role role) {
		if (!data.containsKey(personId))
			throw new ItemNotFoundException(personId.toString());
		if (!(data.get(personId) instanceof Person))
			throw new BadRequestException(personId, "fatal", "class", "Class is not Person: " + personId);
		List<Role> roles = new ArrayList<Role>(((Person)data.get(personId)).getUser().getRoles());
		roles.add(role);
		Person updated = ((Person)data.get(personId)).withUser(((Person)data.get(personId)).getUser().withRoles(roles));
		data.put(personId, updated);
		return updated;
	}

	public Person removeUserRole(Identifier personId, Role role) {
		if (!data.containsKey(personId))
			throw new ItemNotFoundException(personId.toString());
		if (!(data.get(personId) instanceof Person))
			throw new BadRequestException(personId, "fatal", "class", "Class is not Person: " + personId);
		List<Role> roles = new ArrayList<Role>(((Person)data.get(personId)).getUser().getRoles());
		roles.remove(role);
		Person updated = ((Person)data.get(personId)).withUser(((Person)data.get(personId)).getUser().withRoles(roles));
		data.put(personId, updated);
		return updated;
	}

	public List<Message> validate(Organization organization) {
		return new ArrayList<Message>();
	}

	public List<Message> validate(Location location) {
		return new ArrayList<Message>();
	}

	public List<Message> validate(Person person) {
		return new ArrayList<Message>();
	}

	public List<Message> validate(List<Role> roles) {
		return new ArrayList<Message>();
	}

}
