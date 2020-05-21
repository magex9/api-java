package ca.magex.json.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class JsonArray extends JsonElement {
	
	private final List<JsonElement> elements;
	
	public JsonArray() {
		this(new ArrayList<JsonElement>());
	}
	
	public JsonArray(String text) {
		this(JsonParser.parseArray(text).elements);
	}
	
	public JsonArray(List<JsonElement> elements) {
		super(digest(elements.stream().map(e -> e.mid()).collect(Collectors.joining(","))));
		this.elements = Collections.unmodifiableList(elements);
	}
	
	public JsonArray with(Object... values) {
		List<JsonElement> updated = new ArrayList<JsonElement>(elements);
		for (Object value : values) {
			updated.add(cast(value));
		}
		return new JsonArray(updated);
	}
	
	public Stream<JsonElement> stream() {
		return elements.stream();
	}
	
	public List<JsonElement> values() {
		return elements;
	}
	
	public JsonElement get(int index) {
		return elements.get(index);
	}
	
	public JsonObject getObject(int index) {
		return (JsonObject)get(index);
	}
	
	public JsonArray getArray(int index) {
		return (JsonArray)get(index);
	}
	
	public String getString(int index) {
		return ((JsonText)get(index)).value();
	}
	
	public Integer getInt(int index) {
		return ((JsonNumber)get(index)).value().intValue();
	}
	
	public Long getLong(int index) {
		return ((JsonNumber)get(index)).value().longValue();
	}
	
	public Float getFloat(int index) {
		return ((JsonNumber)get(index)).value().floatValue();
	}
	
	public Boolean getBoolean(int index) {
		return ((JsonBoolean)get(index)).value();
	}
	
	public LocalDate getDate(int index) {
		return LocalDate.parse(((JsonText)get(index)).value(), DateTimeFormatter.ISO_DATE);
	}
	
	public LocalDateTime getDateTime(int index) {
		return LocalDateTime.parse(((JsonText)get(index)).value(), DateTimeFormatter.ISO_DATE_TIME);
	}
	
	public int size() {
		return elements.size();
	}
	
	public boolean isEmpty() {
		return elements.isEmpty();
	}

}
