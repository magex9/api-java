package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Choice;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.CountryIdentifier;
import ca.magex.crm.api.system.id.ProvinceIdentifier;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;

@Component
public class MailingAddressJsonTransformer extends AbstractJsonTransformer<MailingAddress> {
	
	public MailingAddressJsonTransformer(CrmServices crm) {
		super(crm);
	}

	@Override
	public Class<MailingAddress> getSourceType() {
		return MailingAddress.class;
	}
	
	@Override
	public JsonObject formatRoot(MailingAddress name) {
		return formatLocalized(name, null);
	}
	
	@Override
	public JsonObject formatLocalized(MailingAddress address, Locale locale) {
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		formatType(pairs, locale);
		formatText(pairs, "street", address);
		formatText(pairs, "city", address);
		formatChoice(pairs, "province", address, ProvinceIdentifier.class, locale);
		formatChoice(pairs, "country", address, CountryIdentifier.class, locale);
		formatText(pairs, "postalCode", address);
		return new JsonObject(pairs);
	}
	
	@Override
	public MailingAddress parseJsonObject(JsonObject json, Locale locale) {
		String street = parseText("street", json);
		String city = parseText("city", json);
		Choice<ProvinceIdentifier> province = parseChoice("province", json, Type.PROVINCE, locale);
		Choice<CountryIdentifier> country = parseChoice("country", json, Type.COUNTRY, locale);
		String postalCode = parseText("postalCode", json);
		return new MailingAddress(street, city, province, country, postalCode);
	}
	
}
