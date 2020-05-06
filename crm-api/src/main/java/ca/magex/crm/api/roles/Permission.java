package ca.magex.crm.api.roles;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public class Permission implements Serializable {

	private static final long serialVersionUID = 1L;

	private Identifier permissionId;
	
	private Identifier userId;
	
	private Identifier roleId;

	private Status status;
	
	public Permission(Identifier permissionId, Identifier userId, Identifier roleId, Status status) {
		super();
		this.permissionId = permissionId;
		this.userId = userId;
		this.roleId = roleId;
		this.status = status;
	}
	
	public Identifier getPermissionId() {
		return permissionId;
	}

	public Identifier getUserId() {
		return userId;
	}

	public Identifier getRoleId() {
		return roleId;
	}

	public Status getStatus() {
		return status;
	}
	
	public Permission withStatus(Status status) {
		return new Permission(permissionId, userId, roleId, status);
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
