package ca.magex.crm.graphql.datafetcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.magex.crm.api.crm.PersonDetails;
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
			String id = environment.getArgument("personId");
			return organizations.findPerson(new Identifier(id));
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
}
