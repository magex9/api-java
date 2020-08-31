package ca.magex.crm.restful.controllers;
	
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.json.model.JsonObject;

@Controller
@CrossOrigin
public class RestfulLocationsController extends AbstractRestfulController {

	@GetMapping("/rest/locations")
	public void findLocationSummaries(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, LocationSummary.class, (messages, transformer, locale) -> { 
			return createPage(
				crm.findLocationSummaries(
					extractLocationFilter(req, locale), 
					extractPaging(LocationsFilter.getDefaultPaging(), req)
				), transformer, locale
			);
		});
	}

	@GetMapping("/rest/locations/details")
	public void findLocationDetails(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, LocationSummary.class, (messages, transformer, locale) -> { 
			return createPage(
				crm.findLocationSummaries(
					extractLocationFilter(req, locale), 
					extractPaging(LocationsFilter.getDefaultPaging(), req)
				), transformer, locale
			);
		});
	}
	
	@GetMapping("/rest/locations/count")
	public void countLocations(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, LocationSummary.class, (messages, transformer, locale) -> {
			return new JsonObject().with("total", crm.countLocations(extractLocationFilter(req, locale)));
		});
	}
	
	public LocationsFilter extractLocationFilter(HttpServletRequest req, Locale locale) throws BadRequestException {
		List<Message> messages = new ArrayList<>();
		JsonObject query = extractQuery(req);
		OrganizationIdentifier organizationId = getIdentifier(query, "organizationId", false, null, null, messages);
		String displayName = getString(query, "displayName", false, null, null, messages);
		String reference = getString(query, "reference", false, null, null, messages);
		Status status = getObject(Status.class, query, "status", false, null, null, messages, locale);
		return new LocationsFilter(organizationId, displayName, reference, status);
	}
	
//	private JsonArray formatLocationsActions(Identifier organizationId) {
//		List<JsonElement> actions = new ArrayList<JsonElement>();
//		if (crm.canCreateLocationForOrganization(organizationId)) {
//			actions.add(action("create", "Create Location", "post", "/rest/locations"));
//		}
//		return new JsonArray(actions);
//	}
//	
//	private JsonArray formatLocationActions(Identifier locationId) {
//		List<JsonElement> actions = new ArrayList<JsonElement>();
//		if (crm.canUpdateLocation(locationId)) {
//			actions.add(action("edit", "Edit", "get", "/rest/locations/" + locationId + "/edit"));
//		} else if (crm.canViewLocation(locationId)) {
//			actions.add(action("view", "View", "get", "/rest/locations/" + locationId));
//		}
//		if (crm.canDisableLocation(locationId)) {
//			actions.add(action("disable", "Inactivate", "put", "/rest/locations/" + locationId + "/disable"));
//		}
//		if (crm.canEnableLocation(locationId)) {
//			actions.add(action("enable", "Activate", "put", "/rest/locations/" + locationId + "/enable"));
//		}
//		return new JsonArray(actions);
//	}

	@PostMapping("/rest/locations")
	public void createLocation(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, LocationDetails.class, (messages, transformer, locale) -> { 
			JsonObject body = extractBody(req);
			OrganizationIdentifier organizationId = getIdentifier(body, "organizationId", false, null, null, messages);
			String displayName = getString(body, "displayName", false, "", null, messages);
			String reference = getString(body, "reference", false, "", null, messages);
			MailingAddress address = getObject(MailingAddress.class, body, "address", false, null, null, messages, locale);
			validate(messages);
			return transformer.format(crm.createLocation(organizationId, reference, displayName, address), locale);
		});
	}

	@GetMapping("/rest/locations/{locationId}")
	public void findLocationSummary(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("locationId") LocationIdentifier locationId) throws IOException {
		handle(req, res, LocationSummary.class, (messages, transformer, locale) -> {
			return transformer.format(crm.findLocationSummary(locationId), locale);
		});
	}

	@GetMapping("/rest/locations/{locationId}/details")
	public void findLocationDetails(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("locationId") LocationIdentifier locationId) throws IOException {
		handle(req, res, LocationDetails.class, (messages, transformer, locale) -> {
			return transformer.format(crm.findLocationDetails(locationId), locale);
		});
	}

	@PatchMapping("/rest/locations/{locationId}/details")
	public void updateLocation(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("locationId") LocationIdentifier locationId) throws IOException {
		handle(req, res, LocationDetails.class, (messages, transformer, locale) -> {
			JsonObject body = extractBody(req);
			if (body.contains("displayName")) {
				crm.updateLocationName(locationId, getString(body, "displayName", true, null, locationId, messages));
			}
			if (body.contains("address")) {
				crm.updateLocationAddress(locationId, getObject(MailingAddress.class, body, "address", true, null, locationId, messages, locale));
			}
			validate(messages);
			return transformer.format(crm.findLocationDetails(locationId), locale);
		});
	}

	@GetMapping("/rest/locations/{locationId}/details/address")
	public void findLocationAddress(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("locationId") LocationIdentifier locationId) throws IOException {
		handle(req, res, MailingAddress.class, (messages, transformer, locale) -> {
			return transformer.format(crm.findLocationDetails(locationId).getAddress(), locale);
		});
	}

	@PutMapping("/rest/locations/{locationId}/details/address")
	public void updateLocationAddress(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("locationId") LocationIdentifier locationId) throws IOException {
		handle(req, res, LocationDetails.class, (messages, transformer, locale) -> {
			JsonObject body = extractBody(req);
			crm.updateLocationAddress(locationId, getObject(MailingAddress.class, body, "address", true, null, locationId, messages, locale));
			validate(messages);
			return transformer.format(crm.findLocationDetails(locationId), locale);
		});
	}
	
	@PutMapping("/rest/locations/{locationId}/actions/enable")
	public void enableLocation(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("locationId") LocationIdentifier locationId) throws IOException {
		handle(req, res, LocationSummary.class, (messages, transformer, locale) -> {
			confirm(extractBody(req), locationId, messages);
			return transformer.format(crm.enableLocation(locationId), locale);
		});
	}

	@PutMapping("/rest/locations/{locationId}/actions/disable")
	public void disableLocation(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("locationId") LocationIdentifier locationId) throws IOException {
		handle(req, res, LocationSummary.class, (messages, transformer, locale) -> {
			confirm(extractBody(req), locationId, messages);
			return transformer.format(crm.disableLocation(locationId), locale);
		});
	}

}