package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;

@Component
public class LocationDetailsJsonTransformer extends AbstractJsonTransformer<LocationDetails> {

	private IdentifierJsonTransformer identifierJsonTransformer;
	
	private StatusJsonTransformer statusJsonTransformer;
	
	private MailingAddressJsonTransformer mailingAddressJsonTransformer;

	public LocationDetailsJsonTransformer(CrmServices crm) {
		super(crm);
		this.identifierJsonTransformer = new IdentifierJsonTransformer(crm);
		this.statusJsonTransformer = new StatusJsonTransformer(crm);
		this.mailingAddressJsonTransformer = new MailingAddressJsonTransformer(crm);
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
		formatType(pairs);
		formatIdentifier(pairs, "locationId", location, locale);
		formatIdentifier(pairs, "organizationId", location, locale);
		if (location.getStatus() != null) {
			pairs.add(new JsonPair("status", statusJsonTransformer
				.format(location.getStatus(), locale)));
		}
		formatText(pairs, "reference", location);
		formatText(pairs, "displayName", location);
		if (location.getAddress() != null) {
			pairs.add(new JsonPair("address", mailingAddressJsonTransformer
				.format(location.getAddress(), locale)));
		}
		return new JsonObject(pairs);
	}

	@Override
	public LocationDetails parseJsonObject(JsonObject json, Locale locale) {
		Identifier locationId = parseObject("locationId", json, identifierJsonTransformer, locale);
		Identifier organizationId = parseObject("organizationId", json, identifierJsonTransformer, locale);
		Status status = parseObject("status", json, statusJsonTransformer, locale);
		String reference = parseText("reference", json);
		String displayName = parseText("displayName", json);
		MailingAddress address = parseObject("address", json, mailingAddressJsonTransformer, locale);
		return new LocationDetails(locationId, organizationId, status, reference, displayName, address);
	}

}
