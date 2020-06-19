package ca.magex.crm.api.system;

import java.io.Serializable;
import java.util.Locale;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.magex.crm.api.Crm;

public class Option implements Serializable {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;

	private Identifier optionId;
	
	private Identifier lookupId;
	
	private Status status;
	
	private Localized name;
	
	public Option(Identifier optionId, Identifier lookupId, Status status, Localized name) {
		super();
		this.optionId = optionId;
		this.lookupId = lookupId;
		this.status = status;
		this.name = name;
	}
	
	public Identifier getOptionId() {
		return optionId;
	}
	
	public Identifier getLookupId() {
		return lookupId;
	}
	
	public String getCode() {
		return name.get(Lang.ROOT);
	}
	
	public Status getStatus() {
		return status;
	}
	
	public Option withStatus(Status status) {
		return new Option(optionId, lookupId, status, name);
	}
	
	public Localized getName() {
		return name;
	}
	
	public String getName(Locale locale) {
		return name.get(locale);
	}
	
	public Option withName(Localized name) {
		return new Option(optionId, lookupId, status, name);
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
