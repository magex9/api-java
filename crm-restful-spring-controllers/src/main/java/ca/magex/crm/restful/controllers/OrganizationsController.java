package ca.magex.crm.restful.controllers;

import static ca.magex.crm.restful.controllers.ContentExtractor.extractBody;
import static ca.magex.crm.restful.controllers.ContentExtractor.extractDisplayName;
import static ca.magex.crm.restful.controllers.ContentExtractor.extractPaging;
import static ca.magex.crm.restful.controllers.ContentExtractor.extractStatus;
import static ca.magex.crm.restful.controllers.ContentExtractor.getContentType;
import static ca.magex.crm.restful.controllers.ContentExtractor.getTransformer;

import java.io.IOException;
import java.util.List;
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
import ca.magex.crm.api.secured.SecuredCrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.rest.transformers.JsonTransformer;
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonFormatter;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonText;

@Controller
public class OrganizationsController {

	@Autowired
	private SecuredCrmServices crm;
	
	@GetMapping("/api/organizations")
	public void findOrganizations(HttpServletRequest req, HttpServletResponse res) throws IOException {
		JsonTransformer transformer = getTransformer(req, crm);
		Page<OrganizationSummary> page = crm.findOrganizationSummaries(extractOrganizationFilter(req), extractPaging(req));
		JsonObject data = createPage(page, e -> transformer.formatOrganizationSummary(e));
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(JsonFormatter.formatted(data));
	}
	
	public <T> JsonObject createPage(Page<T> page, Function<T, JsonElement> mapper) {
		return new JsonObject()
			.with("page", page.getNumber())
			.with("total", page.getTotalElements())
			.with("hasNext", page.hasNext())
			.with("hasPrevious", !page.isFirst())
			.with("content", new JsonArray(page.getContent().stream().map(mapper).collect(Collectors.toList())));
	}
	
	public OrganizationsFilter extractOrganizationFilter(HttpServletRequest req) throws BadRequestException {
		return new OrganizationsFilter(extractDisplayName(req), extractStatus(req));
	}

	@PostMapping("/api/organizations")
	public void createOrganization(HttpServletRequest req, HttpServletResponse res) throws IOException {
		JsonTransformer transformer = getTransformer(req, crm);
		JsonObject body = extractBody(req);
		String displayName = body.getString("displayName");
		List<String> groups = body.getArray("groups").stream().map(e -> ((JsonText)e).value()).collect(Collectors.toList());
		JsonElement data = transformer.formatOrganizationDetails(crm.createOrganization(displayName, groups));
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(JsonFormatter.formatted(data));
	}

	@GetMapping("/api/organizations/{organizationId}")
	public void getOrganization(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("organizationId") String id) throws IOException {
		JsonTransformer transformer = getTransformer(req, crm);
		Identifier organizationId = new Identifier(id);
		JsonElement data = transformer.formatOrganizationDetails(crm.findOrganizationDetails(organizationId));
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(JsonFormatter.formatted(data));
	}

	@PatchMapping("/api/organizations/{organizationId}")
	public void updateOrganization(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("organizationId") String id) throws IOException {
		JsonTransformer transformer = getTransformer(req, crm);
		Identifier organizationId = new Identifier(id);
		JsonObject body = extractBody(req);
		if (body.contains("displayName"))
			crm.updateOrganizationDisplayName(organizationId, body.getString("displayName"));
		if (body.contains("mainLocationId"))
			crm.updateOrganizationMainLocation(organizationId, new Identifier(body.getString("mainLocationId")));
		JsonElement data = transformer.formatOrganizationDetails(crm.findOrganizationDetails(organizationId));
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(JsonFormatter.formatted(data));
	}

	@GetMapping("/api/organizations/{organizationId}/summary")
	public void getOrganizationSummary(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("organizationId") String id) throws IOException {
		JsonTransformer transformer = getTransformer(req, crm);
		Identifier organizationId = new Identifier(id);
		JsonElement data = transformer.formatOrganizationDetails(crm.findOrganizationDetails(organizationId));
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(JsonFormatter.formatted(data));
	}

	@GetMapping("/api/organizations/{organizationId}/mainLocation")
	public void getOrganizationMainLocation(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("organizationId") String id) throws IOException {
		JsonTransformer transformer = getTransformer(req, crm);
		Identifier organizationId = new Identifier(id);
		JsonElement data = transformer.formatLocationDetails(crm.findLocationDetails(crm.findOrganizationDetails(organizationId).getMainLocationId()));
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(JsonFormatter.formatted(data));
	}

	@PutMapping("/api/organizations/{organizationId}/enable")
	public void enableOrganization(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("organizationId") String id) throws IOException {
		JsonTransformer transformer = getTransformer(req, crm);
		Identifier organizationId = new Identifier(id);
		JsonElement data = transformer.formatOrganizationSummary(crm.enableOrganization(organizationId));
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(JsonFormatter.formatted(data));
	}

	@PutMapping("/api/organizations/{organizationId}/disable")
	public void disableOrganization(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("organizationId") String id) throws IOException {
		JsonTransformer transformer = getTransformer(req, crm);
		Identifier organizationId = new Identifier(id);
		JsonElement data = transformer.formatOrganizationSummary(crm.disableOrganization(organizationId));
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(JsonFormatter.formatted(data));
	}

}