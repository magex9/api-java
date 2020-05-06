package ca.magex.crm.api.system;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Localized implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Map<Locale, String> text;
	
	public Localized(String english, String french) {
		this(Map.of(Lang.ENGLISH, english, Lang.FRENCH, french));
	}
	
	public Localized(String english) {
		this(Map.of(Lang.ENGLISH, english));
	}
	
	public Localized(Map<Locale, String> text) {
		this.text = text;
	}

	public String get(Locale locale) {
		return text.get(locale);
	}
	
	@Override
	public String toString() {
		return text.toString();
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
