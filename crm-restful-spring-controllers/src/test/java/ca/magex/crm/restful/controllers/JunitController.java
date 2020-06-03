package ca.magex.crm.restful.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.exceptions.PermissionDeniedException;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;

@Controller
public class JunitController extends AbstractCrmController {

//	@PostMapping("/api/junit")
//	public void createJunit(HttpServletRequest req, HttpServletResponse res) throws IOException {
//		handle(req, res, Group.class, (messages, transformer) -> { 
//			JsonObject body = extractBody(req);
//			String code = getString(body, "code", "", null, messages);
//			String englishName = getString(body, "englishName", "", null, messages);
//			String frenchName = getString(body, "frenchName", "", null, messages);
//			Localized name = new Localized(code, englishName, frenchName);
//			validate(messages);
//			return transformer.format(crm.createGroup(name), extractLocale(req));
//		});
//	}
//
//	@GetMapping("/api/junit/{testId}")
//	public void getJunit(HttpServletRequest req, HttpServletResponse res, 
//			@PathVariable("testId") Identifier testId) throws IOException {
//		handle(req, res, Group.class, (messages, transformer) -> {
//			return transformer.format(new Group(testId, Status.ACTIVE, new Localized(Locale.ENGLISH, "Test: " + testId)), extractLocale(req));
//		});
//	}

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