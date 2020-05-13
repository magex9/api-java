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
import ca.magex.crm.api.secured.SecuredCrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.mapping.data.DataArray;
import ca.magex.crm.mapping.data.DataElement;
import ca.magex.crm.mapping.data.DataObject;
import ca.magex.crm.mapping.data.DataPair;
import ca.magex.crm.mapping.data.DataParser;
import ca.magex.crm.mapping.json.JsonTransformer;

public class ContentExtractor {

	public static String getContentType(HttpServletRequest req) {
//		if (req.getHeader("Content-Type") != null && req.getHeader("Content-Type").equals("application/json+ld")) {
//			return "application/json+ld";
//		}
		return "application/json";
	}
	
	public static JsonTransformer getTransformer(HttpServletRequest req, SecuredCrmServices crm) {
		boolean linked = req.getHeader("Content-Type") != null && req.getHeader("Content-Type").equals("application/json+ld");
		return new JsonTransformer(crm, extractLocale(req), linked);
	}
	
	public static Locale extractLocale(HttpServletRequest req) {
		if (req.getHeader("Locale") == null)
			return null;
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
	
	public static DataObject extractBody(HttpServletRequest req) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		StreamUtils.copy(req.getInputStream(), baos);		
		return DataParser.parseObject(new String(baos.toByteArray()));
	}

	public static DataObject action(String name, String title, String method, String href) {
		List<DataPair> pairs = new ArrayList<DataPair>();
		pairs.add(new DataPair("name", name));
		pairs.add(new DataPair("title", title));
		pairs.add(new DataPair("method", method));
		pairs.add(new DataPair("href", href));
		return new DataObject(pairs);
	}
	
	public static <T> DataObject createPage(Page<T> page, Function<T, DataElement> mapper) {
		return new DataObject()
			.with("page", page.getNumber())
			.with("total", page.getTotalElements())
			.with("hasNext", page.hasNext())
			.with("hasPrevious", page.getNumber() > 1)
			.with("content", new DataArray(page.getContent().stream().map(mapper).collect(Collectors.toList())));
	}
	
}