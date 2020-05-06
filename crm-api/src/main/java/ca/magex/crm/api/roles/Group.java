package ca.magex.crm.api.roles;

import java.io.Serializable;
import java.util.Locale;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;

public class Group implements Serializable {

	private static final long serialVersionUID = 1L;

	private Identifier groupId;
	
	private Status status;
	
	private Localized name;

	public Group(Identifier groupId, Status status, Localized name) {
		super();
		this.groupId = groupId;
		this.status = status;
		this.name = name;
	}
	
	public Identifier getGroupId() {
		return groupId;
	}
	
	public Status getStatus() {
		return status;
	}
	
	public Group withStatus(Status status) {
		return new Group(groupId, status, name);
	}
	
	public String getName(Locale locale) {
		return name.get(locale);
	}
	
	public Group withName(Localized name) {
		return new Group(groupId, status, name);
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