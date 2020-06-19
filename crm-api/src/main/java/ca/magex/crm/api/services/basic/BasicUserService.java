package ca.magex.crm.api.services.basic;

import java.util.List;

import ca.magex.crm.api.authentication.CrmPasswordRepository;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.repositories.CrmRepositories;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public class BasicUserService implements CrmUserService {

	private CrmRepositories repos;
	
	private CrmPasswordRepository passwords;
	
	public BasicUserService(CrmRepositories repos, CrmPasswordRepository passwords) {
		this.repos = repos;
		this.passwords = passwords;
	}

	@Override
	public User createUser(Identifier personId, String username, List<String> roles) {
		User user = repos.saveUser(new User(repos.generateId(), username, repos.findPersonSummary(personId), Status.ACTIVE, roles));
		updateUserRoles(user.getUserId(), roles);
		return user;
	}

	@Override
	public User enableUser(Identifier userId) {
		User user = repos.findUser(userId);
		if (user == null) {
			return null;
		}
		return repos.saveUser(user.withStatus(Status.ACTIVE));
	}

	@Override
	public User disableUser(Identifier userId) {
		User user = repos.findUser(userId);
		if (user == null) {
			return null;
		}
		return repos.saveUser(user.withStatus(Status.INACTIVE));
	}

	@Override
	public User findUser(Identifier userId) {
		return repos.findUser(userId);
	}
	
	@Override
	public User findUserByUsername(String username) {
		return repos.findUsers(defaultUsersFilter().withUsername(username), UsersFilter.getDefaultPaging()).getSingleItem();
	}	

	@Override
	public User updateUserRoles(Identifier userId, List<String> roles) {
		User user = repos.findUser(userId);
		if (user == null) {
			return null;
		}
		return repos.saveUser(user.withRoles(roles));
	}

	@Override
	public boolean changePassword(Identifier userId, String currentPassword, String newPassword) {
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
	public String resetPassword(Identifier userId) {
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
