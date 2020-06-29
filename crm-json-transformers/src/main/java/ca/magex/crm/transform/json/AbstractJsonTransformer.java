package ca.magex.crm.transform.json;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ca.magex.crm.api.filters.OptionsFilter;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.transform.Transformer;
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonBoolean;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;
import ca.magex.json.model.JsonText;

public abstract class AbstractJsonTransformer<T> implements Transformer<T, JsonElement> {
	
	protected final CrmServices crm;
	
	public AbstractJsonTransformer(CrmServices crm) {
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
	
	public void formatType(List<JsonPair> parent) {
		parent.add(new JsonPair("@context", "http://magex.ca/crm/" + getSourceType().getName().replaceAll("ca.magex.crm.api.", "").replaceAll("\\.", "/")));
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
	
	@SuppressWarnings("unchecked")
	public <O extends Identifier> O parseIdentifier(String key, JsonObject json, Class<O> cls, Locale locale) {
		return (O)parseObject(key, json, new IdentifierJsonTransformer(crm), locale);
	}
	
	public <O> O parseObject(String key, JsonObject json, Transformer<O, JsonElement> transformer, Locale locale) {
		if (json == null)
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
	
	public void formatOption(List<JsonPair> parent, String key, Object obj, Type type, Locale locale) {
		if (obj == null)
			return;
		String optionCode = getProperty(obj, key, String.class);
		if (optionCode != null) {
			Option option = crm.findOptionByCode(type, optionCode);
			parent.add(new JsonPair(key, new OptionJsonTransformer(crm).format(option, locale)));
		}
	}
	
	@SuppressWarnings("unchecked")
	public <O> void formatTransformer(List<JsonPair> parent, String key, Object obj, Transformer<O, JsonElement> transformer, Locale locale) {
		if (obj == null)
			return;
		O property = (O)getProperty(obj, key, Object.class);
		if (property != null) {
			parent.add(new JsonPair(key, transformer.format(property, locale)));
		}
	}
	
	public void formatOption(List<JsonPair> parent, String key, Object obj, Type type, String parentCode, Locale locale) {
		if (obj == null)
			return;
		String optionCode = getProperty(obj, key, String.class);
		if (optionCode != null) {
			Option option = crm.findOptionByCode(type, optionCode);
			parent.add(new JsonPair(key, new OptionJsonTransformer(crm).format(option, locale)));
		}
	}
	
	public void formatIdentifier(List<JsonPair> parent, String key, Object obj, Locale locale) {
		if (obj == null)
			return;
		Identifier code = getProperty(obj, key, Identifier.class);
		StringBuilder sb = new StringBuilder();
		sb.append("http://magex.ca/crm");
		sb.append(code.toString());
		parent.add(new JsonPair(key, sb.toString()));
	}
	
	public void formatStatus(List<JsonPair> parent, String key, Object obj, Locale locale) {
		if (obj == null)
			return;
		Status status = getProperty(obj, key, Status.class);
		StringBuilder sb = new StringBuilder();
		sb.append("http://magex.ca/crm/statuses/");
		sb.append(status.toString().toLowerCase());
		parent.add(new JsonPair(key, sb.toString()));
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
	
	@SuppressWarnings("unchecked")
	public <O> void formatObjects(List<JsonPair> parent, String key, Object obj, Class<O> type) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), new Class[] { });
				if (!m.getReturnType().equals(List.class))
					throw new IllegalArgumentException("Unexpected return codes, expected List but got: " + m.getReturnType().getName());
				List<O> list = (List<O>)m.invoke(obj, new Object[] { });
				List<JsonElement> elements = new ArrayList<JsonElement>();
				if (list != null) {
					for (O item : list) {
						elements.add(new JsonText(item.toString()));
					}
				}
				parent.add(new JsonPair(key, new JsonArray(elements)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public String parseText(String key, JsonObject json) {
		return json.contains(key) ? json.getString(key) : null;
	}
	
	public Boolean parseBoolean(String key, JsonObject json) {
		return json.contains(key) ? json.getBoolean(key) : null;
	}
	
	public String parseOption(String key, JsonObject json, Type type, Locale locale) {
		if (!json.contains(key)) {
			return null;
		} else if (json.contains(key, JsonObject.class)) {
			return crm.findOptionByCode(type, json.getObject(key).getString("@value")).getCode();
		} else if (json.contains(key, JsonText.class)) {
			return crm.findOptions(crm.defaultOptionsFilter().withType(type).withName(locale, json.getString(key)), OptionsFilter.getDefaultPaging()).getSingleItem().getCode();
		} else {
			throw new IllegalArgumentException("Unexpected type of option: " + key);
		}
	}
	
//	public String parseOption(String key, JsonObject json, String typeCode, String parentCode, String parentKey, Locale locale) {
//		Identifier parentId = Type.of(parentCode).getLookupId();
//		Option parent = null;
//		if (!json.contains(parentKey)) {
//			throw null;
//		} else if (json.contains(parentKey, JsonObject.class)) {
//			parent = crm.findOptionByCode(parentId, json.getObject(parentKey).getString("@value"));
//		} else if (json.contains(parentKey, JsonText.class)) {
//			parent = crm.findOptionByLocalizedName(parentId, locale, json.getString(parentKey));
//		} else {
//			throw new IllegalArgumentException("Unexpected type of option: " + parentKey);
//		}
//		
//		Identifier lookupId = Type.of(typeCode, parent.getCode()).getLookupId();
//		if (!json.contains(key)) {
//			return null;
//		} else if (json.contains(key, JsonObject.class)) {
//			return crm.findOptionByCode(lookupId, json.getObject(key).getString("@value")).getCode();
//		} else if (json.contains(key, JsonText.class)) {
//			return crm.findOptionByLocalizedName(lookupId, locale, json.getString(key)).getCode();
//		} else {
//			throw new IllegalArgumentException("Unexpected type of option: " + key);
//		}
//	}

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
