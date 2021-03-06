package ca.magex.crm.api.system;

import java.io.Serializable;

import javax.validation.constraints.AssertFalse;

import org.apache.commons.lang3.StringUtils;
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
	
	public Choice() {
		this.identifier = null;
		this.other = null;
	}

	public Choice(I identifier) {
		this.identifier = identifier;
		this.other = null;
	}

	public Choice(String other) {
		this.identifier = null;
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
		return StringUtils.isNotBlank(other);
	}
	
	public boolean isEmpty() {
		return !isIdentifer() && !isOther();
	}
	
	public String getValue() {
		if (isIdentifer()) {
			return identifier.getCode();
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