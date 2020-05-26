package ca.magex.crm.api.lookup;

import java.util.Locale;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.magex.crm.api.services.Crm;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;

public class Salutation extends Localized implements CrmLookupItem {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;

	public Salutation(String code, String english, String french) {
		super(code, english, french);
	}
	
	@Override
	public String getCode() {
		return get(Lang.ROOT);
	}
	
	@Override
	public String getName(Locale locale) {
		return get(locale);
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
