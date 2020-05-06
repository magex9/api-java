package ca.magex.crm.api.filters;

import java.io.Serializable;
import java.util.Comparator;

import ca.magex.crm.api.roles.Permission;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public class PermissionsFilter implements Serializable {

	private static final long serialVersionUID = 1L;

	private Identifier userId;

	private Identifier roleId;

	private Status status;

	public PermissionsFilter(Identifier userId, Identifier roleId, Status status) {
		this.userId = userId;
		this.roleId = roleId;
		this.status = status;
	}

	public PermissionsFilter() {
		this(null, null, null);
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

	public Comparator<Permission> getComparator(Paging paging) {
		return paging.new PagingComparator<Permission>();
	}

}
