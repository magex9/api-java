package ca.magex.crm.graphql.datafetcher;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.UserDetails;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.system.id.BusinessRoleIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
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
			return crm.findPersonDetails(new PersonIdentifier(personId));
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
	
	public DataFetcher<Map<String,Boolean>> findPersonActions() {
		return (environment) -> {
			logger.info("Entering findPersonActions@" + PersonDataFetcher.class.getSimpleName());
			PersonDetails source = environment.getSource();
			return Map.of(
					"update", crm.canUpdatePerson(source.getPersonId()),
					"enable", crm.canEnablePerson(source.getPersonId()),
					"disable", crm.canDisablePerson(source.getPersonId()),
					"createUser", crm.canCreateUserForPerson(source.getPersonId()));
		};
	}

	public DataFetcher<PersonDetails> byUser() {
		return (environment) -> {
			logger.info("Entering ByUser@" + PersonDataFetcher.class.getSimpleName());
			UserDetails user = environment.getSource();
			return crm.findPersonDetails(user.getPersonId());
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
					new OrganizationIdentifier((String) environment.getArgument("organizationId")),
					environment.getArgument("displayName"),
					extractPersonName(environment, "legalName"),
					extractMailingAddress(environment, "address"),
					extractCommunication(environment, "communication"),
					extractBusinessRoles(environment, "businessRoleIds"));
		};
	}

	public DataFetcher<PersonDetails> updatePerson() {
		return (environment) -> {
			logger.info("Entering updatePerson@" + PersonDataFetcher.class.getSimpleName());
			PersonIdentifier personId = new PersonIdentifier((String) environment.getArgument("personId"));
			PersonDetails person = crm.findPersonDetails(personId);
			if (environment.getArgument("displayName") != null) {
				String displayName = environment.getArgument("displayName");
				if (!StringUtils.equals(person.getDisplayName(), displayName)) {
					person = crm.updatePersonDisplayName(personId, displayName);
				}
			}
			if (environment.getArgument("legalName") != null) {
				PersonName newName = extractPersonName(environment, "legalName");
				if (!person.getLegalName().equals(newName)) {
					person = crm.updatePersonLegalName(personId, newName);
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
			if (environment.getArgument("businessRoleIds") != null) {
				List<BusinessRoleIdentifier> businessRoles = extractBusinessRoles(environment, "businessRoleIds");
				if (!person.getBusinessRoleIds().containsAll(businessRoles) || !businessRoles.containsAll(person.getBusinessRoleIds())) {
					person = crm.updatePersonBusinessRoles(personId, businessRoles);
				}
			}
			return person;
		};
	}
	
	public DataFetcher<PersonDetails> enablePerson() {
		return (environment) -> {
			logger.info("Entering enablePerson@" + PersonDataFetcher.class.getSimpleName());
			PersonIdentifier personId = new PersonIdentifier((String) environment.getArgument("personId"));
			return crm.findPersonDetails(crm.enablePerson(personId).getPersonId());
		};
	}
	
	public DataFetcher<PersonDetails> disablePerson() {
		return (environment) -> {
			logger.info("Entering disablePerson@" + PersonDataFetcher.class.getSimpleName());
			PersonIdentifier personId = new PersonIdentifier((String) environment.getArgument("personId"));
			return crm.findPersonDetails(crm.disablePerson(personId).getPersonId());
		};
	}
}
