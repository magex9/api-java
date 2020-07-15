package ca.magex.crm.api.system;

import java.util.Locale;

/**
 * The status of on an entity in the system that holds the state in the life cycle.
 *  
 * @author magex
 *
 */
public enum Status {

	// The entity is currently available
	ACTIVE("Active", "Actif"), 
	
	// The entity has been logically deleted and is currently unavailable
	INACTIVE("Inactive", "Inactif"), 
	
	// The entity has been created but not persisted yet
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
	
}