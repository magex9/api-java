package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.OptionIdentifier;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;
import ca.magex.json.model.JsonText;
import ca.magex.json.util.StringConverter;

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
		pairs.add(new JsonPair("@context", buildContext(option, false, null)));
		pairs.add(new JsonPair("@id", buildContext(option, true, null) + "/" + option.getCode().replaceAll("_", "-").toLowerCase()));
		pairs.add(new JsonPair("@value", option.getCode()));
		pairs.add(new JsonPair("@en", option.getName(Lang.ENGLISH)));
		pairs.add(new JsonPair("@fr", option.getName(Lang.FRENCH)));
		return new JsonObject(pairs);
	}
	
	public String buildContext(Option option, boolean identifier, Locale locale) {
		StringBuilder sb = new StringBuilder();
		if (identifier) {
			sb.append(Crm.REST_BASE);
		} else {
			sb.append(Crm.SCHEMA_BASE);
		}
		sb.append("/options/");
		sb.append(formatType(option.getType(), identifier));
		return sb.toString();
	}
	
	public String formatType(Type type, boolean identifier) {
		if (identifier) {
			return StringConverter.upperToLowerCase(type.getCode());
		} else {
			return StringConverter.upperToTitleCase(type.getCode());
		}
	}
	
	@Override
	public JsonElement formatLocalized(Option option, Locale locale) {
		return new JsonText(option.getName(locale));
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
