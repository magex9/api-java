package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;

public class LocationDetailsJsonTransformer extends AbstractJsonTransformer<LocationDetails> {

	public LocationDetailsJsonTransformer(CrmServices crm) {
		super(crm);
	}

	@Override
	public Class<LocationDetails> getType() {
		return LocationDetails.class;
	}
	
	@Override
	public JsonObject formatRoot(LocationDetails location) {
		return formatLocalized(location, null);
	}
	
	@Override
	public JsonObject formatLocalized(LocationDetails location, Locale locale) {
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
		if (location.getAddress() != null) {
			pairs.add(new JsonPair("address", new MailingAddressJsonTransformer(crm)
				.format(location.getAddress(), locale)));
		}
		return new JsonObject(pairs);
	}

	@Override
	public LocationDetails parseJsonObject(JsonObject json, Locale locale) {
		Identifier locationId = parseObject("locationId", json, Identifier.class, IdentifierJsonTransformer.class, locale);
		Identifier organizationId = parseObject("organizationId", json, Identifier.class, IdentifierJsonTransformer.class, locale);
		Status status = parseObject("status", json, Status.class, StatusJsonTransformer.class, locale);
		String reference = parseText("reference", json);
		String displayName = parseText("displayName", json);
		MailingAddress address = parseObject("address", json, MailingAddress.class, MailingAddressJsonTransformer.class, locale);
		return new LocationDetails(locationId, organizationId, status, reference, displayName, address);
	}

}
