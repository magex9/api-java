package ca.magex.crm.graphql.datafetcher;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
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

	public DataFetcher<User> findUser() {
		return (environment) -> {
			logger.info("Entering findUser@" + UserDataFetcher.class.getSimpleName());
			String userId = environment.getArgument("username");
			return crm.findUser(new Identifier(userId));
		};
	}
	
	public DataFetcher<Integer> countUsers() {
		return (environment) -> {
			logger.info("Entering findUsers@" + UserDataFetcher.class.getSimpleName());
			return (int) crm.countUsers(
					extractFilter(extractFilter(environment)));
		};
	}

	public DataFetcher<Page<User>> findUsers() {
		return (environment) -> {
			logger.info("Entering findUsers@" + UserDataFetcher.class.getSimpleName());			
			return crm.findUsers(
					extractFilter(extractFilter(environment)),
					extractPaging(environment));
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
	
	public DataFetcher<User> updateUser() {
		return (environment) -> {
			logger.info("Entering updateUser@" + UserDataFetcher.class.getSimpleName());
			Identifier userId = new Identifier((String) environment.getArgument("userId"));
			User user = crm.findUser(userId);
			
			if (environment.getArgument("roles") != null) {				
				user = crm.setRoles(userId, environment.getArgument("roles"));
			}
			if (environment.getArgument("status") != null) {
				String status = StringUtils.upperCase(environment.getArgument("status"));
				switch(status) {
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
			return user;
		};
	}	
	
	private UsersFilter extractFilter(Map<String, Object> filter) {
		String personId = (String) filter.get("personId");
		String organizationId = (String) filter.get("organizationId");
		String role = (String) filter.get("role");
		
		Status status = null;
		if (filter.containsKey("status") && StringUtils.isNotBlank((String) filter.get("status"))) {
			try {
				status = Status.valueOf((String) filter.get("status"));
			}
			catch(IllegalArgumentException e) {
				throw new ApiException("Invalid status value '" + filter.get("status") + "' expected one of {" + StringUtils.join(Status.values(), ",") + "}");
			}
		}
		return new UsersFilter(
				personId == null ? null : new Identifier(personId),
				organizationId == null ? null : new Identifier(organizationId),
				status,
				role);
	}
}