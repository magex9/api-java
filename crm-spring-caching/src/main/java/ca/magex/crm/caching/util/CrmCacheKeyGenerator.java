package ca.magex.crm.caching.util;

import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.id.OptionIdentifier;

public class CrmCacheKeyGenerator {

	private static CrmCacheKeyGenerator generator = new CrmCacheKeyGenerator();
	
	public static CrmCacheKeyGenerator getInstance() {
		return generator;
	}	
	
	/**
	 * Generates the Key used for the Cache Entry of a Details instance
	 * @param identifier
	 * @return
	 */
	public String generateDetailsKey(Identifier identifier) {
		return "Details_" + identifier;
	}
	
	/**
	 * Generates the Key used for the Cache Entry of a details instance by an Username
	 * @param username
	 * @return
	 */
	public String generateUsernameKey(String username) {
		return "Username_" + username;
	}
	
	/**
	 * Generates the Key used for the Cache Entry of a Summary instance
	 * @param identifier
	 * @return
	 */
	public String generateSummaryKey(Identifier identifier) {
		return "Summary_" + identifier;
	}
	
	/**
	 * Generates the key used for the Cache Entry of an Option instance
	 * @param identifier
	 * @return
	 */
	public String generateOptionKey(OptionIdentifier identifier) {
		return identifier.getType().getCode() + "_" + identifier.getCode();
	}
}