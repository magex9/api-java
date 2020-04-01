package ca.magex.crm.api.system;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Message implements Serializable {

	private static final long serialVersionUID = 1L;

	private Identifier identifier;
	
	private String type;
	
	private String path;
	
	private String message;

	public Message(Identifier identifier, String type, String path, String message) {
		super();
		this.identifier = identifier;
		this.type = type;
		this.path = path;
		this.message = message;
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

	public String getMessage() {
		return message;
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
