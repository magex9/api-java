package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.AuthenticationGroupIdentifier;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;
import ca.magex.json.model.JsonText;

@Component
public class OrganizationDetailsJsonTransformer extends AbstractJsonTransformer<OrganizationDetails> {

	public OrganizationDetailsJsonTransformer(CrmServices crm) {
		super(crm);
	}

	@Override
	public Class<OrganizationDetails> getSourceType() {
		return OrganizationDetails.class;
	}
	
	@Override
	public JsonObject formatRoot(OrganizationDetails organization) {
		return formatLocalized(organization, null);
	}
	
	@Override
	public JsonObject formatLocalized(OrganizationDetails organization, Locale locale) {
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		formatType(pairs);
		formatIdentifier(pairs, "organizationId", organization, locale);
		formatStatus(pairs, "status", organization, locale);
		formatText(pairs, "displayName", organization);
		formatIdentifier(pairs, "mainLocationId", organization, locale);
		formatIdentifier(pairs, "getMainContactId", organization, locale);
		formatObjects(pairs, "groups", organization, Identifier.class);
		return new JsonObject(pairs);
	}

	@Override
	public OrganizationDetails parseJsonObject(JsonObject json, Locale locale) {
		OrganizationIdentifier organizationId = parseIdentifier("organizationId", json, OrganizationIdentifier.class, locale);
		Status status = parseObject("status", json, new StatusJsonTransformer(crm), locale);
		String displayName = parseText("displayName", json);
		LocationIdentifier mainLocationId = parseIdentifier("mainLocationId", json, LocationIdentifier.class, locale);
		PersonIdentifier mainContactId = parseIdentifier("mainContactId", json, PersonIdentifier.class, locale);
		List<AuthenticationGroupIdentifier> groups = json.getArray("groups").stream().map(e -> new AuthenticationGroupIdentifier(((JsonText)e).value())).collect(Collectors.toList());
		return new OrganizationDetails(organizationId, status, displayName, mainLocationId, mainContactId, groups);
	}

}
