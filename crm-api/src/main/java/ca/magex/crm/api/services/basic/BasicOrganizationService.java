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
import ca.magex.crm.api.system.id.BusinessGroupIdentifier;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;

public class BasicOrganizationService implements CrmOrganizationService {

	private CrmRepositories repos;
	
	public BasicOrganizationService(CrmRepositories repos) {
		this.repos = repos;
	}
	
	public OrganizationDetails createOrganization(String organizationDisplayName, List<AuthenticationGroupIdentifier> authenticationGroupIds, List<BusinessGroupIdentifier> businessGroupIds) {
		return repos.saveOrganizationDetails(new OrganizationDetails(repos.generateOrganizationId(), Status.ACTIVE, organizationDisplayName, null, null, authenticationGroupIds, businessGroupIds, null));
	}

	public OrganizationSummary enableOrganization(OrganizationIdentifier organizationId) {
		OrganizationDetails details = findOrganizationDetails(organizationId);
		if (details == null) {
			return null;
		}
		return repos.saveOrganizationDetails(details.withStatus(Status.ACTIVE)).asSummary();
	}

	public OrganizationSummary disableOrganization(OrganizationIdentifier organizationId) {
		OrganizationDetails details = findOrganizationDetails(organizationId);
		if (details == null) {
			return null;
		}
		return repos.saveOrganizationDetails(details.withStatus(Status.INACTIVE)).asSummary();
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

	public OrganizationDetails updateOrganizationAuthenticationGroups(OrganizationIdentifier organizationId, List<AuthenticationGroupIdentifier> authenticationGroupIds) {
		OrganizationDetails details = findOrganizationDetails(organizationId);
		if (details == null) {
			return null;
		}
		return repos.saveOrganizationDetails(details.withAuthenticationGroupIds(authenticationGroupIds));
	}
	
	@Override
	public OrganizationDetails updateOrganizationBusinessGroups(OrganizationIdentifier organizationId, List<BusinessGroupIdentifier> businessGroupIds) {
		OrganizationDetails details = findOrganizationDetails(organizationId);
		if (details == null) {
			return null;
		}
		return repos.saveOrganizationDetails(details.withBusinessGroupIds(businessGroupIds));
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
