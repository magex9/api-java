package ca.magex.crm.restful.controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import java.util.function.Function;
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
import ca.magex.crm.api.exceptions.PermissionDeniedException;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.Crm;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.rest.transformers.JsonTransformer;
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
	
	protected void handle(HttpServletRequest req, HttpServletResponse res, BiFunction<List<Message>, JsonTransformer, JsonElement> func) throws IOException {
		List<Message> messages = new ArrayList<Message>();
		try {
			JsonElement json = func.apply(messages, getTransformer(req, crm));
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

	public String getContentType(HttpServletRequest req) {
//		if (req.getHeader("Content-Type") != null && req.getHeader("Content-Type").equals("application/json+ld")) {
//			return "application/json+ld";
//		}
		return "application/json";
	}

	protected <T> JsonObject createPage(Page<T> page, Function<T, JsonElement> mapper) {
		return new JsonObject()
			.with("page", page.getNumber())
			.with("limit", page.getNumberOfElements())
			.with("total", page.getTotalElements())
			.with("hasNext", page.hasNext())
			.with("hasPrevious", page.hasPrevious())
			.with("content", new JsonArray(page.getContent().stream().map(mapper).collect(Collectors.toList())));
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
	
	public JsonTransformer getTransformer(HttpServletRequest req, Crm crm) {
		boolean linked = req.getHeader("Content-Type") != null && req.getHeader("Content-Type").equals("application/json+ld");
		return new JsonTransformer(crm, extractLocale(req), linked);
	}
	
	public Locale extractLocale(HttpServletRequest req) {
		if (req.getHeader("Locale") == null)
			return Lang.ROOT;
		return Lang.parse(req.getHeader("Locale"));
	}
	
	public Identifier extractOrganizationId(HttpServletRequest req) throws IllegalArgumentException {
		String value = req.getParameter("organizationId");
		if (value == null)
			return null;
		if (value.length() > 60)
			throw new IllegalArgumentException("The organizationId name must be under 60 characters");
		return new Identifier(value);
	}

	public String extractDisplayName(HttpServletRequest req) throws IllegalArgumentException {
		String value = req.getParameter("displayName");
		if (value == null)
			return null;
		if (value.length() > 60)
			throw new IllegalArgumentException("The display name must be under 60 characters");
		return value;
	}

	public String extractReference(HttpServletRequest req) throws IllegalArgumentException {
		String value = req.getParameter("reference");
		if (value == null)
			return null;
		if (value.length() > 60)
			throw new IllegalArgumentException("The reference must be under 60 characters");
		return value;
	}
	
	public Status extractStatus(HttpServletRequest req) throws IllegalArgumentException {
		String value = req.getParameter("status");
		if (value == null)
			return null;
		try {
			return Status.valueOf(value.toUpperCase());
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid status requested: " + value);
		}
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