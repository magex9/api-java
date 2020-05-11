package ca.magex.crm.api.services;

import java.util.List;

import org.springframework.data.domain.Page;

import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.system.Identifier;

/**
 * The CRM Organization service is used to manage organizations that are owned
 * by business customers.
 * 
 * There are must be at least one organization in the system with the "CRM"
 * group assigned to it in order to create and manage new organizations in the
 * system. Organizations with the "CRM" group can then create users with the
 * "CRM_ADMIN" role which will be able to manage the customers organizations.
 * Users in this org with the "CRM_USER" role will have some limited
 * functionality to search and maintain some of the organization information.
 * 
 * All customer organizations should have the "ORG" group assigned to them in
 * order to keep their own organization information up to date. There should be
 * one main location and one main contact that are used for communication
 * information in case information needs to be mailed or an email / phone call
 * is required to get information. All users with the "ORG_ADMIN" role will be
 * able to keep their locations and persons up to date for their organization.
 * 
 * Note that organizations are never deleted from the system just enabled and
 * disabled, so it is important to make sure that the organization does not
 * already exist in the system before creating a new one.
 * 
 * @author scott
 *
 */
public interface CrmOrganizationService {

	/**
	 * Create a new organization for a customer or the system.
	 * 
	 * The "SYS" group should be assigned for system users.
	 * The "APP" group should be assigned for background applications.
	 * The "CRM" group should be assigned for internal users.
	 * The "ORG" group should be assigned for customer users.
	 * 
	 * @param organizationDisplayName The name the organization should be displayed in.
	 * @param groups The list of permission groups the users can be assigned to. 
	 * @return Details about the new organization
	 */
	OrganizationDetails createOrganization(String organizationDisplayName, List<String> groups);

	/**
	 * Enable an existing organization that was disabled. If the organization is
	 * already enabled then nothing will be modified.
	 * 
	 * @param organizationId The organization id to enable.
	 * @return The organization that was enabled.
	 */
	OrganizationSummary enableOrganization(Identifier organizationId);

	/**
	 * Disable an existing organization that is active. If the organization is
	 * already disabled then nothing will be modified.
	 * 
	 * Note that SYS, APP and CRM organizations cannot be disabled as they are required
	 * for the system to function.
	 * 
	 * @param organizationId The organization id to disable.
	 * @return The organization that was disabled.
	 */
	OrganizationSummary disableOrganization(Identifier organizationId);

	OrganizationDetails updateOrganizationDisplayName(Identifier organizationId, String name);

	OrganizationDetails updateOrganizationMainLocation(Identifier organizationId, Identifier locationId);

	OrganizationDetails updateOrganizationMainContact(Identifier organizationId, Identifier personId);

	OrganizationDetails updateOrganizationGroups(Identifier organizationId, List<String> groups);

	OrganizationDetails addOrganizationGroup(Identifier organizationId, String group);

	OrganizationDetails removeOrganizationGroup(Identifier organizationId, String group);

	OrganizationSummary findOrganizationSummary(Identifier organizationId);

	OrganizationDetails findOrganizationDetails(Identifier organizationId);

	long countOrganizations(OrganizationsFilter filter);

	Page<OrganizationDetails> findOrganizationDetails(OrganizationsFilter filter, Paging paging);

	Page<OrganizationSummary> findOrganizationSummaries(OrganizationsFilter filter, Paging paging);

}