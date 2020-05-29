package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;

public class OrganizationSummaryJsonTransformer extends AbstractJsonTransformer<OrganizationSummary> {

	public OrganizationSummaryJsonTransformer(CrmServices crm) {
		super(crm);
	}

	@Override
	public Class<OrganizationSummary> getType() {
		return OrganizationSummary.class;
	}
	
	@Override
	public JsonObject formatRoot(OrganizationSummary organization) {
		return formatLocalized(organization, null);
	}
	
	@Override
	public JsonObject formatLocalized(OrganizationSummary organization, Locale locale) {
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
		return new JsonObject(pairs);
	}

	@Override
	public OrganizationSummary parseJsonObject(JsonObject json, Locale locale) {
		Identifier organizationId = parseObject("organizationId", json, Identifier.class, IdentifierJsonTransformer.class, locale);
		Status status = parseObject("status", json, Status.class, StatusJsonTransformer.class, locale);
		String displayName = parseText("displayName", json);
		return new OrganizationSummary(organizationId, status, displayName);
	}

}
