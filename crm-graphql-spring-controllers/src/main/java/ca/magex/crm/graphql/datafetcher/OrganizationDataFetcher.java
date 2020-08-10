package ca.magex.crm.graphql.datafetcher;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.UserDetails;
import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.AuthenticationGroupIdentifier;
import ca.magex.crm.api.system.id.BusinessGroupIdentifier;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.graphql.controller.GraphQLController;
import graphql.schema.DataFetcher;

@Component
public class OrganizationDataFetcher extends AbstractDataFetcher {

	private static Logger logger = LoggerFactory.getLogger(GraphQLController.class);

	public DataFetcher<OrganizationDetails> byUser() {
		return (environment) -> {
			logger.info("Entering ByUser@" + OrganizationDataFetcher.class.getSimpleName());
			UserDetails user = environment.getSource();
			return crm.findOrganizationDetails(user.getOrganizationId());
		};
	}
	
	public DataFetcher<OrganizationDetails> byLocation() {
		return (environment) -> {
			logger.info("Entering ByUser@" + OrganizationDataFetcher.class.getSimpleName());
			LocationDetails locationDetails = environment.getSource();
			return crm.findOrganizationDetails(locationDetails.getOrganizationId());
		};
	}
	
	public DataFetcher<OrganizationDetails> byPerson() {
		return (environment) -> {
			logger.info("Entering byPerson@" + OrganizationDataFetcher.class.getSimpleName());
			PersonDetails personDetails = environment.getSource();
			return crm.findOrganizationDetails(personDetails.getOrganizationId());
		};
	}
	
	public DataFetcher<OrganizationDetails> createOrganization() {
		return (environment) -> {
			logger.info("Entering createOrganization@" + OrganizationDataFetcher.class.getSimpleName());			
			return crm.createOrganization(
					environment.getArgument("displayName"), 
					extractAuthenticationGroups(environment, "authenticationGroupIds"),
					extractBusinessGroups(environment, "businessGroupIds"));
		};
	}

	public DataFetcher<OrganizationDetails> findOrganization() {
		return (environment) -> {
			logger.info("Entering findOrganization@" + OrganizationDataFetcher.class.getSimpleName());
			String id = environment.getArgument("organizationId");
			return crm.findOrganizationDetails(new OrganizationIdentifier(id));
		};
	}
	
	public DataFetcher<Map<String,Boolean>> findOrganizationActions() {
		return (environment) -> {
			logger.info("Entering findOrganizationActions@" + OrganizationDataFetcher.class.getSimpleName());
			OrganizationDetails source = environment.getSource();
			return Map.of(
					"modify", crm.canUpdateOrganization(source.getOrganizationId()),
					"enable", crm.canEnableOrganization(source.getOrganizationId()),
					"disable", crm.canDisableOrganization(source.getOrganizationId()),
					"createLocation", crm.canCreateLocationForOrganization(source.getOrganizationId()),
					"createPerson", crm.canCreatePersonForOrganization(source.getOrganizationId()));
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
			OrganizationIdentifier organizationId = new OrganizationIdentifier((String) environment.getArgument("organizationId"));
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
				LocationIdentifier newMainLocationId = new LocationIdentifier((String) environment.getArgument("mainLocationId"));
				if (org.getMainLocationId() == null || !org.getMainLocationId().equals(newMainLocationId)) {
					org = crm.updateOrganizationMainLocation(organizationId, newMainLocationId);
				}
			}
			if (environment.getArgument("mainContactId") != null) {
				PersonIdentifier newMainContactId = new PersonIdentifier((String) environment.getArgument("mainContactId"));
				if (org.getMainContactId() == null || !org.getMainContactId().equals(newMainContactId)) {
					org = crm.updateOrganizationMainContact(organizationId, newMainContactId);
				}
			}			
			if (environment.getArgument("authenticationGroupIds") != null) {
				List<AuthenticationGroupIdentifier> newGroups = extractAuthenticationGroups(environment, "authenticationGroupIds");
				if (!org.getAuthenticationGroupIds().containsAll(newGroups) || !newGroups.containsAll(org.getAuthenticationGroupIds())) {
					org = crm.updateOrganizationAuthenticationGroups(organizationId, newGroups);
				}
			}
			if (environment.getArgument("businessGroupIds") != null) {
				List<BusinessGroupIdentifier> newGroups = extractBusinessGroups(environment, "businessGroupIds");
				if (!org.getBusinessGroupIds().containsAll(newGroups) || !newGroups.containsAll(org.getBusinessGroupIds())) {
					org = crm.updateOrganizationBusinessGroups(organizationId, newGroups);
				}
			}

			return org;
		};
	}
}