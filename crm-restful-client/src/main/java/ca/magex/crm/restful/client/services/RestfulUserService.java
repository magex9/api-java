package ca.magex.crm.restful.client.services;

public class RestfulUserService { //implements CrmUserService {
	
//	private RestTemplateClient client;
//	
//	public RestfulUserService(RestTemplateClient client) {
//		this.client = client;
//	}
//
//	@Override
//	public UserDetails createUser(PersonIdentifier personId, String username,
//			List<AuthenticationRoleIdentifier> authenticationRoleIds) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public UserDetails createUser(OrganizationIdentifier organizationId, String displayName, UserName legalName, MailingAddress address,
//			Communication communication, List<BusinessRoleIdentifier> businessRoleIds) {
//		UserDetails details = prototypeUser(organizationId, displayName, legalName, address, communication, businessRoleIds);
//		JsonObject json = client.post("/users", (JsonObject)client.format(details, UserDetails.class));
//		return client.parse(json, UserDetails.class);
//	}
//	
//	@Override
//	public UserSummary enableUser(UserIdentifier userId) {
//		JsonObject json = client.put(userId + "/enable", new JsonObject().with("confirm", true));
//		return client.parse(json, UserSummary.class);	
//	}
//
//	@Override
//	public UserSummary disableUser(UserIdentifier userId) {
//		JsonObject json = client.put(userId + "/disable", new JsonObject().with("confirm", true));
//		return client.parse(json, UserSummary.class);	
//	}
//
//	@Override
//	public UserDetails updateUserDisplayName(UserIdentifier userId, String displayName) {
//		JsonObject json = client.patch(userId, new JsonObject().with("displayName", displayName));
//		return client.parse(json, UserDetails.class);
//	}
//
//	@Override
//	public UserDetails updateUserLegalName(UserIdentifier userId, UserName legalName) {
//		JsonObject json = client.patch(userId, new JsonObject().with("legalName", client.format(legalName, UserName.class)));
//		return client.parse(json, UserDetails.class);
//	}
//
//	@Override
//	public UserDetails updateUserRoles(UserIdentifier userId,
//			List<AuthenticationRoleIdentifier> authenticationRoleIds) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	
//	@Override
//	public UserDetails updateUserRoles(UserIdentifier userId, List<BusinessRoleIdentifier> businessRoleIds) {
//		JsonObject json = client.patch(userId, new JsonObject().with("businessRoleIds", client.formatOptions(businessRoleIds)));
//		return client.parse(json, UserDetails.class);
//	}
//
//	@Override
//	public UserSummary findUserSummary(UserIdentifier userId) {
//		JsonObject json = client.get(userId);
//		return client.parse(json, UserSummary.class);
//	}
//
//	@Override
//	public UserDetails findUserDetails(UserIdentifier userId) {
//		JsonObject json = client.get(userId + "/details");
//		return client.parse(json, UserDetails.class);
//	}
//	
//	public JsonObject formatFilter(UsersFilter filter) {
//		return new JsonObject()
//			.with("displayName", filter.getDisplayName())
//			.with("status", filter.getStatus() == null ? null : (client.format(filter.getStatus(), Status.class)).getString("@value"))
//			.with("organizationId", filter.getOrganizationId() == null ? null : filter.getOrganizationId().toString())
//			.prune();
//	}
//
//	@Override
//	public long countUsers(UsersFilter filter) {
//		JsonObject json = client.get("/users/count", formatFilter(filter));
//		return json.getLong("total");
//	}
//
//	@Override
//	public FilteredPage<UserDetails> findUserDetails(UsersFilter filter, Paging paging) {
//		JsonObject json = client.get("/users/details", client.page(formatFilter(filter), paging));
//		List<UserDetails> content = client.parseList(json.getArray("content"), UserDetails.class);
//		return new FilteredPage<>(filter, paging, content, json.getLong("total"));
//	}
//
//	@Override
//	public FilteredPage<UserSummary> findUserSummaries(UsersFilter filter, Paging paging) {
//		JsonObject json = client.get("/users", client.page(formatFilter(filter), paging));
//		List<UserSummary> content = client.parseList(json.getArray("content"), UserSummary.class);
//		return new FilteredPage<>(filter, paging, content, json.getLong("total"));
//	}
//
//	@Override
//	public boolean changePassword(UserIdentifier userId, String currentPassword, String newPassword) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public String resetPassword(UserIdentifier userId) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public UserDetails findUserByUsername(String username) {
//		// TODO Auto-generated method stub
//		return null;
//	}

}
