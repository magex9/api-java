package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.OptionIdentifier;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;

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
		return formatLocalized(option, null);
	}
	
	@Override
	public JsonElement formatLocalized(Option option, Locale locale) {
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		formatType(pairs, locale);
		if (locale == null)
			pairs.add(new JsonPair("@id", buildContext(option, true, null) + "/" + option.getCode().replaceAll("_", "-").toLowerCase()));
		formatIdentifier(pairs, "optionId", option, OptionIdentifier.class, locale);
		formatIdentifier(pairs, "parentId", option, OptionIdentifier.class, locale);
		formatTransformer(pairs, "type", option, new TypeJsonTransformer(crm), locale);
		formatStatus(pairs, "status", option, locale);
		formatBoolean(pairs, "mutable", option);
		formatLocalized(pairs, "name", option, locale);
		return new JsonObject(pairs);
	}

	@Override
	public Option parseJsonObject(JsonObject json, Locale locale) {
		OptionIdentifier optionId = parseIdentifier("optionId", json, OptionIdentifier.class, locale);
		OptionIdentifier parentId = parseIdentifier("parentId", json, OptionIdentifier.class, locale);
		Type type = null;
		Boolean mutable = false;
		Status status = parseObject("status", json, new StatusJsonTransformer(crm), locale);
		Localized name = parseObject("name", json, new LocalizedJsonTransformer(crm), locale);
		return new Option(optionId, parentId, type, status, mutable, name);
	}

}
