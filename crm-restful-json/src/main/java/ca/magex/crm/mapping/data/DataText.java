package ca.magex.crm.mapping.data;

public final class DataText extends DataElement {

	private final String value;
	
	public DataText(String value) {
		super(digest(value));
		this.value = value;
	}
	
	public String value() {
		return value;
	}
	
}
