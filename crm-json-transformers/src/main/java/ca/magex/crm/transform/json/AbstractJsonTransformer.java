package ca.magex.crm.transform.json;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.services.CrmOptionService;
import ca.magex.crm.api.system.Choice;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.IdentifierFactory;
import ca.magex.crm.api.system.id.MessageTypeIdentifier;
import ca.magex.crm.api.system.id.OptionIdentifier;
import ca.magex.crm.api.system.id.PhraseIdentifier;
import ca.magex.crm.api.transform.Transformer;
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonBoolean;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonNumber;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;
import ca.magex.json.model.JsonText;
import ca.magex.json.util.StringConverter;

public abstract class AbstractJsonTransformer<T> implements Transformer<T, JsonElement> {
	
	protected final CrmOptionService crm;
	
	public AbstractJsonTransformer(CrmOptionService crm) {
		if (crm == null)
			throw new IllegalArgumentException("Crm cannot be null");
		this.crm = crm;
	}
	
	public String getType(Class<?> cls) {
		return cls.getSimpleName();
	}
	
	@Override
	public Class<JsonElement> getTargetType() {
		return JsonElement.class;
	}
	
	protected void validate(List<Message> messages) {
		if (!messages.isEmpty())
			throw new BadRequestException("Validation Errors", messages);
	}
	
	public void formatType(List<JsonPair> parent, Locale locale) {
		if (locale == null)
			parent.add(new JsonPair("@context", Crm.REST_BASE + "/schema/" + getSourceType().getName().replaceAll("ca.magex.crm.api.", "").replaceAll("\\.", "/").replaceAll("^crm", "organization")));
	}
	
	public final JsonElement format(T obj, Locale locale) {
		if (obj == null)
			return null;
		if (locale == null)
			return formatRoot(obj);
		return formatLocalized(obj, locale);
	}
	
	public abstract JsonElement formatRoot(T obj);

	public abstract JsonElement formatLocalized(T obj, Locale locale);

	@Override
	public final T parse(JsonElement json, Locale locale) {
		if (json == null) {
			return null;
		} if (json instanceof JsonText) {
			return parseJsonText((JsonText)json, locale);
		} else if (json instanceof JsonObject) {
			return parseJsonObject((JsonObject)json, locale);
		} else {
			throw new UnsupportedOperationException("Unexpected json element: " + json.getClass());
		}
	}

	public T parseJsonText(JsonText json, Locale locale) {
		throw new UnsupportedOperationException("Unsupported json element: " + json.getClass().getSimpleName());
	}

	public T parseJsonObject(JsonObject json, Locale locale) {
		if (!json.getString("@type").equals(getSourceType().getSimpleName()))
			throw new IllegalArgumentException("Unexpected type for " + getSourceType().getSimpleName() + ": " + json.getString("@type"));
		throw new UnsupportedOperationException("Unsupported json element: " + json.getClass().getSimpleName());
	}
	
	public <O extends OptionIdentifier> List<O> parseOptions(String key, JsonObject json, Class<O> cls, Locale locale) {
		if (!json.contains(key))
			return List.of();
		return json.getArray(key).stream().map(e -> parseOption(e, cls, locale)).collect(Collectors.toList());
	}

	public <O extends OptionIdentifier> O parseOption(JsonElement e, Class<O> cls, Locale locale) {
		String value = locale == null ?
			((JsonObject)e).getString("@id").substring(Crm.REST_BASE.length()) :
			((JsonText)e).value();
		String code = value;
		if (locale == null) {
			String context = IdentifierFactory.getContext(cls);
			if (!value.startsWith(context))
				throw new IllegalArgumentException("Expected context not found: " + context + " !^ " + value);
			code = Arrays.asList(value.substring(context.length()).split("/")).stream().map(s -> StringConverter.lowerToUpperCase(s)).collect(Collectors.joining("/"));
		} else {
			Type type = IdentifierFactory.getType(cls);
			code = crm.findOptions(crm.defaultOptionsFilter().withType(type).withName(new Localized(locale, value))).getSingleItem().getCode();
		}
		return IdentifierFactory.forId(code, cls);
	}

	public <O extends Identifier> O parseIdentifier(String key, JsonObject json, Class<O> cls, Locale locale) {
		try {
			if (locale == null) {
				String identifier = json.getString(key);
				String prefix = Crm.REST_BASE + cls.getField("CONTEXT").get(null);
				if (!identifier.startsWith(prefix))
					throw new IllegalArgumentException("Identifier should start with: " + prefix + " -> " + identifier);
				return IdentifierFactory.forId(identifier.substring(prefix.length()), cls);
			} else {
				return IdentifierFactory.forId(json.getString(key), cls);
			}
		} catch (NoSuchElementException e) {
			return null;
		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to build identifier for: " + cls.getName(), e);
		}
	}
	
	public <O> O parseObject(String key, JsonObject json, Transformer<O, JsonElement> transformer, Locale locale) {
		if (json == null || !json.contains(key))
			return null;
		return transformer.parse(json.get(key), locale);
	}
	
	@SuppressWarnings("unchecked")
	public <P> P getProperty(Object obj, String key, Class<P> type) {
		if (obj == null)
			throw new IllegalArgumentException("Object cannot be null");
		if (key == null)
			throw new IllegalArgumentException("Key cannot be null");
		if (type == null)
			throw new IllegalArgumentException("Type cannot be null");
		try {
			Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
			if (!m.getReturnType().equals(type))
				throw new IllegalArgumentException("Unexpected return code, expected " + type.getSimpleName() + " but got: " + m.getReturnType().getName());
			return (P)m.invoke(obj, new Object[] { });
		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to get " + key + " from " + obj, e);
		}
	}
	
	public void formatText(List<JsonPair> parent, String key, Object obj) {
		if (obj == null)
			return;
		String text = getProperty(obj, key, String.class);
		if (text != null)
			parent.add(new JsonPair(key, new JsonText(text)));
	}
	
	public void formatLong(List<JsonPair> parent, String key, Object obj) {
		if (obj == null)
			return;
		Long value = getProperty(obj, key, Long.class);
		if (value != null)
			parent.add(new JsonPair(key, new JsonNumber(value)));
	}
	
	public void formatLastModified(List<JsonPair> parent, Object obj) {
		if (obj == null)
			return;
		Long value = getProperty(obj, "lastModified", Long.class);
		if (value != null)
			parent.add(new JsonPair("lastModified", new JsonText(JsonObject.formatDateTime(value))));
	}
	
	public <O extends OptionIdentifier> void formatChoice(List<JsonPair> parent, String key, Object obj, Class<O> cls, Locale locale) {
		if (obj == null)
			return;
		Choice<?> choice = getProperty(obj, key, Choice.class);
		if (choice != null) {
			if (choice.isIdentifer()) {
				parent.add(new JsonPair(key, formatOption(crm.findOption(choice.getIdentifier()), locale)));
			} else if (choice.isOther()) {
				parent.add(new JsonPair(key, choice.getOther()));
			}
		}
	}
	
	public <O extends OptionIdentifier> void formatOption(List<JsonPair> parent, String key, Object obj, Class<O> cls, Locale locale) {
		if (obj == null)
			return;
		O identifier = getProperty(obj, key, cls);
		if (identifier != null) {
			parent.add(new JsonPair(key, formatOption(crm.findOption(identifier), locale)));
		}
	}
	
	public JsonElement formatOption(Option option, Locale locale) {
		if (locale == null) {
			List<JsonPair> pairs = new ArrayList<JsonPair>();
			pairs.add(new JsonPair("@context", buildContext(option, false, null)));
			pairs.add(new JsonPair("@id", buildContext(option, true, null) + "/" + option.getCode().replaceAll("_", "-").toLowerCase()));
			pairs.add(new JsonPair("@value", option.getCode()));
			pairs.add(new JsonPair("@en", option.getName(Lang.ENGLISH)));
			pairs.add(new JsonPair("@fr", option.getName(Lang.FRENCH)));
			return new JsonObject(pairs);
		} else {
			return new JsonText(option.getName(locale));
		}
	}
	
	public <O> void formatTransformer(List<JsonPair> parent, String key, Object obj, Transformer<O, JsonElement> transformer, Locale locale) {
		if (obj == null)
			return;
		O property = (O)getProperty(obj, key, transformer.getSourceType());
		if (property != null) {
			parent.add(new JsonPair(key, transformer.format(property, locale)));
		}
	}

	@SuppressWarnings("unchecked")
	public <O extends OptionIdentifier> void formatOptions(List<JsonPair> parent, String key, Object obj, Type type, Locale locale, Identifier objectId, List<Message> messages) {
		if (obj == null)
			return;
		try {
			Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
			if (!m.getReturnType().equals(List.class))
				throw new IllegalArgumentException("Unexpected return codes, expected List but got: " + m.getReturnType().getName());
			List<O> list = (List<O>)m.invoke(obj, new Object[] { });
			List<JsonElement> elements = new ArrayList<JsonElement>();
			if (list != null) {
				for (int i = 0; i < list.size(); i++) {
					O optionId = list.get(i);
					try {
						elements.add(formatOption(crm.findOptionByCode(type, optionId.getCode()), locale));
					} catch (ItemNotFoundException e) {
						messages.add(new Message(objectId, MessageTypeIdentifier.ERROR, key + "[" + i + "]", "", PhraseIdentifier.VALIDATION_OPTION_INVALID));
					}
				}
			}
			parent.add(new JsonPair(key, new JsonArray(elements)));
		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to format options: " + key, e);
		}
	}

	public <O extends Identifier> void formatIdentifier(List<JsonPair> parent, String key, Object obj, Class<O> cls, Locale locale) {
		if (obj == null)
			return;
		O identifier = getProperty(obj, key, cls);
		if (identifier == null)
			return;
		if (locale == null) {
			StringBuilder sb = new StringBuilder();
			sb.append(Crm.REST_BASE);
			sb.append(identifier.toString());
			parent.add(new JsonPair(key, sb.toString()));
		} else {
			parent.add(new JsonPair(key, identifier.getCode()));
		}
	}
	
	@SuppressWarnings("unchecked")
	public <O extends Identifier> void formatIdentifiers(List<JsonPair> parent, String key, Object obj, Class<O> cls, Locale locale, Identifier objectId, List<Message> messages) {
		if (obj == null)
			return;
		try {
			Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
			if (!m.getReturnType().equals(List.class))
				throw new IllegalArgumentException("Unexpected return codes, expected List but got: " + m.getReturnType().getName());
			List<O> list = (List<O>)m.invoke(obj, new Object[] { });
			List<JsonElement> elements = new ArrayList<JsonElement>();
			if (list != null) {
				for (int i = 0; i < list.size(); i++) {
					O identifier = list.get(i);
					try {
						elements.add(new JsonText(Crm.REST_BASE + identifier));
					} catch (ItemNotFoundException e) {
						messages.add(new Message(objectId, MessageTypeIdentifier.ERROR, key + "[" + i + "]", "", PhraseIdentifier.VALIDATION_OPTION_INVALID));
					}
				}
			}
			parent.add(new JsonPair(key, new JsonArray(elements)));
		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to format identifiers: " + key, e);
		}
	}
	
	public void formatStatus(List<JsonPair> parent, String key, Object obj, Locale locale) {
		if (obj == null)
			return;
		Status status = getProperty(obj, key, Status.class);
		if (status != null) {
			parent.add(new JsonPair(key, formatOption(crm.findOptionByCode(Type.STATUS, status.toString()), locale)));
		}
	}
	
	public String buildContext(Option option, boolean identifier, Locale locale) {
		StringBuilder sb = new StringBuilder();
		if (identifier) {
			sb.append(Crm.REST_BASE);
		} else {
			sb.append(Crm.SCHEMA_BASE);
		}
		sb.append("/options/");
		sb.append(formatType(option.getType(), identifier));
		return sb.toString();
	}
	
	public String formatType(Type type, boolean identifier) {
		if (identifier) {
			return StringConverter.upperToLowerCase(type.getCode());
		} else {
			return StringConverter.upperToTitleCase(type.getCode());
		}
	}
	
	public void formatLocalized(List<JsonPair> parent, String key, Object obj, Locale locale) {
		if (obj == null)
			return;
		Localized name = getProperty(obj, key, Localized.class);
		if (name != null) {
			parent.add(new JsonPair(key, new LocalizedJsonTransformer(crm).format(name, locale)));
		}
	}
	
	public void formatBoolean(List<JsonPair> parent, String key, Object obj) {
		if (obj == null)
			return;
		Boolean bool = getProperty(obj, key, Boolean.class);
		if (bool != null)
			parent.add(new JsonPair(key, new JsonBoolean(bool)));
	}
	
	public String parseText(String key, JsonObject json) {
		return json.contains(key) ? json.getString(key) : null;
	}
	
	public Long parseLong(String key, JsonObject json) {
		return json.contains(key) ? json.getLong(key) : null;
	}
	
	public Long parseLastModified(JsonObject json) {
		return json.contains("lastModified") ? json.getDateTime("lastModified").toEpochSecond() * 1000 : null;
	}
	
	public Boolean parseBoolean(String key, JsonObject json) {
		return json.contains(key) ? json.getBoolean(key) : null;
	}
	
	public Status parseStatus(String key, JsonObject json, Locale locale) {
		if (!json.contains(key)) {
			return null;
		} else if (json.contains(key, JsonObject.class)) {
			return Status.of(json.getObject(key).getString("@value"));
		} else if (json.contains(key, JsonText.class)) {
			if (locale == null) {
				return Status.of(json.getString(key));
			} else {
				return Status.of(json.getString(key), locale);
			}
		} else {
			throw new IllegalArgumentException("Unexpected type of option: " + key);
		}
	}
	
	public <I extends OptionIdentifier> I parseOption(String key, JsonObject json, Type type, Locale locale) {
		if (!json.contains(key)) {
			return null;
		} else if (json.contains(key, JsonObject.class)) {
			return crm.findOptionByCode(type, json.getObject(key).getString("@value")).getOptionId();
		} else if (json.contains(key, JsonText.class)) {
			if (locale == null) {
				return IdentifierFactory.forOptionId(json.getString(key));
			} else {
				return crm.findOptions(crm.defaultOptionsFilter().withType(type).withName(new Localized(locale, json.getString(key)))).getSingleItem().getOptionId();
			}
		} else {
			throw new IllegalArgumentException("Unexpected type of option: " + key);
		}
	}
	
	public <I extends OptionIdentifier> Choice<I> parseChoice(String key, JsonObject json, Type type, Locale locale) {
		if (!json.contains(key)) {
			return null;
		} else if (json.contains(key, JsonObject.class)) {
			return crm.findOptionByCode(type, json.getObject(key).getString("@value")).asChoice();
		} else if (json.contains(key, JsonText.class)) {
			if (locale != null) {
				try {
					return new Choice<I>(crm.findOptions(crm.defaultOptionsFilter().withType(type).withName(new Localized(locale, json.getString(key)))).getSingleItem().getOptionId());
				} catch (ItemNotFoundException e) { }
			}
			return new Choice<I>(json.getString(key));
		} else {
			throw new IllegalArgumentException("Unexpected type of option: " + key);
		}
	}
	
	public List<String> parseTexts(String key, JsonObject json) {
		List<String> list = new ArrayList<String>();
		for (JsonElement child : json.getArray(key).values()) {
			if (child instanceof JsonText) {
				list.add(((JsonText)child).value());
			} else if (child instanceof JsonObject) {
				list.add(((JsonObject)child).getString("@id"));
			} else {
				throw new IllegalArgumentException("Unexpected data type for child: " + child.getClass());
			}
		}
		return list;
	}

}
