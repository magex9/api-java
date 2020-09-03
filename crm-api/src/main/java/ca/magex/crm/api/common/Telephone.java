package ca.magex.crm.api.common;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.lang.Nullable;

import ca.magex.crm.api.Crm;

public class Telephone implements Serializable {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	@Nullable
	private String number;

	@Nullable
	private String extension;

	public Telephone(String number, String extension) {
		super();
		this.number = number;
		this.extension = extension;
	}
	
	public Telephone(String number) {
		super();
		this.number = number;
		this.extension = "";
	}

	public String getNumber() {
		return number;
	}

	public String getExtension() {
		return extension;
	}
	
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}
}