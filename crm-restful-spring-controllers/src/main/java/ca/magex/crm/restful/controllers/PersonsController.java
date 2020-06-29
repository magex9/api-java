package ca.magex.crm.restful.controllers;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.json.model.JsonObject;

@Controller
public class PersonsController extends AbstractCrmController {

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
	
	public PersonsFilter extractPersonFilter(HttpServletRequest req, Locale locale) throws BadRequestException {
		Identifier organizationId = req.getParameter("organization") == null ? null : new Identifier(req.getParameter("organization"));
		String displayName = req.getParameter("displayName");
		Status status = req.getParameter("status") == null ? null : Status.valueOf(crm.findOptionByLocalizedName(Crm.STATUS, locale, req.getParameter("status")).getCode().toUpperCase());
		return new PersonsFilter(organizationId, displayName, status);
	}
	
	@PostMapping("/rest/persons")
	public void createPerson(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, PersonDetails.class, (messages, transformer, locale) -> { 
			JsonObject body = extractBody(req);
			Identifier organizationId = getIdentifier(body, "organizationId", null, null, messages);
			PersonName name = getObject(PersonName.class, body, "name", null, null, messages, locale);
			MailingAddress address = getObject(MailingAddress.class, body, "address", null, null, messages, locale);
			Communication communication = getObject(Communication.class, body, "communication", null, null, messages, locale);
			BusinessPosition position = getObject(BusinessPosition.class, body, "position", null, null, messages, locale);
			validate(messages);
			return transformer.format(crm.createPerson(organizationId, name, address, communication, position), locale);
		});
	}

	@GetMapping("/rest/persons/{personId}")
	public void getPerson(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("personId") Identifier personId) throws IOException {
		handle(req, res, PersonDetails.class, (messages, transformer, locale) -> {
			return transformer.format(crm.findPersonDetails(personId), locale);
		});
	}

	@PatchMapping("/rest/persons/{personId}")
	public void updateLocation(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("personId") Identifier personId) throws IOException {
		handle(req, res, PersonDetails.class, (messages, transformer, locale) -> {
			JsonObject body = extractBody(req);
			if (body.contains("name")) {
				crm.updatePersonName(personId, getObject(PersonName.class, body, "name", null, personId, messages, locale));
			}
			if (body.contains("address")) {
				crm.updatePersonAddress(personId, getObject(MailingAddress.class, body, "address", null, personId, messages, locale));
			}
			if (body.contains("communication")) {
				crm.updatePersonCommunication(personId, getObject(Communication.class, body, "communication", null, personId, messages, locale));
			}
			if (body.contains("position")) {
				crm.updatePersonBusinessPosition(personId, getObject(BusinessPosition.class, body, "position", null, personId, messages, locale));
			}
			validate(messages);
			return transformer.format(crm.findPersonDetails(personId), locale);
		});
	}

	@GetMapping("/rest/persons/{personId}/summary")
	public void getPersonSummary(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("personId") Identifier personId) throws IOException {
		handle(req, res, PersonSummary.class, (messages, transformer, locale) -> {
			return transformer.format(crm.findPersonDetails(personId), locale);
		});
	}

	@GetMapping("/rest/persons/{personId}/name")
	public void getPersonLegalname(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("personId") Identifier personId) throws IOException {
		handle(req, res, PersonName.class, (messages, transformer, locale) -> {
			return transformer.format(crm.findPersonDetails(personId).getLegalName(), locale);
		});
	}

	@GetMapping("/rest/persons/{personId}/address")
	public void getPersonAddress(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("personId") Identifier personId) throws IOException {
		handle(req, res, MailingAddress.class, (messages, transformer, locale) -> {
			return transformer.format(crm.findPersonDetails(personId).getAddress(), locale);
		});
	}

	@GetMapping("/rest/persons/{personId}/communication")
	public void getPersonCommunication(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("personId") Identifier personId) throws IOException {
		handle(req, res, Communication.class, (messages, transformer, locale) -> {
			return transformer.format(crm.findPersonDetails(personId).getCommunication(), locale);
		});
	}

	@GetMapping("/rest/persons/{personId}/position")
	public void getPersonPosition(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("personId") Identifier personId) throws IOException {
		handle(req, res, BusinessPosition.class, (messages, transformer, locale) -> {
			return transformer.format(crm.findPersonDetails(personId).getPosition(), locale);
		});
	}

	@PutMapping("/rest/persons/{personId}/enable")
	public void enableOrganization(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("personId") Identifier personId) throws IOException {
		handle(req, res, PersonSummary.class, (messages, transformer, locale) -> {
			confirm(extractBody(req), personId, messages);
			return transformer.format(crm.enablePerson(personId), locale);
		});
	}

	@PutMapping("/rest/persons/{personId}/disable")
	public void disableOrganization(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("personId") Identifier personId) throws IOException {
		handle(req, res, PersonSummary.class, (messages, transformer, locale) -> {
			confirm(extractBody(req), personId, messages);
			return transformer.format(crm.disablePerson(personId), locale);
		});
	}

}