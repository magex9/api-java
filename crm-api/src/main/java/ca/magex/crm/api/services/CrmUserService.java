package ca.magex.crm.api.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.crm.UserDetails;
import ca.magex.crm.api.crm.UserSummary;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.AuthenticationRoleIdentifier;
import ca.magex.crm.api.system.id.MessageTypeIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.system.id.UserIdentifier;

public interface CrmUserService {

	default UserDetails prototypeUser(PersonIdentifier personId, String username, List<AuthenticationRoleIdentifier> authenticationRoleIds) {
		return new UserDetails(null, null, personId, username, Status.PENDING, authenticationRoleIds, null);
	};

	default UserDetails createUser(UserDetails prototype) {
		return createUser(prototype.getPersonId(), prototype.getUsername(), prototype.getAuthenticationRoleIds());
	}

	UserDetails createUser(PersonIdentifier personId, String username, List<AuthenticationRoleIdentifier> authenticationRoleIds);

	UserSummary enableUser(UserIdentifier userId);

	default UserSummary enableUserByUsername(String username) {
		return disableUser(findUserSummaryByUsername(username).getUserId());
	}

	UserSummary disableUser(UserIdentifier userId);

	default UserSummary disableUserByUsername(String username) {
		return disableUser(findUserSummaryByUsername(username).getUserId());
	}

	default PersonIdentifier findUserPerson(UserIdentifier userId) {
		return findUserDetails(userId).getPersonId();
	}
	
	default PersonIdentifier findUserPersonByUsername(String username) {
		return findUserDetailsByUsername(username).getPersonId();
	}
	
	default List<AuthenticationRoleIdentifier> findUserAuthenticationRoles(UserIdentifier userId) {
		return findUserDetails(userId).getAuthenticationRoleIds();
	}
	
	default List<AuthenticationRoleIdentifier> findUserAuthenticationRolesByUsername(String username) {
		return findUserDetailsByUsername(username).getAuthenticationRoleIds();
	}
	
	UserDetails updateUserAuthenticationRoles(UserIdentifier userId, List<AuthenticationRoleIdentifier> authenticationRoleIds);

	default UserDetails updateUser(UserIdentifier userId,
			PersonIdentifier personId,
			List<AuthenticationRoleIdentifier> authenticationRoleIds) {
		if (authenticationRoleIds != null)
			updateUserAuthenticationRoles(userId, authenticationRoleIds);
		return findUserDetails(userId);
	}

	default UserDetails updateUserByUsername(String username, 
			PersonIdentifier personId,
			List<AuthenticationRoleIdentifier> authenticationRoleIds) {
		UserIdentifier userId = findUserSummaryByUsername(username).getUserId();
		if (authenticationRoleIds != null)
			updateUserAuthenticationRoles(userId, authenticationRoleIds);
		return findUserDetails(userId);
	}

	boolean changePassword(UserIdentifier userId, String currentPassword, String newPassword);

	String resetPassword(UserIdentifier userId);

	UserSummary findUserSummary(UserIdentifier userId);

	UserSummary findUserSummaryByUsername(String username);

	UserDetails findUserDetails(UserIdentifier userId);

	UserDetails findUserDetailsByUsername(String username);

	long countUsers(UsersFilter filter);

	FilteredPage<UserSummary> findUserSummaries(UsersFilter filter, Paging paging);

	FilteredPage<UserDetails> findUserDetails(UsersFilter filter, Paging paging);

	default boolean isValidPasswordFormat(String password) {
		if (StringUtils.isBlank(password))
			return false;
		if (password.length() < 5 || password.length() > 255)
			return false;
		if (!password.matches("[A-Za-z0-9\\!\\@\\#\\$\\%\\^\\&\\*\\(\\)]+"))
			return false;
		return true;
	}

	default FilteredPage<UserSummary> findUserSummaries(UsersFilter filter) {
		return findUserSummaries(filter, defaultUsersPaging());
	}

	default FilteredPage<UserDetails> findUserDetails(UsersFilter filter) {
		return findUserDetails(filter, defaultUsersPaging());
	}

	default FilteredPage<UserSummary> findActiveUserSummariesForOrg(OrganizationIdentifier organizationId) {
		return findUserSummaries(new UsersFilter(organizationId, null, Status.ACTIVE, null, null), UsersFilter.getDefaultPaging());
	}

	default UsersFilter defaultUsersFilter() {
		return new UsersFilter();
	};

	default Paging defaultUsersPaging() {
		return new Paging(UsersFilter.getSortOptions().get(0));
	}

	static List<Message> validateUser(Crm crm, UserDetails user) {
		List<Message> messages = new ArrayList<Message>();
		
		MessageTypeIdentifier error = crm.findOptionByCode(Type.MESSAGE_TYPE, "ERROR").getOptionId();

		// Status
		if (user.getStatus() == null) {
			messages.add(new Message(user.getUserId(), error, "status", null, crm.findMessageId("validation.field.required")));
		} else if (user.getStatus() == Status.PENDING && user.getUserId() != null) {
			messages.add(new Message(user.getUserId(), error, "status", user.getStatus().name(), crm.findMessageId("validation.status.pending")));
		}

		// Organization
		if (user.getPersonId() == null) {
			messages.add(new Message(null, error, "personId", null, crm.findMessageId("validation.field.required")));
		} else {
			try {
				PersonSummary personSummary = crm.findPersonSummary(user.getPersonId());
				if (user.getUserId() != null && !personSummary.getOrganizationId().equals(user.getOrganizationId())) {
					messages.add(new Message(user.getPersonId(), error, "organizationId", user.getOrganizationId().getCode(), crm.findMessageId("validation.field.invalid")));
				}
			} catch (ItemNotFoundException e) {
				messages.add(new Message(user.getPersonId(), error, "personId", user.getPersonId().getCode(), crm.findMessageId("validation.field.invalid")));
			}
		}

		// Display Name
		if (StringUtils.isBlank(user.getUsername())) {
			messages.add(new Message(user.getUserId(), error, "username", user.getUsername(), crm.findMessageId("validation.field.required")));
		} else if (user.getUsername().length() > 20) {
			messages.add(new Message(user.getUserId(), error, "username", user.getUsername(), crm.findMessageId("validation.field.maxlength")));
		}

		// Roles
		if (user.getAuthenticationRoleIds().isEmpty()) {
			messages.add(new Message(user.getUserId(), error, "authenticationRoleIds", null, crm.findMessageId("validation.field.required")));
		} else {
			for (int i = 0; i < user.getAuthenticationRoleIds().size(); i++) {
				AuthenticationRoleIdentifier roleId = user.getAuthenticationRoleIds().get(i);
				try {
					if (!crm.findOption(roleId).getStatus().equals(Status.ACTIVE))
						messages.add(new Message(user.getUserId(), error, "authenticationRoleIds[" + i + "]", roleId.getCode(), crm.findMessageId("validation.field.inactive")));
				} catch (ItemNotFoundException e) {
					messages.add(new Message(user.getUserId(), error, "authenticationRoleIds[" + i + "]", roleId.getCode(), crm.findMessageId("validation.field.invalid")));
				}
			}
		}

		return messages;
	}

}