package ca.magex.crm.api.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.system.Status;

public class StructureValidationService implements CrmValidation {

	private CrmLookupService lookups;
	
	private CrmPermissionService permissions;
	
	private CrmOrganizationService organizations;
	
	private CrmLocationService locations;

	public StructureValidationService(CrmLookupService lookups, CrmPermissionService permissions, CrmOrganizationService organizations,
			CrmLocationService locations) {
		super();
		this.lookups = lookups;
		this.permissions = permissions;
		this.organizations = organizations;
		this.locations = locations;
	}

	public OrganizationDetails validate(OrganizationDetails organization) throws BadRequestException {
		List<Message> messages = new ArrayList<Message>();
		
		// Display Name
		if (StringUtils.isBlank(organization.getDisplayName())) {
			messages.add(new Message(organization.getOrganizationId(), "error", "displayName", new Localized("Display name is mandatory for an organization")));
		} else if (organization.getDisplayName().length() > 60) {
			messages.add(new Message(organization.getOrganizationId(), "error", "displayName", new Localized("Display name must be 60 characters or less")));
		}
		
		// Status
		if (organization.getStatus() == null)
			messages.add(new Message(organization.getOrganizationId(), "error", "status", new Localized("Status is mandatory for an organization")));
		
		// Main location reference
		if (organization.getMainLocationId() != null && !locations.findLocationDetails(organization.getMainLocationId()).getOrganizationId().equals(organization.getOrganizationId())) {
			messages.add(new Message(organization.getOrganizationId(), "error", "mainLocation", new Localized("Main location organization has invalid referential integrity")));
		}

		// Group
		if (organization.getGroups().isEmpty()) {
			messages.add(new Message(organization.getOrganizationId(), "error", "groups", new Localized("Organizations must have a permission group assigned to them")));
		} else {
			for (int i = 0; i < organization.getGroups().size(); i++) {
				String group = organization.getGroups().get(i);
				try {
					if (!permissions.findGroupByCode(group).getStatus().equals(Status.ACTIVE))
						messages.add(new Message(organization.getOrganizationId(), "error", "groups[" + i + "]", new Localized("Group is not active: " + group)));
				} catch (ItemNotFoundException e) {
					messages.add(new Message(organization.getOrganizationId(), "error", "groups[" + i + "]", new Localized("Group does not exist: " + group)));
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
			messages.add(new Message(location.getLocationId(), "error", "organizationId", new Localized("Organization cannot be null")));
		} else {
			try {
				organizations.findOrganizationDetails(location.getOrganizationId());
			} catch (ItemNotFoundException e) {
				messages.add(new Message(location.getLocationId(), "error", "organizationId", new Localized("Organization does not exist")));
			}
		}
		
		// Status
		if (location.getStatus() == null)
			messages.add(new Message(location.getLocationId(), "error", "status", new Localized("Status is mandatory for a location")));
		
		// Reference
		if (StringUtils.isBlank(location.getReference())) {
			messages.add(new Message(location.getLocationId(), "error", "reference", new Localized("Reference is mandatory for aa location")));
		} else if (location.getReference().matches("[A-Z0-9-]{0,60}")) {
			messages.add(new Message(location.getLocationId(), "error", "reference", new Localized("Reference is not in the correct format")));
		}
		
		// Display Name
		if (StringUtils.isBlank(location.getDisplayName())) {
			messages.add(new Message(location.getLocationId(), "error", "displayName", new Localized("Display name is mandatory for a location")));
		} else if (location.getDisplayName().length() > 60) {
			messages.add(new Message(location.getLocationId(), "error", "displayName", new Localized("Display name must be 60 characters or less")));
		}
		
		// Address
		if (location.getAddress() == null) {
			messages.add(new Message(location.getLocationId(), "error", "address", new Localized("Mailing address is mandatory for a location")));
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
			messages.add(new Message(identifier, "error", prefix + ".street", new Localized("Street address is mandatory")));
		} else if (address.getStreet().length() > 60) {
			messages.add(new Message(identifier, "error", prefix + ".street", new Localized("Street must be 60 characters or less")));
		}
		
		// City
		if (StringUtils.isBlank(address.getCity())) {
			messages.add(new Message(identifier, "error", prefix + ".city", new Localized("City is mandatory")));
		} else if (address.getCity().length() > 60) {
			messages.add(new Message(identifier, "error", prefix + ".city", new Localized("City must be 60 characters or less")));
		}
		
		// Province
		if (StringUtils.isBlank(address.getProvince())) {
			messages.add(new Message(identifier, "error", prefix + ".province", new Localized("Province is mandatory")));
		} else if (address.getCountry() == null) {
			messages.add(new Message(identifier, "error", prefix + ".province", new Localized("Province is forbidden unless there is a country")));
		}
		
		// Country
		if (address.getCountry() == null) {
			messages.add(new Message(identifier, "error", prefix + ".country", new Localized("Country is mandatory")));
		} else {
			try {
				lookups.findCountryByCode(address.getCountry());
			} catch (ItemNotFoundException e) {
				messages.add(new Message(identifier, "error", prefix + ".country", new Localized("Country code is not in the lookup")));
			}
		}
		
		// Postal Code
		if (StringUtils.isNotBlank(address.getPostalCode())) {
			if (address.getCountry() != null && lookups.findCountryByCode(address.getCountry()).getCode().equals("CA")) {
				if (!address.getPostalCode().matches("[A-Z][0-9][A-Z][0-9][A-Z][0-9]")) {
					messages.add(new Message(identifier, "error", prefix + ".provinceCode", new Localized("Canadian province format is invalid")));
				}
			}
		}
	}
	
	private void validatePersonName(PersonName name, List<Message> messages, Identifier identifier, String prefix) {

		// Salutation
		if (name.getSalutation() == null) {
			messages.add(new Message(identifier, "error", prefix + ".salutation", new Localized("Salutation is mandatory")));
		} else {
			try {
				lookups.findSalutationByCode(name.getSalutation());
			} catch (ItemNotFoundException e) {
				messages.add(new Message(identifier, "error", prefix + ".salutation", new Localized("Salutation code is not in the lookup")));
			}
		}
		
		// First Name
		if (StringUtils.isBlank(name.getFirstName())) {
			messages.add(new Message(identifier, "error", prefix + ".firstName", new Localized("First name is required")));
		} else if (name.getFirstName().length() > 60) {
			messages.add(new Message(identifier, "error", prefix + ".firstName", new Localized("First name must be 60 characters or less")));
		}
		
		// Middle Name
		if (StringUtils.isBlank(name.getFirstName())) {
			messages.add(new Message(identifier, "error", prefix + ".middleName", new Localized("Middle name is required")));
		} else if (name.getFirstName().length() > 30) {
			messages.add(new Message(identifier, "error", prefix + ".middleName", new Localized("Middle name must be 60 characters or less")));
		}
		
		// Last Name
		if (StringUtils.isBlank(name.getFirstName())) {
			messages.add(new Message(identifier, "error", prefix + ".lastName", new Localized("Last name is required")));
		} else if (name.getFirstName().length() > 60) {
			messages.add(new Message(identifier, "error", prefix + ".lastName", new Localized("Last name must be 60 characters or less")));
		}
	}

	public PersonDetails validate(PersonDetails person) throws BadRequestException {
		List<Message> messages = new ArrayList<Message>();
		
		// Organization
		if (person.getOrganizationId() == null) {
			messages.add(new Message(person.getPersonId(), "error", "organizationId", new Localized("Organization cannot be null")));
		} else {
			try {
				organizations.findOrganizationDetails(person.getOrganizationId());
			} catch (ItemNotFoundException e) {
				messages.add(new Message(person.getPersonId(), "error", "organizationId", new Localized("Organization does not exist")));
			}
		}
		
		// Status
		if (person.getStatus() == null)
			messages.add(new Message(person.getPersonId(), "error", "status", new Localized("Status is mandatory for a person")));
		
		// Display Name
		if (StringUtils.isBlank(person.getDisplayName())) {
			messages.add(new Message(person.getPersonId(), "error", "displayName", new Localized("Display name is mandatory for a person")));
		} else if (person.getDisplayName().length() > 60) {
			messages.add(new Message(person.getPersonId(), "error", "displayName", new Localized("Display name must be 60 characters or less")));
		}
		
		// Legal Name
		if (person.getLegalName() == null) {
			messages.add(new Message(person.getPersonId(), "error", "legalName", new Localized("Legal name is mandatory for a person")));
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
				messages.add(new Message(personId, "error", "roles('" + role + "')", new Localized("Role does not exist")));
			}
		}
		
		if (!messages.isEmpty())
			throw new BadRequestException("Roles has validation errors", messages);
		return roles;
	}

}