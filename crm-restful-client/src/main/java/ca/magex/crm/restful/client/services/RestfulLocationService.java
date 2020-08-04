package ca.magex.crm.restful.client.services;

import java.util.List;
import java.util.stream.Collectors;

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
import ca.magex.crm.api.transform.Transformer;
import ca.magex.crm.restful.client.RestTemplateClient;
import ca.magex.crm.transform.json.LocationDetailsJsonTransformer;
import ca.magex.crm.transform.json.LocationSummaryJsonTransformer;
import ca.magex.crm.transform.json.MailingAddressJsonTransformer;
import ca.magex.crm.transform.json.StatusJsonTransformer;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;

public class RestfulLocationService implements CrmLocationService {
	
	private RestTemplateClient client;
	
	private Transformer<Status, JsonElement> statusTransformer;
	
	private Transformer<MailingAddress, JsonElement> mailingAddressTransformer;
	
	private Transformer<LocationDetails, JsonElement> detailsTransformer;
	
	private Transformer<LocationSummary, JsonElement> summaryTransformer;
	
	public RestfulLocationService(RestTemplateClient client) {
		this.client = client;
		this.statusTransformer = new StatusJsonTransformer(client.getOptions());
		this.mailingAddressTransformer = new MailingAddressJsonTransformer(client.getOptions());
		this.detailsTransformer = new LocationDetailsJsonTransformer(client.getOptions());
		this.summaryTransformer = new LocationSummaryJsonTransformer(client.getOptions());
	}

	@Override
	public LocationDetails createLocation(OrganizationIdentifier organizationId, String reference, String displayName,
			MailingAddress address) {
		LocationDetails details = prototypeLocation(organizationId, reference, displayName, address);
		JsonObject json = client.post("/locations", (JsonObject)detailsTransformer.format(details, client.getLocale()));
		return detailsTransformer.parse(json, client.getLocale());
	}

	@Override
	public LocationSummary enableLocation(LocationIdentifier locationId) {
		JsonObject json = client.put(locationId + "/enable", new JsonObject().with("confirm", true));
		return summaryTransformer.parse(json, client.getLocale());	
	}

	@Override
	public LocationSummary disableLocation(LocationIdentifier locationId) {
		JsonObject json = client.put(locationId + "/disable", new JsonObject().with("confirm", true));
		return summaryTransformer.parse(json, client.getLocale());	
	}

	@Override
	public LocationDetails updateLocationName(LocationIdentifier locationId, String displaysName) {
		JsonObject json = client.patch(locationId, new JsonObject().with("displayName", displaysName));
		return detailsTransformer.parse(json, client.getLocale());
	}

	@Override
	public LocationDetails updateLocationAddress(LocationIdentifier locationId, MailingAddress address) {
		JsonObject json = client.patch(locationId, new JsonObject().with("address", mailingAddressTransformer.format(address, client.getLocale())));
		return detailsTransformer.parse(json, client.getLocale());
	}

	@Override
	public LocationSummary findLocationSummary(LocationIdentifier locationId) {
		JsonObject json = client.get(locationId);
		return summaryTransformer.parse(json, client.getLocale());
	}

	@Override
	public LocationDetails findLocationDetails(LocationIdentifier locationId) {
		JsonObject json = client.get(locationId + "/details");
		return detailsTransformer.parse(json, client.getLocale());
	}
	
	public JsonObject formatFilter(LocationsFilter filter) {
		return new JsonObject()
			.with("displayName", filter.getDisplayName())
			.with("status", filter.getStatus() == null ? null : ((JsonObject)statusTransformer.format(filter.getStatus(), client.getLocale())).getString("@value"))
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
		JsonObject json = client.get("/locations/details", formatFilter(filter));
		List<LocationDetails> content = json.getArray("content", JsonObject.class).stream()
			.map(e -> detailsTransformer.parse(e, client.getLocale())).collect(Collectors.toList());
		return new FilteredPage<>(filter, paging, content, json.getLong("total"));
	}

	@Override
	public FilteredPage<LocationSummary> findLocationSummaries(LocationsFilter filter, Paging paging) {
		JsonObject json = client.get("/locations", formatFilter(filter));
		List<LocationSummary> content = json.getArray("content", JsonObject.class).stream()
			.map(e -> summaryTransformer.parse(e, client.getLocale())).collect(Collectors.toList());
		return new FilteredPage<>(filter, paging, content, json.getLong("total"));
	}

}
