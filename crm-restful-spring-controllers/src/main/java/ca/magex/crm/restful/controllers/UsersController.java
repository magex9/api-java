package ca.magex.crm.restful.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.UserDetails;
import ca.magex.crm.api.crm.UserSummary;
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.AuthenticationRoleIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.system.id.UserIdentifier;
import ca.magex.json.model.JsonObject;

@Controller
public class UsersController extends AbstractCrmController {

	@GetMapping("/rest/users")
	public void findUsers(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, UserDetails.class, (messages, transformer, locale) -> { 
			return createPage(
				crm.findUserDetails(
					extractUserFilter(req, locale), 
					extractPaging(UsersFilter.getDefaultPaging(), req)
				), transformer, locale
			);
		});
	}
	
	public UsersFilter extractUserFilter(HttpServletRequest req, Locale locale) throws BadRequestException {
		List<Message> messages = new ArrayList<>();
		JsonObject query = extractQuery(req);
		OrganizationIdentifier organizationId = getIdentifier(query, "organizationId", false, null, null, messages);
		PersonIdentifier personId = getIdentifier(query, "personId", false, null, null, messages);
		String username = getString(query, "username", false, null, null, messages);
		Status status = getObject(Status.class, query, "status", false, null, null, messages, locale);
		AuthenticationRoleIdentifier role = getOptionIdentifier(query, "authenticationRoleId", false, null, null, messages, AuthenticationRoleIdentifier.class, locale);
		validate(messages);
		return new UsersFilter(organizationId, personId, status, username, role);
	}
	
	@PostMapping("/rest/users")
	public void createUser(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, UserDetails.class, (messages, transformer, locale) -> { 
			JsonObject body = extractBody(req);
			PersonIdentifier personId = getIdentifier(body, "personId", true, null, null, messages);
			String username = getString(body, "username", true, null, null, messages);
			List<AuthenticationRoleIdentifier> authenticationRoleIds = getOptionIdentifiers(body, "authenticationRoleIds", true, List.of(), null, messages, AuthenticationRoleIdentifier.class, locale);
			validate(messages);
			return transformer.format(crm.createUser(personId, username, authenticationRoleIds), locale);
		});
	}

	@GetMapping("/rest/users/{userId}")
	public void getUser(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("userId") UserIdentifier userId) throws IOException {
		handle(req, res, UserDetails.class, (messages, transformer, locale) -> {
			return transformer.format(crm.findUserDetails(userId), locale);
		});
	}

	@GetMapping("/rest/user/{username}")
	public void getUserByUsername(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("username") String username) throws IOException {
		getUser(req, res, crm.findUserByUsername(username).getUserId());
	}

	@PatchMapping("/rest/users/{userId}")
	public void updateUser(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("userId") UserIdentifier userId) throws IOException {
		handle(req, res, UserDetails.class, (messages, transformer, locale) -> {
			JsonObject body = extractBody(req);
			if (body.contains("authenticationRoleIds")) {
				crm.updateUserRoles(userId, getOptionIdentifiers(body, "authenticationRoleIds", true, List.of(), userId, messages, AuthenticationRoleIdentifier.class, locale));
			}
			validate(messages);
			return transformer.format(crm.findUserDetails(userId), locale);
		});
	}

	@PatchMapping("/rest/user/{username}")
	public void updateUserByUsername(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("username") String username) throws IOException {
		updateUser(req, res, crm.findUserByUsername(username).getUserId());
	}

	@GetMapping("/rest/users/{userId}/person")
	public void getUserPerson(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("userId") UserIdentifier userId) throws IOException {
		handle(req, res, PersonDetails.class, (messages, transformer, locale) -> {
			return transformer.format(crm.findPersonDetails(crm.findUserDetails(userId).getPersonId()), locale);
		});
	}

	@GetMapping("/rest/user/{username}/person")
	public void getUserPersonByUsername(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("username") String username) throws IOException {
		getUserPerson(req, res, crm.findUserByUsername(username).getUserId());
	}

	@GetMapping("/rest/users/{userId}/authenticationRoleIds")
	public void getUserRoles(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("userId") UserIdentifier userId) throws IOException {
		handle(req, res, AuthenticationRoleIdentifier.class, (messages, transformer, locale) -> {
			return createList(crm.findUserDetails(userId).getAuthenticationRoleIds(), transformer, locale);
		});
	}

	@GetMapping("/rest/user/{username}/authenticationRoleIds")
	public void getUserRolesByUsername(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("username") String username) throws IOException {
		getUserRoles(req, res, crm.findUserByUsername(username).getUserId());
	}

	@PutMapping("/rest/users/{userId}/enable")
	public void enableUser(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("userId") UserIdentifier userId) throws IOException {
		handle(req, res, UserSummary.class, (messages, transformer, locale) -> {
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
			@PathVariable("userId") UserIdentifier userId) throws IOException {
		handle(req, res, UserSummary.class, (messages, transformer, locale) -> {
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