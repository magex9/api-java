package ca.magex.crm.api.filters;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.services.Crm;
import ca.magex.crm.api.system.Identifier;

public class RolesFilter implements Serializable {
	
	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;

	public static final List<Sort> SORT_OPTIONS = List.of(
		Sort.by(Order.asc("name")),
		Sort.by(Order.desc("name")),
		Sort.by(Order.asc("code")),
		Sort.by(Order.desc("code"))
	);
	
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

	public static List<Sort> getSortOptions() {
		return SORT_OPTIONS;
	}
	
	public static Sort getDefaultSort() {
		return Sort.by(Direction.ASC, "name");
	}

	public static Paging getDefaultPaging() {
		return new Paging(getDefaultSort());
	}

	public Comparator<Role> getComparator(Paging paging) {
		return paging.new PagingComparator<Role>();
	}

}
