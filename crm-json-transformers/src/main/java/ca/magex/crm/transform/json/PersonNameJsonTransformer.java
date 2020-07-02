package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Choice;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.SalutationIdentifier;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;

@Component
public class PersonNameJsonTransformer extends AbstractJsonTransformer<PersonName> {
	
	public PersonNameJsonTransformer(CrmServices crm) {
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
		formatChoice(pairs, "salutation", name, Type.SALUTATION, locale);
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
