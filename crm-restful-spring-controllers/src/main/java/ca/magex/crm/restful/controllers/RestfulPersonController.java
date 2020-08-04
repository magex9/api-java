package ca.magex.crm.restful.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.BusinessRoleIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.json.model.JsonObject;

@Controller
public class RestfulPersonController extends AbstractRestfulController {

	@GetMapping("/rest/persons")
	public void findPersons(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, PersonSummary.class, (messages, transformer, locale) -> { 
			return createPage(
				crm.findPersonSummaries(
					extractPersonFilter(req, locale), 
					extractPaging(PersonsFilter.getDefaultPaging(), req)
				), transformer, locale
			);
		});
	}

	@GetMapping("/rest/persons/details")
	public void findPersonDetails(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, PersonSummary.class, (messages, transformer, locale) -> { 
			return createPage(
				crm.findPersonSummaries(
					extractPersonFilter(req, locale), 
					extractPaging(PersonsFilter.getDefaultPaging(), req)
				), transformer, locale
			);
		});
	}
	
	@GetMapping("/rest/persons/count")
	public void countPersons(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, PersonSummary.class, (messages, transformer, locale) -> {
			return new JsonObject().with("total", crm.countPersons(extractPersonFilter(req, locale)));
		});
	}
	
	public PersonsFilter extractPersonFilter(HttpServletRequest req, Locale locale) throws BadRequestException {
		List<Message> messages = new ArrayList<>();
		JsonObject query = extractQuery(req);
		OrganizationIdentifier organizationId = getIdentifier(query, "organizationId", false, null, null, messages);
		String displayName = getString(query, "displayName", false, null, null, messages);
		Status status = getObject(Status.class, query, "status", false, null, null, messages, locale);
		return new PersonsFilter(organizationId, displayName, status);
	}
	
	@PostMapping("/rest/persons")
	public void createPerson(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, PersonDetails.class, (messages, transformer, locale) -> { 
			JsonObject body = extractBody(req);
			OrganizationIdentifier organizationId = getIdentifier(body, "organizationId", true, null, null, messages);
			PersonName name = getObject(PersonName.class, body, "legalName", true, null, null, messages, locale);
			MailingAddress address = getObject(MailingAddress.class, body, "address", true, null, null, messages, locale);
			Communication communication = getObject(Communication.class, body, "communication", true, null, null, messages, locale);
			List<BusinessRoleIdentifier> roles = getOptionIdentifiers(body, "businessRoleIds", true, List.of(), null, messages, BusinessRoleIdentifier.class, locale);
			validate(messages);
			return transformer.format(crm.createPerson(organizationId, name, address, communication, roles), locale);
		});
	}

	@GetMapping("/rest/persons/{personId}")
	public void getPersonSummary(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("personId") PersonIdentifier personId) throws IOException {
		handle(req, res, PersonSummary.class, (messages, transformer, locale) -> {
			return transformer.format(crm.findPersonDetails(personId), locale);
		});
	}

	@GetMapping("/rest/persons/{personId}/details")
	public void getPersonDetails(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("personId") PersonIdentifier personId) throws IOException {
		handle(req, res, PersonDetails.class, (messages, transformer, locale) -> {
			return transformer.format(crm.findPersonDetails(personId), locale);
		});
	}

	@PatchMapping("/rest/persons/{personId}")
	public void updatePerson(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("personId") PersonIdentifier personId) throws IOException {
		handle(req, res, PersonDetails.class, (messages, transformer, locale) -> {
			JsonObject body = extractBody(req);
			if (body.contains("name")) {
				crm.updatePersonName(personId, getObject(PersonName.class, body, "name", true, null, personId, messages, locale));
			}
			if (body.contains("address")) {
				crm.updatePersonAddress(personId, getObject(MailingAddress.class, body, "address", true, null, personId, messages, locale));
			}
			if (body.contains("communication")) {
				crm.updatePersonCommunication(personId, getObject(Communication.class, body, "communication", true, null, personId, messages, locale));
			}
			if (body.contains("businessRoleIds")) {
				crm.updatePersonRoles(personId, getOptionIdentifiers(body, "businessRoleIds", true, List.of(), personId, messages, BusinessRoleIdentifier.class, locale));
			}
			validate(messages);
			return transformer.format(crm.findPersonDetails(personId), locale);
		});
	}

	@GetMapping("/rest/persons/{personId}/name")
	public void getPersonLegalname(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("personId") PersonIdentifier personId) throws IOException {
		handle(req, res, PersonName.class, (messages, transformer, locale) -> {
			return transformer.format(crm.findPersonDetails(personId).getLegalName(), locale);
		});
	}

	@GetMapping("/rest/persons/{personId}/address")
	public void getPersonAddress(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("personId") PersonIdentifier personId) throws IOException {
		handle(req, res, MailingAddress.class, (messages, transformer, locale) -> {
			return transformer.format(crm.findPersonDetails(personId).getAddress(), locale);
		});
	}

	@GetMapping("/rest/persons/{personId}/communication")
	public void getPersonCommunication(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("personId") PersonIdentifier personId) throws IOException {
		handle(req, res, Communication.class, (messages, transformer, locale) -> {
			return transformer.format(crm.findPersonDetails(personId).getCommunication(), locale);
		});
	}

	@GetMapping("/rest/persons/{personId}/businessRoles")
	public void getPersonPosition(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("personId") PersonIdentifier personId) throws IOException {
		handle(req, res, BusinessRoleIdentifier.class, (messages, transformer, locale) -> {
			return createList(crm.findPersonDetails(personId).getBusinessRoleIds(), transformer, locale);
		});
	}

	@PutMapping("/rest/persons/{personId}/enable")
	public void enableOrganization(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("personId") PersonIdentifier personId) throws IOException {
		handle(req, res, PersonSummary.class, (messages, transformer, locale) -> {
			confirm(extractBody(req), personId, messages);
			return transformer.format(crm.enablePerson(personId), locale);
		});
	}

	@PutMapping("/rest/persons/{personId}/disable")
	public void disableOrganization(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("personId") PersonIdentifier personId) throws IOException {
		handle(req, res, PersonSummary.class, (messages, transformer, locale) -> {
			confirm(extractBody(req), personId, messages);
			return transformer.format(crm.disablePerson(personId), locale);
		});
	}

}