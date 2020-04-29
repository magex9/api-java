package ca.magex.crm.api.services;

import java.util.List;

import ca.magex.crm.api.common.User;
import ca.magex.crm.api.system.Identifier;

public interface CrmUserService {

	User createUser(Identifier personId, String username, List<String> roles);
	User findUserById(Identifier userId);	
	User findUserByUsername(String username);
	User addUserRole(Identifier userId, String role);
	User removeUserRole(Identifier userId, String role);
	User setUserRoles(Identifier userId, List<String> roles);
	User setUserPassword(Identifier userId, String password, boolean encoded);
}
