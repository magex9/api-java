package ca.magex.crm.graphql.datafetcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.services.OrganizationService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.graphql.controller.OrganizationController;
import graphql.schema.DataFetcher;

/**
 * Contains the data fetcher implementations for each of the organization API methods
 * 
 * @author Jonny
 */
public class OrganizationDataFetcher extends AbstractDataFetcher {

	private static Logger logger = LoggerFactory.getLogger(OrganizationController.class);

	public OrganizationDataFetcher(OrganizationService organizations) {
		super(organizations);
	}

	public DataFetcher<OrganizationDetails> findOrganization() {
		return (environment) -> {
			logger.debug("Entering findOrganization@" + OrganizationDataFetcher.class.getSimpleName());
			String id = environment.getArgument("organizationId");
			return organizations.findOrganization(new Identifier(id));
		};
	}
	
	public DataFetcher<Integer> countOrganizations() {
		return (environment) -> {
			logger.debug("Entering countOrganizations@" + OrganizationDataFetcher.class.getSimpleName());
			return (int) organizations.countOrganizations(new OrganizationsFilter(
					extractFilter(environment)));
		};
	}

	public DataFetcher<Page<OrganizationDetails>> findOrganizations() {
		return (environment) -> {
			logger.debug("Entering findOrganizations@" + OrganizationDataFetcher.class.getSimpleName());
			return organizations.findOrganizationDetails(new OrganizationsFilter(
					extractFilter(environment)), 
					extractPaging(environment));
		};
	}
	
	public DataFetcher<OrganizationDetails> createOrganization() {
		return (environment) -> {
			logger.debug("Entering createOrganization@" + OrganizationDataFetcher.class.getSimpleName());
			String organizationName = environment.getArgument("organizationName");
			return organizations.createOrganization(organizationName);
		};
	}

	public DataFetcher<OrganizationDetails> enableOrganization() {
		return (environment) -> {
			logger.debug("Entering enableOrganization@" + OrganizationDataFetcher.class.getSimpleName());
			Identifier organizationId = new Identifier((String) environment.getArgument("organizationId"));
			organizations.enableOrganization(organizationId);
			return organizations.findOrganization(organizationId);
		};
	}

	public DataFetcher<OrganizationDetails> disableOrganization() {
		return (environment) -> {
			logger.debug("Entering disableOrganization@" + OrganizationDataFetcher.class.getSimpleName());
			Identifier organizationId = new Identifier((String) environment.getArgument("organizationId"));
			organizations.disableOrganization(organizationId);
			return organizations.findOrganization(organizationId);
		};
	}

	public DataFetcher<OrganizationDetails> updateOrganization() {
		return (environment) -> {
			logger.debug("Entering updateOrganization@" + OrganizationDataFetcher.class.getSimpleName());
			Identifier organizationId = new Identifier((String) environment.getArgument("organizationId"));
			if (environment.getArgument("organizationName") != null) {
				organizations.updateOrganizationName(
						organizationId,
						environment.getArgument("organizationName"));
			}
			if (environment.getArgument("locationId") != null) {
				organizations.updateOrganizationMainLocation(
						organizationId,
						new Identifier((String) environment.getArgument("locationId")));
			}
			return organizations.findOrganization(organizationId);
		};
	}
}
