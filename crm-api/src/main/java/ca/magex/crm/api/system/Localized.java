package ca.magex.crm.api.system;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ca.magex.crm.api.services.Crm;

public class Localized implements Serializable {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	private Map<Locale, String> text;
	
	public Localized(Locale locale, String value) {
		this(Map.of(notNull("Locale", locale), notNull("Value", value)));
	}
	
	public Localized(String code, String englishName, String frenchName) {
		this(Map.of(Lang.ROOT, notNull("Code", code), Lang.ENGLISH, notNull("English", englishName), Lang.FRENCH, notNull("French", frenchName)));
	}
	
	private static <T> T notNull(String name, T obj) {
		if (obj == null)
			throw new IllegalArgumentException(name + " cannot be null");
		return obj;
	}
	
	public Localized(Map<Locale, String> text) {
		this.text = text;
	}
	
	public String getCode() {
		return text.get(Lang.ROOT);
	}
	
	public String getEnglishName() {
		return text.get(Lang.ENGLISH);
	}
	
	public String getFrenchName() {
		return text.get(Lang.FRENCH);
	}

	public String get(Locale locale) {
		return text.get(locale);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"code\":\"");
		sb.append(getCode());
		sb.append("\",\"en\":\"");
		sb.append(getEnglishName());
		sb.append("\",\"fr\":\"");
		sb.append(getFrenchName());
		sb.append("\"}");
		return sb.toString();
	}
	
	public static class Comparator implements java.util.Comparator<Localized> {

		private Locale sortBy;
		
		private List<String> startCodes;
		
		private List<String> endCodes;
		
		public Comparator(Locale locale) {
			this(locale, List.of(), List.of());
		}
		
		public Comparator(Locale sortBy, List<String> startCodes, List<String> endCodes) {
			this.sortBy = sortBy;
			this.startCodes = startCodes;
			this.endCodes = endCodes;
		}
		
		@Override
		public int compare(Localized o1, Localized o2) {
			if (startCodes.contains(o1.getCode()) && !startCodes.contains(o2.getCode()))
				return -1;
			if (startCodes.contains(o2.getCode()) && !startCodes.contains(o1.getCode()))
				return 1;
			if (endCodes.contains(o1.getCode()) && !endCodes.contains(o2.getCode()))
				return 1;
			if (endCodes.contains(o2.getCode()) && !endCodes.contains(o1.getCode()))
				return -1;
			return o1.get(sortBy).compareTo(o2.get(sortBy));
		}
		
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