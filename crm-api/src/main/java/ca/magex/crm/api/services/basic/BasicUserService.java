package ca.magex.crm.api.services.basic;

import java.util.List;

import ca.magex.crm.api.authentication.CrmPasswordService;
import ca.magex.crm.api.crm.UserDetails;
import ca.magex.crm.api.crm.UserSummary;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.repositories.CrmRepositories;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.AuthenticationRoleIdentifier;
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
	public UserDetails createUser(PersonIdentifier personId, String username, List<AuthenticationRoleIdentifier> roleIds) {
		return repos.saveUserDetails(new UserDetails(repos.generateUserId(), repos.findPersonSummary(personId).getOrganizationId(), personId, username, Status.ACTIVE, roleIds, null));
	}

	@Override
	public UserSummary enableUser(UserIdentifier userId) {
		UserDetails user = repos.findUserDetails(userId);
		if (user == null) {
			return null;
		}
		if (user.getStatus() == Status.ACTIVE) {
			return user.asSummary();
		}
		return repos.saveUserDetails(user.withStatus(Status.ACTIVE)).asSummary();
	}

	@Override
	public UserSummary disableUser(UserIdentifier userId) {
		UserDetails user = repos.findUserDetails(userId);
		if (user == null) {
			return null;
		}
		if (user.getStatus() == Status.INACTIVE) {
			return user.asSummary();
		}
		return repos.saveUserDetails(user.withStatus(Status.INACTIVE)).asSummary();
	}

	@Override
	public UserSummary findUserSummary(UserIdentifier userId) {
		UserDetails user = repos.findUserDetails(userId);
		if (user == null) {
			return null;
		}
		return user.asSummary();
	}

	@Override
	public UserSummary findUserSummaryByUsername(String username) {
		return repos.findUserDetails(defaultUsersFilter().withUsername(username), UsersFilter.getDefaultPaging()).getSingleItem();
	}

	@Override
	public UserDetails findUserDetails(UserIdentifier userId) {
		return repos.findUserDetails(userId);
	}
	
	@Override
	public UserDetails findUserDetailsByUsername(String username) {
		return repos.findUserDetails(defaultUsersFilter().withUsername(username), UsersFilter.getDefaultPaging()).getSingleItem();
	}
	
	@Override
	public UserDetails updateUserAuthenticationRoles(UserIdentifier userId, List<AuthenticationRoleIdentifier> authenticationRoleIds) {
		UserDetails user = repos.findUserDetails(userId);
		if (user == null) {
			return null;
		}
		if (user.getAuthenticationRoleIds().containsAll(authenticationRoleIds) && authenticationRoleIds.containsAll(user.getAuthenticationRoleIds())) {
			return user;
		}
		return repos.saveUserDetails(user.withAuthenticationRoleIds(authenticationRoleIds));
	}

	@Override
	public boolean changePassword(UserIdentifier userId, String currentPassword, String newPassword) {
		if (!isValidPasswordFormat(newPassword))
			return false;
		UserDetails user = repos.findUserDetails(userId);
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
		return passwords.generateTemporaryPassword(repos.findUserDetails(userId).getUsername());
	}

	@Override
	public long countUsers(UsersFilter filter) {
		return repos.countUsers(filter);
	}

	@Override
	public FilteredPage<UserSummary> findUserSummaries(UsersFilter filter, Paging paging) {
		return repos.findUserSummaries(filter, paging);
	}	

	@Override
	public FilteredPage<UserDetails> findUserDetails(UsersFilter filter, Paging paging) {
		return repos.findUserDetails(filter, paging);
	}
}