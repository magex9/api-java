package ca.magex.crm.graphql.datafetcher;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.graphql.controller.GraphQLController;
import graphql.schema.DataFetcher;

@Component
public class OptionDataFetcher extends AbstractDataFetcher {

	private static Logger logger = LoggerFactory.getLogger(GraphQLController.class);

	public DataFetcher<List<Option>> findAuthenticationGroupsForOrg() {
		return (environment) -> {
			logger.info("Entering findAuthenticationGroupsForOrg@" + OptionDataFetcher.class.getSimpleName());
			OrganizationDetails source = environment.getSource();
			return source.getAuthenticationGroupIds()
				.stream()
				.map((id) -> crm.findOption(id))
				.collect(Collectors.toList());			
		};
	}
	
	public DataFetcher<List<Option>> findBusinessGroupsForOrg() {
		return (environment) -> {
			logger.info("Entering findBusinessGroupsForOrg@" + OptionDataFetcher.class.getSimpleName());
			OrganizationDetails source = environment.getSource();
			return source.getBusinessGroupIds()
				.stream()
				.map((id) -> crm.findOption(id))
				.collect(Collectors.toList());			
		};
	}
	
	public DataFetcher<List<Option>> findBusinessRolesForPerson() {
		return (environment) -> {
			logger.info("Entering findBusinessGroupsForOrg@" + OptionDataFetcher.class.getSimpleName());
			PersonDetails source = environment.getSource();
			return source.getBusinessRoleIds()
				.stream()
				.map((id) -> crm.findOption(id))
				.collect(Collectors.toList());			
		};
	}
}
