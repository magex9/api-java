package ca.magex.crm.graphql.datafetcher;

import java.util.List;

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

	public DataFetcher<OrganizationDetails> createOrganization() {
		return (environment) -> {
			logger.info("Entering createOrganization@" + OrganizationDataFetcher.class.getSimpleName());
			String organizationDisplayName = environment.getArgument("displayName");
			List<String> groups = environment.getArgument("groups");
			return crm.createOrganization(organizationDisplayName, groups);
		};
	}

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
			return (int) crm.countOrganizations(new OrganizationsFilter(extractFilter(environment)));
		};
	}

	public DataFetcher<Page<OrganizationDetails>> findOrganizations() {
		return (environment) -> {
			logger.info("Entering findOrganizations@" + OrganizationDataFetcher.class.getSimpleName());
			return crm.findOrganizationDetails(
					new OrganizationsFilter(extractFilter(environment)),
					extractPaging(environment));
		};
	}

	public DataFetcher<OrganizationDetails> updateOrganization() {
		return (environment) -> {
			logger.info("Entering updateOrganization@" + OrganizationDataFetcher.class.getSimpleName());
			Identifier organizationId = new Identifier((String) environment.getArgument("organizationId"));
			OrganizationDetails org = crm.findOrganizationDetails(organizationId);
			/* update status first because the other updates have validation based on status */
			if (environment.getArgument("status") != null) {
				String newStatus = StringUtils.upperCase(environment.getArgument("status"));
				switch (newStatus) {
				case "ACTIVE":
					if (org.getStatus() != Status.ACTIVE) {
						crm.enableOrganization(organizationId);
						org = org.withStatus(Status.ACTIVE);
					}
					break;
				case "INACTIVE":
					if (org.getStatus() != Status.INACTIVE) {
						crm.disableOrganization(organizationId);
						org = org.withStatus(Status.INACTIVE);
					}
					break;
				default:
					throw new ApiException("Invalid status '" + newStatus + "', one of {ACTIVE, INACTIVE} expected");
				}
			}
			if (environment.getArgument("displayName") != null) {
				String newDisplayName = environment.getArgument("displayName");
				if (!StringUtils.equals(org.getDisplayName(), newDisplayName)) {
					org = crm.updateOrganizationDisplayName(organizationId, newDisplayName);
				}
			}
			if (environment.getArgument("mainLocationId") != null) {
				Identifier newMainLocationId = new Identifier((String) environment.getArgument("mainLocationId"));
				if (org.getMainLocationId() == null || !org.getMainLocationId().equals(newMainLocationId)) {
					org = crm.updateOrganizationMainLocation(organizationId, newMainLocationId);
				}
			}
			if (environment.getArgument("mainContactId") != null) {
				Identifier newMainContactId = new Identifier((String) environment.getArgument("mainContactId"));
				if (org.getMainContactId() == null || !org.getMainContactId().equals(newMainContactId)) {
					org = crm.updateOrganizationMainContact(organizationId, newMainContactId);
				}
			}
			if (environment.getArgument("groups") != null) {
				List<String> newGroups = environment.getArgument("groups");
				if (!org.getGroups().containsAll(newGroups) || !newGroups.containsAll(org.getGroups())) {
					org = crm.updateOrganizationGroups(organizationId, newGroups);
				}
			}

			return crm.findOrganizationDetails(organizationId);
		};
	}
}