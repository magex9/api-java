package ca.magex.crm.api.system;

public class Role {

	private Integer code;
	
	private String name;

	public Role(Integer code, String name) {
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
