package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.Choice;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.PhraseIdentifier;
import ca.magex.crm.api.system.id.MessageTypeIdentifier;
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
		formatType(pairs, locale);
		formatIdentifier(pairs, "identifier", message, Identifier.class, locale);
		formatOption(pairs, "type", message, Type.MESSAGE_TYPE, locale);
		formatText(pairs, "path", message);
		formatLocalized(pairs, "reason", message, locale);
		return new JsonObject(pairs);
	}

	@Override
	public Message parseJsonObject(JsonObject json, Locale locale) {
		Identifier identifier = json.contains("identifier") ? parseObject("identifier", json, new IdentifierJsonTransformer(crm), locale) : null;
		MessageTypeIdentifier type = parseOption("type", json, Type.MESSAGE_TYPE, locale);
		String path = parseText("path", json);
		Choice<PhraseIdentifier> reason = parseChoice("reason", json, Type.PHRASE, locale);
		return new Message(identifier, type, path, reason);
	}
	
}
