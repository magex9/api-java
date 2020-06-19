package ca.magex.crm.api.system;

import java.io.Serializable;
import java.util.Locale;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.magex.crm.api.Crm;

public class Lookup implements Serializable {
	
	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	private Identifier lookupId;
	
	private Status status;
	
	private Boolean mutable;
	
	private Localized name;
	
	private Option parent;
	
	public Lookup(Identifier lookupId, Status status, Boolean mutable, Localized name, Option parent) {
		super();
		this.lookupId = lookupId;
		this.status = status;
		this.mutable = mutable;
		this.name = name;
		this.parent = parent;
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
	
	public Lookup withStatus(Status status) {
		return new Lookup(lookupId, status, mutable, name, parent);
	}
	
	public Boolean isMutable() {
		return mutable;
	}
	
	public Localized getName() {
		return name;
	}
	
	public String getName(Locale locale) {
		return name.get(locale);
	}
	
	public Lookup withName(Localized name) {
		return new Lookup(lookupId, status, mutable, name, parent);
	}
	
	public Option getParent() {
		return parent;
	}
	
	public Lookup withParent(Option parent) {
		return new Lookup(lookupId, status, mutable, name, parent);
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