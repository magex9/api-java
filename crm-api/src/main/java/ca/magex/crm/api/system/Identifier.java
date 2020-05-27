package ca.magex.crm.api.system;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ca.magex.crm.api.services.Crm;

public class Identifier implements Serializable {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	public static final Identifier UNDEFINED = new Identifier("undefined");
	
	public static final String PATTERN = "[A-Za-z0-9]+";
	
	private String id;
	
	public Identifier(String id) {
		if (StringUtils.isBlank(id))
			throw new IllegalArgumentException("Id cannot be blank");
		if (!id.matches(PATTERN))
			throw new IllegalArgumentException("Id must match the pattern " + PATTERN);
		this.id = id;
	}
	
	public Identifier(Identifier identifier) {
		this(identifier.id);
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