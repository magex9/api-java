package ca.magex.crm.graphql.datafetcher;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import graphql.schema.DataFetcher;

@Component
public class LocationDataFetcher extends AbstractDataFetcher {

	private static Logger logger = LoggerFactory.getLogger(LocationDataFetcher.class);

	public DataFetcher<LocationDetails> findLocation() {
		return (environment) -> {
			logger.info("Entering findLocation@" + LocationDataFetcher.class.getSimpleName());
			String locationId = environment.getArgument("locationId");
			return crm.findLocationDetails(new LocationIdentifier(locationId));
		};
	}

	public DataFetcher<Integer> countLocations() {
		return (environment) -> {
			logger.info("Entering findLocations@" + LocationDataFetcher.class.getSimpleName());
			return (int) crm.countLocations(new LocationsFilter(extractFilter(environment)));
		};
	}

	public DataFetcher<Page<LocationDetails>> findLocations() {
		return (environment) -> {
			logger.info("Entering findLocations@" + LocationDataFetcher.class.getSimpleName());
			return crm.findLocationDetails(new LocationsFilter(extractFilter(environment)), extractPaging(environment));
		};
	}
	
	public DataFetcher<Map<String,Boolean>> findLocationActions() {
		return (environment) -> {
			logger.info("Entering findLocationActions@" + LocationDataFetcher.class.getSimpleName());
			LocationDetails source = environment.getSource();
			return Map.of(
					"modify", crm.canUpdateLocation(source.getLocationId()),
					"enable", crm.canEnableLocation(source.getLocationId()),
					"disable", crm.canDisableLocation(source.getLocationId()));
		};
	}

	public DataFetcher<LocationDetails> byOrganization() {
		return (environment) -> {
			logger.info("Entering byOrganization@" + LocationDataFetcher.class.getSimpleName());
			OrganizationDetails organization = environment.getSource();
			if (organization.getMainLocationId() != null) {
				return crm.findLocationDetails(organization.getMainLocationId());
			} else {
				return null;
			}
		};
	}

	public DataFetcher<LocationDetails> createLocation() {
		return (environment) -> {
			logger.info("Entering createLocation@" + LocationDataFetcher.class.getSimpleName());
			return crm.createLocation(
					new OrganizationIdentifier((String) environment.getArgument("organizationId")),
					environment.getArgument("reference"),
					environment.getArgument("displayName"),
					extractMailingAddress(environment, "address"));
		};
	}

	public DataFetcher<LocationDetails> updateLocation() {
		return (environment) -> {
			logger.info("Entering updateLocation@" + LocationDataFetcher.class.getSimpleName());
			LocationIdentifier locationId = new LocationIdentifier((String) environment.getArgument("locationId"));
			LocationDetails loc = crm.findLocationDetails(locationId);
			/* update status first because other elements depend on the status for validation */
			if (environment.getArgument("status") != null) {
				String status = StringUtils.upperCase(environment.getArgument("status"));
				switch (status) {
				case "ACTIVE":
					if (loc.getStatus() != Status.ACTIVE) {
						loc = loc.withStatus(Status.ACTIVE).withLastModified(crm.enableLocation(locationId).getLastModified());
					}
					break;
				case "INACTIVE":
					if (loc.getStatus() != Status.INACTIVE) {
						loc = loc.withStatus(Status.INACTIVE).withLastModified(crm.disableLocation(locationId).getLastModified());
					}
					break;
				default:
					throw new ApiException("Invalid status '" + status + "', one of {ACTIVE, INACTIVE} expected");
				}
			}
			if (environment.getArgument("displayName") != null) {
				String newLocationName = environment.getArgument("displayName");
				if (!StringUtils.equals(loc.getDisplayName(), newLocationName)) {
					loc = crm.updateLocationName(locationId, newLocationName);
				}
			}
			if (environment.getArgument("address") != null) {
				MailingAddress newLocationAddress = extractMailingAddress(environment, "address");
				if (!loc.getAddress().equals(newLocationAddress)) {
					loc = crm.updateLocationAddress(locationId, newLocationAddress);
				}
			}
			return loc;
		};
	}
}