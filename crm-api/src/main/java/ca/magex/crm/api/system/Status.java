package ca.magex.crm.api.system;

import java.util.Locale;

import ca.magex.crm.api.lookup.CrmLookupItem;

public enum Status implements CrmLookupItem {

	ACTIVE("Active", "Actif"), 
	INACTIVE("Inactive", "Inactif"), 
	PENDING("Pending", "En attente");
	
	private Localized name;

	private Status(String english, String french) {
		this.name = new Localized(toString().toLowerCase(), english, french);
	}
	
	@Override
	public String getCode() {
		return name.get(Lang.ROOT);
	}
	
	@Override
	public String getName(Locale locale) {
		return name.get(locale);
	}
}