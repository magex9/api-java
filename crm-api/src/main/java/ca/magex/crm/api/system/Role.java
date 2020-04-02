package ca.magex.crm.api.system;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Role implements Serializable {

	private static final long serialVersionUID = 1L;

	private String code;
	
	private Map<Locale, String> names;

	public Role(String code, String english, String french) {
		super();
		this.code = code;
		this.names = new HashMap<Locale, String>();
		this.names.put(Lang.ENGLISH, english);
		this.names.put(Lang.FRENCH, french);
	}
	
	public String getCode() {
		return code;
	}
	
	public String getName(Locale locale) {
		return names.get(locale);
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
