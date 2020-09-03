package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.services.CrmOptionService;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;

public class PersonSummaryJsonTransformer extends AbstractJsonTransformer<PersonSummary> {

	public PersonSummaryJsonTransformer(CrmOptionService crm) {
		super(crm);
	}

	@Override
	public Class<PersonSummary> getSourceType() {
		return PersonSummary.class;
	}
	
	@Override
	public JsonObject formatRoot(PersonSummary person) {
		return formatLocalized(person, null);
	}
	
	@Override
	public JsonObject formatLocalized(PersonSummary person, Locale locale) {
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		formatType(pairs, locale);
		formatIdentifier(pairs, "personId", person, PersonIdentifier.class, locale);
		formatIdentifier(pairs, "organizationId", person, OrganizationIdentifier.class, locale);
		formatStatus(pairs, "status", person, locale);
		formatText(pairs, "displayName", person);
		formatLastModified(pairs, person);
		return new JsonObject(pairs);
	}

	@Override
	public PersonSummary parseJsonObject(JsonObject json, Locale locale) {
		PersonIdentifier personId = parseIdentifier("personId", json, PersonIdentifier.class, locale);
		OrganizationIdentifier organizationId = parseIdentifier("organizationId", json, OrganizationIdentifier.class, locale);
		Status status = parseObject("status", json, new StatusJsonTransformer(crm), locale);
		String displayName = parseText("displayName", json);
		Long lastModified = parseLastModified(json);
		return new PersonSummary(personId, organizationId, status, displayName, lastModified);
	}

}
