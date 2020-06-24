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
	 * Generates the Key used for the Cache Entry of a details instance by an ID
	 * @param identifier
	 * @return
	 */
	public static String generateUsernameKey(String name) {
		return "Username_" + name;
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