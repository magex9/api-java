package ca.magex.crm.restful.controllers;

import static ca.magex.crm.test.CrmAsserts.GROUP;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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

	@PostMapping("/api/junit/identifier/{key}")
	public void getIdentifier(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("key") String key) throws IOException {
		handle(req, res, Group.class, (messages, transformer) -> {
			JsonObject body = extractBody(req);
			Identifier identifier = getIdentifier(body, key, null, null, messages);
			validate(messages);
			return transformer.format(new Group(identifier, Status.ACTIVE, GROUP), extractLocale(req));
		});
	}

	@PostMapping("/api/junit/strings/{key}")
	public void getStrings(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("key") String key) throws IOException {
		handle(req, res, Group.class, (messages, transformer) -> {
			JsonObject body = extractBody(req);
			getStrings(body, key, null, null, messages);
			validate(messages);
			return transformer.format(new Group(new Identifier("test"), Status.ACTIVE, GROUP), extractLocale(req));
		});
	}

	@PostMapping("/api/junit/400")
	public void createBadRequestException(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, Group.class, (messages, transformer) -> {
			throw new BadRequestException("JUnit bad request denied", new Identifier("junit"), "error", "path", 
				new Localized("RSN", "English Reason", "French Reason"));
		});
	}

	@PostMapping("/api/junit/403")
	public void createPermissionDeniedException(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, Group.class, (messages, transformer) -> {
			throw new PermissionDeniedException("JUnit permission denied");
		});
	}

	@PostMapping("/api/junit/404")
	public void createItemNotFoundException(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, Group.class, (messages, transformer) -> {
			throw new ItemNotFoundException("JUnit item not found");
		});
	}
	
	@PostMapping("/api/junit/500")
	public void createException(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, Group.class, (messages, transformer) -> {
			throw new RuntimeException("Exception controller test");
		});
	}
	
}