package ca.magex.crm.api.system;

import java.io.Serializable;

public class Role implements Serializable {

	private static final long serialVersionUID = 1L;

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
	
	@Override
	public boolean equals(Object obj) {
		return obj != null && obj instanceof Role && code.equals(((Role)obj).getCode());
	}
	
}
