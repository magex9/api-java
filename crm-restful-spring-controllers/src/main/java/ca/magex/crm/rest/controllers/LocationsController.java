package ca.magex.crm.rest.controllers;
	
import static ca.magex.crm.rest.controllers.ContentExtractor.extractBody;
import static ca.magex.crm.rest.controllers.ContentExtractor.extractDisplayName;
import static ca.magex.crm.rest.controllers.ContentExtractor.extractOrganizationId;
import static ca.magex.crm.rest.controllers.ContentExtractor.extractPaging;
import static ca.magex.crm.rest.controllers.ContentExtractor.extractStatus;
import static ca.magex.crm.rest.controllers.ContentExtractor.getContentType;
import static ca.magex.crm.rest.controllers.ContentExtractor.getTransformer;

import java.io.IOException;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.services.SecuredCrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.mapping.data.DataArray;
import ca.magex.crm.mapping.data.DataElement;
import ca.magex.crm.mapping.data.DataFormatter;
import ca.magex.crm.mapping.data.DataObject;
import ca.magex.crm.mapping.json.JsonTransformer;

@Controller
public class LocationsController {

	@Autowired
	private SecuredCrmServices crm;
	
	public LocationsFilter extractLocationFilter(HttpServletRequest req) throws BadRequestException {
		return new LocationsFilter(extractOrganizationId(req), extractDisplayName(req), extractStatus(req));
	}

	@GetMapping("/api/locations")
	public void findLocations(HttpServletRequest req, HttpServletResponse res) throws IOException {
		JsonTransformer transformer = getTransformer(req, crm);
		DataElement data = new DataArray(crm.findLocationSummaries(extractLocationFilter(req), extractPaging(req)).getContent().stream()
				.map(e -> transformer.formatLocationSummary(e)).collect(Collectors.toList()));
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(DataFormatter.formatted(data));
	}
	
	@PostMapping("/api/locations")
	public void createLocation(HttpServletRequest req, HttpServletResponse res) throws IOException {
		JsonTransformer transformer = getTransformer(req, crm);
		DataObject body = extractBody(req);
		Identifier organizationId = new Identifier(body.getString("organizationId"));
		String displayName = body.getString("displayName");
		String reference = body.getString("reference");
		MailingAddress address = transformer.parseMailingAddress("address", body);
		DataElement data = transformer.formatLocationDetails(crm.createLocation(organizationId, displayName, reference, address));
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(DataFormatter.formatted(data));
	}

	@GetMapping("/api/locations/{locationId}")
	public void getLocation(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("locationId") String id) throws IOException {
		JsonTransformer transformer = getTransformer(req, crm);
		Identifier locationId = new Identifier(id);
		DataElement data = transformer.formatLocationDetails(crm.findLocationDetails(locationId));
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(DataFormatter.formatted(data));
	}

	@PatchMapping("/api/locations/{locationId}")
	public void updateLocation(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("locationId") String id) throws IOException {
		JsonTransformer transformer = getTransformer(req, crm);
		Identifier locationId = new Identifier(id);
		DataObject body = extractBody(req);
		if (body.contains("displayName"))
			crm.updateLocationName(locationId, body.getString("displayName"));
		if (body.contains("address"))
			crm.updateLocationAddress(locationId, transformer.parseMailingAddress("address", body));
		DataElement data = transformer.formatLocationDetails(crm.findLocationDetails(locationId));
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(DataFormatter.formatted(data));
	}

	@GetMapping("/api/locations/{locationId}/summary")
	public void getLocationSummary(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("locationId") String id) throws IOException {
		JsonTransformer transformer = getTransformer(req, crm);
		Identifier locationId = new Identifier(id);
		DataElement data = transformer.formatLocationSummary(crm.findLocationDetails(locationId));
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(DataFormatter.formatted(data));
	}

	@GetMapping("/api/locations/{locationId}/address")
	public void getLocationMainLocation(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("locationId") String id) throws IOException {
		JsonTransformer transformer = getTransformer(req, crm);
		Identifier locationId = new Identifier(id);
		DataElement data = transformer.formatLocationDetails(crm.findLocationDetails(locationId)).getObject("address");
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(DataFormatter.formatted(data));
	}

	@PutMapping("/api/locations/{locationId}/enable")
	public void enableLocation(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("locationId") String id) throws IOException {
		JsonTransformer transformer = getTransformer(req, crm);
		Identifier locationId = new Identifier(id);
		DataElement data = transformer.formatLocationSummary(crm.enableLocation(locationId));
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(DataFormatter.formatted(data));
	}

	@PutMapping("/api/locations/{locationId}/disable")
	public void disableLocation(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("locationId") String id) throws IOException {
		JsonTransformer transformer = getTransformer(req, crm);
		Identifier locationId = new Identifier(id);
		DataElement data = transformer.formatLocationSummary(crm.disableLocation(locationId));
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(DataFormatter.formatted(data));
	}

}