package ca.magex.crm.api.decorators;

import ca.magex.crm.api.services.CrmUserService;

import java.util.List;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public class CrmUserServiceDelegate implements CrmUserService {
	
	private CrmUserService delegate;
	
	public CrmUserServiceDelegate(CrmUserService delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public User prototypeUser(Identifier personId, String username, List<String> roles) {
		return delegate.prototypeUser(personId, username, roles);
	}
	
	@Override
	public User createUser(User prototype) {
		return delegate.createUser(prototype);
	}
	
	@Override
	public User createUser(Identifier personId, String username, List<String> roles) {
		return delegate.createUser(personId, username, roles);
	}
	
	@Override
	public User enableUser(Identifier userId) {
		return delegate.enableUser(userId);
	}
	
	@Override
	public User disableUser(Identifier userId) {
		return delegate.disableUser(userId);
	}
	
	@Override
	public User updateUserRoles(Identifier userId, List<String> roles) {
		return delegate.updateUserRoles(userId, roles);
	}
	
	@Override
	public boolean changePassword(Identifier userId, String currentPassword, String newPassword) {
		return delegate.changePassword(userId, currentPassword, newPassword);
	}
	
	@Override
	public String resetPassword(Identifier userId) {
		return delegate.resetPassword(userId);
	}
	
	@Override
	public User findUser(Identifier userId) {
		return delegate.findUser(userId);
	}
	
	@Override
	public User findUserByUsername(String username) {
		return delegate.findUserByUsername(username);
	}
	
	@Override
	public long countUsers(UsersFilter filter) {
		return delegate.countUsers(filter);
	}
	
	@Override
	public FilteredPage<User> findUsers(UsersFilter filter, Paging paging) {
		return delegate.findUsers(filter, paging);
	}
	
	@Override
	public boolean isValidPasswordFormat(String password) {
		return delegate.isValidPasswordFormat(password);
	}
	
	@Override
	public FilteredPage<User> findUsers(UsersFilter filter) {
		return delegate.findUsers(filter);
	}
	
	@Override
	public FilteredPage<User> findActiveUserForOrg(Identifier organizationId) {
		return delegate.findActiveUserForOrg(organizationId);
	}
	
	@Override
	public UsersFilter defaultUsersFilter() {
		return delegate.defaultUsersFilter();
	}
	
	@Override
	public Paging defaultUsersPaging() {
		return delegate.defaultUsersPaging();
	}
	
}
