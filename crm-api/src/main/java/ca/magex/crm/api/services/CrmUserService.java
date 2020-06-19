package ca.magex.crm.api.services;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public interface CrmUserService {
	
	default User prototypeUser(
			Identifier personId, 
			String username, 
			List<String> roles) {
		return new User(null, username, new PersonSummary(personId, null, null, null), Status.PENDING, roles);
	};
	
	default User createUser(User prototype) {
		return createUser(
			prototype.getPerson().getPersonId(), 
			prototype.getUsername(), 
			prototype.getRoles());
	}

	User createUser(
		Identifier personId, 
		String username, 
		List<String> roles
	);

	User enableUser(
		Identifier userId
	);

	User disableUser(
		Identifier userId
	);
	
	User updateUserRoles(
		Identifier userId, 
		List<String> roles
	);

	boolean changePassword(
		Identifier userId, 
		String currentPassword, 
		String newPassword
	);

	String resetPassword(
		Identifier userId
	);
	
	User findUser(
	  	Identifier userId
	);
    
    User findUserByUsername(
    	String username
    );
    
    long countUsers(
   		UsersFilter filter
   	);
    
    FilteredPage<User> findUsers(
    	UsersFilter filter, 
    	Paging paging
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
}