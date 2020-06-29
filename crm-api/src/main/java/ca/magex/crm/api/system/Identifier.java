package ca.magex.crm.api.system;

import java.io.Serializable;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.utils.StringEscapeUtils;

public class Identifier implements CharSequence, Serializable {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	public static final String CONTEXT = "/";
	
	public static final String COMPONENT_REGEX = "[A-Za-z0-9/]+";
		
	private CharSequence id;
	
	/**
	 * Creates a new Identifier by validating the ID and appending the Context as Required
	 * 
	 * @param id
	 */
	protected Identifier(CharSequence id) {
		CharSequence fullId = (Pattern.matches(COMPONENT_REGEX, id) && !StringUtils.startsWith(id, "/")) ? getContext() + id : id;
		if (StringUtils.isBlank(fullId)) {
			throw new IllegalArgumentException("Id cannot be blank");
		}
		String pattern = StringEscapeUtils.escapeRegex(getContext()) + "[A-Za-z0-9/]+";
		if (!Pattern.matches(pattern, fullId)) {
			throw new IllegalArgumentException("Id '" + fullId + "' must match the pattern " + pattern);
		}
		this.id = fullId;
	}
	
	/**
	 * Returns the context associated with the given identifier
	 * 
	 * @return
	 */
	public String getContext() {
		return CONTEXT;
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
		return id == null ? null : id.toString();
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