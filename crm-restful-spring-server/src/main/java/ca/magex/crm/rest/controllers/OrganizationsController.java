package ca.magex.crm.rest.controllers;

import java.io.IOException;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.SecuredOrganizationService;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.ld.LinkedDataFormatter;
import ca.magex.crm.ld.crm.OrganizationSummaryTransformer;
import ca.magex.crm.ld.data.DataArray;
import ca.magex.crm.ld.data.DataElement;

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
		if (req.getHeader("Accept").equals("application/json")) {
			res.getWriter().write(data.stringify(LinkedDataFormatter.json()));
		} else {
			res.getWriter().write(data.stringify(LinkedDataFormatter.full()));
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
	
//	@PostMapping("/organizations")
//	public Organization createOrganization(String organizationName) {
//		return organizations.createOrganization(organizationName);
//	}
//	
//	@GetMapping("/organizations/{organizationId}")
//	public Organization one(@PathVariable String organizationId) {
//		return organizations.findOrganization(new Identifier(organizationId));
//	}
	
}