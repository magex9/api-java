package ca.magex.crm.graphql.datafetcher;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.roles.User;
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
			logger.info("Entering countPersons@" + PersonDataFetcher.class.getSimpleName());
			return (int) crm.countPersons(new PersonsFilter(extractFilter(environment)));
		};
	}

	public DataFetcher<Page<PersonDetails>> findPersons() {
		return (environment) -> {
			logger.info("Entering findPersons@" + PersonDataFetcher.class.getSimpleName());
			return crm.findPersonDetails(new PersonsFilter(extractFilter(environment)), extractPaging(environment));
		};
	}

	public DataFetcher<PersonDetails> byUser() {
		return (environment) -> {
			logger.info("Entering ByUser@" + PersonDataFetcher.class.getSimpleName());
			User user = environment.getSource();
			return crm.findPersonDetails(user.getPerson().getPersonId());
		};
	}

	public DataFetcher<PersonDetails> byOrganization() {
		return (environment) -> {
			logger.info("Entering byOrganization@" + PersonDataFetcher.class.getSimpleName());
			OrganizationDetails organization = environment.getSource();
			if (organization.getMainContactId() != null) {
				return crm.findPersonDetails(organization.getMainContactId());
			} else {
				return null;
			}
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

	public DataFetcher<PersonDetails> updatePerson() {
		return (environment) -> {
			logger.info("Entering updatePerson@" + PersonDataFetcher.class.getSimpleName());
			Identifier personId = new Identifier((String) environment.getArgument("personId"));
			PersonDetails person = crm.findPersonDetails(personId);
			/* always do status first because the others depend on status for validation */
			if (environment.getArgument("status") != null) {
				String status = StringUtils.upperCase(environment.getArgument("status"));
				switch (status) {
				case "ACTIVE":
					if (person.getStatus() != Status.ACTIVE) {
						crm.enablePerson(personId);
						person = person.withStatus(Status.ACTIVE);
					}
					break;
				case "INACTIVE":
					if (person.getStatus() != Status.INACTIVE) {
						crm.disablePerson(personId);
						person = person.withStatus(Status.INACTIVE);
					}
					break;
				default:
					throw new ApiException("Invalid status '" + status + "', one of {ACTIVE, INACTIVE} expected");
				}
			}
			if (environment.getArgument("name") != null) {
				PersonName newName = extractPersonName(environment, "name");
				if (!person.getLegalName().equals(newName)) {
					person = crm.updatePersonName(personId, newName);
				}
			}
			if (environment.getArgument("address") != null) {
				MailingAddress newAddress = extractMailingAddress(environment, "address");
				if (!person.getAddress().equals(newAddress)) {
					person = crm.updatePersonAddress(personId, newAddress);
				}
			}
			if (environment.getArgument("communication") != null) {
				Communication newCommunication = extractCommunication(environment, "communication");
				if (!person.getCommunication().equals(newCommunication)) {
					person = crm.updatePersonCommunication(personId, newCommunication);
				}
			}
			if (environment.getArgument("position") != null) {
				BusinessPosition newPosition = extractBusinessPosition(environment, "position");
				if (!person.getPosition().equals(newPosition)) {
					person = crm.updatePersonBusinessPosition(personId, newPosition);
				}
			}
			return person;
		};
	}
}
