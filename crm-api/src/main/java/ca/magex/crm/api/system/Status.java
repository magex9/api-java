package ca.magex.crm.api.system;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import ca.magex.crm.api.exceptions.ItemNotFoundException;

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
	
	private static final List<Status> STATUSES = List.of(ACTIVE, INACTIVE, PENDING);
	
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
		try {
			return valueOf(StringUtils.upperCase(value));
		}
		catch(IllegalArgumentException ia) {
			throw new ItemNotFoundException("Status Code '" + value + "'");
		}
	}
	
	public static Status of(String name, Locale locale) {
		if (locale == null) 
			return of(name);
		for (Status status : STATUSES) {
			if (status.getName(locale).equals(name)) 
				return status;
		}
		throw new ItemNotFoundException("Status Name '" + name + "' in " + locale);
	}
	
}