package ca.magex.crm.restful.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.User;
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonObject;

@Controller
public class UsersController extends AbstractCrmController {

	@GetMapping("/rest/users")
	public void findUsers(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, User.class, (messages, transformer, locale) -> { 
			return createPage(
				crm.findUsers(
					extractUserFilter(req, locale), 
					extractPaging(UsersFilter.getDefaultPaging(), req)
				), transformer, locale
			);
		});
	}
	
	public UsersFilter extractUserFilter(HttpServletRequest req, Locale locale) throws BadRequestException {
		Identifier organizationId = req.getParameter("organization") == null ? null : new Identifier(req.getParameter("organization"));
		Identifier personId = req.getParameter("person") == null ? null : new Identifier(req.getParameter("person"));
		String username = req.getParameter("username");
		Status status = req.getParameter("status") == null ? null : Status.valueOf(crm.findOptionByLocalizedName(Crm.STATUS, locale, req.getParameter("status")).getCode().toUpperCase());
		String role = req.getParameter("role");
		return new UsersFilter(organizationId, personId, status, username, role);
	}
	
	@PostMapping("/rest/users")
	public void createUser(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, User.class, (messages, transformer, locale) -> { 
			JsonObject body = extractBody(req);
			Identifier personId = getIdentifier(body, "personId", null, null, messages);
			String username = getString(body, "username", null, null, messages);
			List<String> roles = getStrings(body, "roles", List.of(), null, messages);
			validate(messages);
			return transformer.format(crm.createUser(personId, username, roles), locale);
		});
	}

	@GetMapping("/rest/users/{userId}")
	public void getUser(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("userId") Identifier userId) throws IOException {
		handle(req, res, User.class, (messages, transformer, locale) -> {
			return transformer.format(crm.findUser(userId), locale);
		});
	}

	@GetMapping("/rest/user/{username}")
	public void getUserByUsername(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("username") String username) throws IOException {
		getUser(req, res, crm.findUserByUsername(username).getUserId());
	}

	@PatchMapping("/rest/users/{userId}")
	public void updateUser(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("userId") Identifier userId) throws IOException {
		handle(req, res, User.class, (messages, transformer, locale) -> {
			JsonObject body = extractBody(req);
			if (body.contains("roles")) {
				crm.updateUserRoles(userId, getStrings(body, "roles", null, userId, messages));
			}
			validate(messages);
			return transformer.format(crm.findUser(userId), locale);
		});
	}

	@PatchMapping("/rest/user/{username}")
	public void updateUserByUsername(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("username") String username) throws IOException {
		updateUser(req, res, crm.findUserByUsername(username).getUserId());
	}

	@GetMapping("/rest/users/{userId}/person")
	public void getUserPerson(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("userId") Identifier userId) throws IOException {
		handle(req, res, PersonDetails.class, (messages, transformer, locale) -> {
			return transformer.format(crm.findPersonDetails(crm.findUser(userId).getPerson().getPersonId()), locale);
		});
	}

	@GetMapping("/rest/user/{username}/person")
	public void getUserPersonByUsername(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("username") String username) throws IOException {
		getUserPerson(req, res, crm.findUserByUsername(username).getUserId());
	}

	@GetMapping("/rest/users/{userId}/roles")
	public void getUserRoles(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("userId") Identifier userId) throws IOException {
		handle(req, res, String.class, (messages, transformer, locale) -> {
			return new JsonArray(crm.findUser(userId).getRoles().stream()
				.map(s -> transformer.format(s, locale)).collect(Collectors.toList()));
		});
	}

	@GetMapping("/rest/user/{username}/roles")
	public void getUserRolesByUsername(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("username") String username) throws IOException {
		getUserRoles(req, res, crm.findUserByUsername(username).getUserId());
	}

	@PutMapping("/rest/users/{userId}/enable")
	public void enableUser(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("userId") Identifier userId) throws IOException {
		handle(req, res, User.class, (messages, transformer, locale) -> {
			confirm(extractBody(req), userId, messages);
			return transformer.format(crm.enableUser(userId), locale);
		});
	}

	@PutMapping("/rest/user/{username}/enable")
	public void enableUserByUsername(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("username") String username) throws IOException {
		enableUser(req, res, crm.findUserByUsername(username).getUserId());
	}

	@PutMapping("/rest/users/{userId}/disable")
	public void disableUser(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("userId") Identifier userId) throws IOException {
		handle(req, res, User.class, (messages, transformer, locale) -> {
			confirm(extractBody(req), userId, messages);
			return transformer.format(crm.disableUser(userId), locale);
		});
	}

	@PutMapping("/rest/user/{username}/disable")
	public void disableUserByUsername(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("username") String username) throws IOException {
		disableUser(req, res, crm.findUserByUsername(username).getUserId());
	}

}