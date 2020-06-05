package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.lookup.Province;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Lang;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;
import ca.magex.json.model.JsonText;

public class MailingAddressJsonTransformer extends AbstractJsonTransformer<MailingAddress> {

	public MailingAddressJsonTransformer(CrmServices crm) {
		super(crm);
	}

	@Override
	public Class<MailingAddress> getType() {
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
		if (address.getCountry() != null) {
			pairs.add(new JsonPair("country", new CountryJsonTransformer(crm)
				.format(crm.findCountryByCode(address.getCountry()), locale)));
		}
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
				Province province = crm.findProvinceByCode(address.getProvince(), address.getCountry());
				if (locale == null) {
					List<JsonPair> pairs = new ArrayList<JsonPair>();
					pairs.add(new JsonPair("@type", getType(Province.class)));
					pairs.add(new JsonPair("@value", province.getCode()));
					pairs.add(new JsonPair("@en", province.getName(Lang.ENGLISH)));
					pairs.add(new JsonPair("@fr", province.getName(Lang.FRENCH)));
					parent.add(new JsonPair(key, new JsonObject(pairs)));
				} else {
					parent.add(new JsonPair(key, new JsonText(province.get(locale))));
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
		Country country = parseCountry("country", json, locale);
		String province = parseProvince("province", "country", json, locale);
		String postalCode = parseText("postalCode", json);
		return new MailingAddress(street, city, province, country == null ? null : country.getCode(), postalCode);
	}
	
	public Country parseCountry(String key, JsonObject json, Locale locale) {
		if (!json.contains(key))
			return null;
		return parseObject("country", json, Country.class, CountryJsonTransformer.class, locale);
	}
	
	public String parseProvince(String provinceKey, String countryKey, JsonObject json, Locale locale) {
		if (json == null || !json.contains(provinceKey))
			return null;
		Country country = parseCountry("country", json, locale);
		if (country == null) {
			return json.getString(provinceKey);
		} else if (json.get(provinceKey) instanceof JsonText) {
			String province = ((JsonText)json.get(provinceKey)).value();
			try {
				return crm.findProvinceByLocalizedName(locale == null ? Lang.ROOT : locale, province, country.get(locale == null ? Lang.ROOT : locale)).getCode();
			} catch (IllegalArgumentException e) {
				return province;
			}
		} else if (json.get(provinceKey) instanceof JsonObject) {
			String province = ((JsonObject)json.get(provinceKey)).getString("@value");
			try {
				return crm.findProvinceByLocalizedName(locale == null ? Lang.ROOT : locale, province, country.get(locale == null ? Lang.ROOT : locale)).getCode();
			} catch (IllegalArgumentException e) {
				return province;
			}
		}
		return null;
	}

}
