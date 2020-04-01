package ca.magex.crm.api.system;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Identifier implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String id;
	
	public Identifier(String id) {
		this.id = id;
	}
	
	public Identifier(Identifier identifier) {
		this.id = identifier.id;
	}
		
	@Override
	public String toString() {
		return id;
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
