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
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.AuthenticationGroupIdentifier;
import ca.magex.crm.api.system.id.BusinessGroupIdentifier;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.MessageTypeIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;

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
			List<AuthenticationGroupIdentifier> authenticationGroupIds, 
			List<BusinessGroupIdentifier> businessGroupIds) {
		return new OrganizationDetails(null, Status.PENDING, displayName, null, null, authenticationGroupIds, businessGroupIds);
	}
	
	default OrganizationDetails createOrganization(OrganizationDetails prototype) {
		return createOrganization(prototype.getDisplayName(), prototype.getAuthenticationGroupIds(), prototype.getBusinessGroupIds());
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
	 * @param authenticationGroupIds The list of permission groups the users can be assigned to. 
	 * @return Details about the new organization
	 */
	OrganizationDetails createOrganization(String displayName, List<AuthenticationGroupIdentifier> authenticationGroupIds, List<BusinessGroupIdentifier> businessGroupIds);

	/**
	 * Enable an existing organization that was disabled. If the organization is
	 * already enabled then nothing will be modified.
	 * 
	 * @param organizationId The organization id to enable.
	 * @return The organization that was enabled.
	 */
	OrganizationSummary enableOrganization(OrganizationIdentifier organizationId);

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
	OrganizationSummary disableOrganization(OrganizationIdentifier organizationId);

	OrganizationDetails updateOrganizationDisplayName(OrganizationIdentifier organizationId, String name);

	OrganizationDetails updateOrganizationMainLocation(OrganizationIdentifier organizationId, LocationIdentifier locationId);

	OrganizationDetails updateOrganizationMainContact(OrganizationIdentifier organizationId, PersonIdentifier personId);

	OrganizationDetails updateOrganizationAuthenticationGroups(OrganizationIdentifier organizationId, List<AuthenticationGroupIdentifier> groupIds);
	
	OrganizationDetails updateOrganizationBusinessGroups(OrganizationIdentifier organizationId, List<BusinessGroupIdentifier> groupIds);

	OrganizationSummary findOrganizationSummary(OrganizationIdentifier organizationId);

	OrganizationDetails findOrganizationDetails(OrganizationIdentifier organizationId);

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
		
		MessageTypeIdentifier error = crm.findOptionByCode(Type.MESSAGE_TYPE, "ERROR").getOptionId();

		// Status
		if (organization.getStatus() == null) {
			messages.add(new Message(organization.getOrganizationId(), error, "status", null, crm.findMessageId("validation.field.required")));
		} else if (organization.getStatus() == Status.PENDING && organization.getOrganizationId() != null) {
			messages.add(new Message(organization.getOrganizationId(), error, "status", organization.getStatus().name(), crm.findMessageId("validation.status.pending")));
		}

		// Display Name
		if (StringUtils.isBlank(organization.getDisplayName())) {
			messages.add(new Message(organization.getOrganizationId(), error, "displayName", organization.getDisplayName(), crm.findMessageId("validation.field.required")));
		} else if (organization.getDisplayName().length() > 60) {
			messages.add(new Message(organization.getOrganizationId(), error, "displayName", organization.getDisplayName(), crm.findMessageId("validation.field.maxlength")));
		}

		// Main contact reference
		if (organization.getMainContactId() != null) {
			PersonSummary person = crm.findPersonSummary(organization.getMainContactId());
			// Make sure main contact belongs to current org
			if (!person.getOrganizationId().equals(organization.getOrganizationId())) {
				messages.add(new Message(organization.getOrganizationId(), error, "mainContactId", organization.getMainContactId().getCode(), crm.findMessageId("validation.field.invalid")));
			}
			// Make sure main contact is active
			if (!person.getStatus().equals(Status.ACTIVE)) {
				messages.add(new Message(organization.getOrganizationId(), error, "mainContactId", organization.getMainContactId().getCode(), crm.findMessageId("validation.field.inactive")));
			}
		}

		// Main location reference
		if (organization.getMainLocationId() != null) {
			LocationSummary location = crm.findLocationSummary(organization.getMainLocationId());
			// Make sure main location belongs to current org
			if (!location.getOrganizationId().equals(organization.getOrganizationId())) {
				messages.add(new Message(organization.getOrganizationId(), error, "mainLocationId", organization.getMainLocationId().getCode(), crm.findMessageId("validation.field.invalid")));
			}
			// Make sure main location is active
			if (!location.getStatus().equals(Status.ACTIVE)) {
				messages.add(new Message(organization.getOrganizationId(), error, "mainLocationId", organization.getMainLocationId().getCode(), crm.findMessageId("validation.field.inactive")));
			}
		}

		// Group
		if (organization.getAuthenticationGroupIds().isEmpty()) {
			messages.add(new Message(organization.getOrganizationId(), error, "authenticationGroupIds", null, crm.findMessageId("validation.field.required")));
		} else {
			for (int i = 0; i < organization.getAuthenticationGroupIds().size(); i++) {
				AuthenticationGroupIdentifier groupId = organization.getAuthenticationGroupIds().get(i);
				try {
					if (!crm.findOption(groupId).getStatus().equals(Status.ACTIVE))
						messages.add(new Message(organization.getOrganizationId(), error, "authenticationGroupIds[" + i + "]", groupId.getCode(), crm.findMessageId("validation.field.inactive")));
				} catch (ItemNotFoundException e) {
					messages.add(new Message(organization.getOrganizationId(), error, "authenticationGroupIds[" + i + "]", groupId.getCode(), crm.findMessageId("validation.field.invalid")));
				}
			}
		}

		return messages;
	}
	
}