package ca.magex.crm.graphql.datafetcher;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import graphql.schema.DataFetcher;

@Component
public class LocationDataFetcher extends AbstractDataFetcher {

	private static Logger logger = LoggerFactory.getLogger(LocationDataFetcher.class);

	public DataFetcher<LocationDetails> findLocation() {
		return (environment) -> {
			logger.info("Entering findLocation@" + LocationDataFetcher.class.getSimpleName());
			String locationId = environment.getArgument("locationId");
			return crm.findLocationDetails(new Identifier(locationId));
		};
	}

	public DataFetcher<Integer> countLocations() {
		return (environment) -> {
			logger.info("Entering findLocations@" + LocationDataFetcher.class.getSimpleName());
			return (int) crm.countLocations(extractFilter(
					extractFilter(environment)));
		};
	}

	public DataFetcher<Page<LocationDetails>> findLocations() {
		return (environment) -> {
			logger.info("Entering findLocations@" + LocationDataFetcher.class.getSimpleName());
			return crm.findLocationDetails(extractFilter(
					extractFilter(environment)),
					extractPaging(environment));
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
					new Identifier((String) environment.getArgument("organizationId")),
					environment.getArgument("locationName"),
					environment.getArgument("locationReference"),
					extractMailingAddress(environment, "locationAddress"));
		};
	}

	public DataFetcher<LocationDetails> updateLocation() {
		return (environment) -> {
			logger.info("Entering updateLocation@" + LocationDataFetcher.class.getSimpleName());
			Identifier locationId = new Identifier((String) environment.getArgument("locationId"));
			LocationDetails loc = crm.findLocationDetails(locationId);
			if (environment.getArgument("locationName") != null) {
				loc = crm.updateLocationName(
						locationId,
						environment.getArgument("locationName"));
			}
			if (environment.getArgument("locationAddress") != null) {
				loc = crm.updateLocationAddress(
						locationId,
						extractMailingAddress(environment, "locationAddress"));
			}
			if (environment.getArgument("status") != null) {
				String status = StringUtils.upperCase(environment.getArgument("status"));
				switch (status) {
				case "ACTIVE":
					if (loc.getStatus() != Status.ACTIVE) {
						crm.enableLocation(locationId);
						loc = loc.withStatus(Status.ACTIVE);
					}
					break;
				case "INACTIVE":
					if (loc.getStatus() != Status.INACTIVE) {
						crm.disableLocation(locationId);
						loc = loc.withStatus(Status.INACTIVE);
					}
					break;
				default:
					throw new ApiException("Invalid status '" + status + "', one of {ACTIVE, INACTIVE} expected");
				}
			}
			return loc;
		};
	}

	private LocationsFilter extractFilter(Map<String, Object> filter) {
		String displayName = (String) filter.get("displayName");
		String organizationId = (String) filter.get("organizationId");
		Status status = null;
		if (filter.containsKey("status") && StringUtils.isNotBlank((String) filter.get("status"))) {
			try {
				status = Status.valueOf((String) filter.get("status"));
			} catch (IllegalArgumentException e) {
				throw new ApiException("Invalid status value '" + filter.get("status") + "' expected one of {" + StringUtils.join(Status.values(), ",") + "}");
			}
		}
		return new LocationsFilter(organizationId == null ? null : new Identifier(organizationId), displayName, status);
	}
}