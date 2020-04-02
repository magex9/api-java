package ca.magex.crm.rest.controllers;

import static ca.magex.crm.rest.controllers.ContentExtractor.extractBody;
import static ca.magex.crm.rest.controllers.ContentExtractor.extractDisplayName;
import static ca.magex.crm.rest.controllers.ContentExtractor.extractPaging;
import static ca.magex.crm.rest.controllers.ContentExtractor.extractStatus;
import static ca.magex.crm.rest.controllers.ContentExtractor.formatter;
import static ca.magex.crm.rest.controllers.ContentExtractor.getContentType;

import java.io.IOException;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.services.SecuredOrganizationService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.ld.crm.LocationDetailsTransformer;
import ca.magex.crm.ld.crm.OrganizationDetailsTransformer;
import ca.magex.crm.ld.crm.OrganizationSummaryTransformer;
import ca.magex.crm.ld.data.DataArray;
import ca.magex.crm.ld.data.DataElement;
import ca.magex.crm.ld.data.DataObject;
import ca.magex.crm.ld.system.StatusTransformer;

@Controller
public class OrganizationsController {

	@Autowired
	private SecuredOrganizationService service;
	
	@GetMapping("/api/organizations")
	public void findOrganizations(HttpServletRequest req, HttpServletResponse res) throws IOException {
		OrganizationSummaryTransformer transformer = new OrganizationSummaryTransformer(service);
		DataElement data = new DataArray(service.findOrganizationSummaries(extractOrganizationFilter(req), extractPaging(req)).getContent().stream()
				.map(e -> transformer.format(e)).collect(Collectors.toList()));
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(data.stringify(formatter(req)));
	}
	
	public OrganizationsFilter extractOrganizationFilter(HttpServletRequest req) throws BadRequestException {
		return new OrganizationsFilter(extractDisplayName(req), extractStatus(req));
	}

	@PostMapping("/api/organizations")
	public void createOrganization(HttpServletRequest req, HttpServletResponse res) throws IOException {
		OrganizationDetailsTransformer transformer = new OrganizationDetailsTransformer(service);
		DataObject body = extractBody(req);
		String displayName = body.getString("displayName");
		DataElement data = transformer.format(service.createOrganization(displayName));
		res.setStatus(200);
		res.getWriter().write(data.stringify(formatter(req)));
	}

	@GetMapping("/api/organizations/{organizationId}")
	public void getOrganization(HttpServletRequest req, HttpServletResponse res, 
			@PathParam("organizationId") String id) throws IOException {
		OrganizationDetailsTransformer transformer = new OrganizationDetailsTransformer(service);
		Identifier organizationId = new Identifier(id);
		DataElement data = transformer.format(service.findOrganizationDetails(organizationId));
		res.setStatus(200);
		res.getWriter().write(data.stringify(formatter(req)));
	}

	@PatchMapping("/api/organizations/{organizationId}")
	public void updateOrganization(HttpServletRequest req, HttpServletResponse res, 
			@PathParam("organizationId") String id) throws IOException {
		OrganizationDetailsTransformer transformer = new OrganizationDetailsTransformer(service);
		Identifier organizationId = new Identifier(id);
		DataObject body = extractBody(req);
		if (body.contains("displayName"))
			service.updateOrganizationName(organizationId, body.getString("displayName"));
		if (body.contains("mainLocationId"))
			service.updateOrganizationMainLocation(organizationId, new Identifier(body.getString("mainLocationId")));
		DataElement data = transformer.format(service.findOrganizationDetails(organizationId));
		res.setStatus(200);
		res.getWriter().write(data.stringify(formatter(req)));
	}

	@GetMapping("/api/organizations/{organizationId}/summary")
	public void getOrganizationSummary(HttpServletRequest req, HttpServletResponse res, 
			@PathParam("organizationId") String id) throws IOException {
		OrganizationSummaryTransformer transformer = new OrganizationSummaryTransformer(service);
		Identifier organizationId = new Identifier(id);
		DataElement data = transformer.format(service.findOrganizationDetails(organizationId));
		res.setStatus(200);
		res.getWriter().write(data.stringify(formatter(req)));
	}

	@GetMapping("/api/organizations/{organizationId}/mainLocation")
	public void getOrganizationMainLocation(HttpServletRequest req, HttpServletResponse res, 
			@PathParam("organizationId") String id) throws IOException {
		LocationDetailsTransformer transformer = new LocationDetailsTransformer(service);
		Identifier organizationId = new Identifier(id);
		DataElement data = transformer.format(service.findLocationDetails(service.findOrganizationDetails(organizationId).getMainLocationId()));
		res.setStatus(200);
		res.getWriter().write(data.stringify(formatter(req)));
	}

	@PutMapping("/api/organizations/{organizationId}/enable")
	public void enableOrganization(HttpServletRequest req, HttpServletResponse res, 
			@PathParam("organizationId") String id) throws IOException {
		StatusTransformer transformer = new StatusTransformer(service);
		Identifier organizationId = new Identifier(id);
		DataElement data = transformer.format(service.enableOrganization(organizationId).getStatus());
		res.setStatus(200);
		res.getWriter().write(data.stringify(formatter(req)));
	}

	@PutMapping("/api/organizations/{organizationId}/disable")
	public void disableOrganization(HttpServletRequest req, HttpServletResponse res, 
			@PathParam("organizationId") String id) throws IOException {
		StatusTransformer transformer = new StatusTransformer(service);
		Identifier organizationId = new Identifier(id);
		DataElement data = transformer.format(service.enableOrganization(organizationId).getStatus());
		res.setStatus(200);
		res.getWriter().write(data.stringify(formatter(req)));
	}

}