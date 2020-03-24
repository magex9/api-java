package ca.magex.crm.graphql.service;

import java.io.File;
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
import ca.magex.crm.graphql.datafetcher.LocationAddressDataFetcher;
import ca.magex.crm.graphql.datafetcher.LocationsDataFetcher;
import ca.magex.crm.graphql.datafetcher.OrganizationDataFetcher;
import ca.magex.crm.graphql.datafetcher.OrganizationLocationDataFetcher;
import ca.magex.crm.graphql.datafetcher.OrganizationsDataFetcher;
import ca.magex.crm.graphql.error.ApiGraphQLError;
import graphql.GraphQL;
import graphql.execution.AsyncExecutionStrategy;
import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.SimpleDataFetcherExceptionHandler;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

@Service("graphQLOrganizationService")
public class GraphQLOrganizationsService {

	private static Logger logger = LoggerFactory.getLogger(GraphQLOrganizationsService.class);
	
	private OrganizationService organizations = new OrganizationServiceAmnesiaImpl();

	@Value("classpath:organizations.graphql")
	private Resource resource;

	private GraphQL graphQL;

	@PostConstruct
	private void loadSchema() throws IOException {
		logger.info("Entering loadSchema@" + getClass().getName());
		
		OrganizationServiceTestDataPopulator.populate(organizations);

		File file = resource.getFile();

		TypeDefinitionRegistry typeDefinitionRegistry = new SchemaParser().parse(file);
		
		RuntimeWiring runtimeWiring = buildRuntimeWiring();
		
		GraphQLSchema graphQLSchema = new SchemaGenerator().makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);
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

	private RuntimeWiring buildRuntimeWiring() {
		return RuntimeWiring.newRuntimeWiring()
				.type("Query", typeWiring -> typeWiring.dataFetcher("findLocations", new LocationsDataFetcher(organizations)))
				.type("Query", typeWiring -> typeWiring.dataFetcher("findOrganization", new OrganizationDataFetcher(organizations)))
				.type("Query", typeWiring -> typeWiring.dataFetcher("findOrganizations", new OrganizationsDataFetcher(organizations)))				
				.type("Organization", typeWiring -> typeWiring.dataFetcher("mainLocation", new OrganizationLocationDataFetcher(organizations)))
				.type("Location", typeWiring -> typeWiring.dataFetcher("address", new LocationAddressDataFetcher()))
				.build();
	}

	public GraphQL getGraphQL() {
		return graphQL;
	}
}