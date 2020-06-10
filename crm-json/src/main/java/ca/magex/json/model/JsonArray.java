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
	
	@SuppressWarnings("unchecked")
	public <T> List<T> values(Class<T> type) {
		if (JsonElement.class.isAssignableFrom(type)) {
			return elements.stream().map(e -> (T)e).collect(Collectors.toList());
		} else if (String.class.isAssignableFrom(type)) {
			return elements.stream()
				.map(e -> e instanceof JsonText ? ((JsonText)e).value() : e.toString())
				.map(e -> (T)e)
				.collect(Collectors.toList());
		} else if (Integer.class.isAssignableFrom(type)) {
			return elements.stream()
				.map(e -> ((JsonNumber)e).value().intValue())
				.map(e -> (T)e)
				.collect(Collectors.toList());
		} else {
			throw new IllegalArgumentException("Unsuport class casting for list: " + type);
		}
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
	
	public Number getNumber(int index) {
		return ((JsonNumber)get(index)).value();
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
	
	public JsonArray prune() {
		List<JsonElement> pruned = new ArrayList<JsonElement>();
		for (JsonElement element : elements) {
			if (element.getClass().equals(JsonObject.class)) {
				JsonObject obj = ((JsonObject)element).prune();
				if (!obj.isEmpty())
					pruned.add(obj);
			} else if (element.getClass().equals(JsonArray.class)) {
				JsonArray array = ((JsonArray)element).prune();
				if (!array.isEmpty())
					pruned.add(array);
			} else if (element.getClass().equals(JsonText.class)) {
				if (!((JsonText)element).isEmpty())
					pruned.add(element);
			} else if (element.getClass().equals(JsonNumber.class)) {
				if (!((JsonNumber)element).isEmpty())
					pruned.add(element);
			} else if (element.getClass().equals(JsonBoolean.class)) {
				if (!((JsonBoolean)element).isEmpty())
					pruned.add(element);
			} else {
				throw new IllegalArgumentException("Unexpected element to prune: " + element.getClass());
			}
		}
		return new JsonArray(pruned);
	}

}
