package ca.magex.crm.restful.controllers;

import java.io.IOException;
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

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.json.model.JsonObject;

@Controller
public class OrganizationsController extends AbstractCrmController {

	@GetMapping("/rest/organizations")
	public void findOrganizations(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, OrganizationSummary.class, (messages, transformer, locale) -> { 
			return createPage(
				crm.findOrganizationSummaries(
					extractOrganizationFilter(locale, req), 
					extractPaging(OrganizationsFilter.getDefaultPaging(), req)
				), transformer, locale
			);
		});
	}
	
	public OrganizationsFilter extractOrganizationFilter(Locale locale, HttpServletRequest req) throws BadRequestException {
		Status status = req.getParameter("status") == null ? null : Status.valueOf(crm.findOptionByLocalizedName(Crm.STATUS, locale, req.getParameter("status")).getCode().toUpperCase());
		String displayName = req.getParameter("displayName");
		String group = req.getParameter("group");
		return new OrganizationsFilter(displayName, status, group);
	}

	@PostMapping("/rest/organizations")
	public void createOrganization(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, OrganizationDetails.class, (messages, transformer, locale) -> { 
			JsonObject body = extractBody(req);
			String displayName = getString(body, "displayName", "", null, messages);
			List<String> groups = getStrings(body, "groups", List.of(), null, messages);
			validate(messages);
			return transformer.format(crm.createOrganization(displayName, groups), locale);
		});
	}

	@GetMapping("/rest/organizations/{organizationId}")
	public void getOrganization(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("organizationId") Identifier organizationId) throws IOException {
		handle(req, res, OrganizationDetails.class, (messages, transformer, locale) -> {
			return transformer.format(crm.findOrganizationDetails(organizationId), locale);
		});
	}

	@PatchMapping("/rest/organizations/{organizationId}")
	public void updateOrganization(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("organizationId") Identifier organizationId) throws IOException {
		handle(req, res, OrganizationDetails.class, (messages, transformer, locale) -> {
			JsonObject body = extractBody(req);
			if (body.contains("displayName")) {
				crm.updateOrganizationDisplayName(organizationId, getString(body, "displayName", null, null, messages));
			}
			if (body.contains("mainLocationId")) {
				crm.updateOrganizationMainLocation(organizationId, getIdentifier(body, "mainLocationId", null, null, messages));
			}
			if (body.contains("mainContactId")) {
				crm.updateOrganizationMainContact(organizationId, getIdentifier(body, "mainContactId", null, null, messages));
			}
			validate(messages);
			return transformer.format(crm.findOrganizationDetails(organizationId), locale);
		});
	}

	@GetMapping("/rest/organizations/{organizationId}/summary")
	public void getOrganizationSummary(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("organizationId") Identifier organizationId) throws IOException {
		handle(req, res, OrganizationSummary.class, (messages, transformer, locale) -> {
			return transformer.format(crm.findOrganizationDetails(organizationId), locale);
		});
	}

	@GetMapping("/rest/organizations/{organizationId}/mainLocation")
	public void getOrganizationMainLocation(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("organizationId") Identifier organizationId) throws IOException {
		handle(req, res, LocationDetails.class, (messages, transformer, locale) -> {
			return transformer.format(crm.findLocationDetails(crm.findOrganizationDetails(organizationId).getMainLocationId()), locale);
		});
	}

	@GetMapping("/rest/organizations/{organizationId}/mainContact")
	public void getOrganizationMainContact(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("organizationId") Identifier organizationId) throws IOException {
		handle(req, res, PersonDetails.class, (messages, transformer, locale) -> {
			return transformer.format(crm.findPersonDetails(crm.findOrganizationDetails(organizationId).getMainContactId()), locale);
		});
	}

	@PutMapping("/rest/organizations/{organizationId}/enable")
	public void enableOrganization(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("organizationId") Identifier organizationId) throws IOException {
		handle(req, res, OrganizationSummary.class, (messages, transformer, locale) -> {
			confirm(extractBody(req), organizationId, messages);
			return transformer.format(crm.enableOrganization(organizationId), locale);
		});
	}

	@PutMapping("/rest/organizations/{organizationId}/disable")
	public void disableOrganization(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("organizationId") Identifier organizationId) throws IOException {
		handle(req, res, OrganizationSummary.class, (messages, transformer, locale) -> {
			confirm(extractBody(req), organizationId, messages);
			return transformer.format(crm.disableOrganization(organizationId), locale);
		});
	}

}