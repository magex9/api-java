package ca.magex.crm.api.system;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.magex.crm.api.Crm;

public class Message implements Serializable {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	private Identifier identifier;
	
	private String type;
	
	private String path;
	
	private Localized reason;

	public Message(Identifier identifier, String type, String path, Localized reason) {
		super();
		this.identifier = identifier;
		this.type = type;
		this.path = path;
		this.reason = reason;
	}

	public Identifier getIdentifier() {
		return identifier;
	}

	public String getType() {
		return type;
	}

	public String getPath() {
		return path;
	}

	public Localized getReason() {
		return reason;
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
