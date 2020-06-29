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
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.AuthenticationRoleIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.system.id.UserIdentifier;

public interface CrmUserService {

	default User prototypeUser(PersonIdentifier personId, String username, List<AuthenticationRoleIdentifier> roles) {
		return new User(null, personId, username, Status.PENDING, roles);
	};

	default User createUser(User prototype) {
		return createUser(prototype.getPersonId(), prototype.getUsername(), prototype.getRoles());
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

		// Status
		if (user.getStatus() == null) {
			messages.add(new Message(user.getUserId(), "error", "status", new Localized(Lang.ENGLISH, "Status is mandatory for a person")));
		} else if (user.getStatus() == Status.PENDING && user.getUserId() != null) {
			messages.add(new Message(user.getUserId(), "error", "status", new Localized(Lang.ENGLISH, "Pending statuses should not have identifiers")));
		}

		// Organization
		if (user.getPersonId() == null) {
			messages.add(new Message(null, "error", "person", new Localized(Lang.ENGLISH, "Person cannot be null")));
		} else {
			try {
				crm.findPersonDetails(user.getPersonId());
			} catch (ItemNotFoundException e) {
				messages.add(new Message(user.getPersonId(), "error", "person", new Localized(Lang.ENGLISH, "Person does not exist")));
			}
		}

		// Display Name
		if (StringUtils.isBlank(user.getUsername())) {
			messages.add(new Message(user.getUserId(), "error", "username", new Localized(Lang.ENGLISH, "Username is mandatory for a user")));
		} else if (user.getUsername().length() > 20) {
			messages.add(new Message(user.getUserId(), "error", "username", new Localized(Lang.ENGLISH, "Username must be 20 characters or less")));
		}

		// Roles
		if (user.getRoles().isEmpty()) {
			messages.add(new Message(user.getUserId(), "error", "roles", new Localized(Lang.ENGLISH, "Users must have a permission role assigned to them")));
		} else {
			for (int i = 0; i < user.getRoles().size(); i++) {
				AuthenticationRoleIdentifier roleId = user.getRoles().get(i);
				try {
					if (!crm.findOption(roleId).getStatus().equals(Status.ACTIVE))
						messages.add(new Message(user.getUserId(), "error", "roles[" + i + "]", new Localized(Lang.ENGLISH, "Role is not active: " + roleId)));
				} catch (ItemNotFoundException e) {
					messages.add(new Message(user.getUserId(), "error", "roles[" + i + "]", new Localized(Lang.ENGLISH, "Role does not exist: " + roleId)));
				}
			}
		}

		return messages;
	}

}