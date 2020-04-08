package ca.magex.crm.api.lookup;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.magex.crm.api.system.Lang;

public class Country implements CrmLookupItem {

	private static final long serialVersionUID = 1L;

	private String code;
	
	private Map<Locale, String> names;

	public Country(String code, String english, String french) {
		super();
		this.code = code;
		this.names = new HashMap<Locale, String>();
		this.names.put(Lang.ENGLISH, english);
		this.names.put(Lang.FRENCH, french);
	}
	
	@Override
	public String getCode() {
		return code;
	}
	
	@Override
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