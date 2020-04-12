package ca.magex.crm.graphql.datafetcher;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.graphql.controller.GraphQLController;
import graphql.schema.DataFetcher;

/**
 * Contains the data fetcher implementations for each of the person API methods
 * 
 * @author Jonny
 */
@Component
public class PersonDataFetcher extends AbstractDataFetcher {

	private static Logger logger = LoggerFactory.getLogger(GraphQLController.class);

	public DataFetcher<PersonDetails> findPerson() {
		return (environment) -> {
			logger.info("Entering findPerson@" + PersonDataFetcher.class.getSimpleName());
			String personId = environment.getArgument("personId");
			return crm.findPersonDetails(new Identifier(personId));
		};
	}
	
	public DataFetcher<Integer> countPersons() {
		return (environment) -> {
			logger.info("Entering countPersons@" + OrganizationDataFetcher.class.getSimpleName());
			return (int) crm.countPersons(
					extractFilter(extractFilter(environment)));
		};
	}

	public DataFetcher<Page<PersonDetails>> findPersons() {
		return (environment) -> {
			logger.info("Entering findPersons@" + OrganizationDataFetcher.class.getSimpleName());
			return crm.findPersonDetails(
					extractFilter(extractFilter(environment)), 
					extractPaging(environment));
		};
	}

	public DataFetcher<PersonDetails> createPerson() {
		return (environment) -> {
			logger.info("Entering createPerson@" + PersonDataFetcher.class.getSimpleName());

			return crm.createPerson(
					new Identifier((String) environment.getArgument("organizationId")),
					extractPersonName(environment, "name"),
					extractMailingAddress(environment, "address"),
					extractCommunication(environment, "communication"),
					extractBusinessPosition(environment, "position"));
		};
	}

	public DataFetcher<PersonDetails> enablePerson() {
		return (environment) -> {
			logger.info("Entering enablePerson@" + PersonDataFetcher.class.getSimpleName());
			Identifier personId = new Identifier((String) environment.getArgument("personId"));
			crm.enablePerson(personId);
			return crm.findPersonDetails(personId);
		};
	}

	public DataFetcher<PersonDetails> disablePerson() {
		return (environment) -> {
			logger.info("Entering disablePerson@" + PersonDataFetcher.class.getSimpleName());
			Identifier personId = new Identifier((String) environment.getArgument("personId"));
			crm.disablePerson(personId);
			return crm.findPersonDetails(personId);
		};
	}

	public DataFetcher<PersonDetails> updatePerson() {
		return (environment) -> {
			logger.info("Entering updatePerson@" + PersonDataFetcher.class.getSimpleName());
			Identifier personId = new Identifier((String) environment.getArgument("personId"));
			if (environment.getArgument("name") != null) {
				crm.updatePersonName(
						personId,
						extractPersonName(environment, "name"));
			}
			if (environment.getArgument("address") != null) {
				crm.updatePersonAddress(
						personId,
						extractMailingAddress(environment, "address"));
			}
			if (environment.getArgument("communication") != null) {
				crm.updatePersonCommunication(
						personId,
						extractCommunication(environment, "communication"));
			}
			if (environment.getArgument("position") != null) {
				crm.updatePersonBusinessPosition(
						personId,
						extractBusinessPosition(environment, "position"));
			}
			return crm.findPersonDetails(personId);
		};
	}
	
	public DataFetcher<PersonDetails> addUserRole() {
		return (environment) -> {
			logger.info("Entering addUserRole@" + PersonDataFetcher.class.getSimpleName());
			Identifier personId = new Identifier((String) environment.getArgument("personId"));
			return crm.addUserRole(
					personId, 
					extractRole(environment, "role"));
		};
	}
	
	public DataFetcher<PersonDetails> removeUserRole() {
		return (environment) -> {
			logger.info("Entering removeUserRole@" + PersonDataFetcher.class.getSimpleName());
			Identifier personId = new Identifier((String) environment.getArgument("personId"));
			return crm.removeUserRole(
					personId, 
					extractRole(environment, "role"));
		};
	}
	
	public DataFetcher<PersonDetails> setUserRoles() {
		return (environment) -> {
			logger.info("Entering setUserRoles@" + PersonDataFetcher.class.getSimpleName());
			Identifier personId = new Identifier((String) environment.getArgument("personId"));			
			return crm.setUserRoles(
					personId, 
					extractRoles(environment, "roles"));
		};
	}
	
	private PersonsFilter extractFilter(Map<String, Object> filter) {
		String displayName = (String) filter.get("displayName");
		String organizationId = (String) filter.get("organizationId");
		Status status = null;
		if (filter.containsKey("status") && StringUtils.isNotBlank((String) filter.get("status"))) {
			try {
				status = Status.valueOf((String) filter.get("status"));
			}
			catch(IllegalArgumentException e) {
				throw new ApiException("Invalid status value '" + filter.get("status") + "' expected one of {" + StringUtils.join(Status.values(), ",") + "}");
			}
		}
		return new PersonsFilter(organizationId == null ? null : new Identifier(organizationId), displayName, status);
	}
}
