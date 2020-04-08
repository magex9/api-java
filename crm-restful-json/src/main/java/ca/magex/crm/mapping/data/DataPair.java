package ca.magex.crm.mapping.data;

public final class DataPair extends DataElement {

	private final String key;
	
	private final DataElement value;
	
	public DataPair(String key, DataElement value) {
		super(digest(key + ":" + value.mid()));
		this.key = key;
		this.value = value;
	}
	
	public DataPair(String key, String value) {
		this(key, new DataText(value));
	}
	
	public DataPair(String key, Number value) {
		this(key, new DataNumber(value));
	}
	
	public DataPair(String key, Boolean value) {
		this(key, new DataBoolean(value));
	}
	
	public String key() {
		return key;
	}
	
	public DataElement value() {
		return value;
	}
	
}
