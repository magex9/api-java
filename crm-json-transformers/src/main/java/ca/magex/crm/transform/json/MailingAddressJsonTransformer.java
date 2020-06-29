package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Type;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;
import ca.magex.json.model.JsonText;

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
		formatType(pairs);
		formatText(pairs, "street", address);
		formatText(pairs, "city", address);
		formatProvince(pairs, "province", address, locale);
		formatOption(pairs, "country", address, Type.COUNTRY, locale);
		formatText(pairs, "postalCode", address);
		return new JsonObject(pairs);
	}
	
	public void formatProvince(List<JsonPair> parent, String key, MailingAddress address, Locale locale) {
		if (address.getProvince() == null) {
			return;
		} else if (address.getCountry() == null) {
			parent.add(new JsonPair(key, new JsonText(address.getProvince())));
			return;
		} else {
			try {
				Option country = crm.findOptionByCode(Type.COUNTRY, address.getCountry());
				Option province = crm.findOptions(crm.defaultOptionsFilter()
					.withType(Type.PROVINCE)
					.withOptionCode(address.getProvince())
					.withParentId(country.getOptionId())
				).getSingleItem();
				if (locale == null) {
					List<JsonPair> pairs = new ArrayList<JsonPair>();
					pairs.add(new JsonPair("@type", Type.PROVINCE.getCode()));
					pairs.add(new JsonPair("@value", province.getCode()));
					pairs.add(new JsonPair("@en", province.getName(Lang.ENGLISH)));
					pairs.add(new JsonPair("@fr", province.getName(Lang.FRENCH)));
					parent.add(new JsonPair(key, new JsonObject(pairs)));
				} else {
					parent.add(new JsonPair(key, new JsonText(province.getName(locale))));
				}
			} catch (IllegalArgumentException | ItemNotFoundException e) { 
				parent.add(new JsonPair(key, new JsonText(address.getProvince())));
			}
		}
	}

	@Override
	public MailingAddress parseJsonObject(JsonObject json, Locale locale) {
		String street = parseText("street", json);
		String city = parseText("city", json);
		String country = parseOption("country", json, Type.COUNTRY, locale);
		String province = parseProvince("province", "country", json, locale);
		String postalCode = parseText("postalCode", json);
		return new MailingAddress(street, city, province, country == null ? null : country, postalCode);
	}
	
	public String parseProvince(String provinceKey, String countryKey, JsonObject json, Locale locale) {
		if (json == null || !json.contains(provinceKey))
			return null;
		String country = parseOption("country", json, Type.COUNTRY, locale);
		if (country == null) {
			return json.getString(provinceKey);
		} else if (json.get(provinceKey) instanceof JsonText) {
			String province = ((JsonText)json.get(provinceKey)).value();
			try {
				return crm.findOptions(crm.defaultOptionsFilter()
					.withType(Type.PROVINCE)
					.withName(locale, province)
					.withParentId(crm.findOptionByCode(Type.COUNTRY, country).getOptionId())
				).getSingleItem().getCode();
			} catch (IllegalArgumentException | ItemNotFoundException e) {
				return province;
			}
		} else if (json.get(provinceKey) instanceof JsonObject) {
			String province = ((JsonObject)json.get(provinceKey)).getString("@value");
			try {
				return crm.findOptions(crm.defaultOptionsFilter()
					.withType(Type.PROVINCE)
					.withName(locale, province)
					.withParentId(crm.findOptionByCode(Type.COUNTRY, country).getOptionId())
				).getSingleItem().getCode();
			} catch (IllegalArgumentException | ItemNotFoundException e) {
				return province;
			}
		}
		return null;
	}

}
