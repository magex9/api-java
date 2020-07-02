package ca.magex.crm.api.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.MessageTypeIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;

/**
 * Interface for manipulating the location information within the System
 * 
 * @author Jonny
 */
public interface CrmLocationService {

	/**
	 * Creates a Prototype of a Location without persisting and assigning an identifier
	 * @param organizationId
	 * @param reference
	 * @param displayName
	 * @param address
	 * @return
	 */
	default LocationDetails prototypeLocation(OrganizationIdentifier organizationId, String reference, String displayName, MailingAddress address) {
		return new LocationDetails(null, organizationId, Status.PENDING, reference, displayName, address);
	};

	/**
	 * Persists a prototype and assigns a unique identifier for it
	 * @param prototype
	 * @return
	 */
	default LocationDetails createLocation(LocationDetails prototype) {
		return createLocation(
				prototype.getOrganizationId(),
				prototype.getReference(),
				prototype.getDisplayName(),
				prototype.getAddress());
	}

	/**
	 * Persists a new location from the details provided
	 * @param organizationId
	 * @param reference
	 * @param displayName
	 * @param address
	 * @return
	 */
	LocationDetails createLocation(OrganizationIdentifier organizationId, String reference, String displayName, MailingAddress address);

	/**
	 * updates the status of the location to ACTIVE
	 * @param locationId
	 * @return
	 */
	LocationSummary enableLocation(LocationIdentifier locationId);

	/**
	 * update the status of the location to INACTIVE
	 * @param locationId
	 * @return
	 */
	LocationSummary disableLocation(LocationIdentifier locationId);

	/**
	 * updates the location name
	 * @param locationId
	 * @param displaysName
	 * @return
	 */
	LocationDetails updateLocationName(LocationIdentifier locationId, String displaysName);

	/**
	 * updates the location address
	 * @param locationId
	 * @param address
	 * @return
	 */
	LocationDetails updateLocationAddress(LocationIdentifier locationId, MailingAddress address);

	/**
	 * returns the location summary for the given location identifier
	 * @param locationId
	 * @return
	 */
	LocationSummary findLocationSummary(LocationIdentifier locationId);

	/**
	 * returns the full location details for the given location identifier
	 * @param locationId
	 * @return
	 */
	LocationDetails findLocationDetails(LocationIdentifier locationId);

	/**
	 * returns the number of locations in the system that match the provided filter
	 * @param filter
	 * @return
	 */
	long countLocations(LocationsFilter filter);

	/**
	 * returns the location details page for all locations matching the filter
	 * @param filter
	 * @param paging
	 * @return
	 */
	FilteredPage<LocationDetails> findLocationDetails(LocationsFilter filter, Paging paging);

	/**
	 * returns the locations summaries page for all locations matching the filter
	 * @param filter
	 * @param paging
	 * @return
	 */
	FilteredPage<LocationSummary> findLocationSummaries(LocationsFilter filter, Paging paging);

	/**
	 * returns the locations details matching the filter with default paging
	 * @param filter
	 * @return
	 */
	default FilteredPage<LocationDetails> findLocationDetails(LocationsFilter filter) {
		return findLocationDetails(filter, defaultLocationsPaging());
	}

	/**
	 * returns the locations summaries matching the filter with default paging
	 * @param filter
	 * @return
	 */
	default FilteredPage<LocationSummary> findLocationSummaries(LocationsFilter filter) {
		return findLocationSummaries(filter, defaultLocationsPaging());
	}

	/**
	 * returns the location summaries for all active locations for the provided organization
	 * @param organizationId
	 * @return
	 */
	default FilteredPage<LocationSummary> findActiveLocationSummariesForOrg(OrganizationIdentifier organizationId) {
		return findLocationSummaries(new LocationsFilter(organizationId, null, null, Status.ACTIVE));
	}

	/**
	 * returns the default locations filter
	 * @return
	 */
	default LocationsFilter defaultLocationsFilter() {
		return new LocationsFilter();
	};

	/**
	 * returns the default locations paging
	 * @return
	 */
	default Paging defaultLocationsPaging() {
		return new Paging(LocationsFilter.getSortOptions().get(0));
	}

	/**
	 * Validates the given location details against the data in the system
	 * @param crm
	 * @param location
	 * @return
	 */
	static List<Message> validateLocationDetails(Crm crm, LocationDetails location) {
		List<Message> messages = new ArrayList<Message>();
		
		MessageTypeIdentifier error = crm.findOptionByCode(Type.MESSAGE_TYPE, "ERROR").getOptionId();
		
		// Organization
		if (location.getOrganizationId() == null) {
			messages.add(new Message(location.getLocationId(), error, "organizationId", crm.findMessageId("validation.field.required")));
		} else {
			try {
				crm.findOrganizationDetails(location.getOrganizationId());
			} catch (ItemNotFoundException e) {
				messages.add(new Message(location.getLocationId(), error, "organizationId", crm.findMessageId("validation.entity.missing")));
			}
		}

		// Status
		if (location.getStatus() == null) {
			messages.add(new Message(location.getLocationId(), error, "status", crm.findMessageId("validation.field.required")));
		} else if (location.getStatus() == Status.PENDING && location.getLocationId() != null) {
			messages.add(new Message(location.getLocationId(), error, "status", crm.findMessageId("validation.status.pending")));
		}

		// Reference
		if (StringUtils.isBlank(location.getReference())) {
			messages.add(new Message(location.getLocationId(), error, "reference", crm.findMessageId("validation.field.required")));
		} else if (!location.getReference().matches("[A-Z0-9-]{1,60}")) {
			messages.add(new Message(location.getLocationId(), error, "reference", crm.findMessageId("validation.format.invalid")));
		}

		// Display Name
		if (StringUtils.isBlank(location.getDisplayName())) {
			messages.add(new Message(location.getLocationId(), error, "displayName", crm.findMessageId("validation.field.required")));
		} else if (location.getDisplayName().length() > 60) {
			messages.add(new Message(location.getLocationId(), error, "displayName", crm.findMessageId("validation.field.maxlength")));
		}

		// Address
		if (location.getAddress() == null) {
			messages.add(new Message(location.getLocationId(), error, "address", crm.findMessageId("validation.field.required")));
		} else {
			messages.addAll(validateMailingAddress(crm, location.getAddress(), location.getLocationId(), "address"));
		}

		return messages;
	}

	/**
	 * validates the mailing address
	 * @param crm
	 * @param address
	 * @param identifier
	 * @param path
	 * @return
	 */
	static List<Message> validateMailingAddress(Crm crm, MailingAddress address, Identifier identifier, String path) {
		List<Message> messages = new ArrayList<Message>();
		
		MessageTypeIdentifier error = crm.findOptionByCode(Type.MESSAGE_TYPE, "ERROR").getOptionId();

		// Street
		if (StringUtils.isBlank(address.getStreet())) {
			messages.add(new Message(identifier, error, path + ".street", crm.findMessageId("validation.field.required")));
		} else if (address.getStreet().length() > 60) {
			messages.add(new Message(identifier, error, path + ".street", crm.findMessageId("validation.field.maxlength")));
		}

		// City
		if (StringUtils.isBlank(address.getCity())) {
			messages.add(new Message(identifier, error, path + ".city", crm.findMessageId("validation.field.required")));
		} else if (address.getCity().length() > 60) {
			messages.add(new Message(identifier, error, path + ".city", crm.findMessageId("validation.field.maxlength")));
		}

		// Province
		if (address.getProvince().isEmpty()) {
			messages.add(new Message(identifier, error, path + ".province", crm.findMessageId("validation.field.required")));
		} else if (address.getCountry().isEmpty()) {
			messages.add(new Message(identifier, error, path + ".province", crm.findMessageId("validation.field.forbidden")));
		}

		// Country
		if (address.getCountry().isEmpty()) {
			messages.add(new Message(identifier, error, path + ".country", crm.findMessageId("validation.field.required")));
		} else {
			try {
				if (!crm.findOption(address.getCountry().getIdentifier()).getType().equals(Type.COUNTRY))
					messages.add(new Message(identifier, error, path + ".country", crm.findMessageId("validation.option.type")));
			} catch (ItemNotFoundException e) {
				messages.add(new Message(identifier, error, path + ".country", crm.findMessageId("validation.option.missing")));
			}
		}

		// Postal Code
		if (StringUtils.isNotBlank(address.getPostalCode())) {
			if (address.getCountry() != null && address.getCountry().isIdentifer() && address.getCountry().getIdentifier().equals(crm.findOptionByCode(Type.COUNTRY, "CA").getOptionId())) {
				if (!address.getPostalCode().matches("[A-Z][0-9][A-Z] ?[0-9][A-Z][0-9]")) {
					messages.add(new Message(identifier, error, path + ".provinceCode", crm.findMessageId("validation.field.format.invalid")));
				}
			}
		}

		return messages;
	}

}
