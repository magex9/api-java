package ca.magex.crm.api.system;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.id.PhraseIdentifier;
import ca.magex.crm.api.system.id.MessageTypeIdentifier;

public class Message implements Serializable {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	private Identifier identifier;
	
	private MessageTypeIdentifier type;
	
	private String path;
	
	private String value;
	
	private Choice<PhraseIdentifier> reason;

	public Message(Identifier identifier, MessageTypeIdentifier type, String path, String value, String reason) {
		this(identifier, type, path, value, new Choice<>(reason));
	}

	public Message(Identifier identifier, MessageTypeIdentifier type, String path, String value, PhraseIdentifier reason) {
		this(identifier, type, path, value, new Choice<>(reason));
	}
	
	public Message(Identifier identifier, MessageTypeIdentifier type, String path, String value, Choice<PhraseIdentifier> reason) {
		super();
		this.identifier = identifier;
		this.type = type;
		this.path = path;
		this.value = value;
		this.reason = reason;
	}

	public Identifier getIdentifier() {
		return identifier;
	}

	public MessageTypeIdentifier getType() {
		return type;
	}

	public String getPath() {
		return path;
	}
	
	public String getValue() {
		return value;
	}	

	public Choice<PhraseIdentifier> getReason() {
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
