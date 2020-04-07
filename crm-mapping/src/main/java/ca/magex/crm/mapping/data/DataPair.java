package ca.magex.crm.mapping.data;

public final class DataPair extends DataElement {

	private final String key;
	
	private final DataElement value;
	
	public DataPair(String key, DataElement value) {
		super(digest(key + ":" + value.mid()));
		this.key = key;
		this.value = value;
	}
	
	public String key() {
		return key;
	}
	
	public DataElement value() {
		return value;
	}
	
}
