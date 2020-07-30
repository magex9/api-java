package ca.magex.crm.restful.client.services;

import java.util.List;
import java.util.stream.Collectors;

import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.AuthenticationGroupIdentifier;
import ca.magex.crm.api.system.id.BusinessGroupIdentifier;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.transform.Transformer;
import ca.magex.crm.restful.client.RestTemplateClient;
import ca.magex.crm.transform.json.OrganizationDetailsJsonTransformer;
import ca.magex.crm.transform.json.OrganizationSummaryJsonTransformer;
import ca.magex.crm.transform.json.StatusJsonTransformer;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;

public class RestfulOrganizationService implements CrmOrganizationService {
	
	private RestTemplateClient client;
	
	private Transformer<Status, JsonElement> statusTransformer;
	
	private Transformer<OrganizationDetails, JsonElement> detailsTransformer;
	
	private Transformer<OrganizationSummary, JsonElement> summaryTransformer;
	
	public RestfulOrganizationService(RestTemplateClient client) {
		this.client = client;
		this.statusTransformer = new StatusJsonTransformer(client.getOptions());
		this.detailsTransformer = new OrganizationDetailsJsonTransformer(client.getOptions());
		this.summaryTransformer = new OrganizationSummaryJsonTransformer(client.getOptions());
	}
	
	@Override
	public OrganizationDetails createOrganization(String displayName,
			List<AuthenticationGroupIdentifier> authenticationGroupIds,
			List<BusinessGroupIdentifier> businessGroupIds) {
		OrganizationDetails details = prototypeOrganization(displayName, authenticationGroupIds, businessGroupIds);
		JsonObject json = client.post("/organizations", (JsonObject)detailsTransformer.format(details, client.getLocale()));
		return detailsTransformer.parse(json, client.getLocale());
	}

	@Override
	public OrganizationSummary enableOrganization(OrganizationIdentifier organizationId) {
		JsonObject json = client.put(organizationId + "/enable", new JsonObject().with("confirm", true));
		return summaryTransformer.parse(json, client.getLocale());	
	}

	@Override
	public OrganizationSummary disableOrganization(OrganizationIdentifier organizationId) {
		JsonObject json = client.put(organizationId + "/disable", new JsonObject().with("confirm", true));
		return summaryTransformer.parse(json, client.getLocale());	
	}
	
	@Override
	public OrganizationDetails updateOrganizationDisplayName(OrganizationIdentifier organizationId, String name) {
		JsonObject json = client.patch(organizationId, new JsonObject().with("displayName", name));
		return detailsTransformer.parse(json, client.getLocale());
	}

	@Override
	public OrganizationDetails updateOrganizationMainLocation(OrganizationIdentifier organizationId,
			LocationIdentifier locationId) {
		JsonObject json = client.patch(organizationId, new JsonObject().with("mainLocationId", locationId));
		return detailsTransformer.parse(json, client.getLocale());
	}

	@Override
	public OrganizationDetails updateOrganizationMainContact(OrganizationIdentifier organizationId,
			PersonIdentifier personId) {
		JsonObject json = client.patch(organizationId, new JsonObject().with("mainContactId", personId));
		return detailsTransformer.parse(json, client.getLocale());
	}

	@Override
	public OrganizationDetails updateOrganizationAuthenticationGroups(OrganizationIdentifier organizationId,
			List<AuthenticationGroupIdentifier> groupIds) {
		JsonObject json = client.patch(organizationId, new JsonObject().with("authenticationGroupIds", groupIds));
		return detailsTransformer.parse(json, client.getLocale());
	}

	@Override
	public OrganizationDetails updateOrganizationBusinessGroups(OrganizationIdentifier organizationId,
			List<BusinessGroupIdentifier> groupIds) {
		JsonObject json = client.patch(organizationId, new JsonObject().with("businessGroupIds", groupIds));
		return detailsTransformer.parse(json, client.getLocale());
	}

	@Override
	public OrganizationSummary findOrganizationSummary(OrganizationIdentifier organizationId) {
		JsonObject json = client.get(organizationId + "/summary");
		return summaryTransformer.parse(json, client.getLocale());
	}

	@Override
	public OrganizationDetails findOrganizationDetails(OrganizationIdentifier organizationId) {
		JsonObject json = client.get(organizationId);
		return detailsTransformer.parse(json, client.getLocale());
	}
	
	public JsonObject formatFilter(OrganizationsFilter filter) {
		return new JsonObject()
			.with("displayName", filter.getDisplayName())
			.with("status", filter.getStatus() == null ? null : ((JsonObject)statusTransformer.format(filter.getStatus(), client.getLocale())).getString("@value"))
			.with("authenticationGroupId", filter.getAuthenticationGroupId())
			.with("businessGroupId", filter.getBusinessGroupId())
			.prune();
	}

	@Override
	public long countOrganizations(OrganizationsFilter filter) {
		JsonObject json = client.get("/organizations/count", formatFilter(filter));
		return json.getLong("total");
	}

	@Override
	public FilteredPage<OrganizationDetails> findOrganizationDetails(OrganizationsFilter filter, Paging paging) {
		JsonObject json = client.get("/organizations", formatFilter(filter));
		List<OrganizationDetails> content = json.getArray("content", JsonObject.class).stream()
			.map(e -> detailsTransformer.parse(e, client.getLocale())).collect(Collectors.toList());
		return new FilteredPage<>(filter, paging, content, json.getLong("total"));
	}

	@Override
	public FilteredPage<OrganizationSummary> findOrganizationSummaries(OrganizationsFilter filter, Paging paging) {
		JsonObject json = client.get("/organizations/summaries", formatFilter(filter));
		List<OrganizationSummary> content = json.getArray("content", JsonObject.class).stream()
			.map(e -> summaryTransformer.parse(e, client.getLocale())).collect(Collectors.toList());
		return new FilteredPage<>(filter, paging, content, json.getLong("total"));
	}

}
