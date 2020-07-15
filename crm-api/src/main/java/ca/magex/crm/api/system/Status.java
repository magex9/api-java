package ca.magex.crm.api.system;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

/**
 * The status of on an entity in the system that holds the state in the lifecycle.
 * 
 * <dl>
 * 		<dt>active</dt><dd>The entity is currently available</dd>
 * 		<dt>inactive</dt><dd>The entity is logically deleted and unavailable</dd>
 * 		<dt>pending</dt><dd>The entity has been created but not available yet</dd>
 * </dl>
 *  
 * @author magex
 *
 */
public enum Status {

	ACTIVE("Active", "Actif"), 
	INACTIVE("Inactive", "Inactif"), 
	PENDING("Pending", "En attente");
	
	private Localized name;

	private Status(String english, String french) {
		this.name = new Localized(toString(), english, french);
	}
	
	/**
	 * Get the root code for the status
	 * @return
	 */
	public String getCode() {
		return name.get(Lang.ROOT);
	}
	
	/**
	 * Get the all the localized names for the status
	 * @return
	 */
	public Localized getName() {
		return name;
	}
	
	/**
	 * Get a specific localized name for the status
	 * @param locale
	 * @return
	 */
	public String getName(Locale locale) {
		return name.get(locale);
	}
	
	public static Status of(String value) {
		if (StringUtils.isBlank(value)) {
			return null;
		}
		return valueOf(StringUtils.upperCase(value));
	}
}