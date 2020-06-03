package ca.magex.crm.transform.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.services.Crm;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Message;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;

@Component
public class MessageJsonTransformer extends AbstractJsonTransformer<Message> {

	private IdentifierJsonTransformer identifierJsonTransformer;
	
	private LocalizedJsonTransformer localizedJsonTransformer;
	
	public MessageJsonTransformer(Crm crm) {
		super(crm);
		this.identifierJsonTransformer = new IdentifierJsonTransformer(crm);
		this.localizedJsonTransformer = new LocalizedJsonTransformer(crm);
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
		if (message.getIdentifier() != null) {
			pairs.add(new JsonPair("identifier", identifierJsonTransformer
				.format(message.getIdentifier(), locale)));
		}
		formatText(pairs, "type", message);
		formatText(pairs, "path", message);
		if (message.getReason() != null) {
			pairs.add(new JsonPair("reason", localizedJsonTransformer
				.format(message.getReason(), locale)));
		}
		return new JsonObject(pairs);
	}

	@Override
	public Message parseJsonObject(JsonObject json, Locale locale) {
		Identifier identifier = json.contains("identifier") ? parseObject("identifier", json, identifierJsonTransformer, locale) : null;
		String type = parseText("type", json);
		String path = parseText("path", json);
		Localized reason = parseObject("reason", json, localizedJsonTransformer, locale);
		return new Message(identifier, type, path, reason);
	}
	
}
