package ca.magex.crm.api.lookup;

public class Country {

	private String code;
	
	private String name;

	public Country(String code, String name) {
		super();
		this.code = code;
		this.name = name;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return getName();
	}
}
