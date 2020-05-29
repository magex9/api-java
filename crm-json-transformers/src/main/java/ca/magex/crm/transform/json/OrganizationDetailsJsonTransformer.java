package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;
import ca.magex.json.model.JsonText;

public class OrganizationDetailsJsonTransformer extends AbstractJsonTransformer<OrganizationDetails> {

	public OrganizationDetailsJsonTransformer(CrmServices crm) {
		super(crm);
	}

	@Override
	public Class<OrganizationDetails> getType() {
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
		if (organization.getOrganizationId() != null) {
			pairs.add(new JsonPair("organizationId", new IdentifierJsonTransformer(crm)
				.format(organization.getOrganizationId(), locale)));
		}
		if (organization.getStatus() != null) {
			pairs.add(new JsonPair("status", new StatusJsonTransformer(crm)
				.format(organization.getStatus(), locale)));
		}
		formatText(pairs, "displayName", organization);
		if (organization.getMainLocationId() != null) {
			pairs.add(new JsonPair("mainLocationId", new IdentifierJsonTransformer(crm)
				.format(organization.getMainLocationId(), locale)));
		}
		if (organization.getMainContactId() != null) {
			pairs.add(new JsonPair("mainContactId", new IdentifierJsonTransformer(crm)
				.format(organization.getMainContactId(), locale)));
		}
		formatTexts(pairs, "groups", organization, String.class);
		return new JsonObject(pairs);
	}

	@Override
	public OrganizationDetails parseJsonObject(JsonObject json, Locale locale) {
		Identifier organizationId = parseObject("organizationId", json, Identifier.class, IdentifierJsonTransformer.class, locale);
		Status status = parseObject("status", json, Status.class, StatusJsonTransformer.class, locale);
		String displayName = parseText("displayName", json);
		Identifier mainLocationId = parseObject("mainLocationId", json, Identifier.class, IdentifierJsonTransformer.class, locale);
		Identifier mainContactId = parseObject("mainContactId", json, Identifier.class, IdentifierJsonTransformer.class, locale);
		List<String> groups = json.getArray("groups").stream().map(e -> ((JsonText)e).value()).collect(Collectors.toList());
		return new OrganizationDetails(organizationId, status, displayName, mainLocationId, mainContactId, groups);
	}

}
