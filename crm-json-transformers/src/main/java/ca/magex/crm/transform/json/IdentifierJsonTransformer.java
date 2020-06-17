package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;
import ca.magex.json.model.JsonText;

@Component
public class IdentifierJsonTransformer extends AbstractJsonTransformer<Identifier> {

	public IdentifierJsonTransformer(CrmServices crm) {
		super(crm);
	}

	@Override
	public Class<Identifier> getSourceType() {
		return Identifier.class;
	}

	@Override
	public JsonElement formatRoot(Identifier identifier) {
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		pairs.add(new JsonPair("@type", getType(Identifier.class)));
		pairs.add(new JsonPair("@id", identifier.toString()));
		return new JsonObject(pairs);
	}
	
	@Override
	public JsonElement formatLocalized(Identifier identifier, Locale locale) {
		return new JsonText(identifier.toString());
	}

	@Override
	public Identifier parseJsonText(JsonText json, Locale locale) {
		return new Identifier(json.value());
	}

	@Override
	public Identifier parseJsonObject(JsonObject json, Locale locale) {
		return new Identifier(json.getString("@id"));
	}

}
