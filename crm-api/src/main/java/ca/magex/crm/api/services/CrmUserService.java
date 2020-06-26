package ca.magex.crm.api.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.crm.User;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.system.Status;

public interface CrmUserService {
	
	default User prototypeUser(Identifier personId, String username, List<Identifier> roles) {
		return new User(null, username, new PersonSummary(personId, null, null, null), Status.PENDING, roles);
	};

	default User createUser(User prototype) {
		return createUser(prototype.getPerson().getPersonId(), prototype.getUsername(), prototype.getRoles());
	}

	User createUser(Identifier personId, String username, List<Identifier> roles);

	User enableUser(Identifier userId);

	User disableUser(Identifier userId);

	User updateUserRoles(Identifier userId, List<Identifier> roles);

	boolean changePassword(Identifier userId, String currentPassword, String newPassword);

	String resetPassword(Identifier userId);

	User findUser(Identifier userId);

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

	default FilteredPage<User> findActiveUserForOrg(Identifier organizationId) {
		return findUsers(new UsersFilter(organizationId, null, Status.ACTIVE, null, null));
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
		if (user.getPerson() == null || user.getPerson().getPersonId() == null) {
			messages.add(new Message(null, "error", "person", new Localized(Lang.ENGLISH, "Person cannot be null")));
		} else {
			try {
				crm.findPersonDetails(user.getPerson().getPersonId());
			} catch (ItemNotFoundException e) {
				messages.add(new Message(user.getPerson().getPersonId(), "error", "person", new Localized(Lang.ENGLISH, "Person does not exist")));
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
				Identifier roleId = user.getRoles().get(i);
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