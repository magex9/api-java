package ca.magex.crm.restful.controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.util.StreamUtils;

import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.exceptions.PermissionDeniedException;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.Crm;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.transform.RequestHandler;
import ca.magex.crm.api.transform.Transformer;
import ca.magex.crm.transform.json.JsonTransformerFactory;
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonFormatter;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;
import ca.magex.json.model.JsonParser;
import ca.magex.json.model.JsonText;

public abstract class AbstractCrmController {

	private static final Logger logger = LoggerFactory.getLogger(AbstractCrmController.class);
	
	@Autowired
	protected Crm crm;
	
	@Autowired
	protected JsonTransformerFactory jsonTransformerFactory;
	
	protected <T> void handle(HttpServletRequest req, HttpServletResponse res, Class<T> type, RequestHandler<List<Message>, Transformer<T, JsonElement>, Locale, JsonElement> func) throws IOException {
		List<Message> messages = new ArrayList<Message>();
		try {
			JsonElement json = func.apply(messages, jsonTransformerFactory.findByClass(type), extractLocale(req));
			res.setStatus(200);
			res.setContentType(getContentType(req));
			res.getWriter().write(JsonFormatter.formatted(json));
		} catch (BadRequestException e) {
			logger.info("Bad request information: " + e.getMessages());
			JsonArray errors = createErrorMessages(extractLocale(req), e);
			res.setStatus(400);
			res.setContentType(getContentType(req));
			res.getWriter().write(JsonFormatter.formatted(errors));
		} catch (PermissionDeniedException e) {
			logger.warn("Permission denied:" + req.getPathInfo(), e);
			res.setStatus(403);
		} catch (ItemNotFoundException e) {
			logger.info("Item not found:" + req.getPathInfo(), e);
			res.setStatus(404);
		} catch (Exception e) {
			logger.error("Exception handling request:" + req.getPathInfo(), e);
			res.setStatus(500);
		}
	}
	
	protected void validate(List<Message> messages) {
		if (!messages.isEmpty())
			throw new BadRequestException("User input validation errors", messages);
	}
	
	protected Identifier getIdentifier(JsonObject json, String key, Identifier defaultValue, Identifier identifier, List<Message> messages) {
		try {
			return new Identifier(json.getString(key));
		} catch (ClassCastException e) {
			messages.add(new Message(identifier, "error", key, new Localized(Lang.ENGLISH, "Invalid format")));
			return defaultValue;
		} catch (NoSuchElementException e) {
			messages.add(new Message(identifier, "error", key, new Localized(Lang.ENGLISH, "Field is mandatory")));
			return defaultValue;
		}
	}
	
	protected String getString(JsonObject json, String key, String defaultValue, Identifier identifier, List<Message> messages) {
		try {
			return json.getString(key);
		} catch (ClassCastException e) {
			messages.add(new Message(identifier, "error", key, new Localized(Lang.ENGLISH, "Invalid format")));
			return defaultValue;
		} catch (NoSuchElementException e) {
			messages.add(new Message(identifier, "error", key, new Localized(Lang.ENGLISH, "Field is mandatory")));
			return defaultValue;
		}
	}
	
	protected List<String> getStrings(JsonObject json, String key, List<String> defaultValue, Identifier identifier, List<Message> messages) {
		try {
			return json.getArray(key).stream().map(e -> ((JsonText)e).value()).collect(Collectors.toList());
		} catch (ClassCastException e) {
			messages.add(new Message(identifier, "error", key, new Localized(Lang.ENGLISH, "Invalid format")));
			return defaultValue;
		} catch (NoSuchElementException e) {
			messages.add(new Message(identifier, "error", key, new Localized(Lang.ENGLISH, "Field is mandatory")));
			return defaultValue;
		}
	}
	
	protected <T> T getObject(Class<T> cls, JsonObject body, String key, T defaultValue, Identifier identifier, List<Message> messages, Locale locale) {
		try {
			return jsonTransformerFactory.findByClass(cls).parse(body.getObject(key), locale);
		} catch (ClassCastException e) {
			messages.add(new Message(identifier, "error", key, new Localized(Lang.ENGLISH, "Invalid format")));
			return defaultValue;
		} catch (NoSuchElementException e) {
			messages.add(new Message(identifier, "error", key, new Localized(Lang.ENGLISH, "Field is mandatory")));
			return defaultValue;
		}
	}

	public String getContentType(HttpServletRequest req) {
		if (req.getContentType() != null)
			return req.getContentType();
		return "application/json";
	}

	protected <T> JsonObject createPage(Page<T> page, Transformer<T, JsonElement> transfomer, Locale locale) {
		return new JsonObject()
			.with("page", page.getNumber())
			.with("limit", page.getPageable().getPageSize())
			.with("total", page.getTotalElements())
			.with("hasNext", page.hasNext())
			.with("hasPrevious", page.hasPrevious())
			.with("content", new JsonArray(page.getContent().stream().map(i -> transfomer.format(i, locale)).collect(Collectors.toList())));
	}
	
	protected <T> JsonObject createList(List<T> list, Transformer<T, JsonElement> transfomer, Locale locale) {
		return new JsonObject()
			.with("total", list.size())
			.with("content", new JsonArray(list.stream().map(i -> transfomer.format(i, locale)).collect(Collectors.toList())));
	}
	
	protected <T> JsonObject createList(List<T> list, Transformer<T, JsonElement> transfomer, Locale locale, Comparator<T> comparator) {
		return new JsonObject()
			.with("total", list.size())
			.with("content", new JsonArray(list.stream().sorted(comparator).map(i -> transfomer.format(i, locale)).collect(Collectors.toList())));
	}
	
	protected JsonArray createErrorMessages(Locale locale, BadRequestException e) {
		List<JsonElement> elements = new ArrayList<JsonElement>();
		for (Message message : e.getMessages()) {
			elements.add(new JsonObject()
				.with("identifier", message.getIdentifier() == null ? null : message.getIdentifier().toString())
				.with("type", message.getType())
				.with("path", message.getPath())
				.with("reason", message.getReason().get(locale)));
		}
		return new JsonArray(elements);
	}
	
	protected JsonObject extractBody(HttpServletRequest req) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			StreamUtils.copy(req.getInputStream(), baos);
			String json = new String(baos.toByteArray());
			if (StringUtils.isBlank(json))
				return new JsonObject();
			return JsonParser.parseObject(json);
		} catch (IOException e) {
			throw new RuntimeException("Unable to extract body from request", e);
		}
	}

	protected void confirm(JsonObject body, Identifier identifier, List<Message> messages) {
		try {
			if (!body.contains("confirm") || !body.getBoolean("confirm"))
				messages.add(new Message(identifier, "error", "confirm", new Localized(Lang.ENGLISH, "You must send in the confirmation message")));
		} catch (ClassCastException e) {
			messages.add(new Message(identifier, "error", "confirm", new Localized(Lang.ENGLISH, "Confirmation message must be a boolean")));
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
	
	public JsonObject action(String name, String title, String method, String href) {
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		pairs.add(new JsonPair("name", name));
		pairs.add(new JsonPair("title", title));
		pairs.add(new JsonPair("method", method));
		pairs.add(new JsonPair("href", href));
		return new JsonObject(pairs);
	}
	
}