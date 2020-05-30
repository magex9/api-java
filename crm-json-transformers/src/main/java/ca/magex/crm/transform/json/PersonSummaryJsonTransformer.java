package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;

@Component
public class PersonSummaryJsonTransformer extends AbstractJsonTransformer<PersonSummary> {

	private IdentifierJsonTransformer identifierJsonTransformer;
	
	private StatusJsonTransformer statusJsonTransformer;

	public PersonSummaryJsonTransformer(CrmServices crm, IdentifierJsonTransformer identifierJsonTransformer,
			StatusJsonTransformer statusJsonTransformer) {
		super(crm);
		this.identifierJsonTransformer = identifierJsonTransformer;
		this.statusJsonTransformer = statusJsonTransformer;
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
		formatType(pairs);
		if (person.getPersonId() != null) {
			pairs.add(new JsonPair("personId", identifierJsonTransformer
				.format(person.getPersonId(), locale)));
		}
		if (person.getOrganizationId() != null) {
			pairs.add(new JsonPair("organizationId", identifierJsonTransformer
				.format(person.getOrganizationId(), locale)));
		}
		if (person.getStatus() != null) {
			pairs.add(new JsonPair("status", statusJsonTransformer
				.format(person.getStatus(), locale)));
		}
		formatText(pairs, "displayName", person);
		return new JsonObject(pairs);
	}

	@Override
	public PersonSummary parseJsonObject(JsonObject json, Locale locale) {
		Identifier personId = parseObject("personId", json, identifierJsonTransformer, locale);
		Identifier organizationId = parseObject("organizationId", json, identifierJsonTransformer, locale);
		Status status = parseObject("status", json, statusJsonTransformer, locale);
		String displayName = parseText("displayName", json);
		return new PersonSummary(personId, organizationId, status, displayName);
	}

}
