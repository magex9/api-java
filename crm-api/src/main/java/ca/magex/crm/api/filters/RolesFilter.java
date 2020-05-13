package ca.magex.crm.api.filters;

import java.io.Serializable;
import java.util.Comparator;

import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.system.Identifier;

public class RolesFilter implements Serializable {

	private static final long serialVersionUID = 1L;

	private Identifier groupId;
	
	private String role;

	public RolesFilter(Identifier groupId, String role) {
		this.groupId = groupId;
		this.role = role;
	}

	public RolesFilter() {
		this(null, null);
	}
	
	public Identifier getGroupId() {
		return groupId;
	}
	
	public RolesFilter withGroupId(Identifier groupId) {
		return new RolesFilter(groupId, role);
	}

	public String getRole() {
		return role;
	}
	
	public RolesFilter withRole(String role) {
		return new RolesFilter(groupId, role);
	}

	public Comparator<Role> getComparator(Paging paging) {
		return paging.new PagingComparator<Role>();
	}

}
