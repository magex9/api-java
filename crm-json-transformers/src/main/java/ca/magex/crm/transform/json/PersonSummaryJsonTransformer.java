package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;

public class PersonSummaryJsonTransformer extends AbstractJsonTransformer<PersonSummary> {

	public PersonSummaryJsonTransformer(CrmServices crm) {
		super(crm);
	}

	@Override
	public Class<PersonSummary> getType() {
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
			pairs.add(new JsonPair("personId", new IdentifierJsonTransformer(crm)
				.format(person.getPersonId(), locale)));
		}
		if (person.getOrganizationId() != null) {
			pairs.add(new JsonPair("organizationId", new IdentifierJsonTransformer(crm)
				.format(person.getOrganizationId(), locale)));
		}
		if (person.getStatus() != null) {
			pairs.add(new JsonPair("status", new StatusJsonTransformer(crm)
				.format(person.getStatus(), locale)));
		}
		formatText(pairs, "displayName", person);
		return new JsonObject(pairs);
	}

	@Override
	public PersonSummary parseJsonObject(JsonObject json, Locale locale) {
		Identifier personId = parseObject("personId", json, Identifier.class, IdentifierJsonTransformer.class, locale);
		Identifier organizationId = parseObject("organizationId", json, Identifier.class, IdentifierJsonTransformer.class, locale);
		Status status = parseObject("status", json, Status.class, StatusJsonTransformer.class, locale);
		String displayName = parseText("displayName", json);
		return new PersonSummary(personId, organizationId, status, displayName);
	}

}
