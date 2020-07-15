package ca.magex.crm.api.system;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

public enum Status {

	ACTIVE("Active", "Actif"), 
	INACTIVE("Inactive", "Inactif"), 
	PENDING("Pending", "En attente");
	
	private Localized name;

	private Status(String english, String french) {
		this.name = new Localized(toString(), english, french);
	}
	
	public String getCode() {
		return name.get(Lang.ROOT);
	}
	
	public Localized getName() {
		return name;
	}
	
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