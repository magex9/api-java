package ca.magex.crm.caching.util;

import ca.magex.crm.api.system.Identifier;

public class CrmCacheKeyGenerator {

	/**
	 * Generates the Key used for the Cache Entry of a Details instance
	 * @param identifier
	 * @return
	 */
	public static String generateDetailsKey(Identifier identifier) {
		return "Details_" + identifier;
	}
	
	/**
	 * Generates the Key used for the Cache Entry of a details instance by an Username
	 * @param username
	 * @return
	 */
	public static String generateUsernameKey(String username) {
		return "Username_" + username;
	}
	
	/**
	 * Generates the Key used for the Cache Entry of a details instance by a Code
	 * @param code
	 * @return
	 */
	public static String generateCodeKey(String code) {
		return "Code_" + code;
	}
	
	/**
	 * Generates the Key used for the Cache Entry of a details instance by a Code
	 * @param localized
	 * @return
	 */
	public static String generateLocalizedNameKey(String localized) {
		return "Localized_" + localized;
	}
	
	/**
	 * Generates the Key used for the Cache Entry of a Summary instance
	 * @param identifier
	 * @return
	 */
	public static String generateSummaryKey(Identifier identifier) {
		return "Summary_" + identifier;
	}
}