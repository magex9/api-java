package ca.magex.crm.api.system.id;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Type;

/**
 * A Specific Identifier used for Option Identification
 * 
 * @author Jonny
 */
public class IdentifierFactory {

	public static final List<Class<? extends Identifier>> IDENTIFIERS = List.of(
		AuthenticationGroupIdentifier.class,
		AuthenticationRoleIdentifier.class,
		BusinessGroupIdentifier.class,
		BusinessRoleIdentifier.class,
		ConfigurationIdentifier.class,
		CountryIdentifier.class,
		DictionaryIdentifier.class,
		LanguageIdentifier.class,
		LocaleIdentifier.class,
		LocationIdentifier.class,
		MessageTypeIdentifier.class,
		OrganizationIdentifier.class,
		PersonIdentifier.class,
		PhraseIdentifier.class,
		ProvinceIdentifier.class,
		SalutationIdentifier.class,
		StatusIdentifier.class,
		UserIdentifier.class
	);
	
	public static final Map<Class<? extends Identifier>, Identifier> CLASS_IDENTIFIER = IDENTIFIERS
		.stream().collect(Collectors.toMap(cls -> cls, cls -> {
			try {
				return ((Identifier)cls.getConstructor(CharSequence.class).newInstance("INVALID"));
			} catch (ReflectiveOperationException e) {
				throw new IllegalArgumentException("Unable to create identifier: " + cls.getName(), e);
			}
		}));
	
	public static final Map<String, Class<? extends Identifier>> CONTEXT_CLASS = CLASS_IDENTIFIER.values()
		.stream().collect(Collectors.toMap(i -> i.getContext(), i -> i.getClass()));
	
	public static final Map<Class<? extends Identifier>, String> CLASS_CONTEXT = CLASS_IDENTIFIER.values()
		.stream().collect(Collectors.toMap(i -> i.getClass(), i -> i.getContext()));
		
	public static final Map<Class<? extends OptionIdentifier>, Type> OPTION_TYPE = CLASS_IDENTIFIER.values()
		.stream().filter(i -> i instanceof OptionIdentifier)
		.map(i -> (OptionIdentifier)i)
		.collect(Collectors.toMap(i -> i.getClass(), i -> i.getType()));
			
	public static final Map<Type, Class<? extends OptionIdentifier>> TYPE_OPTION = CLASS_IDENTIFIER.values()
		.stream().filter(i -> i instanceof OptionIdentifier)
		.map(i -> (OptionIdentifier)i)
		.collect(Collectors.toMap(i -> i.getType(), i -> i.getClass()));
			
	private IdentifierFactory() { }
	
	public static <I extends Identifier> String getContext(Class<I> cls) {
		return CLASS_CONTEXT.get(cls);
	}
	
	public static <I extends OptionIdentifier> Type getType(Class<I> cls) {
		return OPTION_TYPE.get(cls);
	}
	
	public static Class<? extends OptionIdentifier> forType(Type type) {
		return TYPE_OPTION.get(type);
	}
	
	@SuppressWarnings("unchecked")
	public static <I extends Identifier> I forId(CharSequence id) {
		if (StringUtils.isBlank(id)) {
			return null;
		}
		if (StringUtils.startsWith(id, Crm.REST_BASE))
			id = id.subSequence(Crm.REST_BASE.length(), id.length());
		for (String context : CONTEXT_CLASS.keySet()) {
			if (StringUtils.startsWith(id, context)) {		
				try {
					return (I) CONTEXT_CLASS.get(context).getConstructor(CharSequence.class).newInstance(id);
				} catch (ReflectiveOperationException e) {
					throw new IllegalArgumentException("Unable to create identifier: " + context + " with " + id, e);
				}
			}
		}
		throw new IllegalArgumentException("Unidentifiable id: " + id);
	}
	
	@SuppressWarnings("unchecked")
	public static <I extends Identifier> I forOptionId(CharSequence id) {
		if (StringUtils.isBlank(id)) {
			return null;
		}
		if (StringUtils.startsWith(id, Crm.REST_BASE))
			id = id.subSequence(Crm.REST_BASE.length(), id.length());
		for (String context : CONTEXT_CLASS.keySet()) {
			if (StringUtils.startsWith(id, context)) {		
				try {
					return (I) CONTEXT_CLASS.get(context).getConstructor(CharSequence.class)
						.newInstance(id.subSequence(context.length(), id.length()).toString().toUpperCase());
				} catch (ReflectiveOperationException e) {
					throw new IllegalArgumentException("Unable to create identifier: " + context + " with " + id, e);
				}
			}
		}
		throw new IllegalArgumentException("Unidentifiable id: " + id);
	}
	
	public static <I extends Identifier> I forId(CharSequence id, Class<I> cls) {
		try {
			return (I)cls.getConstructor(CharSequence.class).newInstance(id);
		} catch (ReflectiveOperationException e) {
			throw new IllegalArgumentException("Unable to create identifier: " + cls.getName() + " with " + id, e);
		}
	}
		
}