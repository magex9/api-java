package ca.magex.crm.amnesia.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
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
import ca.magex.crm.api.services.OrganizationService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.system.Role;
import ca.magex.crm.api.system.Status;

public class OrganizationServiceAmnesiaImpl implements OrganizationService {

	private IdGenerator idGenerator;
	
	private Map<Identifier, Serializable> data;		
	
	public OrganizationServiceAmnesiaImpl() {
		idGenerator = new AmnesiaBase58IdGenerator();
		data = new HashMap<Identifier, Serializable>();
	}
	
	public Identifier generateId() {
		return idGenerator.generate();
	}

	public OrganizationDetails createOrganization(String organizationName) {
		Identifier organizationId = generateId();
		OrganizationDetails organization = new OrganizationDetails(organizationId, Status.ACTIVE, organizationName, null);
		data.put(organizationId, organization);
		return organization;
	}

	public OrganizationSummary enableOrganization(Identifier organizationId) {
		OrganizationDetails updated = findOrganization(organizationId).withStatus(Status.ACTIVE);
		data.put(organizationId, updated);
		return updated;
	}

	public OrganizationSummary disableOrganization(Identifier organizationId) {
		OrganizationDetails updated = findOrganization(organizationId).withStatus(Status.INACTIVE);
		data.put(organizationId, updated);
		return updated;
	}

	public OrganizationDetails updateOrganizationName(Identifier organizationId, String name) {
		OrganizationDetails updated = findOrganization(organizationId).withDisplayName(name);
		data.put(organizationId, updated);
		return updated;
	}

	public OrganizationDetails updateOrganizationMainLocation(Identifier organizationId, Identifier locationId) {
		OrganizationDetails updated = findOrganization(organizationId).withMainLocationId(findLocation(locationId).getLocationId());
		data.put(organizationId, updated);
		return updated;
	}
	
	public OrganizationDetails findOrganization(Identifier organizationId) {
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
			.map(i -> SerializationUtils.clone(i))
			.sorted(Comparator.comparing(OrganizationSummary::getDisplayName))
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
			.map(i -> SerializationUtils.clone(i))
			.sorted(Comparator.comparing(OrganizationSummary::getDisplayName))
			.collect(Collectors.toList());
		return PageBuilder.buildPageFor(allMatchingOrgs, paging);
	}

	public LocationDetails createLocation(Identifier organizationId, String locationName, String locationReference, MailingAddress address) {
		Identifier locationId = generateId();
		LocationDetails location = new LocationDetails(locationId, findOrganization(organizationId).getOrganizationId(), Status.ACTIVE, locationReference, locationName, address);
		data.put(locationId, location);
		return location;
	}

	public LocationDetails updateLocationName(Identifier locationId, String locationName) {
		LocationDetails updated = findLocation(locationId).withDisplayName(locationName);
		data.put(locationId, updated);
		return updated;
	}

	public LocationDetails updateLocationAddress(Identifier locationId, MailingAddress address) {
		LocationDetails updated = findLocation(locationId).withAddress(address);
		data.put(locationId, updated);
		return updated;
	}

	public LocationSummary enableLocation(Identifier locationId) {		
		LocationDetails updated = findLocation(locationId).withStatus(Status.ACTIVE);
		data.put(locationId, updated);
		return updated;
	}

	public LocationSummary disableLocation(Identifier locationId) {		
		LocationDetails updated = findLocation(locationId).withStatus(Status.INACTIVE);
		data.put(locationId, updated);
		return updated;
	}	
	
	public LocationDetails findLocation(Identifier locationId) {
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
			.map(i -> SerializationUtils.clone(i))
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
			.map(i -> SerializationUtils.clone(i))
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
		PersonDetails updated = findPerson(personId).withLegalName(legalName);
		data.put(personId, updated);
		return updated;
	}

	public PersonDetails updatePersonAddress(Identifier personId, MailingAddress address) {		
		PersonDetails updated = findPerson(personId).withAddress(address);
		data.put(personId, updated);
		return updated;
	}

	public PersonDetails updatePersonCommunication(Identifier personId, Communication communication) {		
		PersonDetails updated = findPerson(personId).withCommunication(communication);
		data.put(personId, updated);
		return updated;
	}
	
	@Override
	public PersonDetails updatePersonBusinessUnit(Identifier personId, BusinessPosition position) {		
		PersonDetails updated = findPerson(personId).withPosition(position);
		data.put(personId, updated);
		return updated;
	}

	public PersonSummary enablePerson(Identifier personId) {		
		PersonDetails updated = findPerson(personId).withStatus(Status.ACTIVE);
		data.put(personId, updated);
		return summary(updated);
	}

	public PersonSummary disablePerson(Identifier personId) {		
		PersonDetails updated = findPerson(personId).withStatus(Status.INACTIVE);
		data.put(personId, updated);
		return summary(updated);
	}
	
	public PersonSummary summary(PersonDetails details) {
		return new PersonSummary(details.getPersonId(), details.getOrganizationId(), details.getStatus(), details.getDisplayName());
	}
	
	public PersonDetails findPerson(Identifier personId) {
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
			.map(i -> SerializationUtils.clone(i))
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
			.map(i -> SerializationUtils.clone(i))
			.collect(Collectors.toList());
		return PageBuilder.buildPageFor(allMatchingPersons, paging);
	}

	public PersonDetails addUserRole(Identifier personId, Role role) {
		
		PersonDetails person = findPerson(personId);		
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
		List<Role> roles = new ArrayList<Role>(findPerson(personId).getUser().getRoles());
		roles.remove(role);
		PersonDetails updated = ((PersonDetails)data.get(personId)).withUser(((PersonDetails)data.get(personId)).getUser().withRoles(roles));
		data.put(personId, updated);
		return updated;
	}

	public List<Message> validate(OrganizationDetails organization) {
		return new ArrayList<Message>();
	}

	public List<Message> validate(LocationDetails location) {
		return new ArrayList<Message>();
	}

	public List<Message> validate(PersonDetails person) {
		return new ArrayList<Message>();
	}

	public List<Message> validate(List<Role> roles) {
		return new ArrayList<Message>();
	}

}