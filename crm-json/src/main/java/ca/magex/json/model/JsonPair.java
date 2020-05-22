package ca.magex.json.model;

public final class JsonPair extends JsonElement {

	private final String key;
	
	private final JsonElement value;
	
	public JsonPair(String key, JsonElement value) {
		super(digest(validateKey(key) + ":" + (value == null ? JsonElement.UNDEFINED.mid() : value.mid())));
		this.key = key;
		this.value = value == null ? JsonElement.UNDEFINED : value;
	}
	
	public JsonPair(String key, String value) {
		this(key, new JsonText(value));
	}
	
	public JsonPair(String key, Number value) {
		this(key, new JsonNumber(value));
	}
	
	public JsonPair(String key, Boolean value) {
		this(key, new JsonBoolean(value));
	}
	
	public String key() {
		return key;
	}
	
	public JsonElement value() {
		return value;
	}
	
}
