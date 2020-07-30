package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.services.CrmOptionService;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;

@Component
public class LocationDetailsJsonTransformer extends AbstractJsonTransformer<LocationDetails> {

	public LocationDetailsJsonTransformer(CrmOptionService crm) {
		super(crm);
	}

	@Override
	public Class<LocationDetails> getSourceType() {
		return LocationDetails.class;
	}
	
	@Override
	public JsonObject formatRoot(LocationDetails location) {
		return formatLocalized(location, null);
	}
	
	@Override
	public JsonObject formatLocalized(LocationDetails location, Locale locale) {
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		formatType(pairs, locale);
		formatIdentifier(pairs, "locationId", location, LocationIdentifier.class, locale);
		formatIdentifier(pairs, "organizationId", location, OrganizationIdentifier.class, locale);
		formatStatus(pairs, "status", location, locale);
		formatText(pairs, "reference", location);
		formatText(pairs, "displayName", location);
		formatTransformer(pairs, "address", location, new MailingAddressJsonTransformer(crm), locale);
		return new JsonObject(pairs);
	}

	@Override
	public LocationDetails parseJsonObject(JsonObject json, Locale locale) {
		LocationIdentifier locationId = parseIdentifier("locationId", json, LocationIdentifier.class, locale);
		OrganizationIdentifier organizationId = parseIdentifier("organizationId", json, OrganizationIdentifier.class, locale);
		Status status = parseObject("status", json, new StatusJsonTransformer(crm), locale);
		String reference = parseText("reference", json);
		String displayName = parseText("displayName", json);
		MailingAddress address = parseObject("address", json, new MailingAddressJsonTransformer(crm), locale);
		return new LocationDetails(locationId, organizationId, status, reference, displayName, address);
	}

}
