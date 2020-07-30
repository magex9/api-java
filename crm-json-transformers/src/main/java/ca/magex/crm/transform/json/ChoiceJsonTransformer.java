package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.services.CrmOptionService;
import ca.magex.crm.api.system.Choice;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.IdentifierFactory;
import ca.magex.crm.api.system.id.OptionIdentifier;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;
import ca.magex.json.model.JsonText;
import ca.magex.json.util.StringConverter;

@Component
public class ChoiceJsonTransformer<I extends OptionIdentifier> extends AbstractJsonTransformer<Choice<I>> {

	public ChoiceJsonTransformer(CrmOptionService crm) {
		super(crm);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Class<Choice<I>> getSourceType() {
		return (Class<Choice<I>>) new Choice<I>().getClass();
	}
	
	@Override
	public JsonElement formatRoot(Choice<I> choice) {
		if (choice.isIdentifer()) {
			Option option = crm.findOption(choice.getIdentifier());
			List<JsonPair> pairs = new ArrayList<JsonPair>();
			pairs.add(new JsonPair("@context", buildContext(option, false, null)));
			pairs.add(new JsonPair("@id", buildContext(option, true, null) + "/" + option.getCode().replaceAll("_", "-").toLowerCase()));
			pairs.add(new JsonPair("@value", option.getCode()));
			pairs.add(new JsonPair("@en", option.getName(Lang.ENGLISH)));
			pairs.add(new JsonPair("@fr", option.getName(Lang.FRENCH)));
			return new JsonObject(pairs);
		} else if (choice.isOther()) {
			return new JsonText(choice.getOther());
		} else {
			return JsonElement.UNDEFINED;
		}
	}
	
	public String buildContext(Option option, boolean identifier, Locale locale) {
		StringBuilder sb = new StringBuilder();
		if (identifier) {
			sb.append(Crm.REST_BASE + "/options/");
		} else {
			sb.append(Crm.SCHEMA_BASE + "/options/");
		}
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
	public JsonElement formatLocalized(Choice<I> choice, Locale locale) {
		if (choice.isIdentifer()) {
			Option option = crm.findOption(choice.getIdentifier());
			if (Lang.ROOT.equals(locale)) {
				return new JsonText(buildContext(option, true, locale) + "/" + option.getName().getCode().replaceAll("_", "-").toLowerCase());
			} else {
				return new JsonText(option.getName(locale));
			}
		} else if (choice.isOther()) {
			return new JsonText(choice.getOther());
		} else {
			return JsonElement.UNDEFINED;
		}
	}

	@Override
	public Choice<I> parseJsonObject(JsonObject json, Locale locale) {
        return new Choice<I>(IdentifierFactory.forId(json.getString("optionId")));
	}
	
}
