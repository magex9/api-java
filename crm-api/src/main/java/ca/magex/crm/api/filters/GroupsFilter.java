package ca.magex.crm.api.filters;

import java.io.Serializable;
import java.util.Comparator;

import ca.magex.crm.api.roles.Group;

public class GroupsFilter implements Serializable {

	private static final long serialVersionUID = 1L;

	private String group;

	public GroupsFilter(String group) {
		this.group = group;
	}

	public GroupsFilter() {
		this(null);
	}
	
	public String getGroup() {
		return group;
	}
	
	public GroupsFilter withRole(String group) {
		return new GroupsFilter(group);
	}

	public Comparator<Group> getComparator(Paging paging) {
		return paging.new PagingComparator<Group>();
	}

}
