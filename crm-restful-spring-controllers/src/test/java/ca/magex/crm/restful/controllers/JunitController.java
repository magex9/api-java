package ca.magex.crm.restful.controllers;

import static ca.magex.crm.test.CrmAsserts.GROUP;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.exceptions.PermissionDeniedException;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;
import ca.magex.json.model.JsonObject;

@Controller
public class JunitController extends AbstractCrmController {

	@PostMapping("/rest/junit/identifier/{key}")
	public void getIdentifier(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("key") String key) throws IOException {
		handle(req, res, Group.class, (messages, transformer, locale) -> {
			JsonObject body = extractBody(req);
			Identifier identifier = getIdentifier(body, key, null, null, messages);
			validate(messages);
			return transformer.format(new Group(identifier, Status.ACTIVE, GROUP), locale);
		});
	}

	@PostMapping("/rest/junit/strings/{key}")
	public void getStrings(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("key") String key) throws IOException {
		handle(req, res, Group.class, (messages, transformer, locale) -> {
			JsonObject body = extractBody(req);
			getStrings(body, key, null, null, messages);
			validate(messages);
			return transformer.format(new Group(new Identifier("test"), Status.ACTIVE, GROUP), locale);
		});
	}

	@PostMapping("/rest/junit/object/{key}")
	public void getObject(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("key") String key) throws IOException {
		handle(req, res, Group.class, (messages, transformer, locale) -> {
			JsonObject body = extractBody(req);
			getObject(MailingAddress.class, body, key, null, null, messages, locale);
			validate(messages);
			return transformer.format(new Group(new Identifier("test"), Status.ACTIVE, GROUP), locale);
		});
	}

	@PostMapping("/rest/junit/400")
	public void createBadRequestException(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, Group.class, (messages, transformer, locale) -> {
			throw new BadRequestException("JUnit bad request denied", new Identifier("junit"), "error", "path", 
				new Localized("RSN", "English Reason", "French Reason"));
		});
	}

	@PostMapping("/rest/junit/403")
	public void createPermissionDeniedException(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, Group.class, (messages, transformer, locale) -> {
			throw new PermissionDeniedException("JUnit permission denied");
		});
	}

	@PostMapping("/rest/junit/404")
	public void createItemNotFoundException(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, Group.class, (messages, transformer, locale) -> {
			throw new ItemNotFoundException("JUnit item not found");
		});
	}
	
	@PostMapping("/rest/junit/500")
	public void createException(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, Group.class, (messages, transformer, locale) -> {
			throw new RuntimeException("Exception controller test");
		});
	}
	
}