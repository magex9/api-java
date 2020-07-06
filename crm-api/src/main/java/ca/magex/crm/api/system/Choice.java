package ca.magex.crm.api.system;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.id.OptionIdentifier;

public class Choice<I extends OptionIdentifier> implements Serializable {
	
	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;

	private I identifier;
	
	private String other;

	public Choice(I identifier) {
		this.identifier = identifier;
	}

	public Choice(String other) {
		this.other = other;
	}

	public I getIdentifier() {
		return identifier;
	}
	
	public String getOther() {
		return other;
	}
	
	public boolean isIdentifer() {
		return identifier != null;
	}
	
	public boolean isOther() {
		return other != null;
	}
	
	public boolean isEmpty() {
		return !isIdentifer() && !isOther();
	}
	
	public String getValue() {
		if (isIdentifer()) {
			return identifier.getId();
		}
		return other;
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