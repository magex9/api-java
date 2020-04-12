package ca.magex.crm.graphql.service;

import java.io.IOException;
import java.util.Locale;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import ca.magex.crm.graphql.datafetcher.LocationDataFetcher;
import ca.magex.crm.graphql.datafetcher.LookupDataFetcher;
import ca.magex.crm.graphql.datafetcher.OrganizationDataFetcher;
import ca.magex.crm.graphql.datafetcher.PersonDataFetcher;
import ca.magex.crm.graphql.error.ApiDataFetcherExceptionHandler;
import graphql.GraphQL;
import graphql.execution.AsyncExecutionStrategy;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;

@Service("graphQLOrganizationService")
public class GraphQLCrmServices {

	private static Logger logger = LoggerFactory.getLogger(GraphQLCrmServices.class);
	
	@Autowired LocationDataFetcher locationDataFetcher;
	@Autowired OrganizationDataFetcher organizationDataFetcher;
	@Autowired PersonDataFetcher personDataFetcher;
	@Autowired LookupDataFetcher lookupDataFetcher;

	@Value("classpath:organizations.graphql")
	private Resource resource;

	private GraphQL graphQL;

	public GraphQL getGraphQL() {
		return graphQL;
	}

	@PostConstruct
	private void loadSchema() throws IOException {
		logger.info("Loading GraphQL Schema " + resource.getFilename());

		/* parse our schema */
		GraphQLSchema graphQLSchema = new SchemaGenerator()
				.makeExecutableSchema(
						new SchemaParser().parse(resource.getFile()),
						buildRuntimeWiring());

		/* create our graphQL engine */
		graphQL = GraphQL
				.newGraphQL(graphQLSchema)
				.queryExecutionStrategy(new AsyncExecutionStrategy(new ApiDataFetcherExceptionHandler()))
				.mutationExecutionStrategy(new AsyncExecutionStrategy(new ApiDataFetcherExceptionHandler()))
				.subscriptionExecutionStrategy(new AsyncExecutionStrategy(new ApiDataFetcherExceptionHandler()))
				.build();
	}

	/**
	 * Construct the runtime wiring for our graphQL engine
	 * 
	 * @return
	 */
	private RuntimeWiring buildRuntimeWiring() {
		logger.info("Building GraphQL runtime wiring");
		return RuntimeWiring.newRuntimeWiring()
				.type("Query", typeWiring -> typeWiring.dataFetcher("findOrganization", organizationDataFetcher.findOrganization()))
				.type("Query", typeWiring -> typeWiring.dataFetcher("countOrganizations", organizationDataFetcher.countOrganizations()))
				.type("Query", typeWiring -> typeWiring.dataFetcher("findOrganizations", organizationDataFetcher.findOrganizations()))
				.type("Mutation", typeWiring -> typeWiring.dataFetcher("createOrganization", organizationDataFetcher.createOrganization()))
				.type("Mutation", typeWiring -> typeWiring.dataFetcher("updateOrganization", organizationDataFetcher.updateOrganization()))
				.type("Mutation", typeWiring -> typeWiring.dataFetcher("enableOrganization", organizationDataFetcher.enableOrganization()))
				.type("Mutation", typeWiring -> typeWiring.dataFetcher("disableOrganization", organizationDataFetcher.disableOrganization()))

				.type("Query", typeWiring -> typeWiring.dataFetcher("findLocation", locationDataFetcher.findLocation()))
				.type("Query", typeWiring -> typeWiring.dataFetcher("countLocations", locationDataFetcher.countLocations()))
				.type("Query", typeWiring -> typeWiring.dataFetcher("findLocations", locationDataFetcher.findLocations()))
				.type("Mutation", typeWiring -> typeWiring.dataFetcher("createLocation", locationDataFetcher.createLocation()))
				.type("Mutation", typeWiring -> typeWiring.dataFetcher("updateLocation", locationDataFetcher.updateLocation()))
				.type("Mutation", typeWiring -> typeWiring.dataFetcher("enableLocation", locationDataFetcher.enableLocation()))
				.type("Mutation", typeWiring -> typeWiring.dataFetcher("disableLocation", locationDataFetcher.disableLocation()))
				
				.type("Query", typeWiring -> typeWiring.dataFetcher("findPerson", personDataFetcher.findPerson()))
				.type("Query", typeWiring -> typeWiring.dataFetcher("countPersons", personDataFetcher.countPersons()))
				.type("Query", typeWiring -> typeWiring.dataFetcher("findPersons", personDataFetcher.findPersons()))
				.type("Mutation", typeWiring -> typeWiring.dataFetcher("createPerson", personDataFetcher.createPerson()))
				.type("Mutation", typeWiring -> typeWiring.dataFetcher("updatePerson", personDataFetcher.updatePerson()))
				.type("Mutation", typeWiring -> typeWiring.dataFetcher("enablePerson", personDataFetcher.enablePerson()))
				.type("Mutation", typeWiring -> typeWiring.dataFetcher("disablePerson", personDataFetcher.disablePerson()))
				.type("Mutation", typeWiring -> typeWiring.dataFetcher("addUserRole", personDataFetcher.addUserRole()))
				.type("Mutation", typeWiring -> typeWiring.dataFetcher("removeUserRole", personDataFetcher.removeUserRole()))
				.type("Mutation", typeWiring -> typeWiring.dataFetcher("setUserRoles", personDataFetcher.setUserRoles()))
				
				.type("Query", typeWiring -> typeWiring.dataFetcher("findCodeLookups", lookupDataFetcher.findCodeLookups()))
				
				.type("Organization", typeWiring -> typeWiring.dataFetcher("mainLocation", locationDataFetcher.byOrganization()))
				.type("CodeLookup", typeWiring -> typeWiring.dataFetcher("englishName", lookupDataFetcher.getNameByLocale(Locale.CANADA)))
				.type("CodeLookup", typeWiring -> typeWiring.dataFetcher("frenchName", lookupDataFetcher.getNameByLocale(Locale.CANADA_FRENCH)))				
				.build();
	}
}