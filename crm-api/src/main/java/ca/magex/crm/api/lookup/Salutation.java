package ca.magex.crm.api.lookup;

import java.util.Locale;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.magex.crm.api.system.Localized;

public class Salutation implements CrmLookupItem {

	private static final long serialVersionUID = 1L;

	private String code;
	
	private Localized name;

	public Salutation(String code, String english, String french) {
		super();
		this.code = code;
		this.name = new Localized(english, french);
	}
	
	@Override
	public String getCode() {
		return code;
	}
	
	@Override
	public String getName(Locale locale) {
		return name.get(locale);
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
