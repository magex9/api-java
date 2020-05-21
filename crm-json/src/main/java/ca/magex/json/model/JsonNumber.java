package ca.magex.json.model;

public final class JsonNumber extends JsonElement {

	private final Number value;
	
	public JsonNumber(Number value) {
		super(value == null ? JsonElement.UNDEFINED.mid() : digest(value));
		this.value = value;
	}
	
	public Number value() {
		return value;
	}
	
}
