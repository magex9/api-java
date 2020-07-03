package ca.magex.crm.api.system;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.magex.crm.api.exceptions.ItemNotFoundException;

public enum Type {
	
	STATUS("STATUSES", "Statuses", "Statuts", null, false, false, false), 
	LOCALE("LOCALES", "Locales", "Locaux", null, false, false, false),
	DICTIONARY("DICTIONARIES", "Dictionaries", "Dictionnaires", null, true, true, false),
	PHRASE("PHRASES", "Phrases", "Phrases", DICTIONARY, true, true, true),
	MESSAGE_TYPE("MESSAGE_TYPES", "Message Types", "Types de messages", null, false, true, false),
	AUTHENTICATION_GROUP("AUTH_GROUPS", "Authentication Groups", "Groupes d'authentification", null, true, true, false), 
	AUTHENTICATION_ROLE("AUTH_ROLES", "Authentication Roles", "Rôles d'authentification", AUTHENTICATION_GROUP, false, true, false),
	SALUTATION("SALUTATIONS", "Salutations", "Salutations", null, false, true, true), 
	LANGUAGE("LANGUAGES", "Languages", "Langages", null, false, true, true),
	COUNTRY("COUNTRIES", "Countries", "Des pays", null, false, true, true),
	PROVINCE("PROVINCES", "Canadian Provinces", "Provinces canadiennes", COUNTRY, false, true, true),
	BUSINESS_GROUP("BUSINESS_GROUPS", "Business Sector", "Secteur d'activité", null, true, true, false),
	BUSINESS_ROLE("BUSINESS_ROLES", "Business Unit", "Équipe commerciale", BUSINESS_GROUP, false, true, true);
	
	private static final Map<String, Type> INDEX = Arrays.asList(values()).stream().collect(Collectors.toMap(t -> t.getCode(), t -> t));
	
	public static final Set<String> keys() {
		return INDEX.keySet();
	}
	
	public static final Type of(String typeCode) {
		if (!INDEX.containsKey(typeCode))
			throw new ItemNotFoundException("Type Code '" + typeCode + "'");
		return INDEX.get(typeCode);
	}

	private Localized name;
	
	private Type parent;
	
	private boolean recrussive;
	
	private boolean extendable;
	
	private boolean choice;
	
	private Type(String code, String english, String french, Type parent, boolean recurrsive, boolean extendable, boolean choice) {
		this.name = new Localized(code, english, french);
		this.parent = parent;
		this.recrussive = recurrsive;
		this.extendable = extendable;
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
	
	public Type getParent() {
		return parent;
	}
	
	public boolean isRecrussive() {
		return recrussive;
	}
	
	public boolean isExtendable() {
		return extendable;
	}
	
	public boolean isChoice() {
		return choice;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE, false, Type.class);
	}	
	
}