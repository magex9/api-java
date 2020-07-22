package ca.magex.crm.graphql.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import ca.magex.crm.graphql.datafetcher.CommonDataFetcher;
import ca.magex.crm.graphql.datafetcher.LocationDataFetcher;
import ca.magex.crm.graphql.datafetcher.OptionDataFetcher;
import ca.magex.crm.graphql.datafetcher.OrganizationDataFetcher;
import ca.magex.crm.graphql.datafetcher.PersonDataFetcher;
import ca.magex.crm.graphql.datafetcher.UserDataFetcher;
import ca.magex.crm.graphql.error.GraphQLExceptionHandler;
import graphql.GraphQL;
import graphql.execution.AsyncExecutionStrategy;
import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.SimpleDataFetcherExceptionHandler;
import graphql.schema.GraphQLSchema;
import graphql.schema.PropertyDataFetcherHelper;
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
	private OptionDataFetcher optionDataFetcher;
	private CommonDataFetcher commonDataFetcher;
	
	/* graphql instance used to parse and execute the query */
	private GraphQL graphQL;	

	public GraphQLCrmServices(
			OrganizationDataFetcher organizationDataFetcher,
			LocationDataFetcher locationDataFetcher,
			PersonDataFetcher personDataFetcher,
			UserDataFetcher userDataFetcher,
			OptionDataFetcher optionDataFetcher,
			CommonDataFetcher commonDataFetcher) {
		this.organizationDataFetcher = organizationDataFetcher;
		this.locationDataFetcher = locationDataFetcher;
		this.personDataFetcher = personDataFetcher;
		this.userDataFetcher = userDataFetcher;
		this.optionDataFetcher = optionDataFetcher;
		this.commonDataFetcher = commonDataFetcher;
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
		
		/* NOTE: this is required for spring development hot deploys or any other deployment where the classes are swapped at runtime (think jrebel, etc) */
		PropertyDataFetcherHelper.clearReflectionCache();
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
				.type("Organization", typeWiring -> typeWiring.dataFetcher("authenticationGroups", optionDataFetcher.findAuthenticationGroupsForOrg()))
				.type("Organization", typeWiring -> typeWiring.dataFetcher("businessGroups", optionDataFetcher.findBusinessGroupsForOrg()))

				// location data fetching
				.type("Query", typeWiring -> typeWiring.dataFetcher("findLocation", locationDataFetcher.findLocation()))
				.type("Query", typeWiring -> typeWiring.dataFetcher("countLocations", locationDataFetcher.countLocations()))
				.type("Query", typeWiring -> typeWiring.dataFetcher("findLocations", locationDataFetcher.findLocations()))
				.type("Mutation", typeWiring -> typeWiring.dataFetcher("createLocation", locationDataFetcher.createLocation()))
				.type("Mutation", typeWiring -> typeWiring.dataFetcher("updateLocation", locationDataFetcher.updateLocation()))
				.type("Location", typeWiring -> typeWiring.dataFetcher("organization", organizationDataFetcher.byLocation()))

				// person data fetching
				.type("Query", typeWiring -> typeWiring.dataFetcher("findPerson", personDataFetcher.findPerson()))
				.type("Query", typeWiring -> typeWiring.dataFetcher("countPersons", personDataFetcher.countPersons()))
				.type("Query", typeWiring -> typeWiring.dataFetcher("findPersons", personDataFetcher.findPersons()))
				.type("Mutation", typeWiring -> typeWiring.dataFetcher("createPerson", personDataFetcher.createPerson()))
				.type("Mutation", typeWiring -> typeWiring.dataFetcher("updatePerson", personDataFetcher.updatePerson()))
				.type("Person", typeWiring -> typeWiring.dataFetcher("businessRoles", optionDataFetcher.findBusinessRolesForPerson()))
				.type("Person", typeWiring -> typeWiring.dataFetcher("organization", organizationDataFetcher.byPerson()))

				// user data fetching
				.type("Query", typeWiring -> typeWiring.dataFetcher("findUser", userDataFetcher.findUser()))
				.type("Query", typeWiring -> typeWiring.dataFetcher("countUsers", userDataFetcher.countUsers()))
				.type("Query", typeWiring -> typeWiring.dataFetcher("findUsers", userDataFetcher.findUsers()))
				.type("Mutation", typeWiring -> typeWiring.dataFetcher("createUser", userDataFetcher.createUser()))
				.type("Mutation", typeWiring -> typeWiring.dataFetcher("updateUser", userDataFetcher.updateUser()))
				.type("Mutation", typeWiring -> typeWiring.dataFetcher("resetUserPassword", userDataFetcher.resetUserPassword()))
				.type("Mutation", typeWiring -> typeWiring.dataFetcher("changeUserPassword", userDataFetcher.changeUserPassword()))
				.type("User", typeWiring -> typeWiring.dataFetcher("person", personDataFetcher.byUser()))
				.type("User", typeWiring -> typeWiring.dataFetcher("organization", organizationDataFetcher.byUser()))
				.type("User", typeWiring -> typeWiring.dataFetcher("authenticationRoles", optionDataFetcher.findAuthenticationRolesForUser()))
				
				// option data fetching
				.type("Query", typeWiring -> typeWiring.dataFetcher("findOption", optionDataFetcher.findOption()))
				.type("Query", typeWiring -> typeWiring.dataFetcher("countOptions", optionDataFetcher.countOptions()))
				.type("Query", typeWiring -> typeWiring.dataFetcher("findOptions", optionDataFetcher.findOptions()))
				.type("Mutation", typeWiring -> typeWiring.dataFetcher("createOption", optionDataFetcher.createOption()))
				.type("Mutation", typeWiring -> typeWiring.dataFetcher("updateOption", optionDataFetcher.updateOption()))
				.type("Option", typeWiring -> typeWiring.dataFetcher("parent", optionDataFetcher.findParentOption()))
				
				// common data fetching
				.type("Option", typeWiring -> typeWiring.dataFetcher("type", commonDataFetcher.getOptionTypeValue()))
				.type("MailingAddress", typeWiring -> typeWiring.dataFetcher("country", commonDataFetcher.getCountryChoice()))
				.type("MailingAddress", typeWiring -> typeWiring.dataFetcher("province", commonDataFetcher.getProvinceChoice()))				
				.type("PersonName", typeWiring -> typeWiring.dataFetcher("salutation", commonDataFetcher.getSalutationValue()))				
				.type("Communication", typeWiring -> typeWiring.dataFetcher("language", commonDataFetcher.getLanguageValue()))				
				.type("Localized", typeWiring -> typeWiring.dataFetcher("english", commonDataFetcher.getEnglishValue()))
				.type("Localized", typeWiring -> typeWiring.dataFetcher("french", commonDataFetcher.getFrenchValue()))
				.type("Localized", typeWiring -> typeWiring.dataFetcher("english", commonDataFetcher.getEnglishValue()))
				.type("Localized", typeWiring -> typeWiring.dataFetcher("french", commonDataFetcher.getFrenchValue()))
				.build();
	}
}