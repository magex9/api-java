package ca.magex.crm.restful.client.services;

import java.util.List;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.restful.client.RestTemplateClient;
import ca.magex.json.model.JsonObject;

public class RestfulLocationService implements CrmLocationService {
	
	private RestTemplateClient client;
	
	public RestfulLocationService(RestTemplateClient client) {
		this.client = client;
	}

	@Override
	public LocationDetails createLocation(OrganizationIdentifier organizationId, String reference, String displayName,
			MailingAddress address) {
		LocationDetails details = prototypeLocation(organizationId, reference, displayName, address);
		JsonObject json = client.post("/locations", (JsonObject)client.format(details, LocationDetails.class));
		return client.parse(json, LocationDetails.class);
	}

	@Override
	public LocationSummary enableLocation(LocationIdentifier locationId) {
		JsonObject json = client.put(locationId + "/enable", new JsonObject().with("confirm", true));
		return client.parse(json, LocationSummary.class);
	}

	@Override
	public LocationSummary disableLocation(LocationIdentifier locationId) {
		JsonObject json = client.put(locationId + "/disable", new JsonObject().with("confirm", true));
		return client.parse(json, LocationSummary.class);
	}

	@Override
	public LocationDetails updateLocationName(LocationIdentifier locationId, String displaysName) {
		JsonObject json = client.patch(locationId, new JsonObject().with("displayName", displaysName));
		return client.parse(json, LocationDetails.class);
	}

	@Override
	public LocationDetails updateLocationAddress(LocationIdentifier locationId, MailingAddress address) {
		JsonObject json = client.patch(locationId, new JsonObject().with("address", client.format(address, MailingAddress.class)));
		return client.parse(json, LocationDetails.class);
	}

	@Override
	public LocationSummary findLocationSummary(LocationIdentifier locationId) {
		JsonObject json = client.get(locationId);
		return client.parse(json, LocationSummary.class);
	}

	@Override
	public LocationDetails findLocationDetails(LocationIdentifier locationId) {
		JsonObject json = client.get(locationId + "/details");
		return client.parse(json, LocationDetails.class);
	}
	
	public JsonObject formatFilter(LocationsFilter filter) {
		return new JsonObject()
			.with("displayName", filter.getDisplayName())
			.with("status", filter.getStatus() == null ? null : (client.format(filter.getStatus(), Status.class)).getString("@value"))
			.with("organizationId", filter.getOrganizationId() == null ? null : filter.getOrganizationId().toString())
			.with("reference", filter.getReference())
			.prune();
	}

	@Override
	public long countLocations(LocationsFilter filter) {
		JsonObject json = client.get("/locations/count", formatFilter(filter));
		return json.getLong("total");
	}

	@Override
	public FilteredPage<LocationDetails> findLocationDetails(LocationsFilter filter, Paging paging) {
		JsonObject json = client.get("/locations/details", client.page(formatFilter(filter), paging));
		List<LocationDetails> content = client.parseList(json.getArray("content"), LocationDetails.class);
		return new FilteredPage<>(filter, paging, content, json.getLong("total"));
	}

	@Override
	public FilteredPage<LocationSummary> findLocationSummaries(LocationsFilter filter, Paging paging) {
		JsonObject json = client.get("/locations", client.page(formatFilter(filter), paging));
		List<LocationSummary> content = client.parseList(json.getArray("content"), LocationSummary.class);
		return new FilteredPage<>(filter, paging, content, json.getLong("total"));
	}

}
