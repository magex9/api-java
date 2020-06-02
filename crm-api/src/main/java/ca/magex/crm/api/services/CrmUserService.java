package ca.magex.crm.api.services;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;

import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public interface CrmUserService {
	
	default User prototypeUser(
			@NotNull Identifier personId, 
			@NotNull String username, 
			@NotNull List<String> roles) {
		return new User(null, username, new PersonSummary(personId, null, null, null), Status.PENDING, roles);
	};
	
	default User createUser(User prototype) {
		return createUser(
			prototype.getPerson().getPersonId(), 
			prototype.getUsername(), 
			prototype.getRoles());
	}

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
	
	User updateUserRoles(
		@NotNull Identifier userId, 
		@NotNull List<String> roles
	);

	boolean changePassword(
		@NotNull Identifier userId, 
		@NotNull String currentPassword, 
		@NotNull String newPassword
	);

	String resetPassword(
		@NotNull Identifier userId
	);
	
	User findUser(
	  	@NotNull Identifier userId
	);
    
    User findUserByUsername(
    	@NotNull String username
    );
    
    long countUsers(
   		@NotNull UsersFilter filter
   	);
    
    FilteredPage<User> findUsers(
    	@NotNull UsersFilter filter, 
    	@NotNull Paging paging
    );	

	default boolean isValidPasswordFormat(String password) {
		if (StringUtils.isBlank(password))
			return false;
		if (password.length() < 5 || password.length() > 255)
			return false;
		if (!password.matches("[A-Za-z0-9\\!\\@\\#\\$\\%\\^\\&\\*\\(\\)]+"))
			return false;
		return true;
	}
	
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
		return new Paging(UsersFilter.getSortOptions().get(0));
	}
}