package ca.magex.crm.graphql.datafetcher;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.crm.User;
import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.AuthenticationRoleIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.system.id.UserIdentifier;
import ca.magex.crm.graphql.controller.GraphQLController;
import graphql.schema.DataFetcher;

/**
 * Contains the data fetcher implementations for each of the user API methods
 * 
 * @author Jonny
 */
@Component
public class UserDataFetcher extends AbstractDataFetcher {

	private static Logger logger = LoggerFactory.getLogger(GraphQLController.class);

	public DataFetcher<User> createUser() {
		return (environment) -> {
			logger.info("Entering createUser@" + UserDataFetcher.class.getSimpleName());
			/* find the person summary for this person id */
			PersonSummary person = crm.findPersonSummary(new PersonIdentifier((String) environment.getArgument("personId")));
			/* create the new user */
			return crm.createUser(
					person.getOrganizationId(),
					person.getPersonId(),
					environment.getArgument("username"),
					extractAuthenticationRoles(environment, "authenticationRoles"));
		};
	}

	public DataFetcher<User> findUser() {
		return (environment) -> {
			logger.info("Entering findUser@" + UserDataFetcher.class.getSimpleName());
			return crm.findUser(
					new UserIdentifier(environment.getArgument("userId")));
		};
	}

	public DataFetcher<Integer> countUsers() {
		return (environment) -> {
			logger.info("Entering findUsers@" + UserDataFetcher.class.getSimpleName());
			return (int) crm.countUsers(
					new UsersFilter(extractFilter(environment)));
		};
	}

	public DataFetcher<Page<User>> findUsers() {
		return (environment) -> {
			logger.info("Entering findUsers@" + UserDataFetcher.class.getSimpleName());
			return crm.findUsers(
					new UsersFilter(extractFilter(environment)), extractPaging(environment));
		};
	}

	public DataFetcher<User> updateUser() {
		return (environment) -> {
			logger.info("Entering updateUser@" + UserDataFetcher.class.getSimpleName());
			UserIdentifier userId = new UserIdentifier((String) environment.getArgument("userId"));
			User user = crm.findUser(userId);
			/* update status first since other validation requires status */
			if (environment.getArgument("status") != null) {
				String status = StringUtils.upperCase(environment.getArgument("status"));
				switch (status) {
				case "ACTIVE":
					if (user.getStatus() != Status.ACTIVE) {
						user = crm.enableUser(userId);
					}
					break;
				case "INACTIVE":
					if (user.getStatus() != Status.INACTIVE) {
						user = crm.disableUser(userId);
					}
					break;
				default:
					throw new ApiException("Invalid status '" + status + "', one of {ACTIVE, INACTIVE} expected");
				}
			}
			if (environment.getArgument("authenticationRoles") != null) {
				List<AuthenticationRoleIdentifier> authenticationRoles = extractAuthenticationRoles(environment, "authenticationRoles");
				if (!user.getAuthenticationRoleIds().containsAll(authenticationRoles) || !authenticationRoles.containsAll(user.getAuthenticationRoleIds())) {
					user = crm.updateUserRoles(userId, authenticationRoles);
				}
			}
			return user;
		};
	}
	
	public DataFetcher<String> resetUserPassword() {
		return (environment) -> {
			logger.info("Entering resetPassword@" + UserDataFetcher.class.getSimpleName());
			return crm.resetPassword(
					new UserIdentifier((String) environment.getArgument("userId")));			
		};
	}
	
	public DataFetcher<Boolean> changeUserPassword() {
		return (environment) -> {
			logger.info("Entering changePassword@" + UserDataFetcher.class.getSimpleName());
			return crm.changePassword(
					new UserIdentifier((String) environment.getArgument("userId")), 
					environment.getArgument("currentPassword"), 
					environment.getArgument("newPassword"));			
		};
	}
}