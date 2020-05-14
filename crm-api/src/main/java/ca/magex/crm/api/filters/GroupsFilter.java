package ca.magex.crm.api.filters;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.services.Crm;

public class GroupsFilter implements Serializable {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	public static final List<Sort> SORT_OPTIONS = List.of(
		Sort.by(Direction.ASC, "name"),
		Sort.by(Direction.DESC, "name"),
		Sort.by(Direction.ASC, "code"),
		Sort.by(Direction.DESC, "code")
	);

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
	
	public static List<Sort> getSortOptions() {
		return SORT_OPTIONS;
	}
	
	public static Sort getDefaultSort() {
		return Sort.by(Direction.ASC, "name");
	}

	public static Paging defaultPaging() {
		return new Paging(getDefaultSort());
	}

	public Comparator<Group> getComparator(Paging paging) {
		return paging.new PagingComparator<Group>();
	}
	
}
