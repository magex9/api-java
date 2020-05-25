package ca.magex.crm.restful.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.filters.GroupsFilter;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.secured.SecuredCrmServices;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonText;

@Controller
public class OrganizationsController extends AbstractCrmController {

	@Autowired
	private SecuredCrmServices crm;
	
	@GetMapping("/api/organizations")
	public void findOrganizations(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, (messages, transformer) -> { 
			return createPage(crm.findOrganizationSummaries(
				extractOrganizationFilter(extractLocale(req), req), 
				extractPaging(GroupsFilter.getDefaultPaging(), req)), 
				e -> transformer.formatOrganizationSummary(e));
		});
	}
	
	public OrganizationsFilter extractOrganizationFilter(Locale locale, HttpServletRequest req) throws BadRequestException {
		return new OrganizationsFilter(extractDisplayName(req), extractStatus(req));
	}

	@PostMapping("/api/organizations")
	public void createOrganization(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, (messages, transformer) -> { 
			JsonObject body = extractBody(req);
			String displayName = body.getString("displayName");
			List<String> groups = body.getArray("groups").stream().map(e -> ((JsonText)e).value()).collect(Collectors.toList());
			return transformer.formatOrganizationDetails(crm.createOrganization(displayName, groups));
		});
	}

//	@GetMapping("/api/organizations/{organizationId}")
//	public void getOrganization(HttpServletRequest req, HttpServletResponse res, 
//			@PathVariable("organizationId") String id) throws IOException {
//		JsonTransformer transformer = getTransformer(req, crm);
//		Identifier organizationId = new Identifier(id);
//		JsonElement data = transformer.formatOrganizationDetails(crm.findOrganizationDetails(organizationId));
//		res.setStatus(200);
//		res.setContentType(getContentType(req));
//		res.getWriter().write(JsonFormatter.formatted(data));
//	}
//
//	@PatchMapping("/api/organizations/{organizationId}")
//	public void updateOrganization(HttpServletRequest req, HttpServletResponse res, 
//			@PathVariable("organizationId") String id) throws IOException {
//		JsonTransformer transformer = getTransformer(req, crm);
//		Identifier organizationId = new Identifier(id);
//		JsonObject body = extractBody(req);
//		if (body.contains("displayName"))
//			crm.updateOrganizationDisplayName(organizationId, body.getString("displayName"));
//		if (body.contains("mainLocationId"))
//			crm.updateOrganizationMainLocation(organizationId, new Identifier(body.getString("mainLocationId")));
//		JsonElement data = transformer.formatOrganizationDetails(crm.findOrganizationDetails(organizationId));
//		res.setStatus(200);
//		res.setContentType(getContentType(req));
//		res.getWriter().write(JsonFormatter.formatted(data));
//	}
//
//	@GetMapping("/api/organizations/{organizationId}/summary")
//	public void getOrganizationSummary(HttpServletRequest req, HttpServletResponse res, 
//			@PathVariable("organizationId") String id) throws IOException {
//		JsonTransformer transformer = getTransformer(req, crm);
//		Identifier organizationId = new Identifier(id);
//		JsonElement data = transformer.formatOrganizationDetails(crm.findOrganizationDetails(organizationId));
//		res.setStatus(200);
//		res.setContentType(getContentType(req));
//		res.getWriter().write(JsonFormatter.formatted(data));
//	}
//
//	@GetMapping("/api/organizations/{organizationId}/mainLocation")
//	public void getOrganizationMainLocation(HttpServletRequest req, HttpServletResponse res, 
//			@PathVariable("organizationId") String id) throws IOException {
//		JsonTransformer transformer = getTransformer(req, crm);
//		Identifier organizationId = new Identifier(id);
//		JsonElement data = transformer.formatLocationDetails(crm.findLocationDetails(crm.findOrganizationDetails(organizationId).getMainLocationId()));
//		res.setStatus(200);
//		res.setContentType(getContentType(req));
//		res.getWriter().write(JsonFormatter.formatted(data));
//	}
//
//	@PutMapping("/api/organizations/{organizationId}/enable")
//	public void enableOrganization(HttpServletRequest req, HttpServletResponse res, 
//			@PathVariable("organizationId") String id) throws IOException {
//		JsonTransformer transformer = getTransformer(req, crm);
//		Identifier organizationId = new Identifier(id);
//		JsonElement data = transformer.formatOrganizationSummary(crm.enableOrganization(organizationId));
//		res.setStatus(200);
//		res.setContentType(getContentType(req));
//		res.getWriter().write(JsonFormatter.formatted(data));
//	}
//
//	@PutMapping("/api/organizations/{organizationId}/disable")
//	public void disableOrganization(HttpServletRequest req, HttpServletResponse res, 
//			@PathVariable("organizationId") String id) throws IOException {
//		JsonTransformer transformer = getTransformer(req, crm);
//		Identifier organizationId = new Identifier(id);
//		JsonElement data = transformer.formatOrganizationSummary(crm.disableOrganization(organizationId));
//		res.setStatus(200);
//		res.setContentType(getContentType(req));
//		res.getWriter().write(JsonFormatter.formatted(data));
//	}

}