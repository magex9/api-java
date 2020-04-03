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
import ca.magex.crm.ld.common.MailingAddressTransformer;
import ca.magex.crm.ld.crm.LocationDetailsTransformer;
import ca.magex.crm.ld.crm.LocationSummaryTransformer;
import ca.magex.crm.ld.data.DataArray;
import ca.magex.crm.ld.data.DataElement;
import ca.magex.crm.ld.data.DataObject;
import ca.magex.crm.ld.system.StatusTransformer;

@Controller
public class LocationsController {

	@Autowired
	private SecuredCrmServices crm;
	
	@GetMapping("/api/locations")
	public void findLocations(HttpServletRequest req, HttpServletResponse res) throws IOException {
		LocationSummaryTransformer transformer = new LocationSummaryTransformer(crm);
		DataElement data = new DataArray(crm.findLocationSummaries(extractLocationFilter(req), extractPaging(req)).getContent().stream()
				.map(e -> transformer.format(e)).collect(Collectors.toList()));
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(data.stringify(formatter(req)));
	}
	
	public LocationsFilter extractLocationFilter(HttpServletRequest req) throws BadRequestException {
		return new LocationsFilter(extractDisplayName(req), extractStatus(req));
	}

	@PostMapping("/api/locations")
	public void createLocation(HttpServletRequest req, HttpServletResponse res) throws IOException {
		LocationDetailsTransformer transformer = new LocationDetailsTransformer(crm);
		DataObject body = extractBody(req);
		Identifier organizationId = new Identifier(body.getString("organizationId"));
		String locationName = body.getString("locationName");
		String locationReference = body.getString("locationReference");
		MailingAddress address = new MailingAddressTransformer(crm).parse(body.getObject("address"));
		DataElement data = transformer.format(crm.createLocation(organizationId, locationName, locationReference, address));
		res.setStatus(200);
		res.getWriter().write(data.stringify(formatter(req)));
	}

	@GetMapping("/api/locations/{locationId}")
	public void getLocation(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("locationId") String id) throws IOException {
		LocationDetailsTransformer transformer = new LocationDetailsTransformer(crm);
		Identifier locationId = new Identifier(id);
		DataElement data = transformer.format(crm.findLocationDetails(locationId));
		res.setStatus(200);
		res.getWriter().write(data.stringify(formatter(req)));
	}

	@PatchMapping("/api/locations/{locationId}")
	public void updateLocation(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("locationId") String id) throws IOException {
		LocationDetailsTransformer transformer = new LocationDetailsTransformer(crm);
		Identifier locationId = new Identifier(id);
		DataObject body = extractBody(req);
		if (body.contains("displayName"))
			crm.updateLocationName(locationId, body.getString("displayName"));
		if (body.contains("mainLocationId"))
			crm.updateLocationAddress(locationId, new MailingAddressTransformer(crm).parse(body.getObject("address")));
		DataElement data = transformer.format(crm.findLocationDetails(locationId));
		res.setStatus(200);
		res.getWriter().write(data.stringify(formatter(req)));
	}

	@GetMapping("/api/locations/{locationId}/summary")
	public void getLocationSummary(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("locationId") String id) throws IOException {
		LocationSummaryTransformer transformer = new LocationSummaryTransformer(crm);
		Identifier locationId = new Identifier(id);
		DataElement data = transformer.format(crm.findLocationDetails(locationId));
		res.setStatus(200);
		res.getWriter().write(data.stringify(formatter(req)));
	}

	@GetMapping("/api/locations/{locationId}/address")
	public void getLocationMainLocation(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("locationId") String id) throws IOException {
		MailingAddressTransformer transformer = new MailingAddressTransformer(crm);
		Identifier locationId = new Identifier(id);
		DataElement data = transformer.format(crm.findLocationDetails(locationId).getAddress());
		res.setStatus(200);
		res.getWriter().write(data.stringify(formatter(req)));
	}

	@PutMapping("/api/locations/{locationId}/enable")
	public void enableLocation(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("locationId") String id) throws IOException {
		StatusTransformer transformer = new StatusTransformer(crm);
		Identifier locationId = new Identifier(id);
		DataElement data = transformer.format(crm.enableLocation(locationId).getStatus());
		res.setStatus(200);
		res.getWriter().write(data.stringify(formatter(req)));
	}

	@PutMapping("/api/locations/{locationId}/disable")
	public void disableLocation(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("locationId") String id) throws IOException {
		StatusTransformer transformer = new StatusTransformer(crm);
		Identifier locationId = new Identifier(id);
		DataElement data = transformer.format(crm.enableLocation(locationId).getStatus());
		res.setStatus(200);
		res.getWriter().write(data.stringify(formatter(req)));
	}

}