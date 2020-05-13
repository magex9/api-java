package ca.magex.crm.api.services;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public interface CrmUserService {

	User createUser(
		@NotNull Identifier personId, 
		@NotNull String username, 
		@NotNull List<String> roles
	);

	User enableUser(
		@NotNull Identifier userId
	);

	User disableUser(
		@NotNull Identifier userId
	);

    User findUser(
    	@NotNull Identifier userId
    );
    
    User findUserByUsername(
    	@NotNull String username
    );
    
    List<String> getRoles(
    	@NotNull Identifier userId
    );
	
	User addUserRole(
		@NotNull Identifier userId, 
		@NotNull String role
	);
	
	User removeUserRole(
		@NotNull Identifier userId, 
		@NotNull String role
	);
	
	User updateUserRoles(
		@NotNull Identifier userId, 
		@NotNull List<String> roles
	);
	
	boolean changePassword(
		@NotNull Identifier userId, 
		@NotNull String currentPassword, 
		@NotNull String newPassword
	);

	boolean resetPassword(
		@NotNull Identifier userId
	);

	long countUsers(
		@NotNull UsersFilter filter
	);

	FilteredPage<User> findUsers(
		@NotNull UsersFilter filter, 
		@NotNull Paging paging
	);
	
	default Page<User> findUsers(@NotNull UsersFilter filter) {
		return findUsers(filter, defaultUsersPaging());
	}
	
	default Page<User> findActiveUserForOrg(@NotNull Identifier organizationId) {
		return findUsers(new UsersFilter(organizationId, null, Status.ACTIVE, null, null));
	}
	
	default UsersFilter defaultUsersFilter() {
		return new UsersFilter();
	};
	
	default Paging defaultUsersPaging() {
		return new Paging(SORT_OPTIONS.get(0));
	}
	
	public static final List<Sort> SORT_OPTIONS = List.of(
		Sort.by(Order.asc("username")),
		Sort.by(Order.desc("username")),
		Sort.by(Order.asc("personName")),
		Sort.by(Order.desc("personName")),
		Sort.by(Order.asc("organizationName")),
		Sort.by(Order.desc("organizationName")),
		Sort.by(Order.asc("status")),
		Sort.by(Order.desc("status"))
	);
	
	default List<Sort> getUsersSortOptions() {
		return SORT_OPTIONS;
	}
	
}