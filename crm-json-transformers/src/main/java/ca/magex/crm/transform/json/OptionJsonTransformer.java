package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.services.CrmOptionService;
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
	
	public OptionJsonTransformer(CrmOptionService crm) {
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
		Type type = Type.of(json.get("type") instanceof JsonObject ? json.getObject("type").getString("@value") : json.getString("type"));
		OptionIdentifier optionId = parseOption("optionId", json, type, locale);
		OptionIdentifier parentId = parseOption("parentId", json, type.getParent(), locale);
		Boolean mutable = parseBoolean("mutable", json);
		Status status = parseStatus("status", json, locale);
		Localized name = parseObject("name", json, new LocalizedJsonTransformer(crm), locale);
		return new Option(optionId, parentId, type, status, mutable, name);
	}

}
