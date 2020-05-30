package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;

@Component
public class OrganizationSummaryJsonTransformer extends AbstractJsonTransformer<OrganizationSummary> {

	private IdentifierJsonTransformer identifierJsonTransformer;
	
	private StatusJsonTransformer statusJsonTransformer;

	public OrganizationSummaryJsonTransformer(CrmServices crm, IdentifierJsonTransformer identifierJsonTransformer,
			StatusJsonTransformer statusJsonTransformer) {
		super(crm);
		this.identifierJsonTransformer = identifierJsonTransformer;
		this.statusJsonTransformer = statusJsonTransformer;
	}

	@Override
	public Class<OrganizationSummary> getSourceType() {
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
			pairs.add(new JsonPair("organizationId", identifierJsonTransformer
				.format(organization.getOrganizationId(), locale)));
		}
		if (organization.getStatus() != null) {
			pairs.add(new JsonPair("status", statusJsonTransformer
				.format(organization.getStatus(), locale)));
		}
		formatText(pairs, "displayName", organization);
		return new JsonObject(pairs);
	}

	@Override
	public OrganizationSummary parseJsonObject(JsonObject json, Locale locale) {
		Identifier organizationId = parseObject("organizationId", json, identifierJsonTransformer, locale);
		Status status = parseObject("status", json, statusJsonTransformer, locale);
		String displayName = parseText("displayName", json);
		return new OrganizationSummary(organizationId, status, displayName);
	}

}
