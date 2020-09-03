package ca.magex.crm.restful.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.exceptions.PermissionDeniedException;
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonFormatter;
import ca.magex.json.model.JsonObject;

@ControllerAdvice
public class RestfulExceptionHandler extends AbstractRestfulController {

	public static final Logger logger = LoggerFactory.getLogger(RestfulExceptionHandler.class);
	
	@ExceptionHandler(BadRequestException.class)
	public void handleBadRequestException(HttpServletRequest req, HttpServletResponse res, BadRequestException e) throws IOException {
		logger.info("Bad request information: " + e.getMessages());
		JsonArray errors = createErrorMessages(extractLocale(req), e);
		res.setStatus(400);
		res.setContentType(getContentType(req));
		res.getWriter().write(JsonFormatter.formatted(errors));
	}
	
	@ExceptionHandler(PermissionDeniedException.class)
	public void handlePermissionDeniedException(HttpServletRequest req, HttpServletResponse res, PermissionDeniedException e) throws IOException {
		logger.warn("Permission denied:" + req.getPathInfo());
		res.setStatus(403);
	}
	
	@ExceptionHandler(ItemNotFoundException.class)
	public void handleItemNotFoundException(HttpServletRequest req, HttpServletResponse res, ItemNotFoundException e) throws IOException {
		logger.info("Item not found:" + req.getPathInfo());
		res.setStatus(404);
		res.getWriter().write(new JsonObject()
			.with("reason", e.getReason())
			.with("error", e.getErrorCode())
			.toString());
	}
	
	@ExceptionHandler(Exception.class)
	public void handleSystemException(HttpServletRequest req, HttpServletResponse res, Exception e) throws IOException {
		logger.error("Exception handling request:" + req.getPathInfo(), e);
		res.setStatus(500);
	}
	
}
