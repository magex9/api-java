package ca.magex.crm.graphql.service;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import ca.magex.crm.api.services.Crm;
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
public class GraphQLOrganizationsService {

	private static Logger logger = LoggerFactory.getLogger(GraphQLOrganizationsService.class);

	@Autowired(required=true) private Crm crm;

	@Value("classpath:organizations.graphql")
	private Resource resource;

	private GraphQL graphQL;

	public GraphQL getGraphQL() {
		return graphQL;
	}

	@PostConstruct
	private void loadSchema() throws IOException {
		logger.info("Entering loadSchema@" + getClass().getName());

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
		LocationDataFetcher locationDataFetcher = new LocationDataFetcher(crm);
		OrganizationDataFetcher organizationDataFetcher = new OrganizationDataFetcher(crm);
		PersonDataFetcher personDataFetcher = new PersonDataFetcher(crm);
		LookupDataFetcher lookupDataFetcher = new LookupDataFetcher();

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
				
				.type("Organization", typeWiring -> typeWiring.dataFetcher("mainLocation", locationDataFetcher.byOrganization()))
				.type("Country", typeWiring -> typeWiring.dataFetcher("name", lookupDataFetcher.getNameByLocale()))
				.type("Salutation", typeWiring -> typeWiring.dataFetcher("name", lookupDataFetcher.getNameByLocale()))
				.type("Role", typeWiring -> typeWiring.dataFetcher("name", lookupDataFetcher.getNameByLocale()))
				
				.build();
	}
}