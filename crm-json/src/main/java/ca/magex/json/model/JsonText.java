package ca.magex.json.model;

public final class JsonText extends JsonElement {

	private final String value;
	
	public JsonText(String value) {
		super(value == null ? JsonElement.UNDEFINED.mid() : digest(value));
		this.value = value;
	}
	
	public String value() {
		return value;
	}
	
}
