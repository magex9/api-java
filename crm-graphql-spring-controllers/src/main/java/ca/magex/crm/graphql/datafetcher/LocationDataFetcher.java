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
import ca.magex.crm.api.filters.LocationsFilter;
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
					"update", crm.canUpdateLocation(source.getLocationId()),
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
	
	public DataFetcher<LocationDetails> enableLocation() {
		return (environment) -> {
			logger.info("Entering enableLocation@" + LocationDataFetcher.class.getSimpleName());
			LocationIdentifier locationId = new LocationIdentifier((String) environment.getArgument("locationId"));
			return crm.findLocationDetails(crm.enableLocation(locationId).getLocationId());
		};
	}
	
	public DataFetcher<LocationDetails> disableLocation() {
		return (environment) -> {
			logger.info("Entering disableLocation@" + LocationDataFetcher.class.getSimpleName());
			LocationIdentifier locationId = new LocationIdentifier((String) environment.getArgument("locationId"));
			return crm.findLocationDetails(crm.disableLocation(locationId).getLocationId());
		};
	}
	
}