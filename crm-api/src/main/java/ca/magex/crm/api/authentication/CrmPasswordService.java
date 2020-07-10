package ca.magex.crm.api.authentication;

import ca.magex.crm.api.crm.User;

public interface CrmPasswordService {
	
	User findUser(String username);

	String getEncodedPassword(String username);

	boolean isTempPassword(String username);

	boolean isExpiredPassword(String username);

	boolean verifyPassword(String username, String rawPassword);

	String generateTemporaryPassword(String username);

	void updatePassword(String username, String encodedPassword);

	String encodePassword(String rawPassword);

}
