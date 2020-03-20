package ca.magex.crm.api.lookup;

public class Salutation {

	private Integer code;
	
	private String name;

	public Salutation(Integer code, String name) {
		super();
		this.code = code;
		this.name = name;
	}
	
	public Integer getCode() {
		return code;
	}
	
	public String getName() {
		return name;
	}
	
}
