package ca.magex.crm.api.lookup;

public class BusinessSector {

	private Integer code;
	
	private String name;

	public BusinessSector(Integer code, String name) {
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
	
	@Override
	public boolean equals(Object obj) {
		return obj != null && obj instanceof BusinessSector && code.equals(((BusinessSector)obj).getCode());
	}
	
}
