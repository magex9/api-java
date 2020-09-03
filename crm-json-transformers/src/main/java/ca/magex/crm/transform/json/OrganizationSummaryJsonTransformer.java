package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.services.CrmOptionService;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;

public class OrganizationSummaryJsonTransformer extends AbstractJsonTransformer<OrganizationSummary> {

	public OrganizationSummaryJsonTransformer(CrmOptionService crm) {
		super(crm);
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
		formatType(pairs, locale);
		formatIdentifier(pairs, "organizationId", organization, OrganizationIdentifier.class, locale);
		formatStatus(pairs, "status", organization, locale);
		formatText(pairs, "displayName", organization);
		formatLastModified(pairs, organization);
		return new JsonObject(pairs);
	}

	@Override
	public OrganizationSummary parseJsonObject(JsonObject json, Locale locale) {
		OrganizationIdentifier organizationId = parseIdentifier("organizationId", json, OrganizationIdentifier.class, locale);
		Status status = parseObject("status", json, new StatusJsonTransformer(crm), locale);
		String displayName = parseText("displayName", json);
		Long lastModified = parseLastModified(json);
		return new OrganizationSummary(organizationId, status, displayName, lastModified);
	}

}
