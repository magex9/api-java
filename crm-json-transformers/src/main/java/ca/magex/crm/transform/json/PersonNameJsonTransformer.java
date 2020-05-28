package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.lookup.Salutation;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;

public class PersonNameJsonTransformer extends AbstractJsonTransformer<PersonName> {

	public PersonNameJsonTransformer(CrmServices crm) {
		super(crm);
	}

	@Override
	public Class<PersonName> getType() {
		return PersonName.class;
	}
	
	@Override
	public JsonObject formatRoot(PersonName name) {
		return formatLocalized(name, null);
	}
	
	@Override
	public JsonObject formatLocalized(PersonName name, Locale locale) {
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		formatType(pairs);
		if (name.getSalutation() != null) {
			pairs.add(new JsonPair("salutation", new SalutationJsonTransformer(crm)
				.format(crm.findSalutationByCode(name.getSalutation()), locale)));
		}
		formatText(pairs, "firstName", name);
		formatText(pairs, "middleName", name);
		formatText(pairs, "lastName", name);
		return new JsonObject(pairs);
	}

	@Override
	public PersonName parseJsonObject(JsonObject json, Locale locale) {
		String salutation = parseObject("salutation", json, Salutation.class, SalutationJsonTransformer.class, locale).getCode();
		String firstName = parseText("firstName", json);
		String middleName = parseText("middleName", json);
		String lastName = parseText("lastName", json);
		return new PersonName(salutation, firstName, middleName, lastName);
	}

}
