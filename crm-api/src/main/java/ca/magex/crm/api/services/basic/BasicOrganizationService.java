package ca.magex.crm.api.services.basic;

import java.util.List;

import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.repositories.CrmRepositories;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.AuthenticationGroupIdentifier;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;

public class BasicOrganizationService implements CrmOrganizationService {

	private CrmRepositories repos;
	
	public BasicOrganizationService(CrmRepositories repos) {
		this.repos = repos;
	}
	
	public OrganizationDetails createOrganization(String organizationDisplayName, List<AuthenticationGroupIdentifier> groupIds) {
		return repos.saveOrganizationDetails(new OrganizationDetails(repos.generateOrganizationId(), Status.ACTIVE, organizationDisplayName, null, null, groupIds));
	}

	public OrganizationSummary enableOrganization(OrganizationIdentifier organizationId) {
		OrganizationDetails details = findOrganizationDetails(organizationId);
		if (details == null) {
			return null;
		}
		return repos.saveOrganizationDetails(details.withStatus(Status.ACTIVE));
	}

	public OrganizationSummary disableOrganization(OrganizationIdentifier organizationId) {
		OrganizationDetails details = findOrganizationDetails(organizationId);
		if (details == null) {
			return null;
		}
		return repos.saveOrganizationDetails(details.withStatus(Status.INACTIVE));
	}

	public OrganizationDetails updateOrganizationDisplayName(OrganizationIdentifier organizationId, String name) {
		OrganizationDetails details = findOrganizationDetails(organizationId);
		if (details == null) {
			return null;
		}
		return repos.saveOrganizationDetails(details.withDisplayName(name));
	}

	public OrganizationDetails updateOrganizationMainLocation(OrganizationIdentifier organizationId, LocationIdentifier locationId) {
		OrganizationDetails details = findOrganizationDetails(organizationId);
		if (details == null) {
			return null;
		}
		return repos.saveOrganizationDetails(details.withMainLocationId(locationId));
	}
	
	public OrganizationDetails updateOrganizationMainContact(OrganizationIdentifier organizationId, PersonIdentifier personId) {
		OrganizationDetails details = findOrganizationDetails(organizationId);
		if (details == null) {
			return null;
		}
		return repos.saveOrganizationDetails(details.withMainContactId(personId));
	}

	public OrganizationDetails updateOrganizationGroups(OrganizationIdentifier organizationId, List<AuthenticationGroupIdentifier> withAuthenticationGroupIds) {
		OrganizationDetails details = findOrganizationDetails(organizationId);
		if (details == null) {
			return null;
		}
		return repos.saveOrganizationDetails(details.withAuthenticationGroupIds(withAuthenticationGroupIds));
	}

	@Override
	public OrganizationSummary findOrganizationSummary(OrganizationIdentifier organizationId) {
		return repos.findOrganizationSummary(organizationId);
	}
	
	public OrganizationDetails findOrganizationDetails(OrganizationIdentifier organizationId) {
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
