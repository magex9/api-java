package ca.magex.crm.graphql.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import ca.magex.crm.api.system.Lang;
import ca.magex.crm.graphql.datafetcher.LocationDataFetcher;
import ca.magex.crm.graphql.datafetcher.OrganizationDataFetcher;
import ca.magex.crm.graphql.datafetcher.PersonDataFetcher;
import ca.magex.crm.graphql.datafetcher.UserDataFetcher;
import ca.magex.crm.graphql.error.GraphQLExceptionHandler;
import graphql.GraphQL;
import graphql.execution.AsyncExecutionStrategy;
import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.SimpleDataFetcherExceptionHandler;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;

@Service("graphQLOrganizationService")
public class GraphQLCrmServices {

	private static Logger logger = LoggerFactory.getLogger(GraphQLCrmServices.class);

	/* schema location injected via classpath lookup by spring */
	@Value("classpath:crm.graphql") private Resource resource;
	
	/* data fetchers used for implementing the queries */
	private LocationDataFetcher locationDataFetcher;
	private OrganizationDataFetcher organizationDataFetcher;
	private PersonDataFetcher personDataFetcher;
	private UserDataFetcher userDataFetcher;
	
	/* graphql instance used to parse and execute the query */
	private GraphQL graphQL;	

	public GraphQLCrmServices(
			OrganizationDataFetcher organizationDataFetcher,
			LocationDataFetcher locationDataFetcher,
			PersonDataFetcher personDataFetcher,
			UserDataFetcher userDataFetcher) {
		this.organizationDataFetcher = organizationDataFetcher;
		this.locationDataFetcher = locationDataFetcher;
		this.personDataFetcher = personDataFetcher;
		this.userDataFetcher = userDataFetcher;
	}

	/**
	 * returns our GraphQL Engine
	 * @return
	 */
	public GraphQL getGraphQL() {
		return graphQL;
	}

	@PostConstruct
	private void loadSchema() throws IOException {
		logger.info("Loading GraphQL Schema " + resource.getFilename());

		/* parse our schema */
		GraphQLSchema graphQLSchema = null;
		try (Reader reader = new InputStreamReader(resource.getInputStream(), "UTF8")) {
			graphQLSchema = new SchemaGenerator()
					.makeExecutableSchema(
							new SchemaParser().parse(reader),
							buildRuntimeWiring());
		}

		/* create our graphQL engine */
		DataFetcherExceptionHandler delegate = new SimpleDataFetcherExceptionHandler();
		graphQL = GraphQL
				.newGraphQL(graphQLSchema)
				.queryExecutionStrategy(new AsyncExecutionStrategy(new GraphQLExceptionHandler(delegate)))
				.mutationExecutionStrategy(new AsyncExecutionStrategy(new GraphQLExceptionHandler(delegate)))
				.subscriptionExecutionStrategy(new AsyncExecutionStrategy(new GraphQLExceptionHandler(delegate)))
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
				// organization data fetching
				.type("Query", typeWiring -> typeWiring.dataFetcher("findOrganization", organizationDataFetcher.findOrganization()))
				.type("Query", typeWiring -> typeWiring.dataFetcher("countOrganizations", organizationDataFetcher.countOrganizations()))
				.type("Query", typeWiring -> typeWiring.dataFetcher("findOrganizations", organizationDataFetcher.findOrganizations()))
				.type("Mutation", typeWiring -> typeWiring.dataFetcher("createOrganization", organizationDataFetcher.createOrganization()))
				.type("Mutation", typeWiring -> typeWiring.dataFetcher("updateOrganization", organizationDataFetcher.updateOrganization()))
				.type("Organization", typeWiring -> typeWiring.dataFetcher("mainLocation", locationDataFetcher.byOrganization()))
				.type("Organization", typeWiring -> typeWiring.dataFetcher("mainContact", personDataFetcher.byOrganization()))
//				.type("Organization", typeWiring -> typeWiring.dataFetcher("groups", permissionDataFetcher.groupsByOrganization()))

				// location data fetching
				.type("Query", typeWiring -> typeWiring.dataFetcher("findLocation", locationDataFetcher.findLocation()))
				.type("Query", typeWiring -> typeWiring.dataFetcher("countLocations", locationDataFetcher.countLocations()))
				.type("Query", typeWiring -> typeWiring.dataFetcher("findLocations", locationDataFetcher.findLocations()))
				.type("Mutation", typeWiring -> typeWiring.dataFetcher("createLocation", locationDataFetcher.createLocation()))
				.type("Mutation", typeWiring -> typeWiring.dataFetcher("updateLocation", locationDataFetcher.updateLocation()))

				// person data fetching
				.type("Query", typeWiring -> typeWiring.dataFetcher("findPerson", personDataFetcher.findPerson()))
				.type("Query", typeWiring -> typeWiring.dataFetcher("countPersons", personDataFetcher.countPersons()))
				.type("Query", typeWiring -> typeWiring.dataFetcher("findPersons", personDataFetcher.findPersons()))
				.type("Mutation", typeWiring -> typeWiring.dataFetcher("createPerson", personDataFetcher.createPerson()))
				.type("Mutation", typeWiring -> typeWiring.dataFetcher("updatePerson", personDataFetcher.updatePerson()))

				// user data fetching
				.type("Query", typeWiring -> typeWiring.dataFetcher("findUser", userDataFetcher.findUser()))
				.type("Query", typeWiring -> typeWiring.dataFetcher("countUsers", userDataFetcher.countUsers()))
				.type("Query", typeWiring -> typeWiring.dataFetcher("findUsers", userDataFetcher.findUsers()))
				.type("Mutation", typeWiring -> typeWiring.dataFetcher("createUser", userDataFetcher.createUser()))
				.type("Mutation", typeWiring -> typeWiring.dataFetcher("updateUser", userDataFetcher.updateUser()))
				.type("Mutation", typeWiring -> typeWiring.dataFetcher("resetUserPassword", userDataFetcher.resetUserPassword()))
				.type("Mutation", typeWiring -> typeWiring.dataFetcher("changeUserPassword", userDataFetcher.changeUserPassword()))
				.type("User", typeWiring -> typeWiring.dataFetcher("person", personDataFetcher.byUser()))
//				.type("User", typeWiring -> typeWiring.dataFetcher("roles", permissionDataFetcher.rolesByUser()))

				// group data fetching
//				.type("Query", typeWiring -> typeWiring.dataFetcher("findGroup", permissionDataFetcher.findGroup()))
//				.type("Query", typeWiring -> typeWiring.dataFetcher("findGroups", permissionDataFetcher.findGroups()))
//				.type("Mutation", typeWiring -> typeWiring.dataFetcher("createGroup", permissionDataFetcher.createGroup()))
//				.type("Mutation", typeWiring -> typeWiring.dataFetcher("updateGroup", permissionDataFetcher.updateGroup()))
//				.type("Group", typeWiring -> typeWiring.dataFetcher("englishName", permissionDataFetcher.getNameByLocale(Lang.ENGLISH)))
//				.type("Group", typeWiring -> typeWiring.dataFetcher("frenchName", permissionDataFetcher.getNameByLocale(Lang.FRENCH)))

				// role data fetching
//				.type("Query", typeWiring -> typeWiring.dataFetcher("findRole", permissionDataFetcher.findRole()))
//				.type("Query", typeWiring -> typeWiring.dataFetcher("findRoles", permissionDataFetcher.findRoles()))
//				.type("Mutation", typeWiring -> typeWiring.dataFetcher("createRole", permissionDataFetcher.createRole()))
//				.type("Mutation", typeWiring -> typeWiring.dataFetcher("updateRole", permissionDataFetcher.updateRole()))
//				.type("Role", typeWiring -> typeWiring.dataFetcher("englishName", permissionDataFetcher.getNameByLocale(Lang.ENGLISH)))
//				.type("Role", typeWiring -> typeWiring.dataFetcher("frenchName", permissionDataFetcher.getNameByLocale(Lang.FRENCH)))

				// lookup data fetching
//				.type("Query", typeWiring -> typeWiring.dataFetcher("findCodeLookups", lookupDataFetcher.findCodeLookups()))
//				.type("CodeLookup", typeWiring -> typeWiring.dataFetcher("englishName", lookupDataFetcher.getNameByLocale(Lang.ENGLISH)))
//				.type("CodeLookup", typeWiring -> typeWiring.dataFetcher("frenchName", lookupDataFetcher.getNameByLocale(Lang.FRENCH)))
//				.type("CodeLookup", typeWiring -> typeWiring.dataFetcher("parent", lookupDataFetcher.findParent()))

				.build();
	}
}