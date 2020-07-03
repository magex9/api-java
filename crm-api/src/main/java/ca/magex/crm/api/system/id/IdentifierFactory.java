package ca.magex.crm.api.system.id;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import ca.magex.crm.api.system.Identifier;

/**
 * A Specific Identifier used for Option Identification
 * 
 * @author Jonny
 */
public class IdentifierFactory {

	public static final Map<String, Class<?>> IDENTIFIERS;
	
	private IdentifierFactory() { }
	
	static {
		Map<String, Class<?>> tmp = new HashMap<String, Class<?>>();
		tmp.put(AuthenticationGroupIdentifier.CONTEXT, AuthenticationGroupIdentifier.class);
		tmp.put(AuthenticationRoleIdentifier.CONTEXT, AuthenticationRoleIdentifier.class);
		tmp.put(ConfigurationIdentifier.CONTEXT, ConfigurationIdentifier.class);
		tmp.put(CountryIdentifier.CONTEXT, CountryIdentifier.class);
		tmp.put(SalutationIdentifier.CONTEXT, SalutationIdentifier.class);
		tmp.put(OptionIdentifier.CONTEXT, OptionIdentifier.class);
		tmp.put(BusinessGroupIdentifier.CONTEXT, BusinessGroupIdentifier.class);
		tmp.put(BusinessRoleIdentifier.CONTEXT, BusinessRoleIdentifier.class);
		tmp.put(CountryIdentifier.CONTEXT, CountryIdentifier.class);
		tmp.put(DictionaryIdentifier.CONTEXT, DictionaryIdentifier.class);
		tmp.put(LanguageIdentifier.CONTEXT, LanguageIdentifier.class);
		tmp.put(LocaleIdentifier.CONTEXT, LocaleIdentifier.class);
		tmp.put(LocationIdentifier.CONTEXT, LocationIdentifier.class);
		tmp.put(MessageTypeIdentifier.CONTEXT, MessageTypeIdentifier.class);
		tmp.put(OrganizationIdentifier.CONTEXT, OrganizationIdentifier.class);
		tmp.put(PersonIdentifier.CONTEXT, PersonIdentifier.class);
		tmp.put(PhraseIdentifier.CONTEXT, PhraseIdentifier.class);
		tmp.put(ProvinceIdentifier.CONTEXT, ProvinceIdentifier.class);
		tmp.put(SalutationIdentifier.CONTEXT, SalutationIdentifier.class);
		tmp.put(StatusIdentifier.CONTEXT, StatusIdentifier.class);
		tmp.put(UserIdentifier.CONTEXT, UserIdentifier.class);
		IDENTIFIERS = Collections.unmodifiableMap(tmp);
	}
		
	public static <I extends Identifier> I forId(CharSequence id) {
		return forId(id, null);
	}
	
	@SuppressWarnings("unchecked")
	public static <I extends Identifier> I forId(CharSequence id, Class<I> cls) {
		for (String context : IDENTIFIERS.keySet()) {
			if (StringUtils.startsWith(id, context)) {		
				try {
					Identifier identifier = (Identifier) IDENTIFIERS.get(context).getConstructor(CharSequence.class).newInstance(id);
					if (cls != null && !(cls.isAssignableFrom(identifier.getClass()))) {
						throw new IllegalArgumentException("Identifier is not of type '" + cls + "': " + id);
					}
					return (I) identifier;
				} catch (ReflectiveOperationException e) {
					throw new IllegalArgumentException("Unable to create identifier: " + context + " with " + id, e);
				}
			}
		}
		throw new IllegalArgumentException("Unidentifiable id: " + id);
	}
	
//	public static OptionIdentifier forOptionId(CharSequence id) {
//		Identifier identifier = IdentifierFactory.forId(id);
//		if (identifier instanceof OptionIdentifier)
//			return (OptionIdentifier)identifier;
//		throw new IllegalArgumentException("Identifier is not an option id: " + identifier.getClass() + " with " + id);
//	}
		
}