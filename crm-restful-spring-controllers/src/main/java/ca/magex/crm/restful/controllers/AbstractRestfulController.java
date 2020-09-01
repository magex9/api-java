package ca.magex.crm.restful.controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.util.StreamUtils;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.system.id.IdentifierFactory;
import ca.magex.crm.api.system.id.MessageTypeIdentifier;
import ca.magex.crm.api.system.id.OptionIdentifier;
import ca.magex.crm.api.system.id.PhraseIdentifier;
import ca.magex.crm.api.transform.RequestHandler;
import ca.magex.crm.api.transform.Transformer;
import ca.magex.crm.transform.json.ChoiceJsonTransformer;
import ca.magex.crm.transform.json.IdentifierJsonTransformer;
import ca.magex.crm.transform.json.JsonTransformerFactory;
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonFormatter;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;
import ca.magex.json.model.JsonParser;
import ca.magex.json.model.JsonText;

public abstract class AbstractRestfulController {

	@Autowired
	protected Crm crm;
	
	@Autowired
	protected JsonTransformerFactory jsonTransformerFactory;
	
	protected <T> void handle(HttpServletRequest req, HttpServletResponse res, Class<T> type, RequestHandler<List<Message>, Transformer<T, JsonElement>, Locale, JsonElement> func) throws IOException {
		JsonElement json = func.apply(new ArrayList<Message>(), jsonTransformerFactory.findByClass(type), extractLocale(req));
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(json == null ? "null" : JsonFormatter.formatted(json));
	}
	
	protected void validate(List<Message> messages) {
		if (!messages.isEmpty())
			throw new BadRequestException("User input validation errors", messages);
	}
	
	protected <I extends Identifier> I getIdentifier(JsonObject json, String key, boolean required, I defaultValue, Identifier identifier, List<Message> messages) {
		try {
			return IdentifierFactory.forId(json.getString(key));
		} catch (ClassCastException e) {
			messages.add(new Message(identifier, MessageTypeIdentifier.ERROR, key, "", PhraseIdentifier.VALIDATION_FIELD_FORMAT));
			return defaultValue;
		} catch (ItemNotFoundException | NoSuchElementException e) {
			if (required)
				messages.add(new Message(identifier, MessageTypeIdentifier.ERROR, key, "", PhraseIdentifier.VALIDATION_FIELD_REQUIRED));
			return defaultValue;
		}
	}
	
	@SuppressWarnings("unchecked")
	protected <I extends Identifier> List<I> getIdentifiers(JsonObject json, String key, boolean required, List<I> defaultValue, Identifier identifier, List<Message> messages) {
		try {
			return json.getArray(key).stream().map(e -> (I)IdentifierFactory.forId(((JsonText)e).value())).collect(Collectors.toList());
		} catch (ClassCastException e) {
			messages.add(new Message(identifier, MessageTypeIdentifier.ERROR, key, "", PhraseIdentifier.VALIDATION_FIELD_FORMAT));
			return defaultValue;
		} catch (ItemNotFoundException | NoSuchElementException e) {
			if (required)
				messages.add(new Message(identifier, MessageTypeIdentifier.ERROR, key, "", PhraseIdentifier.VALIDATION_FIELD_REQUIRED));
			return defaultValue;
		}
	}
	
	protected <I extends OptionIdentifier> I getOptionIdentifier(JsonObject json, String key, boolean required, I defaultValue, Identifier identifier, List<Message> messages, Class<I> cls, Locale locale) {
		try {
			if (locale == null) {
				return IdentifierFactory.forOptionId(json.getString(key));
			} else {
				return crm.findOptions(crm.defaultOptionsFilter().withType(IdentifierFactory.getType(cls)).withName(new Localized(locale, json.getString(key)))).getSingleItem().getOptionId();
			}
		} catch (ClassCastException e) {
			messages.add(new Message(identifier, MessageTypeIdentifier.ERROR, key, "", PhraseIdentifier.VALIDATION_FIELD_FORMAT));
			return defaultValue;
		} catch (ItemNotFoundException | NoSuchElementException e) {
			if (required)
				messages.add(new Message(identifier, MessageTypeIdentifier.ERROR, key, "", PhraseIdentifier.VALIDATION_FIELD_REQUIRED));
			return defaultValue;
		}
	}
	
	@SuppressWarnings("unchecked")
	protected <I extends OptionIdentifier> List<I> getOptionIdentifiers(JsonObject json, String key, boolean required, List<I> defaultValue, Identifier identifier, List<Message> messages, Class<I> cls, Locale locale) {
		try {
			if (locale == null) {
				//return json.getArray(key, JsonText.class).stream().map(e -> (I)IdentifierFactory.forOptionId(e.value())).collect(Collectors.toList());
				return json.getArray(key, JsonObject.class).stream().map(e -> (I)IdentifierFactory.forOptionId(e.getString("@id"))).collect(Collectors.toList());
			} else {
				return json.getArray(key).stream()
					.map(e -> (I)crm.findOptions(crm.defaultOptionsFilter().withType(IdentifierFactory.getType(cls)).withName(new Localized(locale, ((JsonText)e).value()))).getSingleItem().getOptionId())
					.collect(Collectors.toList());
			}
		} catch (ClassCastException e) {
			messages.add(new Message(identifier, MessageTypeIdentifier.ERROR, key, "", PhraseIdentifier.VALIDATION_FIELD_FORMAT));
			return defaultValue;
		} catch (ItemNotFoundException | NoSuchElementException e) {
			if (required)
				messages.add(new Message(identifier, MessageTypeIdentifier.ERROR, key, "", PhraseIdentifier.VALIDATION_FIELD_REQUIRED));
			return defaultValue;
		}
	}
	
	protected String getString(JsonObject json, String key, boolean required, String defaultValue, Identifier identifier, List<Message> messages) {
		try {
			return URLDecoder.decode(json.getString(key), "UTF-8");
		} catch (ClassCastException e) {
			messages.add(new Message(identifier, MessageTypeIdentifier.ERROR, key, "", PhraseIdentifier.VALIDATION_FIELD_FORMAT));
			return defaultValue;
		} catch (ItemNotFoundException | NoSuchElementException e) {
			if (required)
				messages.add(new Message(identifier, MessageTypeIdentifier.ERROR, key, "", PhraseIdentifier.VALIDATION_FIELD_REQUIRED));
			return defaultValue;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	protected List<String> getStrings(JsonObject json, String key, boolean required, List<String> defaultValue, Identifier identifier, List<Message> messages) {
		try {
			return json.getArray(key).stream().map(e -> ((JsonText)e).value()).collect(Collectors.toList());
		} catch (ClassCastException e) {
			messages.add(new Message(identifier, MessageTypeIdentifier.ERROR, key, "", PhraseIdentifier.VALIDATION_FIELD_FORMAT));
			return defaultValue;
		} catch (ItemNotFoundException | NoSuchElementException e) {
			if (required)
				messages.add(new Message(identifier, MessageTypeIdentifier.ERROR, key, "", PhraseIdentifier.VALIDATION_FIELD_REQUIRED));
			return defaultValue;
		}
	}
	
	protected <T> T getObject(Class<T> cls, JsonObject body, String key, boolean required, T defaultValue, Identifier identifier, List<Message> messages, Locale locale) {
		try {
			return jsonTransformerFactory.findByClass(cls).parse(body.get(key), locale);
		} catch (ClassCastException e) {
			messages.add(new Message(identifier, MessageTypeIdentifier.ERROR, key, "", PhraseIdentifier.VALIDATION_FIELD_FORMAT));
			return defaultValue;
		} catch (ItemNotFoundException | NoSuchElementException e) {
			if (required)
				messages.add(new Message(identifier, MessageTypeIdentifier.ERROR, key, "", PhraseIdentifier.VALIDATION_FIELD_REQUIRED));
			return defaultValue;
		}
	}

	public String getContentType(HttpServletRequest req) {
		if (req.getContentType() != null)
			return req.getContentType();
		return "application/json";
	}

	protected <T> JsonObject createPage(Page<T> page, RestfulActionHandler<T> actionHandler, Transformer<T, JsonElement> transfomer, Locale locale) {
		return new JsonObject()
			.with("@context", locale == null ? Crm.SCHEMA_BASE + "/system/Page" : null)
			.with("page", page.getNumber())
			.with("limit", page.getPageable().getPageSize())
			.with("total", page.getTotalElements())
			.with("hasNext", page.hasNext())
			.with("hasPrevious", page.hasPrevious())
			.with("content", new JsonArray(page.getContent().stream()
				.map(i -> ((JsonObject)transfomer.format(i, locale)).with("actions", actionHandler.buildActions(i, crm, locale)))
				.collect(Collectors.toList())));
	}
	
	protected <T> JsonElement createList(List<T> list, Transformer<T, JsonElement> transformer, Locale locale) {
		return new JsonObject()
			.with("total", list.size())
			.with("content", new JsonArray(list.stream().map(i -> transformer.format(i, locale)).collect(Collectors.toList())));
	}
	
	protected <T> JsonElement createList(List<T> list, Transformer<T, JsonElement> transformer, Locale locale, Comparator<T> comparator) {
		return new JsonObject()
			.with("total", list.size())
			.with("content", new JsonArray(list.stream().sorted(comparator).map(i -> transformer.format(i, locale)).collect(Collectors.toList())));
	}
	
	protected JsonArray createErrorMessages(Locale locale, BadRequestException e) {
		List<JsonElement> elements = new ArrayList<JsonElement>();
		for (Message message : e.getMessages()) {
			elements.add(new JsonObject()
				.with("identifier", message.getIdentifier() == null ? null : message.getIdentifier().toString())
				.with("type", new IdentifierJsonTransformer(crm).format(message.getType(), locale))
				.with("path", message.getPath())
				.with("reason", new ChoiceJsonTransformer<PhraseIdentifier>(crm).format(message.getReason(), locale)));
		}
		return new JsonArray(elements);
	}
	
	protected JsonObject extractBody(HttpServletRequest req) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			StreamUtils.copy(req.getInputStream(), baos);
			String json = new String(baos.toByteArray(), StandardCharsets.UTF_8);
			if (StringUtils.isBlank(json))
				return new JsonObject();
			return JsonParser.parseObject(json);
		} catch (IOException e) {
			throw new RuntimeException("Unable to extract body from request", e);
		}
	}
	
	protected JsonObject extractQuery(HttpServletRequest req) {
		try {
			List<JsonPair> pairs = new ArrayList<>();
			for (String key : req.getParameterMap().keySet()) {
				pairs.add(new JsonPair(key, new JsonText(req.getParameter(key))));
			}
			return new JsonObject(pairs);
		} catch (Exception e) {
			throw new RuntimeException("Unable to extract query from request", e);
		}
	}

	protected void confirm(JsonObject body, Identifier identifier, List<Message> messages) {
		try {
			if (!body.contains("confirm") || !body.getBoolean("confirm"))
				messages.add(new Message(identifier, MessageTypeIdentifier.ERROR, "confirm", null, PhraseIdentifier.VALIDATION_FIELD_REQUIRED));
		} catch (ClassCastException e) {
			messages.add(new Message(identifier, MessageTypeIdentifier.ERROR, "confirm", body.get("confirm").toString(), PhraseIdentifier.VALIDATION_FIELD_FORMAT));
		}
		validate(messages);
	}
	
	public Locale extractLocale(HttpServletRequest req) {
		if (getContentType(req).contentEquals("application/json+ld"))
			return null;
		if (req.getHeader("Locale") == null)
			return Lang.ROOT;
		return Lang.parse(req.getHeader("Locale"));
	}

	public Paging extractPaging(Paging paging, HttpServletRequest req) {
		if (req.getParameter("page") != null)
			paging = paging.withPageNumber(Integer.parseInt(req.getParameter("page")));
		if (req.getParameter("limit") != null)
			paging = paging.withPageSize(Integer.parseInt(req.getParameter("limit")));
		Direction direction = Direction.ASC;
		if (req.getParameter("direction") != null)
			direction = Direction.fromString(req.getParameter("direction"));
		if (req.getParameter("order") != null)
			paging = paging.withSort(Sort.by(direction, req.getParameter("order")));
		return paging;
	}
	
}