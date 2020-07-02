package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;

@Component
public class LocationSummaryJsonTransformer extends AbstractJsonTransformer<LocationSummary> {
	
	public LocationSummaryJsonTransformer(CrmServices crm) {
		super(crm);
	}

	@Override
	public Class<LocationSummary> getSourceType() {
		return LocationSummary.class;
	}
	
	@Override
	public JsonObject formatRoot(LocationSummary location) {
		return formatLocalized(location, null);
	}
	
	@Override
	public JsonObject formatLocalized(LocationSummary location, Locale locale) {
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		formatType(pairs, locale);
		formatIdentifier(pairs, "locationId", location, LocationIdentifier.class, locale);
		formatIdentifier(pairs, "organizationId", location, OrganizationIdentifier.class, locale);
		formatStatus(pairs, "status", location, locale);
		formatText(pairs, "reference", location);
		formatText(pairs, "displayName", location);
		return new JsonObject(pairs);
	}

	@Override
	public LocationSummary parseJsonObject(JsonObject json, Locale locale) {
		LocationIdentifier locationId = parseIdentifier("locationId", json, LocationIdentifier.class, locale);
		OrganizationIdentifier organizationId = parseIdentifier("organizationId", json, OrganizationIdentifier.class, locale);
		Status status = parseObject("status", json, new StatusJsonTransformer(crm), locale);
		String reference = parseText("reference", json);
		String displayName = parseText("displayName", json);
		return new LocationSummary(locationId, organizationId, status, reference, displayName);
	}

}
