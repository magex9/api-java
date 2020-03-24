package ca.magex.crm.graphql.service;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import ca.magex.crm.amnesia.services.OrganizationServiceAmnesiaImpl;
import ca.magex.crm.amnesia.services.OrganizationServiceTestDataPopulator;
import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.services.OrganizationService;
import ca.magex.crm.graphql.datafetcher.LocationDataFetcher;
import ca.magex.crm.graphql.datafetcher.OrganizationDataFetcher;
import ca.magex.crm.graphql.error.ApiGraphQLError;
import graphql.GraphQL;
import graphql.execution.AsyncExecutionStrategy;
import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.SimpleDataFetcherExceptionHandler;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;

@Service("graphQLOrganizationService")
public class GraphQLOrganizationsService {

	private static Logger logger = LoggerFactory.getLogger(GraphQLOrganizationsService.class);

	private OrganizationService organizations = new OrganizationServiceAmnesiaImpl();

	@Value("classpath:organizations.graphql")
	private Resource resource;

	private GraphQL graphQL;

	public GraphQL getGraphQL() {
		return graphQL;
	}

	@PostConstruct
	private void loadSchema() throws IOException {
		logger.info("Entering loadSchema@" + getClass().getName());

		/* initialize our dataset */
		OrganizationServiceTestDataPopulator.populate(organizations);

		/* parse our schema */
		GraphQLSchema graphQLSchema = new SchemaGenerator()
				.makeExecutableSchema(
						new SchemaParser().parse(resource.getFile()), 
						buildRuntimeWiring());
		
		/* create our graphQL engine */
		DataFetcherExceptionHandler defaultFetcherExceptionHandler = new SimpleDataFetcherExceptionHandler();
		graphQL = GraphQL.newGraphQL(graphQLSchema).queryExecutionStrategy(new AsyncExecutionStrategy((params) -> {
			/* we want to treat our ApiExceptions specially */
			if (params.getException() instanceof ApiException) {
				params.getExecutionContext().addError(new ApiGraphQLError((ApiException) params.getException()));
			} else {
				defaultFetcherExceptionHandler.accept(params);
			}
		})).build();
	}

	/**
	 * Construct the runtime wiring for our graphQL engine
	 * 
	 * @return
	 */
	private RuntimeWiring buildRuntimeWiring() {
		// TODO make these autowired
		LocationDataFetcher locationDataFetcher = new LocationDataFetcher(organizations);
		OrganizationDataFetcher organizationDataFetcher = new OrganizationDataFetcher(organizations);

		return RuntimeWiring.newRuntimeWiring()
				.type("Query", typeWiring -> typeWiring.dataFetcher("findLocation", locationDataFetcher.byId()))
				.type("Query", typeWiring -> typeWiring.dataFetcher("findLocations", locationDataFetcher.finder()))
				.type("Query", typeWiring -> typeWiring.dataFetcher("findOrganization", organizationDataFetcher.byId()))
				.type("Query", typeWiring -> typeWiring.dataFetcher("findOrganizations", organizationDataFetcher.finder()))
				.build();
	}
}