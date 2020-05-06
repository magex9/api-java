package ca.magex.crm.graphql.datafetcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.system.Identifier;
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
	
	public DataFetcher<User> findUserById() {
		return (environment) -> {
			logger.info("Entering findUserById@" + UserDataFetcher.class.getSimpleName());
			String userId = environment.getArgument("userId");
			return crm.findUserById(new Identifier(userId));
		};
	}
	
	public DataFetcher<User> findUserByUsername() {
		return (environment) -> {
			logger.info("Entering findUserByUsername@" + UserDataFetcher.class.getSimpleName());
			String username = environment.getArgument("username");
			return crm.findUserByUsername(username);
		};
	}
	
	public DataFetcher<User> createUser() {
		return (environment) -> {
			logger.info("Entering createUser@" + UserDataFetcher.class.getSimpleName());
			Identifier personId = new Identifier((String) environment.getArgument("personId"));
			String username = environment.getArgument("username");
			return crm.createUser(
					personId, 
					username, 
					environment.getArgument("roles"));
		};
	}
	
	public DataFetcher<User> addUserRole() {
		return (environment) -> {
			logger.info("Entering addUserRole@" + UserDataFetcher.class.getSimpleName());
			Identifier userId = new Identifier((String) environment.getArgument("userId"));
			return crm.addUserRole(
					userId, 
					environment.getArgument("role"));
		};
	}
	
	public DataFetcher<User> removeUserRole() {
		return (environment) -> {
			logger.info("Entering removeUserRole@" + UserDataFetcher.class.getSimpleName());
			Identifier userId = new Identifier((String) environment.getArgument("userId"));
			return crm.removeUserRole(
					userId, 
					environment.getArgument("role"));
		};
	}
	
	public DataFetcher<User> setUserRoles() {
		return (environment) -> {
			logger.info("Entering setUserRoles@" + UserDataFetcher.class.getSimpleName());
			Identifier userId = new Identifier((String) environment.getArgument("userId"));			
			return crm.setUserRoles(
					userId, 
					environment.getArgument("roles"));
		};
	}
	
	public DataFetcher<User> setUserPassword() {
		return (environment) -> {
			logger.info("Entering setUserPassword@" + UserDataFetcher.class.getSimpleName());
			Identifier userId = new Identifier((String) environment.getArgument("userId"));
			String password = environment.getArgument("password");
			return crm.setUserPassword(
					userId, 
					password,
					false);
		};
	}
}
