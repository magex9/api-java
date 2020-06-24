package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.Telephone;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Option;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;

@Component
public class CommunicationJsonTransformer extends AbstractJsonTransformer<Communication> {
	
	private OptionJsonTransformer optionJsonTransformer;
	
	private TelephoneJsonTransformer telephoneJsonTransformer;

	public CommunicationJsonTransformer(CrmServices crm) {
		super(crm);
		this.optionJsonTransformer = new OptionJsonTransformer(crm);
		this.telephoneJsonTransformer = new TelephoneJsonTransformer(crm);
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
		formatType(pairs);
		formatText(pairs, "jobTitle", communication);
		if (communication.getLanguage() != null) {
			Option option = crm.findOptionByCode(crm.findLookupByCode(Crm.LANGUAGE).getLookupId(), communication.getLanguage());
			pairs.add(new JsonPair("language", optionJsonTransformer.format(option, locale)));
		}
		formatText(pairs, "email", communication);
		if (communication.getHomePhone() != null) {
			pairs.add(new JsonPair("homePhone", telephoneJsonTransformer
				.format(communication.getHomePhone(), locale)));
		}
		formatText(pairs, "faxNumber", communication);
		return new JsonObject(pairs);
	}

	@Override
	public Communication parseJsonObject(JsonObject json, Locale locale) {
		String jobTitle = parseText("jobTitle", json);
		String language = parseOption("language", json, Crm.LANGUAGE, locale).getCode();
		String email = parseText("email", json);
		Telephone homePhone = parseObject("homePhone", json, telephoneJsonTransformer, locale);
		String faxNumber = parseText("faxNumber", json);
		return new Communication(jobTitle, language, email, homePhone, faxNumber);
	}

}
