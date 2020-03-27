package ca.magex.crm.graphql.datafetcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

import ca.magex.crm.api.crm.Organization;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.OrganizationService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.graphql.controller.OrganizationController;
import graphql.schema.DataFetcher;

/**
 * Contains the data fetcher implementations for each of the organization API
 * methods
 * 
 * @author Jonny
 */
public class OrganizationDataFetcher extends AbstractDataFetcher {

	private static Logger logger = LoggerFactory.getLogger(OrganizationController.class);

	public OrganizationDataFetcher(OrganizationService organizations) {
		super(organizations);
	}

	public DataFetcher<Organization> byId() {
		return (environment) -> {
			logger.info("Entering byId@" + OrganizationDataFetcher.class.getSimpleName());
			String id = environment.getArgument("organizationId");
			return organizations.findOrganization(new Identifier(id));
		};
	}

	public DataFetcher<Page<Organization>> finder() {
		return (environment) -> {
			logger.info("Entering finder@" + OrganizationDataFetcher.class.getSimpleName());
			Paging paging = extractPaging(environment);
			return organizations.findOrganizations(new OrganizationsFilter(null, paging));
		};
	}

	public DataFetcher<Organization> createOrganization() {
		return (environment) -> {
			logger.info("Entering createOrganization@" + OrganizationDataFetcher.class.getSimpleName());
			String organizationName = environment.getArgument("organizationName");
			return organizations.createOrganization(organizationName);
		};
	}

	public DataFetcher<Organization> enableOrganization() {
		return (environment) -> {
			logger.info("Entering enableOrganization@" + OrganizationDataFetcher.class.getSimpleName());
			String organizationId = environment.getArgument("organizationId");
			return organizations.enableOrganization(new Identifier(organizationId));
		};
	}

	public DataFetcher<Organization> disableOrganization() {

		return (environment) -> {
			logger.info("Entering disableOrganization@" + OrganizationDataFetcher.class.getSimpleName());
			String organizationId = environment.getArgument("organizationId");
			return organizations.disableOrganization(new Identifier(organizationId));
		};
	}

	public DataFetcher<Organization> updateOrganizationName() {
		return (environment) -> {
			logger.info("Entering updateOrganizationName@" + OrganizationDataFetcher.class.getSimpleName());
			String organizationId = environment.getArgument("organizationId");
			String organizationName = environment.getArgument("organizationName");
			return organizations.updateOrganizationName(new Identifier(organizationId), organizationName);
		};
	}

	public DataFetcher<Organization> updateOrganizationMainLocation() {
		return (environment) -> {
			logger.info("Entering updateOrganizationMainLocation@" + OrganizationDataFetcher.class.getSimpleName());
			String organizationId = environment.getArgument("organizationId");
			String locationId = environment.getArgument("locationId");
			return organizations.updateOrganizationMainLocation(
					new Identifier(organizationId),
					new Identifier(locationId));
		};
	}
}
