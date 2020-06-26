package ca.magex.crm.api.services.basic;

import java.util.List;

import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.repositories.CrmRepositories;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public class BasicOrganizationService implements CrmOrganizationService {

	private CrmRepositories repos;
	
	public BasicOrganizationService(CrmRepositories repos) {
		this.repos = repos;
	}
	
	public OrganizationDetails createOrganization(String organizationDisplayName, List<Identifier> groupIds) {
		return repos.saveOrganizationDetails(new OrganizationDetails(repos.generateOrganizationId(), Status.ACTIVE, organizationDisplayName, null, null, groupIds));
	}

	public OrganizationSummary enableOrganization(Identifier organizationId) {
		OrganizationDetails details = findOrganizationDetails(organizationId);
		if (details == null) {
			return null;
		}
		return repos.saveOrganizationDetails(details.withStatus(Status.ACTIVE));
	}

	public OrganizationSummary disableOrganization(Identifier organizationId) {
		OrganizationDetails details = findOrganizationDetails(organizationId);
		if (details == null) {
			return null;
		}
		return repos.saveOrganizationDetails(details.withStatus(Status.INACTIVE));
	}

	public OrganizationDetails updateOrganizationDisplayName(Identifier organizationId, String name) {
		OrganizationDetails details = findOrganizationDetails(organizationId);
		if (details == null) {
			return null;
		}
		return repos.saveOrganizationDetails(details.withDisplayName(name));
	}

	public OrganizationDetails updateOrganizationMainLocation(Identifier organizationId, Identifier locationId) {
		OrganizationDetails details = findOrganizationDetails(organizationId);
		if (details == null) {
			return null;
		}
		return repos.saveOrganizationDetails(details.withMainLocationId(locationId));
	}
	
	public OrganizationDetails updateOrganizationMainContact(Identifier organizationId, Identifier personId) {
		OrganizationDetails details = findOrganizationDetails(organizationId);
		if (details == null) {
			return null;
		}
		return repos.saveOrganizationDetails(details.withMainContactId(personId));
	}

	public OrganizationDetails updateOrganizationGroups(Identifier organizationId, List<Identifier> groupIds) {
		OrganizationDetails details = findOrganizationDetails(organizationId);
		if (details == null) {
			return null;
		}
		return repos.saveOrganizationDetails(details.withGroupIds(groupIds));
	}

	@Override
	public OrganizationSummary findOrganizationSummary(Identifier organizationId) {
		return repos.findOrganizationSummary(organizationId);
	}
	
	public OrganizationDetails findOrganizationDetails(Identifier organizationId) {
		return repos.findOrganizationDetails(organizationId);
	}
	
	@Override
	public long countOrganizations(OrganizationsFilter filter) {
		return repos.countOrganizations(filter);
	}
	
	public FilteredPage<OrganizationSummary> findOrganizationSummaries(OrganizationsFilter filter, Paging paging) {
		return repos.findOrganizationSummary(filter, paging);
	}
	
	public FilteredPage<OrganizationDetails> findOrganizationDetails(OrganizationsFilter filter, Paging paging) {
		return repos.findOrganizationDetails(filter, paging);
	}
	
}
