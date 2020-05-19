package ca.magex.crm.api.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

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
import ca.magex.crm.api.filters.GroupsFilter;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.system.Status;

public class StructureValidationService implements CrmValidation {

	private CrmLookupService lookups;
	
	private CrmPermissionService permissions;
	
	private CrmOrganizationService organizations;
	
	private CrmPersonService persons;

	private CrmLocationService locations;

	public StructureValidationService(CrmLookupService lookups, CrmPermissionService permissions, CrmOrganizationService organizations,
			CrmLocationService locations, CrmPersonService persons) {
		super();
		this.lookups = lookups;
		this.permissions = permissions;
		this.organizations = organizations;
		this.locations = locations;
		this.persons = persons;
	}

	public Group validate(Group group) {
		List<Message> messages = new ArrayList<Message>();
		
		// Status
		if (group.getStatus() == null)
			throw new BadRequestException("Status cannot be null", group.getGroupId(), "error", "status", new Localized(Lang.ENGLISH, "Status is mandatory for a group"));

		// If disabling the organization, make sure its not already disabled
		if (group.getStatus().equals(Status.INACTIVE)) {
			try {
				Group existing = permissions.findGroup(group.getGroupId());
				if (existing.getStatus().equals(Status.INACTIVE))
					throw new BadRequestException("Creating inactive group", group.getGroupId(), "error", "status", new Localized(Lang.ENGLISH, "Cannot update a group that is already inactive"));
			} catch (ItemNotFoundException e) {
				throw new BadRequestException("Creating inactive group", group.getGroupId(), "error", "status", new Localized(Lang.ENGLISH, "Cannot create a new group that is inactive"));
			}
		}

		// Must be a valid group code
		if (StringUtils.isBlank(group.getCode())) {
			messages.add(new Message(group.getGroupId(), "error", "code", new Localized(Lang.ENGLISH, "Display name is mandatory for an organization")));
		} else if (!group.getCode().matches("[A-Z0-9_]{1,20}")) {
			messages.add(new Message(group.getGroupId(), "error", "code", new Localized(Lang.ENGLISH, "Display name must be 60 characters or less")));
		}
		
		// Make sure the code is unique
		FilteredPage<Group> groups = permissions.findGroups(permissions.defaultGroupsFilter().withCode(group.getCode()), GroupsFilter.getDefaultPaging().allItems());
		for (Group existing : groups.getContent()) {
			if (!existing.getGroupId().equals(group.getGroupId()))
				messages.add(new Message(group.getGroupId(), "error", "code", new Localized(Lang.ENGLISH, "Duplicate code found in another group: " + existing.getGroupId())));
		}
		
		// Make sure there is an English description
		if (StringUtils.isBlank(group.getName(Lang.ENGLISH)))
			messages.add(new Message(group.getGroupId(), "error", "englishName", new Localized(Lang.ENGLISH, "An English description is required")));
		
		// Make sure there is a French description
		if (StringUtils.isBlank(group.getName(Lang.FRENCH)))
			messages.add(new Message(group.getGroupId(), "error", "frenchName", new Localized(Lang.ENGLISH, "An French description is required")));
		
		if (!messages.isEmpty())
			throw new BadRequestException("Group has validation errors", messages);
		return group;
	}

	public Role validate(Role role) {
		List<Message> messages = new ArrayList<Message>();
		
		// Status
		if (role.getStatus() == null)
			throw new BadRequestException("Status cannot be null", role.getGroupId(), "error", "status", new Localized(Lang.ENGLISH, "Status is mandatory for a role"));

		// If disabling the organization, make sure its not already disabled
		if (role.getStatus().equals(Status.INACTIVE)) {
			try {
				Group existing = permissions.findGroup(role.getGroupId());
				if (existing.getStatus().equals(Status.INACTIVE))
					throw new BadRequestException("Creating inactive role", role.getGroupId(), "error", "status", new Localized(Lang.ENGLISH, "Cannot update a role that is already inactive"));
			} catch (ItemNotFoundException e) {
				throw new BadRequestException("Creating inactive role", role.getGroupId(), "error", "status", new Localized(Lang.ENGLISH, "Cannot create a new role that is inactive"));
			}
		}

		// Must be a valid role code
		if (StringUtils.isBlank(role.getCode())) {
			messages.add(new Message(role.getGroupId(), "error", "code", new Localized(Lang.ENGLISH, "Display name is mandatory for an organization")));
		} else if (!role.getCode().matches("[A-Z0-9_]{1,20}")) {
			messages.add(new Message(role.getGroupId(), "error", "code", new Localized(Lang.ENGLISH, "Display name must be 60 characters or less")));
		}
		
		// Make sure the code is unique
		FilteredPage<Group> roles = permissions.findGroups(permissions.defaultGroupsFilter().withCode(role.getCode()), GroupsFilter.getDefaultPaging().allItems());
		for (Group existing : roles.getContent()) {
			if (!existing.getGroupId().equals(role.getGroupId()))
				messages.add(new Message(role.getGroupId(), "error", "code", new Localized(Lang.ENGLISH, "Duplicate code found in another role: " + existing.getGroupId())));
		}
		
		// Make sure there is an English description
		if (StringUtils.isBlank(role.getName(Lang.ENGLISH)))
			messages.add(new Message(role.getGroupId(), "error", "englishName", new Localized(Lang.ENGLISH, "An English description is required")));
		
		// Make sure there is a French description
		if (StringUtils.isBlank(role.getName(Lang.FRENCH)))
			messages.add(new Message(role.getGroupId(), "error", "frenchName", new Localized(Lang.ENGLISH, "An French description is required")));
		
		if (!messages.isEmpty())
			throw new BadRequestException("Group has validation errors", messages);
		return role;
	}

	public OrganizationDetails validate(OrganizationDetails organization) throws BadRequestException {
		List<Message> messages = new ArrayList<Message>();
		
		// Status
		if (organization.getStatus() == null)
			throw new BadRequestException("Status cannot be null", organization.getOrganizationId(), "error", "status", new Localized(Lang.ENGLISH, "Status is mandatory for an organization"));

		// If disabling the organization, make sure its not already disabled
		if (organization.getStatus().equals(Status.INACTIVE)) {
			try {
				OrganizationSummary existing = organizations.findOrganizationSummary(organization.getOrganizationId());
				if (existing.getStatus().equals(Status.INACTIVE))
					throw new BadRequestException("Creating inactive organization", organization.getOrganizationId(), "error", "status", new Localized(Lang.ENGLISH, "Cannot update an organziation that is already inactive"));
			} catch (ItemNotFoundException e) {
				throw new BadRequestException("Creating inactive organization", organization.getOrganizationId(), "error", "status", new Localized(Lang.ENGLISH, "Cannot create a new organization that is inactive"));
			}
		}
		
		// Display Name
		if (StringUtils.isBlank(organization.getDisplayName())) {
			messages.add(new Message(organization.getOrganizationId(), "error", "displayName", new Localized(Lang.ENGLISH, "Display name is mandatory for an organization")));
		} else if (organization.getDisplayName().length() > 60) {
			messages.add(new Message(organization.getOrganizationId(), "error", "displayName", new Localized(Lang.ENGLISH, "Display name must be 60 characters or less")));
		}
		
		// Main contact reference
		if (organization.getMainContactId() != null) {
			PersonSummary person = persons.findPersonSummary(organization.getMainContactId());
			// Make sure main contact belongs to current org
			if (!person.getOrganizationId().equals(organization.getOrganizationId())) {
				messages.add(new Message(organization.getOrganizationId(), "error", "mainContactId", new Localized(Lang.ENGLISH, "Main contact organization has invalid referential integrity")));
			}
			// Make sure main contact is active
			if (!person.getStatus().equals(Status.ACTIVE)) {
				messages.add(new Message(organization.getOrganizationId(), "error", "mainContactId", new Localized(Lang.ENGLISH, "Main contact must be active")));
			}
		}
		
		// Main location reference
		if (organization.getMainLocationId() != null) {
			LocationSummary location = locations.findLocationSummary(organization.getMainLocationId());
			// Make sure main location belongs to current org
			if (!location.getOrganizationId().equals(organization.getOrganizationId())) {
				messages.add(new Message(organization.getOrganizationId(), "error", "mainLocationId", new Localized(Lang.ENGLISH, "Main location organization has invalid referential integrity")));
			}
			// Make sure main location is active
			if (!location.getStatus().equals(Status.ACTIVE)) {
				messages.add(new Message(organization.getOrganizationId(), "error", "mainLocationId", new Localized(Lang.ENGLISH, "Main location must be active")));
			}
		}

		// Group
		if (organization.getGroups().isEmpty()) {
			messages.add(new Message(organization.getOrganizationId(), "error", "groups", new Localized(Lang.ENGLISH, "Organizations must have a permission group assigned to them")));
		} else {
			for (int i = 0; i < organization.getGroups().size(); i++) {
				String group = organization.getGroups().get(i);
				try {
					if (!permissions.findGroupByCode(group).getStatus().equals(Status.ACTIVE))
						messages.add(new Message(organization.getOrganizationId(), "error", "groups[" + i + "]", new Localized(Lang.ENGLISH, "Group is not active: " + group)));
				} catch (ItemNotFoundException e) {
					messages.add(new Message(organization.getOrganizationId(), "error", "groups[" + i + "]", new Localized(Lang.ENGLISH, "Group does not exist: " + group)));
				}
			}
		}
		
		if (!messages.isEmpty())
			throw new BadRequestException("Organization has validation errors", messages);
		return organization;
	}

	public LocationDetails validate(LocationDetails location) throws BadRequestException {
		List<Message> messages = new ArrayList<Message>();
		
		// Organization
		if (location.getOrganizationId() == null) {
			messages.add(new Message(location.getLocationId(), "error", "organizationId", new Localized(Lang.ENGLISH, "Organization cannot be null")));
		} else {
			try {
				organizations.findOrganizationDetails(location.getOrganizationId());
			} catch (ItemNotFoundException e) {
				messages.add(new Message(location.getLocationId(), "error", "organizationId", new Localized(Lang.ENGLISH, "Organization does not exist")));
			}
		}
		
		// Status
		if (location.getStatus() == null)
			messages.add(new Message(location.getLocationId(), "error", "status", new Localized(Lang.ENGLISH, "Status is mandatory for a location")));
		
		// Reference
		if (StringUtils.isBlank(location.getReference())) {
			messages.add(new Message(location.getLocationId(), "error", "reference", new Localized(Lang.ENGLISH, "Reference is mandatory for a location")));
		} else if (!location.getReference().matches("[A-Z0-9-]{1,60}")) {
			messages.add(new Message(location.getLocationId(), "error", "reference", new Localized(Lang.ENGLISH, "Reference is not in the correct format")));
		}
		
		// Display Name
		if (StringUtils.isBlank(location.getDisplayName())) {
			messages.add(new Message(location.getLocationId(), "error", "displayName", new Localized(Lang.ENGLISH, "Display name is mandatory for a location")));
		} else if (location.getDisplayName().length() > 60) {
			messages.add(new Message(location.getLocationId(), "error", "displayName", new Localized(Lang.ENGLISH, "Display name must be 60 characters or less")));
		}
		
		// Address
		if (location.getAddress() == null) {
			messages.add(new Message(location.getLocationId(), "error", "address", new Localized(Lang.ENGLISH, "Mailing address is mandatory for a location")));
		} else {
			validateMailingAddress(location.getAddress(), messages, location.getLocationId(), "address");
		}
		
		if (!messages.isEmpty())
			throw new BadRequestException("Location has validation errors", messages);
		return location;
	}
	
	private void validateMailingAddress(MailingAddress address, List<Message> messages, Identifier identifier, String prefix) {
		// Street
		if (StringUtils.isBlank(address.getStreet())) {
			messages.add(new Message(identifier, "error", prefix + ".street", new Localized(Lang.ENGLISH, "Street address is mandatory")));
		} else if (address.getStreet().length() > 60) {
			messages.add(new Message(identifier, "error", prefix + ".street", new Localized(Lang.ENGLISH, "Street must be 60 characters or less")));
		}
		
		// City
		if (StringUtils.isBlank(address.getCity())) {
			messages.add(new Message(identifier, "error", prefix + ".city", new Localized(Lang.ENGLISH, "City is mandatory")));
		} else if (address.getCity().length() > 60) {
			messages.add(new Message(identifier, "error", prefix + ".city", new Localized(Lang.ENGLISH, "City must be 60 characters or less")));
		}
		
		// Province
		if (StringUtils.isBlank(address.getProvince())) {
			messages.add(new Message(identifier, "error", prefix + ".province", new Localized(Lang.ENGLISH, "Province is mandatory")));
		} else if (address.getCountry() == null) {
			messages.add(new Message(identifier, "error", prefix + ".province", new Localized(Lang.ENGLISH, "Province is forbidden unless there is a country")));
		}
		
		// Country
		if (address.getCountry() == null) {
			messages.add(new Message(identifier, "error", prefix + ".country", new Localized(Lang.ENGLISH, "Country is mandatory")));
		} else {
			try {
				lookups.findCountryByCode(address.getCountry());
			} catch (ItemNotFoundException e) {
				messages.add(new Message(identifier, "error", prefix + ".country", new Localized(Lang.ENGLISH, "Country code is not in the lookup")));
			}
		}
		
		// Postal Code
		if (StringUtils.isNotBlank(address.getPostalCode())) {
			if (address.getCountry() != null && lookups.findCountryByCode(address.getCountry()).getCode().equals("CA")) {
				if (!address.getPostalCode().matches("[A-Z][0-9][A-Z] ?[0-9][A-Z][0-9]")) {
					messages.add(new Message(identifier, "error", prefix + ".provinceCode", new Localized(Lang.ENGLISH, "Canadian province format is invalid")));
				}
			}
		}
	}
	
	private void validatePersonName(PersonName name, List<Message> messages, Identifier identifier, String prefix) {

		// Salutation
		if (name.getSalutation() == null) {
			messages.add(new Message(identifier, "error", prefix + ".salutation", new Localized(Lang.ENGLISH, "Salutation is mandatory")));
		} else {
			try {
				lookups.findSalutationByCode(name.getSalutation());
			} catch (ItemNotFoundException e) {
				messages.add(new Message(identifier, "error", prefix + ".salutation", new Localized(Lang.ENGLISH, "Salutation code is not in the lookup")));
			}
		}
		
		// First Name
		if (StringUtils.isBlank(name.getFirstName())) {
			messages.add(new Message(identifier, "error", prefix + ".firstName", new Localized(Lang.ENGLISH, "First name is required")));
		} else if (name.getFirstName().length() > 60) {
			messages.add(new Message(identifier, "error", prefix + ".firstName", new Localized(Lang.ENGLISH, "First name must be 60 characters or less")));
		}
		
		// Middle Name
		if (StringUtils.isBlank(name.getFirstName())) {
			messages.add(new Message(identifier, "error", prefix + ".middleName", new Localized(Lang.ENGLISH, "Middle name is required")));
		} else if (name.getFirstName().length() > 30) {
			messages.add(new Message(identifier, "error", prefix + ".middleName", new Localized(Lang.ENGLISH, "Middle name must be 60 characters or less")));
		}
		
		// Last Name
		if (StringUtils.isBlank(name.getFirstName())) {
			messages.add(new Message(identifier, "error", prefix + ".lastName", new Localized(Lang.ENGLISH, "Last name is required")));
		} else if (name.getFirstName().length() > 60) {
			messages.add(new Message(identifier, "error", prefix + ".lastName", new Localized(Lang.ENGLISH, "Last name must be 60 characters or less")));
		}
	}

	public PersonDetails validate(PersonDetails person) throws BadRequestException {
		List<Message> messages = new ArrayList<Message>();
		
		// Organization
		if (person.getOrganizationId() == null) {
			messages.add(new Message(person.getPersonId(), "error", "organizationId", new Localized(Lang.ENGLISH, "Organization cannot be null")));
		} else {
			try {
				organizations.findOrganizationDetails(person.getOrganizationId());
			} catch (ItemNotFoundException e) {
				messages.add(new Message(person.getPersonId(), "error", "organizationId", new Localized(Lang.ENGLISH, "Organization does not exist")));
			}
		}
		
		// Status
		if (person.getStatus() == null)
			messages.add(new Message(person.getPersonId(), "error", "status", new Localized(Lang.ENGLISH, "Status is mandatory for a person")));
		
		// Display Name
		if (StringUtils.isBlank(person.getDisplayName())) {
			messages.add(new Message(person.getPersonId(), "error", "displayName", new Localized(Lang.ENGLISH, "Display name is mandatory for a person")));
		} else if (person.getDisplayName().length() > 60) {
			messages.add(new Message(person.getPersonId(), "error", "displayName", new Localized(Lang.ENGLISH, "Display name must be 60 characters or less")));
		}
		
		// Legal Name
		if (person.getLegalName() == null) {
			messages.add(new Message(person.getPersonId(), "error", "legalName", new Localized(Lang.ENGLISH, "Legal name is mandatory for a person")));
		} else {
			validatePersonName(person.getLegalName(), messages, person.getPersonId(), "legalName");
		}
		
		// Address
		if (person.getAddress() != null) {
			validateMailingAddress(person.getAddress(), messages, person.getPersonId(), "address");
		}

		if (!messages.isEmpty())
			throw new BadRequestException("Person has validation errors", messages);
		return person;
	}

	public List<String> validate(List<String> roles, Identifier personId) throws BadRequestException {
		List<Message> messages = new ArrayList<Message>();
		
		for (String role : roles) {
			try {
				//lookups.findRoleByCode(role);
			} catch (ItemNotFoundException e) {
				messages.add(new Message(personId, "error", "roles('" + role + "')", new Localized(Lang.ENGLISH, "Role does not exist")));
			}
		}
		
		if (!messages.isEmpty())
			throw new BadRequestException("Roles has validation errors", messages);
		return roles;
	}

}