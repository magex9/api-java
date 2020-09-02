package ca.magex.crm.graphql.datafetcher;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.crm.UserDetails;
import ca.magex.crm.api.filters.UsersFilter;
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

	public DataFetcher<UserDetails> createUser() {
		return (environment) -> {
			logger.info("Entering createUser@" + UserDataFetcher.class.getSimpleName());
			/* create the new user */
			return crm.createUser(
					new PersonIdentifier((String) environment.getArgument("personId")),
					environment.getArgument("username"),
					extractAuthenticationRoles(environment, "authenticationRoleIds"));
		};
	}

	public DataFetcher<UserDetails> findUser() {
		return (environment) -> {
			logger.info("Entering findUser@" + UserDataFetcher.class.getSimpleName());
			return crm.findUserDetails(
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

	public DataFetcher<Page<UserDetails>> findUsers() {
		return (environment) -> {
			logger.info("Entering findUsers@" + UserDataFetcher.class.getSimpleName());
			return crm.findUserDetails(
					new UsersFilter(extractFilter(environment)), extractPaging(environment));
		};
	}
	
	public DataFetcher<Map<String,Boolean>> findUserActions() {
		return (environment) -> {
			logger.info("Entering findUserActions@" + UserDataFetcher.class.getSimpleName());
			UserDetails source = environment.getSource();
			return Map.of(
					"update", crm.canUpdateUserRole(source.getUserId()),
					"enable", crm.canEnableUser(source.getUserId()),
					"disable", crm.canDisableUser(source.getUserId()),
					"changePassword", crm.canUpdateUserPassword(source.getUserId()));
		};
	}

	public DataFetcher<UserDetails> updateUser() {
		return (environment) -> {
			logger.info("Entering updateUser@" + UserDataFetcher.class.getSimpleName());
			UserIdentifier userId = new UserIdentifier((String) environment.getArgument("userId"));
			UserDetails user = crm.findUserDetails(userId);
			if (environment.getArgument("authenticationRoleIds") != null) {
				List<AuthenticationRoleIdentifier> authenticationRoles = extractAuthenticationRoles(environment, "authenticationRoleIds");
				if (!user.getAuthenticationRoleIds().containsAll(authenticationRoles) || !authenticationRoles.containsAll(user.getAuthenticationRoleIds())) {
					user = crm.updateUserAuthenticationRoles(userId, authenticationRoles);
				}
			}
			return user;
		};
	}
	
	public DataFetcher<UserDetails> enableUser() {
		return (environment) -> {
			logger.info("Entering enableUser@" + UserDataFetcher.class.getSimpleName());
			UserIdentifier userId = new UserIdentifier((String) environment.getArgument("userId"));
			return crm.findUserDetails(crm.enableUser(userId).getUserId());
		};
	}
	
	public DataFetcher<UserDetails> disableUser() {
		return (environment) -> {
			logger.info("Entering disableUser@" + UserDataFetcher.class.getSimpleName());
			UserIdentifier userId = new UserIdentifier((String) environment.getArgument("userId"));
			return crm.findUserDetails(crm.disableUser(userId).getUserId());
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