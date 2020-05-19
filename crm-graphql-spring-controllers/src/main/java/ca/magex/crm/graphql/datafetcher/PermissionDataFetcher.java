package ca.magex.crm.graphql.datafetcher;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.filters.GroupsFilter;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.graphql.controller.GraphQLController;
import graphql.schema.DataFetcher;

/**
 * Contains the data fetcher implementations for fetching permissions such as roles
 * 
 * @author Jonny
 */
@Component
public class PermissionDataFetcher extends AbstractDataFetcher {

	private static Logger logger = LoggerFactory.getLogger(GraphQLController.class);

	public DataFetcher<List<Group>> groupsByOrganization() {
		return (environment) -> {
			logger.info("Entering byOrganization@" + PermissionDataFetcher.class.getSimpleName());
			OrganizationDetails organization = environment.getSource();
			return organization.getGroups().stream().map(groupCode -> crm.findGroupByCode(groupCode)).collect(Collectors.toList());			
		};
	}
	
	public DataFetcher<Group> findGroup() {
		return (environment) -> {
			logger.info("Entering findGroup@" + PermissionDataFetcher.class.getSimpleName());
			Identifier groupId = new Identifier((String) environment.getArgument("groupId"));
			return crm.findGroup(groupId);
		};
	}
	
	public DataFetcher<Page<Group>> findGroups() {
		return (environment) -> {
			logger.info("Entering findGroups@" + PermissionDataFetcher.class.getSimpleName());
			return crm.findGroups(new GroupsFilter(), extractPaging(environment));
		};
	}
	
	public DataFetcher<Group> createGroup() {
		return (environment) -> {
			logger.info("Entering createGroup@" + PermissionDataFetcher.class.getSimpleName());
			String englishName = environment.getArgument("englishName");
			String frenchName = environment.getArgument("frenchName");
			String code = environment.getArgument("code");
			return crm.createGroup(new Localized(code, englishName, frenchName));
		};
	}
	
	public DataFetcher<Group> updateGroup() {
		return (environment) -> {
			logger.info("Entering updateGroup@" + PermissionDataFetcher.class.getSimpleName());
			Identifier groupId = new Identifier((String) environment.getArgument("groupId"));
			Group group = crm.findGroup(groupId);
			/* need to be careful not to nullify names that aren't passed in for update */
			String englishName = environment.getArgumentOrDefault("englishName", group.getName(Lang.ENGLISH));
			String frenchName = environment.getArgumentOrDefault("frenchName", group.getName(Lang.FRENCH));
			if (!StringUtils.equals(group.getName(Lang.ENGLISH), englishName) || !(StringUtils.equals(group.getName(Lang.FRENCH), frenchName))) {
				group = crm.updateGroupName(groupId, new Localized(group.getCode(), englishName, frenchName));
			}
			/* update the status if provided */
			if (environment.getArgument("status") != null) {
				String status = StringUtils.upperCase(environment.getArgument("status"));
				switch(status) {
				case "ACTIVE":
					if (group.getStatus() != Status.ACTIVE) {
						group = crm.enableGroup(groupId);
					}
					break;
				case "INACTIVE":
					if (group.getStatus() != Status.INACTIVE) {
						group = crm.disableGroup(groupId);
					}
					break;
				default:
					throw new ApiException("Invalid status '" + status + "', one of {ACTIVE, INACTIVE} expected");
				}
			}
			return group;
		};
	}	
	
	public DataFetcher<Role> findRole() {
		return (environment) -> {
			logger.info("Entering findRole@" + PermissionDataFetcher.class.getSimpleName());
			Identifier roleId = new Identifier((String) environment.getArgument("roleId"));
			return crm.findRole(roleId);
		};
	}
	
	public DataFetcher<Role> createRole() {
		return (environment) -> {
			logger.info("Entering createRole@" + PermissionDataFetcher.class.getSimpleName());
			Identifier groupId = new Identifier((String) environment.getArgument("groupId"));
			String code = environment.getArgument("code");
			String englishName = environment.getArgument("englishName");
			String frenchName = environment.getArgument("frenchName");
			return crm.createRole(groupId, new Localized(code, englishName, frenchName));
		};
	}
	
	public DataFetcher<Role> updateRole() {
		return (environment) -> {
			logger.info("Entering updateRole@" + PermissionDataFetcher.class.getSimpleName());
			Identifier roleId = new Identifier((String) environment.getArgument("roleId"));			
			Role role = crm.findRole(roleId);
			/* need to be careful not to nullify names that aren't passed in for update */
			String englishName = environment.getArgumentOrDefault("englishName", role.getName(Lang.ENGLISH));
			String frenchName = environment.getArgumentOrDefault("frenchName", role.getName(Lang.FRENCH));
			if (!StringUtils.equals(role.getName(Lang.ENGLISH), englishName) || !(StringUtils.equals(role.getName(Lang.FRENCH), frenchName))) {
				role = crm.updateRoleName(roleId, new Localized(role.getCode(), englishName, frenchName));
			}
			/* update the status if provided */
			if (environment.getArgument("status") != null) {
				String status = StringUtils.upperCase(environment.getArgument("status"));
				switch(status) {
				case "ACTIVE":
					if (role.getStatus() != Status.ACTIVE) {
						role = crm.enableRole(roleId);
					}
					break;
				case "INACTIVE":
					if (role.getStatus() != Status.INACTIVE) {
						role = crm.disableRole(roleId);
					}
					break;
				default:
					throw new ApiException("Invalid status '" + status + "', one of {ACTIVE, INACTIVE} expected");
				}
			}
			return role;
		};
	}
}
