package ca.magex.crm.graphql.datafetcher;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.graphql.controller.GraphQLController;
import graphql.schema.DataFetcher;

@Component
public class OrganizationDataFetcher extends AbstractDataFetcher {

	private static Logger logger = LoggerFactory.getLogger(GraphQLController.class);

	public DataFetcher<OrganizationDetails> findOrganization() {
		return (environment) -> {
			logger.info("Entering findOrganization@" + OrganizationDataFetcher.class.getSimpleName());
			String id = environment.getArgument("organizationId");
			return crm.findOrganizationDetails(new Identifier(id));
		};
	}

	public DataFetcher<Integer> countOrganizations() {
		return (environment) -> {
			logger.info("Entering countOrganizations@" + OrganizationDataFetcher.class.getSimpleName());
			return (int) crm.countOrganizations(extractFilter(
					extractFilter(environment)));
		};
	}

	public DataFetcher<Page<OrganizationDetails>> findOrganizations() {
		return (environment) -> {
			logger.info("Entering findOrganizations@" + OrganizationDataFetcher.class.getSimpleName());
			return crm.findOrganizationDetails(extractFilter(
					extractFilter(environment)),
					extractPaging(environment));
		};
	}

	public DataFetcher<OrganizationDetails> createOrganization() {
		return (environment) -> {
			logger.info("Entering createOrganization@" + OrganizationDataFetcher.class.getSimpleName());
			String organizationDisplayName = environment.getArgument("organizationDisplayName");
			return crm.createOrganization(organizationDisplayName);
		};
	}

	public DataFetcher<OrganizationDetails> enableOrganization() {
		return (environment) -> {
			logger.info("Entering enableOrganization@" + OrganizationDataFetcher.class.getSimpleName());
			Identifier organizationId = new Identifier((String) environment.getArgument("organizationId"));
			crm.enableOrganization(organizationId);
			return crm.findOrganizationDetails(organizationId);
		};
	}

	public DataFetcher<OrganizationDetails> disableOrganization() {
		return (environment) -> {
			logger.info("Entering disableOrganization@" + OrganizationDataFetcher.class.getSimpleName());
			Identifier organizationId = new Identifier((String) environment.getArgument("organizationId"));
			crm.disableOrganization(organizationId);
			return crm.findOrganizationDetails(organizationId);
		};
	}

	public DataFetcher<OrganizationDetails> updateOrganization() {
		return (environment) -> {
			logger.info("Entering updateOrganization@" + OrganizationDataFetcher.class.getSimpleName());
			Identifier organizationId = new Identifier((String) environment.getArgument("organizationId"));
			if (environment.getArgument("organizationDisplayName") != null) {
				crm.updateOrganizationDisplayName(
						organizationId,
						environment.getArgument("organizationDisplayName"));
			}
			if (environment.getArgument("locationId") != null) {
				crm.updateOrganizationMainLocation(
						organizationId,
						new Identifier((String) environment.getArgument("locationId")));
			}
			return crm.findOrganizationDetails(organizationId);
		};
	}

	private OrganizationsFilter extractFilter(Map<String, Object> filter) {
		String displayName = (String) filter.get("displayName");
		Status status = null;
		if (filter.containsKey("status") && StringUtils.isNotBlank((String) filter.get("status"))) {
			try {
				status = Status.valueOf((String) filter.get("status"));
			} catch (IllegalArgumentException e) {
				throw new ApiException("Invalid status value '" + filter.get("status") + "' expected one of {" + StringUtils.join(Status.values(), ",") + "}");
			}
		}
		return new OrganizationsFilter(displayName, status);
	}
}