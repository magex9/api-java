package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.services.CrmOptionService;
import ca.magex.crm.api.system.Choice;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.SalutationIdentifier;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;

public class PersonNameJsonTransformer extends AbstractJsonTransformer<PersonName> {
	
	public PersonNameJsonTransformer(CrmOptionService crm) {
		super(crm);
	}

	@Override
	public Class<PersonName> getSourceType() {
		return PersonName.class;
	}
	
	@Override
	public JsonObject formatRoot(PersonName name) {
		return formatLocalized(name, null);
	}
	
	@Override
	public JsonObject formatLocalized(PersonName name, Locale locale) {
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		formatType(pairs, locale);
		formatChoice(pairs, "salutation", name, SalutationIdentifier.class, locale);
		formatText(pairs, "firstName", name);
		formatText(pairs, "middleName", name);
		formatText(pairs, "lastName", name);
		return new JsonObject(pairs);
	}

	@Override
	public PersonName parseJsonObject(JsonObject json, Locale locale) {
		Choice<SalutationIdentifier> salutation = parseChoice("salutation", json, Type.SALUTATION, locale);
		String firstName = parseText("firstName", json);
		String middleName = parseText("middleName", json);
		String lastName = parseText("lastName", json);
		return new PersonName(salutation, firstName, middleName, lastName);
	}

}
