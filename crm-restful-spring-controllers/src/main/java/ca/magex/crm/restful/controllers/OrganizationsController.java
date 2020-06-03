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

	@GetMapping("/api/organizations")
	public void findOrganizations(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, OrganizationSummary.class, (messages, transformer) -> { 
			return createPage(
				crm.findOrganizationSummaries(
					extractOrganizationFilter(extractLocale(req), req), 
					extractPaging(OrganizationsFilter.getDefaultPaging(), req)
				), transformer, extractLocale(req)
			);
		});
	}
	
	public OrganizationsFilter extractOrganizationFilter(Locale locale, HttpServletRequest req) throws BadRequestException {
		Status status = req.getParameter("status") == null ? null : crm.findStatusByLocalizedName(locale, req.getParameter("status"));
		String displayName = req.getParameter("displayName");
		return new OrganizationsFilter(displayName, status);
	}

	@PostMapping("/api/organizations")
	public void createOrganization(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, OrganizationDetails.class, (messages, transformer) -> { 
			JsonObject body = extractBody(req);
			String displayName = getString(body, "displayName", "", null, messages);
			List<String> groups = getStrings(body, "groups", List.of(), null, messages);
			validate(messages);
			return transformer.format(crm.createOrganization(displayName, groups), extractLocale(req));
		});
	}

	@GetMapping("/api/organizations/{organizationId}")
	public void getOrganization(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("organizationId") Identifier organizationId) throws IOException {
		handle(req, res, OrganizationDetails.class, (messages, transformer) -> {
			return transformer.format(crm.findOrganizationDetails(organizationId), extractLocale(req));
		});
	}

	@PatchMapping("/api/organizations/{organizationId}")
	public void updateOrganization(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("organizationId") Identifier organizationId) throws IOException {
		handle(req, res, OrganizationDetails.class, (messages, transformer) -> {
			JsonObject body = extractBody(req);
			if (body.contains("displayName")) {
				crm.updateOrganizationDisplayName(organizationId, getString(body, "displayName", null, null, messages));
			}
			if (body.contains("mainLocationId")) {
				crm.updateOrganizationMainLocation(organizationId, getIdentifier(body, "mainLocationId", null, null, messages));
			} else if (body.isNull("mainLocationId")) {
				crm.updateOrganizationMainLocation(organizationId, null);
			}
			if (body.contains("mainContactId")) {
				crm.updateOrganizationMainContact(organizationId, getIdentifier(body, "mainContactId", null, null, messages));
			} else if (body.isNull("mainContactId")) {
				crm.updateOrganizationMainContact(organizationId, null);
			}
			validate(messages);
			return transformer.format(crm.findOrganizationDetails(organizationId), extractLocale(req));
		});
	}

	@GetMapping("/api/organizations/{organizationId}/summary")
	public void getOrganizationSummary(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("organizationId") Identifier organizationId) throws IOException {
		handle(req, res, OrganizationSummary.class, (messages, transformer) -> {
			return transformer.format(crm.findOrganizationDetails(organizationId), extractLocale(req));
		});
	}

	@GetMapping("/api/organizations/{organizationId}/mainLocation")
	public void getOrganizationMainLocation(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("organizationId") Identifier organizationId) throws IOException {
		handle(req, res, LocationDetails.class, (messages, transformer) -> {
			return transformer.format(crm.findLocationDetails(crm.findOrganizationDetails(organizationId).getMainLocationId()), extractLocale(req));
		});
	}

	@GetMapping("/api/organizations/{organizationId}/mainContact")
	public void getOrganizationMainContact(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("organizationId") Identifier organizationId) throws IOException {
		handle(req, res, PersonDetails.class, (messages, transformer) -> {
			return transformer.format(crm.findPersonDetails(crm.findOrganizationDetails(organizationId).getMainContactId()), extractLocale(req));
		});
	}

	@PutMapping("/api/organizations/{organizationId}/enable")
	public void enableOrganization(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("organizationId") Identifier organizationId) throws IOException {
		handle(req, res, OrganizationSummary.class, (messages, transformer) -> {
			confirm(extractBody(req), organizationId, messages);
			return transformer.format(crm.enableOrganization(organizationId), extractLocale(req));
		});
	}

	@PutMapping("/api/organizations/{organizationId}/disable")
	public void disableOrganization(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("organizationId") Identifier organizationId) throws IOException {
		handle(req, res, OrganizationSummary.class, (messages, transformer) -> {
			confirm(extractBody(req), organizationId, messages);
			return transformer.format(crm.disableOrganization(organizationId), extractLocale(req));
		});
	}

}