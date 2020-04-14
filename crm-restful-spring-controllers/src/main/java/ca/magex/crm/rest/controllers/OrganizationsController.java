package ca.magex.crm.rest.controllers;

import static ca.magex.crm.rest.controllers.ContentExtractor.extractBody;
import static ca.magex.crm.rest.controllers.ContentExtractor.extractDisplayName;
import static ca.magex.crm.rest.controllers.ContentExtractor.extractPaging;
import static ca.magex.crm.rest.controllers.ContentExtractor.extractStatus;
import static ca.magex.crm.rest.controllers.ContentExtractor.getContentType;
import static ca.magex.crm.rest.controllers.ContentExtractor.getTransformer;

import java.io.IOException;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.services.SecuredCrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.mapping.data.DataArray;
import ca.magex.crm.mapping.data.DataElement;
import ca.magex.crm.mapping.data.DataFormatter;
import ca.magex.crm.mapping.data.DataObject;
import ca.magex.crm.mapping.json.JsonTransformer;

@Controller
public class OrganizationsController {

	@Autowired
	private SecuredCrmServices crm;
	
	@GetMapping("/api/organizations")
	public void findOrganizations(HttpServletRequest req, HttpServletResponse res) throws IOException {
		JsonTransformer transformer = getTransformer(req, crm);
		Page<OrganizationSummary> page = crm.findOrganizationSummaries(extractOrganizationFilter(req), extractPaging(req));
		DataObject data = createPage(page, e -> transformer.formatOrganizationSummary(e));
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(DataFormatter.formatted(data));
	}
	
	public <T> DataObject createPage(Page<T> page, Function<T, DataElement> mapper) {
		return new DataObject()
			.with("page", page.getNumber())
			.with("total", page.getTotalElements())
			.with("hasNext", page.hasNext())
			.with("hasPrevious", !page.isFirst())
			.with("content", new DataArray(page.getContent().stream().map(mapper).collect(Collectors.toList())));
	}
	
	public OrganizationsFilter extractOrganizationFilter(HttpServletRequest req) throws BadRequestException {
		return new OrganizationsFilter(extractDisplayName(req), extractStatus(req));
	}

	@PostMapping("/api/organizations")
	public void createOrganization(HttpServletRequest req, HttpServletResponse res) throws IOException {
		JsonTransformer transformer = getTransformer(req, crm);
		DataObject body = extractBody(req);
		String displayName = body.getString("displayName");
		DataElement data = transformer.formatOrganizationDetails(crm.createOrganization(displayName));
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(DataFormatter.formatted(data));
	}

	@GetMapping("/api/organizations/{organizationId}")
	public void getOrganization(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("organizationId") String id) throws IOException {
		JsonTransformer transformer = getTransformer(req, crm);
		Identifier organizationId = new Identifier(id);
		DataElement data = transformer.formatOrganizationDetails(crm.findOrganizationDetails(organizationId));
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(DataFormatter.formatted(data));
	}

	@PatchMapping("/api/organizations/{organizationId}")
	public void updateOrganization(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("organizationId") String id) throws IOException {
		JsonTransformer transformer = getTransformer(req, crm);
		Identifier organizationId = new Identifier(id);
		DataObject body = extractBody(req);
		if (body.contains("displayName"))
			crm.updateOrganizationDisplayName(organizationId, body.getString("displayName"));
		if (body.contains("mainLocationId"))
			crm.updateOrganizationMainLocation(organizationId, new Identifier(body.getString("mainLocationId")));
		DataElement data = transformer.formatOrganizationDetails(crm.findOrganizationDetails(organizationId));
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(DataFormatter.formatted(data));
	}

	@GetMapping("/api/organizations/{organizationId}/summary")
	public void getOrganizationSummary(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("organizationId") String id) throws IOException {
		JsonTransformer transformer = getTransformer(req, crm);
		Identifier organizationId = new Identifier(id);
		DataElement data = transformer.formatOrganizationDetails(crm.findOrganizationDetails(organizationId));
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(DataFormatter.formatted(data));
	}

	@GetMapping("/api/organizations/{organizationId}/mainLocation")
	public void getOrganizationMainLocation(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("organizationId") String id) throws IOException {
		JsonTransformer transformer = getTransformer(req, crm);
		Identifier organizationId = new Identifier(id);
		DataElement data = transformer.formatLocationDetails(crm.findLocationDetails(crm.findOrganizationDetails(organizationId).getMainLocationId()));
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(DataFormatter.formatted(data));
	}

	@PutMapping("/api/organizations/{organizationId}/enable")
	public void enableOrganization(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("organizationId") String id) throws IOException {
		JsonTransformer transformer = getTransformer(req, crm);
		Identifier organizationId = new Identifier(id);
		DataElement data = transformer.formatOrganizationSummary(crm.enableOrganization(organizationId));
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(DataFormatter.formatted(data));
	}

	@PutMapping("/api/organizations/{organizationId}/disable")
	public void disableOrganization(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("organizationId") String id) throws IOException {
		JsonTransformer transformer = getTransformer(req, crm);
		Identifier organizationId = new Identifier(id);
		DataElement data = transformer.formatOrganizationSummary(crm.disableOrganization(organizationId));
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(DataFormatter.formatted(data));
	}

}