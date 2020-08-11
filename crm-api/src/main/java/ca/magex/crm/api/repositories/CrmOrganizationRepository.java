package ca.magex.crm.api.repositories;

import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.store.CrmStore;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.id.OrganizationIdentifier;

/**
 * Repository interface used for saving/retrieving an Organization
 * 
 * @author Jonny
 */
public interface CrmOrganizationRepository {
	
	/**
	 * returns the next identifier to be assigned to a new Organization
	 * 
	 * @return
	 */
	default OrganizationIdentifier generateOrganizationId() {
		return new OrganizationIdentifier(CrmStore.generateId());
	}
	
	/**
	 * Save the given organization to the repository
	 * 
	 * @param organization
	 * @return
	 */
	public OrganizationDetails saveOrganizationDetails(OrganizationDetails organization);

	/**
	 * returns the full organization details associated with the given organizationId, 
	 * or null if the organizationId does not exist
	 * 
	 * @param organizationId
	 * @return
	 */
	public OrganizationDetails findOrganizationDetails(OrganizationIdentifier organizationId);

	/**
	 * returns the organization summary associated with the given organizationId, 
	 * or null if the organizationId does not exist
	 * 
	 * @param organizationId
	 * @return
	 */
	public OrganizationSummary findOrganizationSummary(OrganizationIdentifier organizationId);

	/**
	 * returns the paged results with the full organization details for any organization that matches the given filter
	 * 
	 * @param filter
	 * @param paging
	 * @return
	 */
	public FilteredPage<OrganizationDetails> findOrganizationDetails(OrganizationsFilter filter, Paging paging);

	/**
	 * returns the paged results with the organization summaries for any organization that matches the given filter
	 * 
	 * @param filter
	 * @param paging
	 * @return
	 */
	public FilteredPage<OrganizationSummary> findOrganizationSummary(OrganizationsFilter filter, Paging paging);

	/**
	 * returns the number of organizations that match the given filter
	 * 
	 * @param filter
	 * @return
	 */
	public long countOrganizations(OrganizationsFilter filter);
}
