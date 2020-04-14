package ca.magex.crm.api.services;

import org.springframework.data.domain.Page;

import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.system.Identifier;

public interface CrmOrganizationService {
	
    OrganizationDetails createOrganization(String organizationDisplayName);
    OrganizationSummary enableOrganization(Identifier organizationId);
    OrganizationSummary disableOrganization(Identifier organizationId);
    OrganizationDetails updateOrganizationDisplayName(Identifier organizationId, String name);
    OrganizationDetails updateOrganizationMainLocation(Identifier organizationId, Identifier locationId);
    OrganizationSummary findOrganizationSummary(Identifier organizationId);
    OrganizationDetails findOrganizationDetails(Identifier organizationId);
    long countOrganizations(OrganizationsFilter filter);
    Page<OrganizationDetails> findOrganizationDetails(OrganizationsFilter filter, Paging paging);
    Page<OrganizationSummary> findOrganizationSummaries(OrganizationsFilter filter, Paging paging);

}
