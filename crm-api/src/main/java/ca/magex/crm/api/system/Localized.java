package ca.magex.crm.api.system;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ca.magex.crm.api.services.Crm;

public class Localized implements Serializable {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	private Map<Locale, String> text;
	
	public Localized(Locale locale, String value) {
		this(Map.of(locale, value));
	}
	
	public Localized(String code, String english, String french) {
		this(Map.of(Lang.ROOT, code, Lang.ENGLISH, english, Lang.FRENCH, french));
	}
	
	public Localized(Map<Locale, String> text) {
		this.text = text;
	}
	
	public String getCode() {
		return text.get(Lang.ROOT);
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
