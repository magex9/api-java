package ca.magex.crm.restful.client.services;

import java.util.List;

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
import ca.magex.crm.restful.client.RestTemplateClient;
import ca.magex.json.model.JsonObject;

public class RestfulOrganizationService implements CrmOrganizationService {
	
	private RestTemplateClient client;
	
	public RestfulOrganizationService(RestTemplateClient client) {
		this.client = client;
	}
	
	@Override
	public OrganizationDetails createOrganization(String displayName,
			List<AuthenticationGroupIdentifier> authenticationGroupIds,
			List<BusinessGroupIdentifier> businessGroupIds) {
		OrganizationDetails details = prototypeOrganization(displayName, authenticationGroupIds, businessGroupIds);
		JsonObject json = client.post("/organizations", client.format(details, OrganizationDetails.class));
		return client.parse(json, OrganizationDetails.class);
	}

	@Override
	public OrganizationSummary enableOrganization(OrganizationIdentifier organizationId) {
		JsonObject json = client.put(organizationId + "/actions/enable", new JsonObject().with("confirm", true));
		return client.parse(json, OrganizationSummary.class);	
	}

	@Override
	public OrganizationSummary disableOrganization(OrganizationIdentifier organizationId) {
		JsonObject json = client.put(organizationId + "/actions/disable", new JsonObject().with("confirm", true));
		return client.parse(json, OrganizationSummary.class);	
	}
	
	@Override
	public OrganizationDetails updateOrganizationDisplayName(OrganizationIdentifier organizationId, String name) {
		JsonObject json = client.patch(organizationId + "/details", new JsonObject().with("displayName", name));
		return client.parse(json, OrganizationDetails.class);
	}

	@Override
	public OrganizationDetails updateOrganizationMainLocation(OrganizationIdentifier organizationId,
			LocationIdentifier locationId) {
		JsonObject json = client.patch(organizationId + "/details", new JsonObject().with("mainLocationId", client.formatIdentifier(locationId)));
		return client.parse(json, OrganizationDetails.class);
	}

	@Override
	public OrganizationDetails updateOrganizationMainContact(OrganizationIdentifier organizationId,
			PersonIdentifier personId) {
		JsonObject json = client.patch(organizationId + "/details", new JsonObject().with("mainContactId", client.formatIdentifier(personId)));
		return client.parse(json, OrganizationDetails.class);
	}

	@Override
	public OrganizationDetails updateOrganizationAuthenticationGroups(OrganizationIdentifier organizationId,
			List<AuthenticationGroupIdentifier> authenticationGroupIds) {
		JsonObject json = client.patch(organizationId + "/details", new JsonObject().with("authenticationGroupIds", client.formatOptions(authenticationGroupIds)));
		return client.parse(json, OrganizationDetails.class);
	}

	@Override
	public OrganizationDetails updateOrganizationBusinessGroups(OrganizationIdentifier organizationId,
			List<BusinessGroupIdentifier> businessGroupIdentifier) {
		JsonObject json = client.patch(organizationId + "/details", new JsonObject().with("businessGroupIds", client.formatOptions(businessGroupIdentifier)));
		return client.parse(json, OrganizationDetails.class);
	}

	@Override
	public OrganizationSummary findOrganizationSummary(OrganizationIdentifier organizationId) {
		JsonObject json = client.get(organizationId);
		return client.parse(json, OrganizationSummary.class);
	}

	@Override
	public OrganizationDetails findOrganizationDetails(OrganizationIdentifier organizationId) {
		JsonObject json = client.get(organizationId + "/details");
		return client.parse(json, OrganizationDetails.class);
	}
	
	public JsonObject formatFilter(OrganizationsFilter filter) {
		return new JsonObject()
			.with("displayName", filter.getDisplayName())
			.with("status", filter.getStatus() == null ? null : (client.format(filter.getStatus(), Status.class)).getString("@value"))
			.with("authenticationGroupId", client.formatIdentifier(filter.getAuthenticationGroupId()))
			.with("businessGroupId", client.formatIdentifier(filter.getBusinessGroupId()))
			.prune();
	}

	@Override
	public long countOrganizations(OrganizationsFilter filter) {
		JsonObject json = client.get("/organizations/count", formatFilter(filter));
		return json.getLong("total");
	}

	@Override
	public FilteredPage<OrganizationDetails> findOrganizationDetails(OrganizationsFilter filter, Paging paging) {
		JsonObject json = client.get("/organizations/details", client.page(formatFilter(filter), paging));
		List<OrganizationDetails> content = client.parseList(json.getArray("content"), OrganizationDetails.class);
		return new FilteredPage<>(filter, paging, content, json.getLong("total"));
	}

	@Override
	public FilteredPage<OrganizationSummary> findOrganizationSummaries(OrganizationsFilter filter, Paging paging) {
		JsonObject json = client.get("/organizations", client.page(formatFilter(filter), paging));
		List<OrganizationSummary> content = client.parseList(json.getArray("content"), OrganizationSummary.class);
		return new FilteredPage<>(filter, paging, content, json.getLong("total"));
	}

}
