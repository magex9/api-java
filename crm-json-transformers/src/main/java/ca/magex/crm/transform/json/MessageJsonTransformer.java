package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Message;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;

@Component
public class MessageJsonTransformer extends AbstractJsonTransformer<Message> {

	public MessageJsonTransformer(Crm crm) {
		super(crm);
	}

	@Override
	public Class<Message> getSourceType() {
		return Message.class;
	}
	
	@Override
	public JsonObject formatRoot(Message name) {
		return formatLocalized(name, null);
	}
	
	@Override
	public JsonObject formatLocalized(Message message, Locale locale) {
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		formatType(pairs);
		formatIdentifier(pairs, "identifier", message, locale);
		formatText(pairs, "type", message);
		formatText(pairs, "path", message);
		formatLocalized(pairs, "reason", message, locale);
		return new JsonObject(pairs);
	}

	@Override
	public Message parseJsonObject(JsonObject json, Locale locale) {
		Identifier identifier = json.contains("identifier") ? parseObject("identifier", json, new IdentifierJsonTransformer(crm), locale) : null;
		String type = parseText("type", json);
		String path = parseText("path", json);
		Localized reason = parseObject("reason", json, new LocalizedJsonTransformer(crm), locale);
		return new Message(identifier, type, path, reason);
	}
	
}
