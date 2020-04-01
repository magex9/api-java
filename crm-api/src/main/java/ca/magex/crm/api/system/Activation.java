package ca.magex.crm.api.system;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Activation implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Identifier identifier;
	
	private LocalDateTime enabled;
	
	private LocalDateTime disabled;
	
	public Activation(Identifier identifier, LocalDateTime enabled, LocalDateTime disabled) {
		this.identifier = identifier;
		this.enabled = enabled;
		this.disabled = disabled;
	}

	public Identifier getIdentifier() {
		return identifier;
	}

	public LocalDateTime getEnabled() {
		return enabled;
	}

	public LocalDateTime getDisabled() {
		return disabled;
	}
	
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

}
