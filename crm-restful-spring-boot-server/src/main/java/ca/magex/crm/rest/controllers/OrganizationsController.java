package ca.magex.crm.rest.controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.SecuredOrganizationService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.ld.LinkedDataFormatter;
import ca.magex.crm.ld.crm.LocationDetailsTransformer;
import ca.magex.crm.ld.crm.OrganizationDetailsTransformer;
import ca.magex.crm.ld.crm.OrganizationSummaryTransformer;
import ca.magex.crm.ld.data.DataArray;
import ca.magex.crm.ld.data.DataElement;
import ca.magex.crm.ld.data.DataObject;
import ca.magex.crm.ld.data.DataParser;
import ca.magex.crm.ld.system.StatusTransformer;

@Controller
public class OrganizationsController {

	@Autowired
	private SecuredOrganizationService service;
	
	@GetMapping("/api/organizations")
	public void findOrganizations(HttpServletRequest req, HttpServletResponse res) throws IOException {
		OrganizationSummaryTransformer transformer = new OrganizationSummaryTransformer();
		DataElement data = new DataArray(service.findOrganizationSummaries(extractOrganizationFilter(req), extractPaging(req)).getContent().stream()
				.map(e -> transformer.format(e)).collect(Collectors.toList()));
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(data.stringify(formatter(req)));
	}
	
	@PostMapping("/api/organizations")
	public void createOrganization(HttpServletRequest req, HttpServletResponse res) throws IOException {
		OrganizationDetailsTransformer transformer = new OrganizationDetailsTransformer();
		DataObject body = extractBody(req);
		String displayName = body.getString("displayName");
		DataElement data = transformer.format(service.createOrganization(displayName));
		res.setStatus(200);
		res.getWriter().write(data.stringify(formatter(req)));
	}

	@GetMapping("/api/organizations/{organizationId}")
	public void getOrganization(HttpServletRequest req, HttpServletResponse res, 
			@PathParam("organizationId") String id) throws IOException {
		OrganizationDetailsTransformer transformer = new OrganizationDetailsTransformer();
		Identifier organizationId = new Identifier(id);
		DataElement data = transformer.format(service.findOrganization(organizationId));
		res.setStatus(200);
		res.getWriter().write(data.stringify(formatter(req)));
	}

	@PatchMapping("/api/organizations/{organizationId}")
	public void updateOrganization(HttpServletRequest req, HttpServletResponse res, 
			@PathParam("organizationId") String id) throws IOException {
		OrganizationDetailsTransformer transformer = new OrganizationDetailsTransformer();
		Identifier organizationId = new Identifier(id);
		DataObject body = extractBody(req);
		if (body.contains("displayName"))
			service.updateOrganizationName(organizationId, body.getString("displayName"));
		if (body.contains("mainLocationId"))
			service.updateOrganizationMainLocation(organizationId, new Identifier(body.getString("mainLocationId")));
		DataElement data = transformer.format(service.findOrganization(organizationId));
		res.setStatus(200);
		res.getWriter().write(data.stringify(formatter(req)));
	}

	@GetMapping("/api/organizations/{organizationId}/summary")
	public void getOrganizationSummary(HttpServletRequest req, HttpServletResponse res, 
			@PathParam("organizationId") String id) throws IOException {
		OrganizationSummaryTransformer transformer = new OrganizationSummaryTransformer();
		Identifier organizationId = new Identifier(id);
		DataElement data = transformer.format(service.findOrganization(organizationId));
		res.setStatus(200);
		res.getWriter().write(data.stringify(formatter(req)));
	}

	@GetMapping("/api/organizations/{organizationId}/mainLocation")
	public void getOrganizationMainLocation(HttpServletRequest req, HttpServletResponse res, 
			@PathParam("organizationId") String id) throws IOException {
		LocationDetailsTransformer transformer = new LocationDetailsTransformer();
		Identifier organizationId = new Identifier(id);
		DataElement data = transformer.format(service.findLocation(service.findOrganization(organizationId).getMainLocationId()));
		res.setStatus(200);
		res.getWriter().write(data.stringify(formatter(req)));
	}

	@PutMapping("/api/organizations/{organizationId}/enable")
	public void enableOrganization(HttpServletRequest req, HttpServletResponse res, 
			@PathParam("organizationId") String id) throws IOException {
		StatusTransformer transformer = new StatusTransformer();
		Identifier organizationId = new Identifier(id);
		DataElement data = transformer.format(service.enableOrganization(organizationId).getStatus());
		res.setStatus(200);
		res.getWriter().write(data.stringify(formatter(req)));
	}

	@PutMapping("/api/organizations/{organizationId}/disable")
	public void disableOrganization(HttpServletRequest req, HttpServletResponse res, 
			@PathParam("organizationId") String id) throws IOException {
		StatusTransformer transformer = new StatusTransformer();
		Identifier organizationId = new Identifier(id);
		DataElement data = transformer.format(service.enableOrganization(organizationId).getStatus());
		res.setStatus(200);
		res.getWriter().write(data.stringify(formatter(req)));
	}
	
	public String getContentType(HttpServletRequest req) {
		if (req.getHeader("Accept").equals("application/json")) {
			return "application/json";
		}
		return "application/json+ld";
	}

	public LinkedDataFormatter formatter(HttpServletRequest req) {
		if (req.getHeader("Accept").equals("application/json")) {
			return LinkedDataFormatter.json();
		} else {
			return LinkedDataFormatter.full();
		}
	}
	
	public OrganizationsFilter extractOrganizationFilter(HttpServletRequest req) {
		String displayName = req.getParameter("displayName") == null ? null : req.getParameter("displayName");
		Status status = req.getParameter("status") == null ? null : Status.valueOf(req.getParameter("status").toUpperCase());
		return new OrganizationsFilter(displayName, status);
	}
	
	public final Paging extractPaging(HttpServletRequest req) {
		Integer page = req.getParameter("page") == null ? 1 : Integer.parseInt(req.getParameter("page"));
		Integer limit = req.getParameter("limit") == null ? 10 : Integer.parseInt(req.getParameter("limit"));
		String order = req.getParameter("order") == null ? "displayName" : req.getParameter("order");
		String direction = req.getParameter("direction") == null ? "asc" : req.getParameter("direction");
		return new Paging(page, limit, Sort.by(Direction.fromString(direction), order));
	}
	
	public DataObject extractBody(HttpServletRequest req) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		IOUtils.copy(req.getInputStream(), baos);
		return DataParser.parseObject(new String(baos.toByteArray()));
	}

}