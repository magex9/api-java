package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;

public class LocationSummaryJsonTransformer extends AbstractJsonTransformer<LocationSummary> {

	public LocationSummaryJsonTransformer(CrmServices crm) {
		super(crm);
	}

	@Override
	public Class<LocationSummary> getType() {
		return LocationSummary.class;
	}
	
	@Override
	public JsonObject formatRoot(LocationSummary location) {
		return formatLocalized(location, null);
	}
	
	@Override
	public JsonObject formatLocalized(LocationSummary location, Locale locale) {
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		formatType(pairs);
		if (location.getLocationId() != null) {
			pairs.add(new JsonPair("locationId", new IdentifierJsonTransformer(crm)
				.format(location.getLocationId(), locale)));
		}
		if (location.getOrganizationId() != null) {
			pairs.add(new JsonPair("organizationId", new IdentifierJsonTransformer(crm)
				.format(location.getOrganizationId(), locale)));
		}
		if (location.getStatus() != null) {
			pairs.add(new JsonPair("status", new StatusJsonTransformer(crm)
				.format(location.getStatus(), locale)));
		}
		formatText(pairs, "reference", location);
		formatText(pairs, "displayName", location);
		return new JsonObject(pairs);
	}

	@Override
	public LocationSummary parseJsonObject(JsonObject json, Locale locale) {
		Identifier locationId = parseObject("locationId", json, Identifier.class, IdentifierJsonTransformer.class, locale);
		Identifier organizationId = parseObject("organizationId", json, Identifier.class, IdentifierJsonTransformer.class, locale);
		Status status = parseObject("status", json, Status.class, StatusJsonTransformer.class, locale);
		String reference = parseText("reference", json);
		String displayName = parseText("displayName", json);
		return new LocationSummary(locationId, organizationId, status, reference, displayName);
	}

}
