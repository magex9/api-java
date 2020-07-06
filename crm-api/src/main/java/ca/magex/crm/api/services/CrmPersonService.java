package ca.magex.crm.api.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.BusinessRoleIdentifier;
import ca.magex.crm.api.system.id.MessageTypeIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;

public interface CrmPersonService {
	
	default PersonDetails prototypePerson(OrganizationIdentifier organizationId, PersonName name, MailingAddress address, Communication communication, List<BusinessRoleIdentifier> getBusinessRoleIds) {
		return new PersonDetails(null, organizationId, Status.PENDING, name.getDisplayName(), name, address, communication, getBusinessRoleIds);
	};

	default PersonDetails createPerson(PersonDetails prototype) {
		return createPerson(prototype.getOrganizationId(), prototype.getLegalName(), prototype.getAddress(), prototype.getCommunication(), prototype.getBusinessRoleIds());
	}

	PersonDetails createPerson(OrganizationIdentifier organizationId, PersonName name, MailingAddress address, Communication communication, List<BusinessRoleIdentifier> getBusinessRoleIds);

	PersonSummary enablePerson(PersonIdentifier personId);

	PersonSummary disablePerson(PersonIdentifier personId);

	PersonDetails updatePersonName(PersonIdentifier personId, PersonName name);

	PersonDetails updatePersonAddress(PersonIdentifier personId, MailingAddress address);

	PersonDetails updatePersonCommunication(PersonIdentifier personId, Communication communication);

	PersonDetails updatePersonRoles(PersonIdentifier personId, List<BusinessRoleIdentifier> roleIds);
	
	PersonSummary findPersonSummary(PersonIdentifier personId);

	PersonDetails findPersonDetails(PersonIdentifier personId);

	long countPersons(PersonsFilter filter);

	FilteredPage<PersonSummary> findPersonSummaries(PersonsFilter filter, Paging paging);

	FilteredPage<PersonDetails> findPersonDetails(PersonsFilter filter, Paging paging);
	
	default FilteredPage<PersonDetails> findPersonDetails(PersonsFilter filter) {
		return findPersonDetails(filter, PersonsFilter.getDefaultPaging());
	}
	
	default FilteredPage<PersonSummary> findPersonSummaries(PersonsFilter filter) {
		return findPersonSummaries(filter, PersonsFilter.getDefaultPaging());
	}
	
	default FilteredPage<PersonSummary> findActivePersonSummariesForOrg(OrganizationIdentifier organizationId) {
		return findPersonSummaries(new PersonsFilter(organizationId, null, Status.ACTIVE), PersonsFilter.getDefaultPaging());
	}
	
	default PersonsFilter defaultPersonsFilter() {
		return new PersonsFilter();
	};
	
	static List<Message> validatePersonDetails(Crm crm, PersonDetails person) throws BadRequestException {
		List<Message> messages = new ArrayList<Message>();
		
		MessageTypeIdentifier error = crm.findOptionByCode(Type.MESSAGE_TYPE, "ERROR").getOptionId();

		// Organization
		if (person.getOrganizationId() == null) {
			messages.add(new Message(person.getPersonId(), error, "organizationId", null, crm.findMessageId("validation.field.required")));
		} else {
			try {
				crm.findOrganizationDetails(person.getOrganizationId());
			} catch (ItemNotFoundException e) {
				messages.add(new Message(person.getPersonId(), error, "organizationId", person.getOrganizationId().getId(), crm.findMessageId("validation.field.invalid")));
			}
		}

		// Status
		if (person.getStatus() == null) {
			messages.add(new Message(person.getPersonId(), error, "status", null, crm.findMessageId("validation.field.required")));
		} else if (person.getStatus() == Status.PENDING && person.getPersonId() != null) {
			messages.add(new Message(person.getPersonId(), error, "status", person.getStatus().name(), crm.findMessageId("validation.status.pending")));
		}

		// Display Name
		if (StringUtils.isBlank(person.getDisplayName())) {
			messages.add(new Message(person.getPersonId(), error, "displayName", person.getDisplayName(), crm.findMessageId("validation.field.required")));
		} else if (person.getDisplayName().length() > 60) {
			messages.add(new Message(person.getPersonId(), error, "displayName", person.getDisplayName(), crm.findMessageId("validation.field.maxlength")));
		}

		// Legal Name
		if (person.getLegalName() == null) {
			messages.add(new Message(person.getPersonId(), error, "legalName", null, crm.findMessageId("validation.field.required")));
		} else {
			messages.addAll(validatePersonName(crm, person.getLegalName(), person.getPersonId(), "legalName"));
		}

		// Address
		if (person.getAddress() != null) {
			messages.addAll(CrmLocationService.validateMailingAddress(crm, person.getAddress(), person.getPersonId(), "address"));
		}

		return messages;
	}
	
	static List<Message> validatePersonName(Crm crm, PersonName name, Identifier identifier, String path) {
		List<Message> messages = new ArrayList<Message>();
		
		MessageTypeIdentifier error = crm.findOptionByCode(Type.MESSAGE_TYPE, "ERROR").getOptionId();
		
		// Salutation
		if (name.getSalutation() != null && !name.getSalutation().isEmpty()) {
			if (name.getSalutation().isIdentifer()) {
				try {
					crm.findOption(name.getSalutation().getIdentifier());
				} catch (ItemNotFoundException e) {
					messages.add(new Message(identifier, error, path + ".salutation", name.getSalutation().getValue(), crm.findMessageId("validation.field.invalid")));
				}
			} else {
				messages.add(new Message(identifier, error, path + ".salutation", name.getSalutation().getValue(), crm.findMessageId("validation.field.forbidden")));
			}
		}

		// First Name
		if (StringUtils.isBlank(name.getFirstName())) {
			messages.add(new Message(identifier, error, path + ".firstName", name.getFirstName(), crm.findMessageId("validation.field.required")));
		} else if (name.getFirstName().length() > 60) {
			messages.add(new Message(identifier, error, path + ".firstName", name.getFirstName(), crm.findMessageId("validation.field.maxlength")));
		}

		// Middle Name
		if (name.getMiddleName() != null && name.getMiddleName().length() > 30) {
			messages.add(new Message(identifier, error, path + ".middleName", name.getMiddleName(), crm.findMessageId("validation.field.maxlength")));
		}

		// Last Name
		if (StringUtils.isBlank(name.getLastName())) {
			messages.add(new Message(identifier, error, path + ".lastName", name.getLastName(), crm.findMessageId("validation.field.required")));
		} else if (name.getLastName().length() > 60) {
			messages.add(new Message(identifier, error, path + ".lastName", name.getLastName(), crm.findMessageId("validation.field.maxlength")));
		}
		
		return messages;
	}
	
}