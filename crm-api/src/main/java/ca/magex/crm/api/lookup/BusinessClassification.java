package ca.magex.crm.api.lookup;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class BusinessClassification implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer code;
	
	private String name;

	public BusinessClassification(Integer code, String name) {
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
		return obj != null && obj instanceof BusinessClassification && code.equals(((BusinessClassification)obj).getCode());
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}
	
}
