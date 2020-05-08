package ca.magex.crm.api.services;

import java.util.List;

import org.springframework.data.domain.Page;

import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.system.Identifier;

public interface CrmOrganizationService {
<<<<<<< HEAD
=======
	
    OrganizationDetails createOrganization(String organizationDisplayName);
    
    OrganizationSummary enableOrganization(Identifier organizationId);
    
    OrganizationSummary disableOrganization(Identifier organizationId);
    
    OrganizationDetails updateOrganizationDisplayName(Identifier organizationId, String name);
    
    OrganizationDetails updateOrganizationMainLocation(Identifier organizationId, Identifier locationId);
    
    OrganizationDetails addGroup(Identifier organizationId, Identifier groupId);
	
    OrganizationDetails removeGroup(Identifier organizationId, Identifier groupId);
	
    OrganizationDetails setGroups(Identifier organizationId, List<Identifier> groupIds);
    
    OrganizationSummary findOrganizationSummary(Identifier organizationId);
    
    OrganizationDetails findOrganizationDetails(Identifier organizationId);
    
    long countOrganizations(OrganizationsFilter filter);
    
    Page<OrganizationDetails> findOrganizationDetails(OrganizationsFilter filter, Paging paging);
    
    Page<OrganizationSummary> findOrganizationSummaries(OrganizationsFilter filter, Paging paging);
>>>>>>> refs/remotes/origin/master

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