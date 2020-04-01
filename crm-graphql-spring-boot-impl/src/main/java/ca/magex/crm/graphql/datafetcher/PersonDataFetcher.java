package ca.magex.crm.graphql.datafetcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.services.OrganizationService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.graphql.controller.OrganizationController;
import graphql.schema.DataFetcher;

/**
 * Contains the data fetcher implementations for each of the person API methods
 * 
 * @author Jonny
 */
public class PersonDataFetcher extends AbstractDataFetcher {

	private static Logger logger = LoggerFactory.getLogger(OrganizationController.class);

	public PersonDataFetcher(OrganizationService organizations) {
		super(organizations);
	}

	public DataFetcher<PersonDetails> findPerson() {
		return (environment) -> {
			logger.debug("Entering findPerson@" + PersonDataFetcher.class.getSimpleName());
			String personId = environment.getArgument("personId");
			return organizations.findPerson(new Identifier(personId));
		};
	}
	
	public DataFetcher<Integer> countPersons() {
		return (environment) -> {
			logger.debug("Entering countPersons@" + OrganizationDataFetcher.class.getSimpleName());
			return (int) organizations.countPersons(
					new PersonsFilter(extractFilter(environment)));
		};
	}

	public DataFetcher<Page<PersonDetails>> findPersons() {
		return (environment) -> {
			logger.debug("Entering findPersons@" + OrganizationDataFetcher.class.getSimpleName());
			return organizations.findPersonDetails(
					new PersonsFilter(extractFilter(environment)), 
					extractPaging(environment));
		};
	}

	public DataFetcher<PersonDetails> createPerson() {
		return (environment) -> {
			logger.debug("Entering createPerson@" + PersonDataFetcher.class.getSimpleName());

			return organizations.createPerson(
					new Identifier((String) environment.getArgument("organizationId")),
					extractPersonName(environment, "name"),
					extractMailingAddress(environment, "address"),
					extractCommunication(environment, "communication"),
					extractBusinessPosition(environment, "position"));
		};
	}

	public DataFetcher<PersonDetails> enablePerson() {
		return (environment) -> {
			logger.debug("Entering enablePerson@" + PersonDataFetcher.class.getSimpleName());
			Identifier personId = new Identifier((String) environment.getArgument("personId"));
			organizations.enablePerson(personId);
			return organizations.findPerson(personId);
		};
	}

	public DataFetcher<PersonDetails> disablePerson() {
		return (environment) -> {
			logger.debug("Entering disablePerson@" + PersonDataFetcher.class.getSimpleName());
			Identifier personId = new Identifier((String) environment.getArgument("personId"));
			organizations.disablePerson(personId);
			return organizations.findPerson(personId);
		};
	}

	public DataFetcher<PersonDetails> updatePerson() {
		return (environment) -> {
			logger.debug("Entering updatePerson@" + PersonDataFetcher.class.getSimpleName());
			Identifier personId = new Identifier((String) environment.getArgument("personId"));
			if (environment.getArgument("name") != null) {
				organizations.updatePersonName(
						personId,
						extractPersonName(environment, "name"));
			}
			if (environment.getArgument("address") != null) {
				organizations.updatePersonAddress(
						personId,
						extractMailingAddress(environment, "address"));
			}
			if (environment.getArgument("communication") != null) {
				organizations.updatePersonCommunication(
						personId,
						extractCommunication(environment, "communication"));
			}
			if (environment.getArgument("position") != null) {
				organizations.updatePersonBusinessUnit(
						personId,
						extractBusinessPosition(environment, "position"));
			}
			return organizations.findPerson(personId);
		};
	}
	
	public DataFetcher<PersonDetails> addUserRole() {
		return (environment) -> {
			logger.debug("Entering addUserRole@" + PersonDataFetcher.class.getSimpleName());
			Identifier personId = new Identifier((String) environment.getArgument("personId"));
			return organizations.addUserRole(
					personId, 
					extractRole(environment, "role"));
		};
	}
	
	public DataFetcher<PersonDetails> removeUserRole() {
		return (environment) -> {
			logger.debug("Entering removeUserRole@" + PersonDataFetcher.class.getSimpleName());
			Identifier personId = new Identifier((String) environment.getArgument("personId"));
			return organizations.removeUserRole(
					personId, 
					extractRole(environment, "role"));
		};
	}
}
