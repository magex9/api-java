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
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;
import ca.magex.json.model.JsonText;

@Component
public class OrganizationDetailsJsonTransformer extends AbstractJsonTransformer<OrganizationDetails> {

	private IdentifierJsonTransformer identifierJsonTransformer;
	
	private StatusJsonTransformer statusJsonTransformer;
	
	public OrganizationDetailsJsonTransformer(CrmServices crm, IdentifierJsonTransformer identifierJsonTransformer,
			StatusJsonTransformer statusJsonTransformer) {
		super(crm);
		this.identifierJsonTransformer = identifierJsonTransformer;
		this.statusJsonTransformer = statusJsonTransformer;
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
		if (organization.getOrganizationId() != null) {
			pairs.add(new JsonPair("organizationId", identifierJsonTransformer
				.format(organization.getOrganizationId(), locale)));
		}
		if (organization.getStatus() != null) {
			pairs.add(new JsonPair("status", statusJsonTransformer
				.format(organization.getStatus(), locale)));
		}
		formatText(pairs, "displayName", organization);
		if (organization.getMainLocationId() != null) {
			pairs.add(new JsonPair("mainLocationId", identifierJsonTransformer
				.format(organization.getMainLocationId(), locale)));
		}
		if (organization.getMainContactId() != null) {
			pairs.add(new JsonPair("mainContactId", identifierJsonTransformer
				.format(organization.getMainContactId(), locale)));
		}
		formatTexts(pairs, "groups", organization, String.class);
		return new JsonObject(pairs);
	}

	@Override
	public OrganizationDetails parseJsonObject(JsonObject json, Locale locale) {
		Identifier organizationId = parseObject("organizationId", json, identifierJsonTransformer, locale);
		Status status = parseObject("status", json, statusJsonTransformer, locale);
		String displayName = parseText("displayName", json);
		Identifier mainLocationId = parseObject("mainLocationId", json, identifierJsonTransformer, locale);
		Identifier mainContactId = parseObject("mainContactId", json, identifierJsonTransformer, locale);
		List<String> groups = json.getArray("groups").stream().map(e -> ((JsonText)e).value()).collect(Collectors.toList());
		return new OrganizationDetails(organizationId, status, displayName, mainLocationId, mainContactId, groups);
	}

}
