package ca.magex.crm.api.services.basic;

import java.util.List;

import ca.magex.crm.api.authentication.CrmPasswordService;
import ca.magex.crm.api.crm.User;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.repositories.CrmRepositories;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.AuthenticationRoleIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.system.id.UserIdentifier;

public class BasicUserService implements CrmUserService {

	private CrmRepositories repos;
	
	private CrmPasswordService passwords;
	
	public BasicUserService(CrmRepositories repos, CrmPasswordService passwords) {
		this.repos = repos;
		this.passwords = passwords;
	}

	@Override
	public User createUser(OrganizationIdentifier organizationIdentifier, PersonIdentifier personId, String username, List<AuthenticationRoleIdentifier> roleIds) {
		return repos.saveUser(new User(repos.generateUserId(), organizationIdentifier, personId, username, Status.ACTIVE, roleIds));
	}

	@Override
	public User enableUser(UserIdentifier userId) {
		User user = repos.findUser(userId);
		if (user == null) {
			return null;
		}
		return repos.saveUser(user.withStatus(Status.ACTIVE));
	}

	@Override
	public User disableUser(UserIdentifier userId) {
		User user = repos.findUser(userId);
		if (user == null) {
			return null;
		}
		return repos.saveUser(user.withStatus(Status.INACTIVE));
	}

	@Override
	public User findUser(UserIdentifier userId) {
		return repos.findUser(userId);
	}
	
	@Override
	public User findUserByUsername(String username) {
		return repos.findUsers(defaultUsersFilter().withUsername(username), UsersFilter.getDefaultPaging()).getSingleItem();
	}	

	@Override
	public User updateUserRoles(UserIdentifier userId, List<AuthenticationRoleIdentifier> roleIds) {
		User user = repos.findUser(userId);
		if (user == null) {
			return null;
		}
		return repos.saveUser(user.withRoles(roleIds));
	}

	@Override
	public boolean changePassword(UserIdentifier userId, String currentPassword, String newPassword) {
		if (!isValidPasswordFormat(newPassword))
			return false;
		User user = repos.findUser(userId);
		if (passwords.verifyPassword(user.getUsername(), currentPassword)) {
			passwords.updatePassword(user.getUsername(), passwords.encodePassword(newPassword));
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public String resetPassword(UserIdentifier userId) {
		return passwords.generateTemporaryPassword(repos.findUser(userId).getUsername());
	}

	@Override
	public long countUsers(UsersFilter filter) {
		return repos.countUsers(filter);
	}

	@Override
	public FilteredPage<User> findUsers(UsersFilter filter, Paging paging) {
		return repos.findUsers(filter, paging);
	}	
}