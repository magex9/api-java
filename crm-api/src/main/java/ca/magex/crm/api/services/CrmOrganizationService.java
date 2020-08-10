package ca.magex.crm.api.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

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
import ca.magex.crm.api.system.id.PhraseIdentifier;

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
	 * Create a prototype of an organization that is not persisted and does not have an organization id
	 * 
	 * @param displayName The common name of the organization
	 * @param authenticationGroupIds The authentication groups users of the organization can have roles from.
	 * @param businessGroupIds The business groups users of the organization can have business roles from.
	 * @return A stub of an organization not persisted in the system
	 */
	default OrganizationDetails prototypeOrganization(
			String displayName, 
			List<AuthenticationGroupIdentifier> authenticationGroupIds, 
			List<BusinessGroupIdentifier> businessGroupIds) {
		return new OrganizationDetails(null, Status.PENDING, displayName, null, null, authenticationGroupIds, businessGroupIds);
	}
	
	/**
	 * Create an organization based on a template or the organization details created from a prototype or 
	 * duplicate the information from an already existing organization.
	 * 
	 * @param prototype The template organization details.
	 * @return A new organization created from the template provided.
	 */
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
	 * @param organizationDisplayName The common name of the organization.
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

	/**
	 * Find the common name for an organization
	 * 
	 * @param organizationId The organization identifier to retrieve information from
	 * @return The display name
	 */
	default String findOrganizationDisplayName(OrganizationIdentifier organizationId) {
		return findOrganizationDetails(organizationId).getDisplayName();
	}
	
	/**
	 * Update the common name for an organization
	 * 
	 * @param organizationId The organization to update
	 * @param displaysName The display name
	 * @return The updated details for an organization
	 */
	OrganizationDetails updateOrganizationDisplayName(OrganizationIdentifier organizationId, String displaysName);

	/**
	 * Find the main location identifier of the requested organization
	 * 
	 * @param organizationId The organization identifier to retrieve information from
	 * @return The main location identifier
	 */
	default LocationIdentifier findOrganizationMainLocation(OrganizationIdentifier organizationId) {
		return findOrganizationDetails(organizationId).getMainLocationId();
	}
	
	/**
	 * Update the main location identifier for an organization
	 * 
	 * @param organizationId The organization to update
	 * @param mainLocationId The main location identifier
	 * @return The updated details for an organization
	 */
	OrganizationDetails updateOrganizationMainLocation(OrganizationIdentifier organizationId, LocationIdentifier mainLocationId);

	/**
	 * Find the main contact identifier of the requested organization
	 * 
	 * @param organizationId The organization identifier to retrieve information from
	 * @return The main contact identifier
	 */
	default PersonIdentifier findOrganizationMainContact(OrganizationIdentifier organizationId) {
		return findOrganizationDetails(organizationId).getMainContactId();
	}
	
	/**
	 * Update the main contact identifier for an organization
	 * 
	 * @param organizationId The organization to update
	 * @param mainLocationId The main contact identifier
	 * @return The updated details for an organization
	 */
	OrganizationDetails updateOrganizationMainContact(OrganizationIdentifier organizationId, PersonIdentifier mainContactId);

	/**
	 * Find the assigned authentication group identifiers of the requested organization
	 * 
	 * @param organizationId The organization identifier to retrieve information from
	 * @return The assigned authentication group identifiers
	 */
	default List<AuthenticationGroupIdentifier> findOrganizationAuthenticationGroups(OrganizationIdentifier organizationId) {
		return findOrganizationDetails(organizationId).getAuthenticationGroupIds();
	}
	
	/**
	 * Update the assigned authentication group identifiers for an organization
	 * 
	 * @param organizationId The organization to update
	 * @param authenticationGroupIds The assigned authentication group identifiers
	 * @return The updated details for an organization
	 */
	OrganizationDetails updateOrganizationAuthenticationGroups(OrganizationIdentifier organizationId, List<AuthenticationGroupIdentifier> authenticationGroupIds);
	
	/**
	 * Find the assigned business group identifiers of the requested organization
	 * 
	 * @param organizationId The organization identifier to retrieve information from
	 * @return The assigned business group identifiers
	 */
	default List<BusinessGroupIdentifier> findOrganizationBusinessGroups(OrganizationIdentifier organizationId) {
		return findOrganizationDetails(organizationId).getBusinessGroupIds();
	}
	
	/**
	 * Update the assigned business group identifiers for an organization
	 * 
	 * @param organizationId The organization to update
	 * @param businessGroupIds The assigned business group identifiers
	 * @return The updated details for an organization
	 */
	OrganizationDetails updateOrganizationBusinessGroups(OrganizationIdentifier organizationId, List<BusinessGroupIdentifier> businessGroupIds);
	
	/**
	 * Update all or some of the information about the organization
	 * @param organizationId The organization to update
	 * @param displaysName The common name for the organization
	 * @param mainLocationId The main location identifier for the organization
	 * @param mainContactId The main contact person identifier for the organization
	 * @param authenticationGroupIds The assigned authentication group identifiers
	 * @param businessGroupIds The assigned business group identifiers
	 * @return The updated details for the organization
	 */
	default OrganizationDetails updateOrganization(OrganizationIdentifier organizationId, 
			String displaysName,
			LocationIdentifier mainLocationId, 
			PersonIdentifier mainContactId,
			List<AuthenticationGroupIdentifier> authenticationGroupIds,
			List<BusinessGroupIdentifier> businessGroupIds) {
		if (displaysName != null)
			updateOrganizationDisplayName(organizationId, displaysName);
		if (mainLocationId != null)
			updateOrganizationMainLocation(organizationId, mainLocationId);
		if (mainContactId != null)
			updateOrganizationMainContact(organizationId, mainContactId);
		if (authenticationGroupIds != null)
			updateOrganizationAuthenticationGroups(organizationId, authenticationGroupIds);
		if (businessGroupIds != null)
			updateOrganizationBusinessGroups(organizationId, businessGroupIds);
		return findOrganizationDetails(organizationId);
	}

	/**
	 * Find the core organization summary information about a specific organization.
	 * 
	 * @param organizationId The organization identifier provided by the user.
	 * @return The core organization information
	 */
	OrganizationSummary findOrganizationSummary(OrganizationIdentifier organizationId);

	/**
	 * Find the full organization details about a specific organization.
	 * 
	 * @param organizationId The organization identifier provided by the user.
	 * @return The full organization details
	 */
	OrganizationDetails findOrganizationDetails(OrganizationIdentifier organizationId);

	/**
	 * Find a paginated list of organization summaries based on the filter criteria provided by the user.
	 * 
	 * @param filter The filter information to limit the organization retrieved
	 * @param paging The paging information
	 * @return The paginated list of information
	 */
	FilteredPage<OrganizationSummary> findOrganizationSummaries(OrganizationsFilter filter, Paging paging);
	
	/**
	 * Find a paginated list of the full organization details based on the filter criteria provided by the user.
	 * 
	 * @param filter The filter information to limit the organization retrieved
	 * @param paging The paging information
	 * @return The paginated list of information
	 */
	FilteredPage<OrganizationDetails> findOrganizationDetails(OrganizationsFilter filter, Paging paging);

	/**
	 * Find the initial page of organization summaries based on the filter criteria provided by the user.
	 * 
	 * @param filter The filter information to limit the organization retrieved
	 * @return The paginated list of information
	 */
	default FilteredPage<OrganizationSummary> findOrganizationSummaries(OrganizationsFilter filter) {
		return findOrganizationSummaries(filter, OrganizationsFilter.getDefaultPaging());
	};
	
	/**
	 * Find the initial page of the full organization details based on the filter criteria provided by the user.
	 * 
	 * @param filter The filter information to limit the organization retrieved
	 * @return The paginated list of information
	 */
	default FilteredPage<OrganizationDetails> findOrganizationDetails(OrganizationsFilter filter) {
		return findOrganizationDetails(filter, OrganizationsFilter.getDefaultPaging());
	};
	
	/**
	 * The number of organizations that match the filter criteria provided by the user.
	 * 
	 * @param filter
	 * @return
	 */
	long countOrganizations(OrganizationsFilter filter);

	static List<Message> validateOrganizationDetails(CrmServices crm, OrganizationDetails organization) {
		List<Message> messages = new ArrayList<Message>();
		
		MessageTypeIdentifier error = crm.findOptionByCode(Type.MESSAGE_TYPE, "ERROR").getOptionId();

		// Status
		if (organization.getStatus() == null) {
			messages.add(new Message(organization.getOrganizationId(), error, "status", null, PhraseIdentifier.VALIDATION_FIELD_REQUIRED));
		} else if (organization.getStatus() == Status.PENDING && organization.getOrganizationId() != null) {
			messages.add(new Message(organization.getOrganizationId(), error, "status", organization.getStatus().name(), PhraseIdentifier.VALIDATION_STATUS_PENDING));
		}

		// Display Name
		if (StringUtils.isBlank(organization.getDisplayName())) {
			messages.add(new Message(organization.getOrganizationId(), error, "displayName", organization.getDisplayName(), PhraseIdentifier.VALIDATION_FIELD_REQUIRED));
		} else if (organization.getDisplayName().length() > 60) {
			messages.add(new Message(organization.getOrganizationId(), error, "displayName", organization.getDisplayName(), PhraseIdentifier.VALIDATION_FIELD_MAXLENGTH));
		}

		// Main contact reference
		if (organization.getMainContactId() != null) {
			PersonSummary person = crm.findPersonSummary(organization.getMainContactId());
			// Make sure main contact belongs to current org
			if (!person.getOrganizationId().equals(organization.getOrganizationId())) {
				messages.add(new Message(organization.getOrganizationId(), error, "mainContactId", organization.getMainContactId().getCode(), PhraseIdentifier.VALIDATION_FIELD_INVALID));
			}
			// Make sure main contact is active
			if (!person.getStatus().equals(Status.ACTIVE)) {
				messages.add(new Message(organization.getOrganizationId(), error, "mainContactId", organization.getMainContactId().getCode(), PhraseIdentifier.VALIDATION_FIELD_INACTIVE));
			}
		}

		// Main location reference
		if (organization.getMainLocationId() != null) {
			LocationSummary location = crm.findLocationSummary(organization.getMainLocationId());
			// Make sure main location belongs to current org
			if (!location.getOrganizationId().equals(organization.getOrganizationId())) {
				messages.add(new Message(organization.getOrganizationId(), error, "mainLocationId", organization.getMainLocationId().getCode(), PhraseIdentifier.VALIDATION_FIELD_INVALID));
			}
			// Make sure main location is active
			if (!location.getStatus().equals(Status.ACTIVE)) {
				messages.add(new Message(organization.getOrganizationId(), error, "mainLocationId", organization.getMainLocationId().getCode(), PhraseIdentifier.VALIDATION_FIELD_INACTIVE));
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
						messages.add(new Message(organization.getOrganizationId(), error, "authenticationGroupIds[" + i + "]", groupId.getCode(), PhraseIdentifier.VALIDATION_FIELD_INACTIVE));
				} catch (ItemNotFoundException e) {
					messages.add(new Message(organization.getOrganizationId(), error, "authenticationGroupIds[" + i + "]", groupId.getCode(), PhraseIdentifier.VALIDATION_OPTION_INVALID));
				}
			}
		}

		return messages;
	}
	
}