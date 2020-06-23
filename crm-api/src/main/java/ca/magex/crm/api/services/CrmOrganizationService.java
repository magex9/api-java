package ca.magex.crm.api.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.system.Status;

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

	default OrganizationDetails prototypeOrganization(
			String displayName,
			List<String> groups) {
		return new OrganizationDetails(null, Status.PENDING, displayName, null, null, groups);
	}
	
	default OrganizationDetails createOrganization(OrganizationDetails prototype) {
		return createOrganization(
			prototype.getDisplayName(), 
			prototype.getGroups());
	}
	
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
	OrganizationDetails createOrganization(
		String displayName,
		List<String> groups
	);

	/**
	 * Enable an existing organization that was disabled. If the organization is
	 * already enabled then nothing will be modified.
	 * 
	 * @param organizationId The organization id to enable.
	 * @return The organization that was enabled.
	 */
	OrganizationSummary enableOrganization(
		Identifier organizationId
	);

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

	OrganizationSummary findOrganizationSummary(Identifier organizationId);

	OrganizationDetails findOrganizationDetails(Identifier organizationId);

	long countOrganizations(OrganizationsFilter filter);

	FilteredPage<OrganizationDetails> findOrganizationDetails(OrganizationsFilter filter, Paging paging);

	FilteredPage<OrganizationSummary> findOrganizationSummaries(OrganizationsFilter filter, Paging paging);
	
	default FilteredPage<OrganizationDetails> findOrganizationDetails(OrganizationsFilter filter) {
		return findOrganizationDetails(filter, OrganizationsFilter.getDefaultPaging());
	};
	
	default FilteredPage<OrganizationSummary> findOrganizationSummaries(OrganizationsFilter filter) {
		return findOrganizationSummaries(filter, OrganizationsFilter.getDefaultPaging());
	};
	
	default OrganizationsFilter defaultOrganizationsFilter() {
		return new OrganizationsFilter();
	};

	static List<Message> validateOrganizationDetails(Crm crm, OrganizationDetails organization) {
		List<Message> messages = new ArrayList<Message>();

		// Status
		if (organization.getStatus() == null) {
			messages.add(new Message(organization.getOrganizationId(), "error", "status", new Localized(Lang.ENGLISH, "Status is mandatory for an organization")));
		} else if (organization.getStatus() == Status.PENDING && organization.getOrganizationId() != null) {
			messages.add(new Message(organization.getOrganizationId(), "error", "status", new Localized(Lang.ENGLISH, "Pending statuses should not have identifiers")));
		}

		// Display Name
		if (StringUtils.isBlank(organization.getDisplayName())) {
			messages.add(new Message(organization.getOrganizationId(), "error", "displayName", new Localized(Lang.ENGLISH, "Display name is mandatory for an organization")));
		} else if (organization.getDisplayName().length() > 60) {
			messages.add(new Message(organization.getOrganizationId(), "error", "displayName", new Localized(Lang.ENGLISH, "Display name must be 60 characters or less")));
		}

		// Main contact reference
		if (organization.getMainContactId() != null) {
			PersonSummary person = crm.findPersonSummary(organization.getMainContactId());
			// Make sure main contact belongs to current org
			if (!person.getOrganizationId().equals(organization.getOrganizationId())) {
				messages.add(new Message(organization.getOrganizationId(), "error", "mainContactId", new Localized(Lang.ENGLISH, "Main contact organization has invalid referential integrity")));
			}
			// Make sure main contact is active
			if (!person.getStatus().equals(Status.ACTIVE)) {
				messages.add(new Message(organization.getOrganizationId(), "error", "mainContactId", new Localized(Lang.ENGLISH, "Main contact must be active")));
			}
		}

		// Main location reference
		if (organization.getMainLocationId() != null) {
			LocationSummary location = crm.findLocationSummary(organization.getMainLocationId());
			// Make sure main location belongs to current org
			if (!location.getOrganizationId().equals(organization.getOrganizationId())) {
				messages.add(new Message(organization.getOrganizationId(), "error", "mainLocationId", new Localized(Lang.ENGLISH, "Main location organization has invalid referential integrity")));
			}
			// Make sure main location is active
			if (!location.getStatus().equals(Status.ACTIVE)) {
				messages.add(new Message(organization.getOrganizationId(), "error", "mainLocationId", new Localized(Lang.ENGLISH, "Main location must be active")));
			}
		}

		// Group
		if (organization.getGroups().isEmpty()) {
			messages.add(new Message(organization.getOrganizationId(), "error", "groups", new Localized(Lang.ENGLISH, "Organizations must have a permission group assigned to them")));
		} else {
			for (int i = 0; i < organization.getGroups().size(); i++) {
				String group = organization.getGroups().get(i);
				try {
					if (!crm.findGroupByCode(group).getStatus().equals(Status.ACTIVE))
						messages.add(new Message(organization.getOrganizationId(), "error", "groups[" + i + "]", new Localized(Lang.ENGLISH, "Group is not active: " + group)));
				} catch (ItemNotFoundException e) {
					messages.add(new Message(organization.getOrganizationId(), "error", "groups[" + i + "]", new Localized(Lang.ENGLISH, "Group does not exist: " + group)));
				}
			}
		}

		return messages;
	}
	
}