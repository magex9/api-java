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

/**
 * The type of option groups available in the system.  This type list is immutable and will not change
 * while the server is running.
 * 
 * @author magex
 *
 */
public enum Type {
	
	/**
	 *  The set of available states for an item within the CRM System 
	 */
	STATUS("STATUSES", "Statuses", "Statuts", null, false, false, false, StatusIdentifier.class),
	
	/**
	 *  The set of locales available within the CRM system for API calls and data retrieval
	 */
	LOCALE("LOCALES", "Locales", "Locaux", null, false, false, false, LocaleIdentifier.class),
	
	/**
	 *  The core dictionary of translated phrases
	 */
	DICTIONARY("DICTIONARIES", "Dictionaries", "Dictionnaires", null, true, true, false, DictionaryIdentifier.class),
	
	/** 
	 * The translated phrases of text that can be used in the system.  Each phrase is part of a dictionary.
	 */
	PHRASE("PHRASES", "Phrases", "Phrases", DICTIONARY, true, true, true, PhraseIdentifier.class),
	
	/**
	 *  The type of message presented to the user
	 */
	MESSAGE_TYPE("MESSAGE_TYPES", "Message Types", "Types de messages", null, false, true, false, MessageTypeIdentifier.class),
	
	/**
	 *  The list of authentication groups an organization can belong to
	 */
	AUTHENTICATION_GROUP("AUTHENTICATION_GROUPS", "Authentication Groups", "Groupes d'authentification", null, true, true, false, AuthenticationGroupIdentifier.class),
	
	/**
	 *  The list of authentication roles a user can be given, they must be associated to the authentication group of their organization
	 */
	AUTHENTICATION_ROLE("AUTHENTICATION_ROLES", "Authentication Roles", "Rôles d'authentification", AUTHENTICATION_GROUP, false, true, false, AuthenticationRoleIdentifier.class),
	
	/**
	 *  The list of salutations for a person
	 */
	SALUTATION("SALUTATIONS", "Salutations", "Salutations", null, false, true, true, SalutationIdentifier.class),
	
	/**
	 *  The list of languages a person can be associated with for identifiable lookups
	 */
	LANGUAGE("LANGUAGES", "Languages", "Langages", null, false, true, true, LanguageIdentifier.class),
	
	/**
	 *  The list of countries available within the system for identifiable lookups
	 */
	COUNTRY("COUNTRIES", "Countries", "Des pays", null, false, true, true, CountryIdentifier.class),
	
	/**
	 *  The core list of provinces within the system, each one associated with a Country Identfier
	 */
	PROVINCE("PROVINCES", "Canadian Provinces", "Provinces canadiennes", COUNTRY, false, true, true, ProvinceIdentifier.class),
	
	/**
	 *  The list of business groups an organization can be assigned
	 */
	BUSINESS_GROUP("BUSINESS_GROUPS", "Business Sector", "Secteur d'activité", null, true, true, false, BusinessGroupIdentifier.class),
	
	/**
	 *  The list of business roles a user can be assigned, they must be associated to the business group of an organization
	 */
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