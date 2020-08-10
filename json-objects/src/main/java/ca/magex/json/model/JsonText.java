package ca.magex.json.model;

import org.apache.commons.lang3.StringUtils;

public final class JsonText extends JsonElement {

	private final String value;
	
	public JsonText(String value) {
		super(value == null ? JsonElement.UNDEFINED.mid() : digest(value));
		this.value = value;
	}
	
	public String value() {
		return value;
	}
	
	public boolean isEmpty() {
		return StringUtils.isEmpty(value);
	}
	
}
