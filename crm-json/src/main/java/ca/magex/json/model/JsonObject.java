package ca.magex.json.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class JsonObject extends JsonElement {

	private final List<JsonPair> pairs;
	
	private final Map<String, JsonElement> map;
	
	private final List<String> keys;
	
	public JsonObject() {
		this(new ArrayList<JsonPair>());
	}
	
	public JsonObject(String text) {
		this(JsonParser.parseObject(text).pairs);
	}
	
	public JsonObject(JsonPair... pairs) {
		this(Arrays.asList(pairs));
	}
	
	public JsonObject(List<JsonPair> pairs) {
		super(digest(pairs.stream().map(e -> e.mid()).collect(Collectors.joining(","))));
		this.pairs = Collections.unmodifiableList(pairs);
		this.map = Collections.unmodifiableMap(pairs.stream().collect(Collectors.toMap(JsonPair::key, JsonPair::value)));
		this.keys = pairs.stream().filter(p -> contains(p.key())).map(p -> p.key()).collect(Collectors.toList());
	}
	
	public JsonObject with(String key, Object value) {
		if (value == null)
			return remove(key);
		return with(new JsonPair(key, cast(value)));
	}
	
	public JsonObject with(JsonPair pair) {
		List<JsonPair> values = new ArrayList<JsonPair>();
		boolean found = false;
		for (int i = 0; i < pairs.size(); i++) {
			JsonPair p = pairs.get(i);
			if (p.key().equals(pair.key())) {
				if (pair.value() != null)
					values.add(pair);
				found = true;
			} else {
				values.add(p);
			}
		}
		if (!found)
			values.add(pair);
		return new JsonObject(values);
	}
	
	public JsonObject append(String key, Object value) {
		if (value == null)
			return this;
		if (!contains(key, JsonArray.class))
			throw new IllegalArgumentException("Key is not an array: " + key);
		return with(key, getArray(key).with(value));
	}
	
	public JsonObject remove(String key) {
		return new JsonObject(new ArrayList<JsonPair>(pairs.stream()
			.filter(p -> !p.key().contentEquals(key)).collect(Collectors.toList())));
	}
	
	public Stream<JsonPair> stream() {
		return pairs.stream();
	}
	
	public List<JsonPair> pairs() {
		return pairs;
	}
	
	public List<String> keys() {
		return keys;
	}
	
	public List<JsonElement> values() {
		return pairs.stream().map(p -> p.value()).collect(Collectors.toList());
	}
	
	public boolean isNull(String key) {
		return map.get(key) != null && map.get(key).getClass().equals(JsonElement.class);
	}
	
	public boolean contains(String key) {
		return map.get(key) != null && !map.get(key).getClass().equals(JsonElement.class);
	}

	public boolean contains(String key, Class<? extends JsonElement> cls) {
		return contains(key) && map.get(key).getClass().equals(cls);
	}
	
	public int size() {
		return keys.size();
	}
	
	public boolean isEmpty() {
		return keys.isEmpty();
	}

	public JsonObject prune() {
		List<JsonPair> pruned = new ArrayList<JsonPair>();
		for (JsonPair pair : pairs) {
			if (pair.value().getClass().equals(JsonObject.class)) {
				JsonObject obj = ((JsonObject)pair.value()).prune();
				if (!obj.isEmpty())
					pruned.add(new JsonPair(pair.key(), obj));
			} else if (pair.value().getClass().equals(JsonArray.class)) {
				JsonArray array = ((JsonArray)pair.value()).prune();
				if (!array.isEmpty())
					pruned.add(new JsonPair(pair.key(), array));
			} else if (pair.value().getClass().equals(JsonText.class)) {
				if (!((JsonText)pair.value()).isEmpty())
					pruned.add(pair);
			} else if (pair.value().getClass().equals(JsonNumber.class)) {
				if (!((JsonNumber)pair.value()).isEmpty())
					pruned.add(pair);
			} else if (pair.value().getClass().equals(JsonBoolean.class)) {
				if (!((JsonBoolean)pair.value()).isEmpty())
					pruned.add(pair);
			} else {
				throw new IllegalArgumentException("Unexpected element to prune: " + pair.value().getClass());
			}
		}
		return new JsonObject(pruned);
	}
	
	public JsonElement get(String key) {
		if (!contains(key))
			throw new NoSuchElementException("Unable to find: " + key);
		return map.get(key);
	}
	
	public JsonElement get(String key, Class<? extends JsonElement> cls) {
		if (!contains(key))
			throw new NoSuchElementException("Unable to find: " + key);
		if (!map.get(key).getClass().equals(cls))
			throw new ClassCastException("Invalid type of object: " + map.get(key).getClass());
		return map.get(key);
	}
	
	public JsonObject getObject(String key) {
		return ((JsonObject)get(key));
	}

	public JsonObject getObject(String key, JsonObject defaultValue) {
		try {
			return getObject(key);
		} catch (NoSuchElementException e) {
			return defaultValue;
		}
	}

	public JsonArray getArray(String key) {
		return ((JsonArray)get(key));
	}

	public JsonArray getArray(String key, JsonArray defaultValue) {
		try {
			return getArray(key);
		} catch (NoSuchElementException e) {
			return defaultValue;
		}
	}

	public String getString(String key) {
		return ((JsonText)get(key)).value();
	}
	
	public String getString(String key, String defaultValue) {
		try {
			return getString(key);
		} catch (NoSuchElementException e) {
			return defaultValue;
		}
	}
	
	public Number getNumber(String key) {
		return ((JsonNumber)get(key)).value();
	}
	
	public Number getNumber(String key, Number defaultValue) {
		try {
			return getNumber(key);
		} catch (NoSuchElementException e) {
			return defaultValue;
		}
	}
	
	public Integer getInt(String key) {
		return ((JsonNumber)get(key)).value().intValue();
	}
	
	public Integer getInt(String key, Integer defaultValue) {
		try {
			return getInt(key);
		} catch (NoSuchElementException e) {
			return defaultValue;
		}
	}
	
	public Long getLong(String key) {
		return ((JsonNumber)get(key)).value().longValue();
	}
	
	public Long getLong(String key, Long defaultValue) {
		try {
			return getLong(key);
		} catch (NoSuchElementException e) {
			return defaultValue;
		}
	}
	
	public Float getFloat(String key) {
		return ((JsonNumber)get(key)).value().floatValue();
	}
	
	public Float getFloat(String key, Float defaultValue) {
		try {
			return getFloat(key);
		} catch (NoSuchElementException e) {
			return defaultValue;
		}
	}
	
	public Boolean getBoolean(String key) {
		return ((JsonBoolean)get(key)).value();
	}
	
	public Boolean getBoolean(String key, Boolean defaultValue) {
		try {
			return getBoolean(key);
		} catch (NoSuchElementException e) {
			return defaultValue;
		}
	}
	
	public LocalDate getDate(String key) {
		return LocalDate.parse(((JsonText)get(key)).value(), DateTimeFormatter.ISO_DATE);
	}
	
	public LocalDate getDate(String key, LocalDate defaultValue) {
		try {
			return getDate(key);
		} catch (NoSuchElementException e) {
			return defaultValue;
		}
	}
	
	public LocalDateTime getDateTime(String key) {
		return LocalDateTime.parse(((JsonText)get(key)).value(), DateTimeFormatter.ISO_DATE_TIME);
	}
	
	public LocalDateTime getDateTime(String key, LocalDateTime defaultValue) {
		try {
			return getDateTime(key);
		} catch (NoSuchElementException e) {
			return defaultValue;
		}
	}
	
}
