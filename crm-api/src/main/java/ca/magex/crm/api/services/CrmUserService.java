package ca.magex.crm.api.services;

import java.util.List;

import org.springframework.data.domain.Page;

import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.system.Identifier;

public interface CrmUserService {

	User createUser(Identifier personId, String username, List<String> roles);

	User enableUser(Identifier userId);

	User disableUser(Identifier userId);

	User findUser(Identifier userId);

	List<Identifier> getRoles(Identifier userId);

	User addUserRole(Identifier userId, Identifier roleId);

	User removeUserRole(Identifier userId, Identifier roleId);

	User setRoles(Identifier userId, List<Identifier> roleIds);

	boolean changePassword(Identifier userId, String currentPassword, String newPassword);

	boolean resetPassword(Identifier userId);

	long countUsers(UsersFilter filter);

	Page<User> findUsers(UsersFilter filter, Paging paging);

}
