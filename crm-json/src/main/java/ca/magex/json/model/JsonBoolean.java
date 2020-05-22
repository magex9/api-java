package ca.magex.json.model;

public final class JsonBoolean extends JsonElement {

	private final Boolean value;
	
	public JsonBoolean(Boolean value) {
		super(value == null ? JsonElement.UNDEFINED.mid() : digest(value));
		this.value = value;
	}
	
	public Boolean value() {
		return value;
	}
	
}
