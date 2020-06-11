package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;

@Component
public class PersonNameJsonTransformer extends AbstractJsonTransformer<PersonName> {
	
	private SalutationJsonTransformer salutationJsonTransformer;

	public PersonNameJsonTransformer(CrmServices crm) {
		super(crm);
		this.salutationJsonTransformer = new SalutationJsonTransformer(crm);
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
		formatType(pairs);
		if (name.getSalutation() != null) {
			pairs.add(new JsonPair("salutation", salutationJsonTransformer
				.format(crm.findSalutationByCode(name.getSalutation()), locale)));
		}
		formatText(pairs, "firstName", name);
		formatText(pairs, "middleName", name);
		formatText(pairs, "lastName", name);
		return new JsonObject(pairs);
	}

	@Override
	public PersonName parseJsonObject(JsonObject json, Locale locale) {
		String salutation = null;
		try {
			salutation = parseObject("salutation", json, salutationJsonTransformer, locale).getCode();
		} catch (NoSuchElementException e) { }
		String firstName = parseText("firstName", json);
		String middleName = parseText("middleName", json);
		String lastName = parseText("lastName", json);
		return new PersonName(salutation, firstName, middleName, lastName);
	}

}
