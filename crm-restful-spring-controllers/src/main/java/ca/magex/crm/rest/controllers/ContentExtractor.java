package ca.magex.crm.rest.controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.mapping.data.DataObject;
import ca.magex.crm.mapping.data.DataParser;

public class ContentExtractor {

	public static String getContentType(HttpServletRequest req) {
//		if (req.getHeader("Content-Type").equals("application/json")) {
//			return "application/json";
//		}
		return "application/json";
	}

//	public static DataFormatter formatter(HttpServletRequest req) {
//		if (req.getHeader("Content-Type").equals("application/json")) {
//			return DataFormatter.json();
//		} else {
//			return LinkedDataFormatter.full();
//		}
//	}
	
	public static String extractDisplayName(HttpServletRequest req) throws IllegalArgumentException {
		String value = req.getParameter("displayName");
		if (value == null)
			return null;
		if (value.length() > 60)
			throw new IllegalArgumentException("The display name must be under 60 characters");
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
		IOUtils.copy(req.getInputStream(), baos);
		return DataParser.parseObject(new String(baos.toByteArray()));
	}

}