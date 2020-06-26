package ca.magex.crm.api.system;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ca.magex.crm.api.Crm;

public class Identifier implements CharSequence, Serializable {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	public static final Identifier UNDEFINED = new Identifier("/type/undefined");
	
	public static final String PATTERN = "/([a-z/]+)/[A-Za-z0-9]+";
	
	private String id;

	public Identifier(String context, String id) {
		this(context + "/" + id);
	}
	
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
	public int length() {
		return id.length();
	}

	@Override
	public char charAt(int index) {
		return id.charAt(index);
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return id.subSequence(start, end);
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