package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Lookup;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Status;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;
import ca.magex.json.model.JsonText;

@Component
public class OptionJsonTransformer extends AbstractJsonTransformer<Option> {

	public OptionJsonTransformer(CrmServices crm) {
		super(crm);
	}

	@Override
	public Class<Option> getSourceType() {
		return Option.class;
	}
	
	@Override
	public JsonElement formatRoot(Option option) {
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		pairs.add(new JsonPair("@context", buildContext(option)));
		pairs.add(new JsonPair("@value", option.getCode()));
		pairs.add(new JsonPair("@en", option.getName(Lang.ENGLISH)));
		pairs.add(new JsonPair("@fr", option.getName(Lang.FRENCH)));
		return new JsonObject(pairs);
	}
	
	public String buildContext(Option option) {
		Lookup lookup = crm.findLookup(option.getLookupId());
		StringBuilder sb = new StringBuilder();
		sb.append("http://magex.ca/crm/lookups/");
		sb.append(lookup.getCode());
		if (lookup.getParent() != null) {
			sb.append("/");
			sb.append(lookup.getParent().getCode());
		}
		return sb.toString();
	}
	
	@Override
	public JsonElement formatLocalized(Option option, Locale locale) {
		return new JsonText(option.getName(locale));
	}

	@Override
	public Option parseJsonObject(JsonObject json, Locale locale) {
		Identifier optionId = parseObject("optionId", json, new IdentifierJsonTransformer(crm), locale);
		Identifier lookupId = parseObject("lookupId", json, new IdentifierJsonTransformer(crm), locale);
		Status status = parseObject("status", json, new StatusJsonTransformer(crm), locale);
		Localized name = parseObject("name", json, new LocalizedJsonTransformer(crm), locale);
		return new Option(optionId, lookupId, status, name);
	}

}
