package ca.magex.crm.restful.controllers;
	
import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.json.model.JsonObject;

@Controller
public class LocationsController extends AbstractCrmController {

	@GetMapping("/api/locations")
	public void findLocations(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, LocationSummary.class, (messages, transformer, locale) -> { 
			return createPage(
				crm.findLocationSummaries(
					extractLocationFilter(req, locale), 
					extractPaging(LocationsFilter.getDefaultPaging(), req)
				), transformer, locale
			);
		});
	}
	
//	private JsonArray formatLocationsActions(Identifier organizationId) {
//		List<JsonElement> actions = new ArrayList<JsonElement>();
//		if (crm.canCreateLocationForOrganization(organizationId)) {
//			actions.add(action("create", "Create Location", "post", "/api/locations"));
//		}
//		return new JsonArray(actions);
//		
//	}
//	
//	private JsonArray formatLocationActions(Identifier locationId) {
//		List<JsonElement> actions = new ArrayList<JsonElement>();
//		if (crm.canUpdateLocation(locationId)) {
//			actions.add(action("edit", "Edit", "get", "/api/locations/" + locationId + "/edit"));
//		} else if (crm.canViewLocation(locationId)) {
//			actions.add(action("view", "View", "get", "/api/locations/" + locationId));
//		}
//		if (crm.canDisableLocation(locationId)) {
//			actions.add(action("disable", "Inactivate", "put", "/api/locations/" + locationId + "/disable"));
//		}
//		if (crm.canEnableLocation(locationId)) {
//			actions.add(action("enable", "Activate", "put", "/api/locations/" + locationId + "/enable"));
//		}
//		return new JsonArray(actions);
//	}
	
	public LocationsFilter extractLocationFilter(HttpServletRequest req, Locale locale) throws BadRequestException {
		Identifier organizationId = req.getParameter("organization") == null ? null : new Identifier(req.getParameter("organization"));
		String displayName = req.getParameter("displayName");
		String reference = req.getParameter("reference");
		Status status = req.getParameter("status") == null ? null : crm.findStatusByLocalizedName(locale, req.getParameter("status"));
		return new LocationsFilter(organizationId, displayName, reference, status);
	}

	@PostMapping("/api/locations")
	public void createLocation(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, LocationDetails.class, (messages, transformer, locale) -> { 
			JsonObject body = extractBody(req);
			Identifier organizationId = getIdentifier(body, "organizationId", null, null, messages);
			String displayName = getString(body, "displayName", "", null, messages);
			String reference = getString(body, "reference", "", null, messages);
			MailingAddress address = getObject(MailingAddress.class, body, "address", null, null, messages, locale);
			validate(messages);
			return transformer.format(crm.createLocation(organizationId, displayName, reference, address), locale);
		});
	}

	@GetMapping("/api/locations/{locationId}")
	public void getLocation(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("locationId") Identifier locationId) throws IOException {
		handle(req, res, LocationDetails.class, (messages, transformer, locale) -> {
			return transformer.format(crm.findLocationDetails(locationId), locale);
		});
	}

	@PatchMapping("/api/locations/{locationId}")
	public void updateLocation(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("locationId") Identifier locationId) throws IOException {
		handle(req, res, LocationDetails.class, (messages, transformer, locale) -> {
			JsonObject body = extractBody(req);
			if (body.contains("displayName")) {
				crm.updateLocationName(locationId, getString(body, "displayName", null, locationId, messages));
			}
			if (body.contains("address")) {
				crm.updateLocationAddress(locationId, getObject(MailingAddress.class, body, "address", null, locationId, messages, locale));
			}
			validate(messages);
			return transformer.format(crm.findLocationDetails(locationId), locale);
		});
	}

	@GetMapping("/api/locations/{locationId}/summary")
	public void getLocationSummary(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("locationId") Identifier locationId) throws IOException {
		handle(req, res, LocationSummary.class, (messages, transformer, locale) -> {
			return transformer.format(crm.findLocationDetails(locationId), locale);
		});
	}

	@GetMapping("/api/locations/{locationId}/address")
	public void getLocationMainLocation(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("locationId") Identifier locationId) throws IOException {
		handle(req, res, MailingAddress.class, (messages, transformer, locale) -> {
			return transformer.format(crm.findLocationDetails(locationId).getAddress(), locale);
		});
	}

	@PutMapping("/api/locations/{locationId}/enable")
	public void enableOrganization(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("locationId") Identifier locationId) throws IOException {
		handle(req, res, OrganizationSummary.class, (messages, transformer, locale) -> {
			confirm(extractBody(req), locationId, messages);
			return transformer.format(crm.enableOrganization(locationId), locale);
		});
	}

	@PutMapping("/api/locations/{locationId}/disable")
	public void disableOrganization(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("locationId") Identifier locationId) throws IOException {
		handle(req, res, OrganizationSummary.class, (messages, transformer, locale) -> {
			confirm(extractBody(req), locationId, messages);
			return transformer.format(crm.disableOrganization(locationId), locale);
		});
	}

}