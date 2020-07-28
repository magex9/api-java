package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Type;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;
import ca.magex.json.model.JsonText;

@Component
public class TypeJsonTransformer extends AbstractJsonTransformer<Type> {

	public TypeJsonTransformer(CrmServices crm) {
		super(crm);
	}

	@Override
	public Class<Type> getSourceType() {
		return Type.class;
	}

	@Override
	public JsonElement formatRoot(Type type) {
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		pairs.add(new JsonPair("@context", "http://magex.ca/crm/Types"));
		pairs.add(new JsonPair("@value", type.getCode()));
		pairs.add(new JsonPair("@en", type.getName(Lang.ENGLISH)));
		pairs.add(new JsonPair("@fr", type.getName(Lang.FRENCH)));
		return new JsonObject(pairs);
	}
	
	@Override
	public JsonElement formatLocalized(Type type, Locale locale) {
		return new JsonText(type.getName(locale));
	}

	@Override
	public Type parseJsonText(JsonText json, Locale locale) {
		return Type.of(json.value().toUpperCase());
	}

	@Override
	public Type parseJsonObject(JsonObject json, Locale locale) {
		return Type.of(json.getString("@value").toUpperCase());
	}

}
