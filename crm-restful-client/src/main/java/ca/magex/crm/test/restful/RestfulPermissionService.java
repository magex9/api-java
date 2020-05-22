package ca.magex.crm.test.restful;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.filters.GroupsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.RolesFilter;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.services.CrmPermissionService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;
import ca.magex.json.model.JsonObject;

public class RestfulPermissionService implements CrmPermissionService {

	private String server;

	private Locale locale;
	
	private String contentType;
	
	public RestfulPermissionService(String server, Locale locale) {
		this.server = server;
		this.locale = locale;
		this.contentType = "application/json";
	}
	
	@Override
	public FilteredPage<Group> findGroups(@NotNull GroupsFilter filter, @NotNull Paging paging) {
//		JsonObject result = get("/api/groups", new JsonObject()
//			.with("displayName", filter.getEnglishName())
//			.with("status", filter.getStatus().toString().toLowerCase())
//			.with("page", paging.getPageNumber())
//			.with("limit", paging.getPageSize()));
//		List<OrganizationSummary> items = result.getArray("content").stream()
//					.map(item -> (JsonObject)item)
//					.map(item -> new OrganizationSummary(
//				new Identifier(item.getString("organizationId")),
//				Status.valueOf(item.getString("status").toUpperCase()),
//				item.getString("displayName")))
//			.collect(Collectors.toList());
//		long total = result.getLong("total");
//		return new FilteredPage<OrganizationSummary>(filter, paging, items, total);
		return null;
	}

	@Override
	public Group findGroup(@NotNull Identifier groupId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Group createGroup(@NotNull Localized name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Group updateGroupName(@NotNull Identifier groupId, @NotNull Localized name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Group enableGroup(@NotNull Identifier groupId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Group disableGroup(@NotNull Identifier groupId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FilteredPage<Role> findRoles(@NotNull RolesFilter filter, @NotNull Paging paging) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Role findRole(@NotNull Identifier roleId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Role createRole(@NotNull Identifier groupId, @NotNull Localized name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Role updateRoleName(@NotNull Identifier roleId, @NotNull Localized name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Role enableRole(@NotNull Identifier roleId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Role disableRole(@NotNull Identifier roleId) {
		// TODO Auto-generated method stub
		return null;
	}

}
