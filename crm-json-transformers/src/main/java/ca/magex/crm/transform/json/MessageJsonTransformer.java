package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.services.CrmOptionService;
import ca.magex.crm.api.system.Choice;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.IdentifierFactory;
import ca.magex.crm.api.system.id.MessageTypeIdentifier;
import ca.magex.crm.api.system.id.PhraseIdentifier;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;

@Component
public class MessageJsonTransformer extends AbstractJsonTransformer<Message> {

	public MessageJsonTransformer(CrmOptionService crm) {
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
		if (locale == null) {
			formatIdentifier(pairs, "identifier", message, Identifier.class, locale);
		} else {
			pairs.add(new JsonPair("context", message.getIdentifier().getContext().substring(1, message.getIdentifier().getContext().length() - 1)));
			formatIdentifier(pairs, "identifier", message, Identifier.class, locale);
		}
		formatOption(pairs, "type", message, MessageTypeIdentifier.class, locale);
		formatText(pairs, "value", message);
		formatText(pairs, "path", message);
		formatChoice(pairs, "reason", message, PhraseIdentifier.class, locale);
		return new JsonObject(pairs);
	}

	@Override
	public Message parseJsonObject(JsonObject json, Locale locale) {
		Identifier identifier = parseIdentifier(json, locale);
		MessageTypeIdentifier type = parseOption("type", json, Type.MESSAGE_TYPE, locale);
		String path = parseText("path", json);
		String value = parseText("value", json);
		Choice<PhraseIdentifier> reason = parseChoice("reason", json, Type.PHRASE, locale);		
		return new Message(identifier, type, path, value, reason);
	}
	
	public Identifier parseIdentifier(JsonObject json, Locale locale) {
		if (!json.contains("identifier")) {
			return null;
		} else if (locale == null) {
			return IdentifierFactory.forId(json.getString("identifier").substring(Crm.REST_BASE.length()));
		} else {
			return IdentifierFactory.forId("/" + json.getString("context") + "/" + json.getString("identifier"));
		}
	}
	
}
