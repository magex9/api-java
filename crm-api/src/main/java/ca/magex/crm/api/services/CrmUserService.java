package ca.magex.crm.api.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.crm.User;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.AuthenticationRoleIdentifier;
import ca.magex.crm.api.system.id.MessageTypeIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.system.id.UserIdentifier;

public interface CrmUserService {

	default User prototypeUser(PersonIdentifier personId, String username, List<AuthenticationRoleIdentifier> roles) {
		return new User(null, personId, username, Status.PENDING, roles);
	};

	default User createUser(User prototype) {
		return createUser(prototype.getPersonId(), prototype.getUsername(), prototype.getRoleIds());
	}

	User createUser(PersonIdentifier personId, String username, List<AuthenticationRoleIdentifier> roles);

	User enableUser(UserIdentifier userId);

	User disableUser(UserIdentifier userId);

	User updateUserRoles(UserIdentifier userId, List<AuthenticationRoleIdentifier> roles);

	boolean changePassword(UserIdentifier userId, String currentPassword, String newPassword);

	String resetPassword(UserIdentifier userId);

	User findUser(UserIdentifier userId);

	User findUserByUsername(String username);

	long countUsers(UsersFilter filter);

	FilteredPage<User> findUsers(UsersFilter filter, Paging paging);

	default boolean isValidPasswordFormat(String password) {
		if (StringUtils.isBlank(password))
			return false;
		if (password.length() < 5 || password.length() > 255)
			return false;
		if (!password.matches("[A-Za-z0-9\\!\\@\\#\\$\\%\\^\\&\\*\\(\\)]+"))
			return false;
		return true;
	}

	default FilteredPage<User> findUsers(UsersFilter filter) {
		return findUsers(filter, defaultUsersPaging());
	}

	default UsersFilter defaultUsersFilter() {
		return new UsersFilter();
	};

	default Paging defaultUsersPaging() {
		return new Paging(UsersFilter.getSortOptions().get(0));
	}

	static List<Message> validateUser(Crm crm, User user) {
		List<Message> messages = new ArrayList<Message>();
		
		MessageTypeIdentifier error = crm.findOptionByCode(Type.MESSAGE_TYPE, "ERROR").getOptionId();

		// Status
		if (user.getStatus() == null) {
			messages.add(new Message(user.getUserId(), error, "status", crm.findMessageId("validation.field.required")));
		} else if (user.getStatus() == Status.PENDING && user.getUserId() != null) {
			messages.add(new Message(user.getUserId(), error, "status", crm.findMessageId("validation.status.pending")));
		}

		// Organization
		if (user.getPersonId() == null) {
			messages.add(new Message(null, error, "person", crm.findMessageId("validation.field.required")));
		} else {
			try {
				crm.findPersonDetails(user.getPersonId());
			} catch (ItemNotFoundException e) {
				messages.add(new Message(user.getPersonId(), error, "person", crm.findMessageId("validation.field.invalid")));
			}
		}

		// Display Name
		if (StringUtils.isBlank(user.getUsername())) {
			messages.add(new Message(user.getUserId(), error, "username", crm.findMessageId("validation.field.required")));
		} else if (user.getUsername().length() > 20) {
			messages.add(new Message(user.getUserId(), error, "username", crm.findMessageId("validation.field.maxlength")));
		}

		// Roles
		if (user.getRoleIds().isEmpty()) {
			messages.add(new Message(user.getUserId(), error, "roleIds", crm.findMessageId("validation.field.required")));
		} else {
			for (int i = 0; i < user.getRoleIds().size(); i++) {
				AuthenticationRoleIdentifier roleId = user.getRoleIds().get(i);
				try {
					if (!crm.findOption(roleId).getStatus().equals(Status.ACTIVE))
						messages.add(new Message(user.getUserId(), error, "roleIds[" + i + "]", crm.findMessageId("validation.field.inactive")));
				} catch (ItemNotFoundException e) {
					messages.add(new Message(user.getUserId(), error, "roleIds[" + i + "]", crm.findMessageId("validation.field.invalid")));
				}
			}
		}

		return messages;
	}

}