package ca.magex.crm.api.services;

import java.util.List;
import java.util.stream.Collectors;

import ca.magex.crm.api.filters.GroupsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;

public interface CrmGroupService {

	default Group prototypeGroup(Localized name) {
		return new Group(null, Status.PENDING, name);
	}

	default Group createGroup(Group group) {
		return createGroup(group.getName());
	}

	Group createGroup(Localized name);

	Group findGroup(Identifier groupId);

	default Group findGroupByCode(String code) {
		return findGroups(
			defaultGroupsFilter().withCode(code), 
			GroupsFilter.getDefaultPaging()
		).getSingleItem();
	};

	Group updateGroupName(Identifier groupId, Localized name);

	Group enableGroup(Identifier groupId);

	Group disableGroup(Identifier groupId);

	default GroupsFilter defaultGroupsFilter() {
		return new GroupsFilter();
	};

	FilteredPage<Group> findGroups(GroupsFilter filter, Paging paging);
	
	default FilteredPage<Group> findGroups(GroupsFilter filter) {
		return findGroups(filter, defaultGroupPaging());
	}
	
	default List<String> findActiveGroupCodes() {
		return findGroups(
			defaultGroupsFilter().withStatus(Status.ACTIVE), 
			GroupsFilter.getDefaultPaging().allItems()
		).stream().map(g -> g.getCode()).collect(Collectors.toList());
	}

	default Paging defaultGroupPaging() {
		return new Paging(GroupsFilter.getSortOptions().get(0));
	}
	
}