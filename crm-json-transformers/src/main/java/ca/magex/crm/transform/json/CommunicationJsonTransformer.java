package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.Telephone;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Choice;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.LanguageIdentifier;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;

@Component
public class CommunicationJsonTransformer extends AbstractJsonTransformer<Communication> {
	
	public CommunicationJsonTransformer(CrmServices crm) {
		super(crm);
	}

	@Override
	public Class<Communication> getSourceType() {
		return Communication.class;
	}
	
	@Override
	public JsonObject formatRoot(Communication communication) {
		return formatLocalized(communication, null);
	}
	
	@Override
	public JsonObject formatLocalized(Communication communication, Locale locale) {
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		formatType(pairs, locale);
		formatText(pairs, "jobTitle", communication);
		formatChoice(pairs, "language", communication, Type.LANGUAGE, locale);
		formatText(pairs, "email", communication);
		formatTransformer(pairs, "homePhone", communication, new TelephoneJsonTransformer(crm), locale);
		formatText(pairs, "faxNumber", communication);
		return new JsonObject(pairs);
	}

	@Override
	public Communication parseJsonObject(JsonObject json, Locale locale) {
		String jobTitle = parseText("jobTitle", json);
		Choice<LanguageIdentifier> language = parseChoice("language", json, Type.LANGUAGE, locale);
		String email = parseText("email", json);
		Telephone homePhone = parseObject("homePhone", json, new TelephoneJsonTransformer(crm), locale);
		String faxNumber = parseText("faxNumber", json);
		return new Communication(jobTitle, language, email, homePhone, faxNumber);
	}

}
