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

import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.AuthenticationGroupIdentifier;
import ca.magex.crm.api.system.id.BusinessGroupIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.json.model.JsonObject;

@Controller
public class RestfulOrganizationController extends AbstractRestfulController {
	
	@GetMapping("/rest/organizations")
	public void findOrganizationSumaries(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, OrganizationSummary.class, (messages, transformer, locale) -> { 
			return createPage(
				crm.findOrganizationSummaries(
					extractOrganizationFilter(req, locale), 
					extractPaging(OrganizationsFilter.getDefaultPaging(), req)
				), transformer, locale
			);
		});
	}
	
	@GetMapping("/rest/organizations/details")
	public void findOrganizationDetails(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, OrganizationDetails.class, (messages, transformer, locale) -> { 
			return createPage(
				crm.findOrganizationDetails(
					extractOrganizationFilter(req, locale), 
					extractPaging(OrganizationsFilter.getDefaultPaging(), req)
				), transformer, locale
			);
		});
	}
	
	@GetMapping("/rest/organizations/count")
	public void countOrganizations(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, OrganizationSummary.class, (messages, transformer, locale) -> {
			return new JsonObject().with("total", crm.countOrganizations(extractOrganizationFilter(req, locale)));
		});
	}
	
	public OrganizationsFilter extractOrganizationFilter(HttpServletRequest req, Locale locale) throws BadRequestException {
		List<Message> messages = new ArrayList<>();
		JsonObject query = extractQuery(req);
		String displayName = getString(query, "displayName", false, null, null, messages);
		Status status = getObject(Status.class, query, "status", false, null, null, messages, locale);
		AuthenticationGroupIdentifier authenticationGroupId = getOptionIdentifier(query, "authenticationGroupId", false, null, null, messages, AuthenticationGroupIdentifier.class, locale);
		BusinessGroupIdentifier businessGroupId = getOptionIdentifier(query, "businessGroupId", false, null, null, messages, BusinessGroupIdentifier.class, locale);
		validate(messages);
		return new OrganizationsFilter(displayName, status, authenticationGroupId, businessGroupId);
	}

	@PostMapping("/rest/organizations")
	public void createOrganization(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, OrganizationDetails.class, (messages, transformer, locale) -> { 
			JsonObject body = extractBody(req);
			String displayName = getString(body, "displayName", true, "", null, messages);
			List<AuthenticationGroupIdentifier> authenticationGroupIds = getOptionIdentifiers(body, "authenticationGroupIds", true, List.of(), null, messages, AuthenticationGroupIdentifier.class, locale);
			List<BusinessGroupIdentifier> businessGroupIds = getOptionIdentifiers(body, "businessGroupIds", true, List.of(), null, messages, BusinessGroupIdentifier.class, locale);
			validate(messages);
			return transformer.format(crm.createOrganization(displayName, authenticationGroupIds, businessGroupIds), locale);
		});
	}

	@GetMapping("/rest/organizations/{organizationId}")
	public void getOrganizationSummary(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("organizationId") OrganizationIdentifier organizationId) throws IOException {
		handle(req, res, OrganizationSummary.class, (messages, transformer, locale) -> {
			return transformer.format(crm.findOrganizationSummary(organizationId), locale);
		});
	}

	@GetMapping("/rest/organizations/{organizationId}/details")
	public void getOrganizationDetails(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("organizationId") OrganizationIdentifier organizationId) throws IOException {
		handle(req, res, OrganizationDetails.class, (messages, transformer, locale) -> {
			return transformer.format(crm.findOrganizationDetails(organizationId), locale);
		});
	}

	@PatchMapping("/rest/organizations/{organizationId}")
	public void updateOrganization(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("organizationId") OrganizationIdentifier organizationId) throws IOException {
		handle(req, res, OrganizationDetails.class, (messages, transformer, locale) -> {
			JsonObject body = extractBody(req);
			if (body.contains("displayName")) {
				crm.updateOrganizationDisplayName(organizationId, getString(body, "displayName", true, null, organizationId, messages));
			}
			if (body.contains("mainLocationId")) {
				crm.updateOrganizationMainLocation(organizationId, getIdentifier(body, "mainLocationId", true, null, organizationId, messages));
			}
			if (body.contains("mainContactId")) {
				crm.updateOrganizationMainContact(organizationId, getIdentifier(body, "mainContactId", true, null, organizationId, messages));
			}
			if (body.contains("authenticationGroupIds")) {
				crm.updateOrganizationAuthenticationGroups(organizationId, getOptionIdentifiers(body, "authenticationGroupIds", true, List.of(), organizationId, messages, AuthenticationGroupIdentifier.class, locale));
			}
			if (body.contains("businessGroupIds")) {
				crm.updateOrganizationBusinessGroups(organizationId, getOptionIdentifiers(body, "businessGroupIds", true, List.of(), organizationId, messages, BusinessGroupIdentifier.class, locale));
			}
			validate(messages);
			return transformer.format(crm.findOrganizationDetails(organizationId), locale);
		});
	}

	@GetMapping("/rest/organizations/{organizationId}/mainLocation")
	public void getOrganizationMainLocation(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("organizationId") OrganizationIdentifier organizationId) throws IOException {
		handle(req, res, LocationDetails.class, (messages, transformer, locale) -> {
			return transformer.format(crm.findLocationDetails(crm.findOrganizationDetails(organizationId).getMainLocationId()), locale);
		});
	}

	@GetMapping("/rest/organizations/{organizationId}/mainContact")
	public void getOrganizationMainContact(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("organizationId") OrganizationIdentifier organizationId) throws IOException {
		handle(req, res, PersonDetails.class, (messages, transformer, locale) -> {
			return transformer.format(crm.findPersonDetails(crm.findOrganizationDetails(organizationId).getMainContactId()), locale);
		});
	}

	@GetMapping("/rest/organizations/{organizationId}/authenticationGroupIds")
	public void getAuthenticationGroupIds(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("organizationId") OrganizationIdentifier organizationId) throws IOException {
		handle(req, res, AuthenticationGroupIdentifier.class, (messages, transformer, locale) -> {
			return createList(crm.findOrganizationDetails(organizationId).getAuthenticationGroupIds(), transformer, locale);
		});
	}

	@GetMapping("/rest/organizations/{organizationId}/businessGroupIds")
	public void getBusinessGroupIds(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("organizationId") OrganizationIdentifier organizationId) throws IOException {
		handle(req, res, BusinessGroupIdentifier.class, (messages, transformer, locale) -> {
			return createList(crm.findOrganizationDetails(organizationId).getBusinessGroupIds(), transformer, locale);
		});
	}

	@PutMapping("/rest/organizations/{organizationId}/enable")
	public void enableOrganization(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("organizationId") OrganizationIdentifier organizationId) throws IOException {
		handle(req, res, OrganizationSummary.class, (messages, transformer, locale) -> {
			confirm(extractBody(req), organizationId, messages);
			return transformer.format(crm.enableOrganization(organizationId), locale);
		});
	}

	@PutMapping("/rest/organizations/{organizationId}/disable")
	public void disableOrganization(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("organizationId") OrganizationIdentifier organizationId) throws IOException {
		handle(req, res, OrganizationSummary.class, (messages, transformer, locale) -> {
			confirm(extractBody(req), organizationId, messages);
			return transformer.format(crm.disableOrganization(organizationId), locale);
		});
	}

}