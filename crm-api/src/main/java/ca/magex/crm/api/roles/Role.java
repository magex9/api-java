package ca.magex.crm.api.roles;

import java.io.Serializable;
import java.util.Locale;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.magex.crm.api.services.Crm;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;

public class Role implements Serializable {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;

	private Identifier roleId;
	
	private Identifier groupId;
	
	private String code;
	
	private Status status;
	
	private Localized name;
	
	public Role(Identifier roleId, Identifier groupId, String code, Status status, Localized name) {
		super();
		this.roleId = roleId;
		this.groupId = groupId;
		this.code = code;
		this.status = status;
		this.name = name;
	}
	
	public Identifier getRoleId() {
		return roleId;
	}
	
	public Identifier getGroupId() {
		return groupId;
	}
	
	public String getCode() {
		return code;
	}
	
	public Status getStatus() {
		return status;
	}
	
	public Role withStatus(Status status) {
		return new Role(roleId, groupId, code, status, name);
	}
	
	public Localized getName() {
		return name;
	}
	
	public String getName(Locale locale) {
		return name.get(locale);
	}
	
	public Role withName(Localized name) {
		return new Role(roleId, groupId, code, status, name);
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