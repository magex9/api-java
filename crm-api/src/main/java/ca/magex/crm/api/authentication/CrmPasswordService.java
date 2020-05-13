package ca.magex.crm.api.authentication;

import javax.validation.constraints.NotNull;

public interface CrmPasswordService {
	
	String getEncodedPassword(@NotNull String username);
	
	boolean isTempPassword(@NotNull String username);
	
	boolean isExpiredPassword(@NotNull String username);
	
	boolean verifyPassword(String username, String encodedPassword);
	
	String generateTemporaryPassword(@NotNull String username);
	
	void updatePassword(@NotNull String username, @NotNull String encodedPassword);
	
}
