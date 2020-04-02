package ca.magex.crm.api.system;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public enum Status {

	ACTIVE("Active", "Actif"), 
	INACTIVE("Inactive", "Inactif"), 
	PENDING("Pending", "En attente");
	
	private String code;
	
	private Map<Locale, String> names;

	private Status(String english, String french) {
		this.code = toString().toLowerCase();
		this.names = new HashMap<Locale, String>();
		this.names.put(Lang.ENGLISH, english);
		this.names.put(Lang.FRENCH, french);
	}
	
	public String getCode() {
		return code;
	}
	
	public String getName(Locale locale) {
		return names.get(locale);
	}
	
}
