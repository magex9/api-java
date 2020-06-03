package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;

@Component
public class LocationSummaryJsonTransformer extends AbstractJsonTransformer<LocationSummary> {

	private IdentifierJsonTransformer identifierJsonTransformer;
	
	private StatusJsonTransformer statusJsonTransformer;
	
	public LocationSummaryJsonTransformer(CrmServices crm) {
		super(crm);
		this.identifierJsonTransformer = new IdentifierJsonTransformer(crm);
		this.statusJsonTransformer = new StatusJsonTransformer(crm);
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
		formatType(pairs);
		if (location.getLocationId() != null) {
			pairs.add(new JsonPair("locationId", identifierJsonTransformer
				.format(location.getLocationId(), locale)));
		}
		if (location.getOrganizationId() != null) {
			pairs.add(new JsonPair("organizationId", identifierJsonTransformer
				.format(location.getOrganizationId(), locale)));
		}
		if (location.getStatus() != null) {
			pairs.add(new JsonPair("status", statusJsonTransformer
				.format(location.getStatus(), locale)));
		}
		formatText(pairs, "reference", location);
		formatText(pairs, "displayName", location);
		return new JsonObject(pairs);
	}

	@Override
	public LocationSummary parseJsonObject(JsonObject json, Locale locale) {
		Identifier locationId = parseObject("locationId", json, identifierJsonTransformer, locale);
		Identifier organizationId = parseObject("organizationId", json, identifierJsonTransformer, locale);
		Status status = parseObject("status", json, statusJsonTransformer, locale);
		String reference = parseText("reference", json);
		String displayName = parseText("displayName", json);
		return new LocationSummary(locationId, organizationId, status, reference, displayName);
	}

}
