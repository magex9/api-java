package ca.magex.crm.api.system;

import java.util.Locale;

import ca.magex.crm.api.lookup.CrmLookupItem;

public enum Status implements CrmLookupItem {

	ACTIVE("Active", "Actif"), 
	INACTIVE("Inactive", "Inactif"), 
	PENDING("Pending", "En attente");
	
	private String code;
	
	private Localized name;

	private Status(String english, String french) {
		this.code = toString().toLowerCase();
		this.name = new Localized(english, french);
	}
	
	@Override
	public String getCode() {
		return code;
	}
	
	@Override
	public String getName(Locale locale) {
		return name.get(locale);
	}
}