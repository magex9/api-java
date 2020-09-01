package ca.magex.crm.restful.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@CrossOrigin
public class RestfulUsersController extends AbstractRestfulController {

	@GetMapping("/rest/users")
	public void findUserSummaries(HttpServletRequest req, HttpServletResponse res) throws IOException {
		RestfulUsersActionHandler<UserSummary> actionHandler = new RestfulUsersActionHandler<>();
		handle(req, res, UserSummary.class, (messages, transformer, locale) -> { 
			return createPage(
				crm.findUserSummaries(
					extractUserFilter(req, locale), 
					extractPaging(UsersFilter.getDefaultPaging(), req)
				), actionHandler, transformer, locale
			);
		});
	}
	
	@GetMapping("/rest/users/details")
	public void findUserDetails(HttpServletRequest req, HttpServletResponse res) throws IOException {
		RestfulUsersActionHandler<UserDetails> actionHandler = new RestfulUsersActionHandler<>();
		handle(req, res, UserDetails.class, (messages, transformer, locale) -> { 
			return createPage(
				crm.findUserDetails(
					extractUserFilter(req, locale), 
					extractPaging(UsersFilter.getDefaultPaging(), req)
				), actionHandler, transformer, locale
			);
		});
	}
	
	@GetMapping("/rest/users/count")
	public void countUsers(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, UserSummary.class, (messages, transformer, locale) -> {
			return new JsonObject().with("total", crm.countUsers(extractUserFilter(req, locale)));
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
	public void findUserSummary(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("userId") UserIdentifier userId) throws IOException {
		handle(req, res, UserDetails.class, (messages, transformer, locale) -> {
			return transformer.format(crm.findUserDetails(userId), locale);
		});
	}

	@GetMapping("/rest/user/{username}")
	public void findUserSummaryByUsername(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("username") String username) throws IOException {
		findUserSummary(req, res, crm.findUserSummaryByUsername(username).getUserId());
	}

	@GetMapping("/rest/users/{userId}/details")
	public void getUserDetails(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("userId") UserIdentifier userId) throws IOException {
		handle(req, res, UserDetails.class, (messages, transformer, locale) -> {
			return transformer.format(crm.findUserDetails(userId), locale);
		});
	}

	@GetMapping("/rest/user/{username}/details")
	public void findUserDetailsByUsername(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("username") String username) throws IOException {
		getUserDetails(req, res, crm.findUserDetailsByUsername(username).getUserId());
	}

	@PatchMapping("/rest/users/{userId}/details")
	public void updateUser(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("userId") UserIdentifier userId) throws IOException {
		handle(req, res, UserDetails.class, (messages, transformer, locale) -> {
			JsonObject body = extractBody(req);
			if (body.contains("authenticationRoleIds")) {
				crm.updateUserAuthenticationRoles(userId, getOptionIdentifiers(body, "authenticationRoleIds", true, List.of(), userId, messages, AuthenticationRoleIdentifier.class, locale));
			}
			validate(messages);
			return transformer.format(crm.findUserDetails(userId), locale);
		});
	}

	@PatchMapping("/rest/user/{username}/details")
	public void updateUserByUsername(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("username") String username) throws IOException {
		updateUser(req, res, crm.findUserSummaryByUsername(username).getUserId());
	}
	
	@PutMapping("/rest/passwords/{userId}/change")
	public void changePassword(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("userId") UserIdentifier userId) throws IOException {
		handle(req, res, Boolean.class, (messages, transformer, locale) -> {
			JsonObject body = extractBody(req);
			String currentPassword = body.getString("currentPassword");
			String newPassword = body.getString("newPassword");
			return transformer.format(crm.changePassword(userId, currentPassword, newPassword), locale);
		});
	}
	
	@PutMapping("/rest/passwords/{userId}/reset")
	public void resetPassword(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("userId") UserIdentifier userId) throws IOException {
		handle(req, res, String.class, (messages, transformer, locale) -> {
			confirm(extractBody(req), userId, messages);
			return transformer.format(crm.resetPassword(userId), locale);
		});
	}

	@GetMapping("/rest/users/{userId}/details/person")
	public void getUserPerson(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("userId") UserIdentifier userId) throws IOException {
		handle(req, res, PersonDetails.class, (messages, transformer, locale) -> {
			return transformer.format(crm.findPersonDetails(crm.findUserDetails(userId).getPersonId()), locale);
		});
	}

	@GetMapping("/rest/user/{username}/details/person")
	public void getUserPersonByUsername(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("username") String username) throws IOException {
		getUserPerson(req, res, crm.findUserSummaryByUsername(username).getUserId());
	}

	@GetMapping("/rest/users/{userId}/details/authenticationRoleIds")
	public void getUserRoles(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("userId") UserIdentifier userId) throws IOException {
		handle(req, res, AuthenticationRoleIdentifier.class, (messages, transformer, locale) -> {
			return createList(crm.findUserDetails(userId).getAuthenticationRoleIds(), transformer, locale);
		});
	}

	@PutMapping("/rest/users/{userId}/details/authenticationRoleIds")
	public void updateUserAuthenticationRoles(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("userId") UserIdentifier userId) throws IOException {
		handle(req, res, UserDetails.class, (messages, transformer, locale) -> {
			List<AuthenticationRoleIdentifier> authenticationRoleIds = getOptionIdentifiers(extractBody(req), "authenticationRoleIds", true, List.of(), userId, messages, AuthenticationRoleIdentifier.class, locale);
			validate(messages);
			return transformer.format(crm.updateUserAuthenticationRoles(userId, authenticationRoleIds), locale);
		});
	}

	@GetMapping("/rest/user/{username}/details/authenticationRoleIds")
	public void getUserRolesByUsername(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("username") String username) throws IOException {
		getUserRoles(req, res, crm.findUserSummaryByUsername(username).getUserId());
	}

	@PutMapping("/rest/user/{username}/details/authenticationRoleIds")
	public void updateUserAuthenticationRolesByUsername(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("username") String username) throws IOException {
		updateUserAuthenticationRoles(req, res, crm.findUserSummaryByUsername(username).getUserId());
	}
	
	@GetMapping("/rest/users/{userId}/actions")
	public void listUserActions(HttpServletRequest req, HttpServletResponse res,
			@PathVariable("userId") UserIdentifier userId) throws IOException {
		handle(req, res, RestfulAction.class, (messages, transformer, locale) -> {
			return new JsonObject().with("actions", new RestfulUsersActionHandler<>().buildActions(crm.findUserDetails(userId), crm, locale));
		});
	}

	@GetMapping("/rest/user/{username}/actions")
	public void listUserActionsByUsername(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("username") String username) throws IOException {
		listUserActions(req, res, crm.findUserSummaryByUsername(username).getUserId());
	}

	@PutMapping("/rest/users/{userId}/actions/enable")
	public void enableUser(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("userId") UserIdentifier userId) throws IOException {
		handle(req, res, UserSummary.class, (messages, transformer, locale) -> {
			confirm(extractBody(req), userId, messages);
			return transformer.format(crm.enableUser(userId), locale);
		});
	}

	@PutMapping("/rest/user/{username}/actions/enable")
	public void enableUserByUsername(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("username") String username) throws IOException {
		enableUser(req, res, crm.findUserSummaryByUsername(username).getUserId());
	}

	@PutMapping("/rest/users/{userId}/actions/disable")
	public void disableUser(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("userId") UserIdentifier userId) throws IOException {
		handle(req, res, UserSummary.class, (messages, transformer, locale) -> {
			confirm(extractBody(req), userId, messages);
			return transformer.format(crm.disableUser(userId), locale);
		});
	}

	@PutMapping("/rest/user/{username}/actions/disable")
	public void disableUserByUsername(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("username") String username) throws IOException {
		disableUser(req, res, crm.findUserSummaryByUsername(username).getUserId());
	}

}