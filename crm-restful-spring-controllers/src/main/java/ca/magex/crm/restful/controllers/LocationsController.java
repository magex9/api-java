package ca.magex.crm.restful.controllers;
	
import org.springframework.stereotype.Controller;

@Controller
public class LocationsController extends AbstractCrmController {

//	@GetMapping("/rest/locations")
//	public void findLocations(HttpServletRequest req, HttpServletResponse res) throws IOException {
//		handle(req, res, LocationSummary.class, (messages, transformer, locale) -> { 
//			return createPage(
//				crm.findLocationSummaries(
//					extractLocationFilter(req, locale), 
//					extractPaging(LocationsFilter.getDefaultPaging(), req)
//				), transformer, locale
//			);
//		});
//	}
//	
////	private JsonArray formatLocationsActions(Identifier organizationId) {
////		List<JsonElement> actions = new ArrayList<JsonElement>();
////		if (crm.canCreateLocationForOrganization(organizationId)) {
////			actions.add(action("create", "Create Location", "post", "/rest/locations"));
////		}
////		return new JsonArray(actions);
////		
////	}
////	
////	private JsonArray formatLocationActions(Identifier locationId) {
////		List<JsonElement> actions = new ArrayList<JsonElement>();
////		if (crm.canUpdateLocation(locationId)) {
////			actions.add(action("edit", "Edit", "get", "/rest/locations/" + locationId + "/edit"));
////		} else if (crm.canViewLocation(locationId)) {
////			actions.add(action("view", "View", "get", "/rest/locations/" + locationId));
////		}
////		if (crm.canDisableLocation(locationId)) {
////			actions.add(action("disable", "Inactivate", "put", "/rest/locations/" + locationId + "/disable"));
////		}
////		if (crm.canEnableLocation(locationId)) {
////			actions.add(action("enable", "Activate", "put", "/rest/locations/" + locationId + "/enable"));
////		}
////		return new JsonArray(actions);
////	}
//	
//	public LocationsFilter extractLocationFilter(HttpServletRequest req, Locale locale) throws BadRequestException {
//		Identifier organizationId = req.getParameter("organization") == null ? null : new Identifier(req.getParameter("organization"));
//		String displayName = req.getParameter("displayName");
//		String reference = req.getParameter("reference");
//		Status status = req.getParameter("status") == null ? null : Status.valueOf(crm.findOptionByLocalizedName(Crm.STATUS, locale, req.getParameter("status")).getCode().toUpperCase());
//		return new LocationsFilter(organizationId, displayName, reference, status);
//	}
//
//	@PostMapping("/rest/locations")
//	public void createLocation(HttpServletRequest req, HttpServletResponse res) throws IOException {
//		handle(req, res, LocationDetails.class, (messages, transformer, locale) -> { 
//			JsonObject body = extractBody(req);
//			Identifier organizationId = getIdentifier(body, "organizationId", null, null, messages);
//			String displayName = getString(body, "displayName", "", null, messages);
//			String reference = getString(body, "reference", "", null, messages);
//			MailingAddress address = getObject(MailingAddress.class, body, "address", null, null, messages, locale);
//			validate(messages);
//			return transformer.format(crm.createLocation(organizationId, reference, displayName, address), locale);
//		});
//	}
//
//	@GetMapping("/rest/locations/{locationId}")
//	public void getLocation(HttpServletRequest req, HttpServletResponse res, 
//			@PathVariable("locationId") Identifier locationId) throws IOException {
//		handle(req, res, LocationDetails.class, (messages, transformer, locale) -> {
//			return transformer.format(crm.findLocationDetails(locationId), locale);
//		});
//	}
//
//	@PatchMapping("/rest/locations/{locationId}")
//	public void updateLocation(HttpServletRequest req, HttpServletResponse res, 
//			@PathVariable("locationId") Identifier locationId) throws IOException {
//		handle(req, res, LocationDetails.class, (messages, transformer, locale) -> {
//			JsonObject body = extractBody(req);
//			if (body.contains("displayName")) {
//				crm.updateLocationName(locationId, getString(body, "displayName", null, locationId, messages));
//			}
//			if (body.contains("address")) {
//				crm.updateLocationAddress(locationId, getObject(MailingAddress.class, body, "address", null, locationId, messages, locale));
//			}
//			validate(messages);
//			return transformer.format(crm.findLocationDetails(locationId), locale);
//		});
//	}
//
//	@GetMapping("/rest/locations/{locationId}/summary")
//	public void getLocationSummary(HttpServletRequest req, HttpServletResponse res, 
//			@PathVariable("locationId") Identifier locationId) throws IOException {
//		handle(req, res, LocationSummary.class, (messages, transformer, locale) -> {
//			return transformer.format(crm.findLocationDetails(locationId), locale);
//		});
//	}
//
//	@GetMapping("/rest/locations/{locationId}/address")
//	public void getLocationMainLocation(HttpServletRequest req, HttpServletResponse res, 
//			@PathVariable("locationId") Identifier locationId) throws IOException {
//		handle(req, res, MailingAddress.class, (messages, transformer, locale) -> {
//			return transformer.format(crm.findLocationDetails(locationId).getAddress(), locale);
//		});
//	}
//
//	@PutMapping("/rest/locations/{locationId}/enable")
//	public void enableOrganization(HttpServletRequest req, HttpServletResponse res, 
//			@PathVariable("locationId") Identifier locationId) throws IOException {
//		handle(req, res, LocationSummary.class, (messages, transformer, locale) -> {
//			confirm(extractBody(req), locationId, messages);
//			return transformer.format(crm.enableLocation(locationId), locale);
//		});
//	}
//
//	@PutMapping("/rest/locations/{locationId}/disable")
//	public void disableOrganization(HttpServletRequest req, HttpServletResponse res, 
//			@PathVariable("locationId") Identifier locationId) throws IOException {
//		handle(req, res, LocationSummary.class, (messages, transformer, locale) -> {
//			confirm(extractBody(req), locationId, messages);
//			return transformer.format(crm.disableLocation(locationId), locale);
//		});
//	}

}