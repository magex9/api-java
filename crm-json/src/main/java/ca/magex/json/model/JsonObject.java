package ca.magex.json.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
	
	public JsonObject(List<JsonPair> pairs) {
		super(digest(pairs.stream().map(e -> e.mid()).collect(Collectors.joining(","))));
		this.pairs = Collections.unmodifiableList(pairs);
		this.map = Collections.unmodifiableMap(pairs.stream().collect(Collectors.toMap(JsonPair::key, JsonPair::value)));
		this.keys = pairs.stream().filter(p -> contains(p.key())).map(p -> p.key()).collect(Collectors.toList());
	}
	
	public JsonObject with(String key, Object value) {
		return with(new JsonPair(key, cast(value)));
	}
	
	public JsonObject with(JsonPair pair) {
		List<JsonPair> values = new ArrayList<JsonPair>();
		boolean found = false;
		for (int i = 0; i < pairs.size(); i++) {
			JsonPair p = pairs.get(i);
			if (p.key().equals(pair.key())) {
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
	
	public JsonElement get(String key) {
		if (!contains(key))
			throw new NoSuchElementException("Unable to find: " + key);
		return map.get(key);
	}
	
	public JsonObject getObject(String key) {
		return ((JsonObject)get(key));
	}

	public JsonArray getArray(String key) {
		return ((JsonArray)get(key));
	}

	public String getString(String key) {
		return ((JsonText)get(key)).value();
	}
	
	public Integer getInt(String key) {
		return ((JsonNumber)get(key)).value().intValue();
	}
	
	public Long getLong(String key) {
		return ((JsonNumber)get(key)).value().longValue();
	}
	
	public Float getFloat(String key) {
		return ((JsonNumber)get(key)).value().floatValue();
	}
	
	public Boolean getBoolean(String key) {
		return ((JsonBoolean)get(key)).value();
	}
	
	public LocalDate getDate(String key) {
		return LocalDate.parse(((JsonText)get(key)).value(), DateTimeFormatter.ISO_DATE);
	}
	
	public LocalDateTime getDateTime(String key) {
		return LocalDateTime.parse(((JsonText)get(key)).value(), DateTimeFormatter.ISO_DATE_TIME);
	}
	
}
