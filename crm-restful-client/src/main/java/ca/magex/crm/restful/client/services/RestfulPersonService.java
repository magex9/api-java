package ca.magex.crm.restful.client.services;

import java.util.List;

import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.BusinessRoleIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.restful.client.RestTemplateClient;
import ca.magex.json.model.JsonObject;

public class RestfulPersonService implements CrmPersonService {
	
	private RestTemplateClient client;
	
	public RestfulPersonService(RestTemplateClient client) {
		this.client = client;
	}

	@Override
	public PersonDetails createPerson(OrganizationIdentifier organizationId, String displayName, PersonName legalName, MailingAddress address,
			Communication communication, List<BusinessRoleIdentifier> businessRoleIds) {
		PersonDetails details = prototypePerson(organizationId, displayName, legalName, address, communication, businessRoleIds);
		JsonObject json = client.post("/persons", (JsonObject)client.format(details, PersonDetails.class));
		return client.parse(json, PersonDetails.class);
	}
	
	@Override
	public PersonSummary enablePerson(PersonIdentifier personId) {
		JsonObject json = client.put(personId + "/actions/enable", new JsonObject().with("confirm", true));
		return client.parse(json, PersonSummary.class);	
	}

	@Override
	public PersonSummary disablePerson(PersonIdentifier personId) {
		JsonObject json = client.put(personId + "/actions/disable", new JsonObject().with("confirm", true));
		return client.parse(json, PersonSummary.class);	
	}

	@Override
	public PersonDetails updatePersonDisplayName(PersonIdentifier personId, String displayName) {
		JsonObject json = client.patch(personId + "/details", new JsonObject().with("displayName", displayName));
		return client.parse(json, PersonDetails.class);
	}

	@Override
	public PersonDetails updatePersonLegalName(PersonIdentifier personId, PersonName legalName) {
		JsonObject json = client.patch(personId + "/details", new JsonObject().with("legalName", client.format(legalName, PersonName.class)));
		return client.parse(json, PersonDetails.class);
	}

	@Override
	public PersonDetails updatePersonCommunication(PersonIdentifier personId, Communication communication) {
		JsonObject json = client.patch(personId + "/details", new JsonObject().with("communication", client.format(communication, Communication.class)));
		return client.parse(json, PersonDetails.class);
	}

	@Override
	public PersonDetails updatePersonAddress(PersonIdentifier personId, MailingAddress address) {
		JsonObject json = client.patch(personId + "/details", new JsonObject().with("address", client.format(address, MailingAddress.class)));
		return client.parse(json, PersonDetails.class);
	}
	
	@Override
	public PersonDetails updatePersonBusinessRoles(PersonIdentifier personId, List<BusinessRoleIdentifier> businessRoleIds) {
		JsonObject json = client.patch(personId + "/details", new JsonObject().with("businessRoleIds", client.formatOptions(businessRoleIds)));
		return client.parse(json, PersonDetails.class);
	}

	@Override
	public PersonSummary findPersonSummary(PersonIdentifier personId) {
		JsonObject json = client.get(personId);
		return client.parse(json, PersonSummary.class);
	}

	@Override
	public PersonDetails findPersonDetails(PersonIdentifier personId) {
		JsonObject json = client.get(personId + "/details");
		return client.parse(json, PersonDetails.class);
	}
	
	public JsonObject formatFilter(PersonsFilter filter) {
		return new JsonObject()
			.with("displayName", filter.getDisplayName())
			.with("status", filter.getStatus() == null ? null : (client.format(filter.getStatus(), Status.class)).getString("@value"))
			.with("organizationId", filter.getOrganizationId() == null ? null : filter.getOrganizationId().toString())
			.prune();
	}

	@Override
	public long countPersons(PersonsFilter filter) {
		JsonObject json = client.get("/persons/count", formatFilter(filter));
		return json.getLong("total");
	}

	@Override
	public FilteredPage<PersonDetails> findPersonDetails(PersonsFilter filter, Paging paging) {
		JsonObject json = client.get("/persons/details", client.page(formatFilter(filter), paging));
		List<PersonDetails> content = client.parseList(json.getArray("content"), PersonDetails.class);
		return new FilteredPage<>(filter, paging, content, json.getLong("total"));
	}

	@Override
	public FilteredPage<PersonSummary> findPersonSummaries(PersonsFilter filter, Paging paging) {
		JsonObject json = client.get("/persons", client.page(formatFilter(filter), paging));
		List<PersonSummary> content = client.parseList(json.getArray("content"), PersonSummary.class);
		return new FilteredPage<>(filter, paging, content, json.getLong("total"));
	}

}
