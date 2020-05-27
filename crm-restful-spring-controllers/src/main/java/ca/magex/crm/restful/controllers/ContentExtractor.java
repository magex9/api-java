package ca.magex.crm.restful.controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.util.StreamUtils;

import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.Crm;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.rest.transformers.JsonTransformer;
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;
import ca.magex.json.model.JsonParser;

public class ContentExtractor {

	public static String getContentType(HttpServletRequest req) {
//		if (req.getHeader("Content-Type") != null && req.getHeader("Content-Type").equals("application/json+ld")) {
//			return "application/json+ld";
//		}
		return "application/json";
	}
	
	public static JsonTransformer getTransformer(HttpServletRequest req, Crm crm) {
		boolean linked = req.getHeader("Content-Type") != null && req.getHeader("Content-Type").equals("application/json+ld");
		return new JsonTransformer(crm, extractLocale(req), linked);
	}
	
	public static Locale extractLocale(HttpServletRequest req) {
		if (req.getHeader("Locale") == null)
			return Lang.ROOT;
		return Lang.parse(req.getHeader("Locale"));
	}
	
	public static Identifier extractOrganizationId(HttpServletRequest req) throws IllegalArgumentException {
		String value = req.getParameter("organizationId");
		if (value == null)
			return null;
		if (value.length() > 60)
			throw new IllegalArgumentException("The organizationId name must be under 60 characters");
		return new Identifier(value);
	}

	public static String extractDisplayName(HttpServletRequest req) throws IllegalArgumentException {
		String value = req.getParameter("displayName");
		if (value == null)
			return null;
		if (value.length() > 60)
			throw new IllegalArgumentException("The display name must be under 60 characters");
		return value;
	}

	public static String extractReference(HttpServletRequest req) throws IllegalArgumentException {
		String value = req.getParameter("reference");
		if (value == null)
			return null;
		if (value.length() > 60)
			throw new IllegalArgumentException("The reference must be under 60 characters");
		return value;
	}
	
	public static Status extractStatus(HttpServletRequest req) throws IllegalArgumentException {
		String value = req.getParameter("status");
		if (value == null)
			return null;
		try {
			return Status.valueOf(value.toUpperCase());
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid status requested: " + value);
		}
	}
	
	public static Paging extractPaging(HttpServletRequest req) {
		Integer page = req.getParameter("page") == null ? 1 : Integer.parseInt(req.getParameter("page"));
		Integer limit = req.getParameter("limit") == null ? 10 : Integer.parseInt(req.getParameter("limit"));
		String order = req.getParameter("order") == null ? "displayName" : req.getParameter("order");
		String direction = req.getParameter("direction") == null ? "asc" : req.getParameter("direction");
		return new Paging(page, limit, Sort.by(Direction.fromString(direction), order));
	}
	
	public static JsonObject extractBody(HttpServletRequest req) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		StreamUtils.copy(req.getInputStream(), baos);		
		return JsonParser.parseObject(new String(baos.toByteArray()));
	}

	public static JsonObject action(String name, String title, String method, String href) {
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		pairs.add(new JsonPair("name", name));
		pairs.add(new JsonPair("title", title));
		pairs.add(new JsonPair("method", method));
		pairs.add(new JsonPair("href", href));
		return new JsonObject(pairs);
	}
	
	public static <T> JsonObject createPage(Page<T> page, Function<T, JsonElement> mapper) {
		return new JsonObject()
			.with("page", page.getNumber())
			.with("total", page.getTotalElements())
			.with("hasNext", page.hasNext())
			.with("hasPrevious", page.getNumber() > 1)
			.with("content", new JsonArray(page.getContent().stream().map(mapper).collect(Collectors.toList())));
	}
	
}