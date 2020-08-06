package ca.magex.crm.restful.client.services;

import java.util.List;

import ca.magex.crm.api.crm.UserDetails;
import ca.magex.crm.api.crm.UserSummary;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.AuthenticationRoleIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.system.id.UserIdentifier;
import ca.magex.crm.restful.client.RestTemplateClient;
import ca.magex.json.model.JsonBoolean;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonText;

public class RestfulUserService implements CrmUserService {
	
	private RestTemplateClient client;
	
	public RestfulUserService(RestTemplateClient client) {
		this.client = client;
	}

	@Override
	public UserDetails createUser(PersonIdentifier personId, String username,
			List<AuthenticationRoleIdentifier> authenticationRoleIds) {
		UserDetails details = prototypeUser(personId, username, authenticationRoleIds);
		JsonObject json = client.post("/users", (JsonObject)client.format(details, UserDetails.class));
		return client.parse(json, UserDetails.class);
	}

	@Override
	public UserSummary enableUser(UserIdentifier userId) {
		JsonObject json = client.put(userId + "/enable", new JsonObject().with("confirm", true));
		return client.parse(json, UserSummary.class);	
	}

	@Override
	public UserSummary disableUser(UserIdentifier userId) {
		JsonObject json = client.put(userId + "/disable", new JsonObject().with("confirm", true));
		return client.parse(json, UserSummary.class);	
	}

	@Override
	public boolean changePassword(UserIdentifier userId, String currentPassword, String newPassword) {
		JsonBoolean json = client.put("/passwords/" + userId.getCode() + "/change", new JsonObject()
			.with("currentPassword", currentPassword)
			.with("newPassword", newPassword));
		return client.parse(json, Boolean.class);	
	}

	@Override
	public String resetPassword(UserIdentifier userId) {
		JsonText json = client.put("/passwords/" + userId.getCode() + "/reset", new JsonObject()
			.with("confirm", true));
		return client.parse(json, String.class);	
	}

	@Override
	public UserDetails updateUserRoles(UserIdentifier userId,
			List<AuthenticationRoleIdentifier> authenticationRoleIds) {
		JsonObject json = client.patch(userId, new JsonObject()
			.with("authenticationRoleIds", client.formatOptions(authenticationRoleIds)));
		return client.parse(json, UserDetails.class);
	}

	@Override
	public UserDetails findUserByUsername(String username) {
		JsonObject json = client.get("/user/" + username + "/details");
		return client.parse(json, UserDetails.class);
	}

	@Override
	public UserSummary findUserSummary(UserIdentifier userId) {
		JsonObject json = client.get(userId);
		return client.parse(json, UserSummary.class);
	}

	@Override
	public UserDetails findUserDetails(UserIdentifier userId) {
		JsonObject json = client.get(userId + "/details");
		return client.parse(json, UserDetails.class);
	}
	
	public JsonObject formatFilter(UsersFilter filter) {
		return new JsonObject()
			.with("organizationId", filter.getOrganizationId() == null ? null : filter.getOrganizationId().toString())
			.with("personId", filter.getPersonId() == null ? null : filter.getOrganizationId().toString())
			.with("status", filter.getStatus() == null ? null : (client.format(filter.getStatus(), Status.class)).getString("@value"))
			.with("username", filter.getUsername())
			.with("authenticationRoleId", filter.getAuthenticationRoleId() == null ? null : filter.getOrganizationId().toString())
			.prune();
	}

	@Override
	public long countUsers(UsersFilter filter) {
		JsonObject json = client.get("/users/count", formatFilter(filter));
		return json.getLong("total");
	}

	@Override
	public FilteredPage<UserDetails> findUserDetails(UsersFilter filter, Paging paging) {
		JsonObject json = client.get("/users/details", client.page(formatFilter(filter), paging));
		List<UserDetails> content = client.parseList(json.getArray("content"), UserDetails.class);
		return new FilteredPage<>(filter, paging, content, json.getLong("total"));
	}

	@Override
	public FilteredPage<UserSummary> findUserSummaries(UsersFilter filter, Paging paging) {
		JsonObject json = client.get("/users", client.page(formatFilter(filter), paging));
		List<UserSummary> content = client.parseList(json.getArray("content"), UserSummary.class);
		return new FilteredPage<>(filter, paging, content, json.getLong("total"));
	}

}
