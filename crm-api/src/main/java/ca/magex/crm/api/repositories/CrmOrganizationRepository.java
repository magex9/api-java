package ca.magex.crm.api.repositories;

import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.store.CrmStore;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;

public interface CrmOrganizationRepository {

	public static final String CONTEXT = "/organizations";
	
	default Identifier generateOrganizationId() {
		return CrmStore.generateId(CONTEXT);
	}

	public FilteredPage<OrganizationDetails> findOrganizationDetails(OrganizationsFilter filter, Paging paging); 

	public FilteredPage<OrganizationSummary> findOrganizationSummary(OrganizationsFilter filter, Paging paging); 

	public long countOrganizations(OrganizationsFilter filter); 
	
	public OrganizationDetails findOrganizationDetails(Identifier organizationId);

	public OrganizationSummary findOrganizationSummary(Identifier organizationId);

	public OrganizationDetails saveOrganizationDetails(OrganizationDetails organization);
	
}
