package ca.magex.crm.api.system;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.system.id.AuthenticationGroupIdentifier;
import ca.magex.crm.api.system.id.AuthenticationRoleIdentifier;
import ca.magex.crm.api.system.id.BusinessGroupIdentifier;
import ca.magex.crm.api.system.id.BusinessRoleIdentifier;
import ca.magex.crm.api.system.id.CountryIdentifier;
import ca.magex.crm.api.system.id.DictionaryIdentifier;
import ca.magex.crm.api.system.id.LanguageIdentifier;
import ca.magex.crm.api.system.id.LocaleIdentifier;
import ca.magex.crm.api.system.id.MessageTypeIdentifier;
import ca.magex.crm.api.system.id.OptionIdentifier;
import ca.magex.crm.api.system.id.PhraseIdentifier;
import ca.magex.crm.api.system.id.ProvinceIdentifier;
import ca.magex.crm.api.system.id.SalutationIdentifier;
import ca.magex.crm.api.system.id.StatusIdentifier;

public enum Type {
	
	STATUS("STATUSES", "Statuses", "Statuts", null, false, false, false, StatusIdentifier.class), 
	LOCALE("LOCALES", "Locales", "Locaux", null, false, false, false, LocaleIdentifier.class),
	DICTIONARY("DICTIONARIES", "Dictionaries", "Dictionnaires", null, true, true, false, DictionaryIdentifier.class),
	PHRASE("PHRASES", "Phrases", "Phrases", DICTIONARY, true, true, true, PhraseIdentifier.class),
	MESSAGE_TYPE("MESSAGE_TYPES", "Message Types", "Types de messages", null, false, true, false, MessageTypeIdentifier.class),
	AUTHENTICATION_GROUP("AUTHENTICATION_GROUPS", "Authentication Groups", "Groupes d'authentification", null, true, true, false, AuthenticationGroupIdentifier.class), 
	AUTHENTICATION_ROLE("AUTHENTICATION_ROLES", "Authentication Roles", "Rôles d'authentification", AUTHENTICATION_GROUP, false, true, false, AuthenticationRoleIdentifier.class),
	SALUTATION("SALUTATIONS", "Salutations", "Salutations", null, false, true, true, SalutationIdentifier.class), 
	LANGUAGE("LANGUAGES", "Languages", "Langages", null, false, true, true, LanguageIdentifier.class),
	COUNTRY("COUNTRIES", "Countries", "Des pays", null, false, true, true, CountryIdentifier.class),
	PROVINCE("PROVINCES", "Canadian Provinces", "Provinces canadiennes", COUNTRY, false, true, true, ProvinceIdentifier.class),
	BUSINESS_GROUP("BUSINESS_GROUPS", "Business Sector", "Secteur d'activité", null, true, true, false, BusinessGroupIdentifier.class),
	BUSINESS_ROLE("BUSINESS_ROLES", "Business Unit", "Équipe commerciale", BUSINESS_GROUP, false, true, true, BusinessRoleIdentifier.class);
	
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
	
	private Class<? extends OptionIdentifier> identifierClass;
	
	private Type(String code, String english, String french, Type parent, boolean recurrsive, boolean extendable, boolean choice, Class<? extends OptionIdentifier> identifierClass) {
		this.name = new Localized(code, english, french);
		this.parent = parent;
		this.recrussive = recurrsive;
		this.extendable = extendable;
		this.identifierClass = identifierClass;
	}
	
	public OptionIdentifier generateId(String code) {
		try {
			return identifierClass.getConstructor(CharSequence.class).newInstance(code);
		}
		catch(ReflectiveOperationException e) {
			throw new RuntimeException("Unable to create identifier", e);
		}
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