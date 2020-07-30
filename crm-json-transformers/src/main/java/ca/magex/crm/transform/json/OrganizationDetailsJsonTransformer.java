package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.services.CrmOptionService;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.AuthenticationGroupIdentifier;
import ca.magex.crm.api.system.id.BusinessGroupIdentifier;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;

@Component
public class OrganizationDetailsJsonTransformer extends AbstractJsonTransformer<OrganizationDetails> {

	public OrganizationDetailsJsonTransformer(CrmOptionService crm) {
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
		formatType(pairs, locale);
		formatIdentifier(pairs, "organizationId", organization, OrganizationIdentifier.class, locale);
		formatStatus(pairs, "status", organization, locale);
		formatText(pairs, "displayName", organization);
		formatIdentifier(pairs, "mainLocationId", organization, LocationIdentifier.class, locale);
		formatIdentifier(pairs, "mainContactId", organization, PersonIdentifier.class, locale);
		formatOptions(pairs, "authenticationGroupIds", organization, Type.AUTHENTICATION_GROUP, locale);
		formatOptions(pairs, "businessGroupIds", organization, Type.BUSINESS_GROUP, locale);
		return new JsonObject(pairs);
	}

	@Override
	public OrganizationDetails parseJsonObject(JsonObject json, Locale locale) {
		OrganizationIdentifier organizationId = parseIdentifier("organizationId", json, OrganizationIdentifier.class, locale);
		Status status = parseObject("status", json, new StatusJsonTransformer(crm), locale);
		String displayName = parseText("displayName", json);
		LocationIdentifier mainLocationId = parseIdentifier("mainLocationId", json, LocationIdentifier.class, locale);
		PersonIdentifier mainContactId = parseIdentifier("mainContactId", json, PersonIdentifier.class, locale);
		List<AuthenticationGroupIdentifier> authenticationGroups = parseOptions("authenticationGroupIds", json, AuthenticationGroupIdentifier.class, locale);
		List<BusinessGroupIdentifier> businessGroups = parseOptions("businessGroupIds", json, BusinessGroupIdentifier.class, locale);
		return new OrganizationDetails(organizationId, status, displayName, mainLocationId, mainContactId, authenticationGroups, businessGroups);
	}
	
}
