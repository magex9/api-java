package ca.magex.crm.api.services;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;

import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.system.Identifier;

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

	Page<User> findUsers(
		@NotNull UsersFilter filter, 
		@NotNull Paging paging
	);
}