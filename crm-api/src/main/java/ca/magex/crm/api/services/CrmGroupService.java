package ca.magex.crm.api.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.GroupsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Message;
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

	static List<Message> validateGroup(Crm crm, Group group) {
		List<Message> messages = new ArrayList<Message>();

		// Status
		if (group.getStatus() == null) {
			messages.add(new Message(group.getGroupId(), "error", "status", crm.getDictionary().getMessage("validation.group.status.required")));
		} else if (group.getStatus() == Status.PENDING && group.getGroupId() != null) {
			messages.add(new Message(group.getGroupId(), "error", "status", crm.getDictionary().getMessage("validation.group.status.pending")));
		}

		// Must be a valid group code
		if (StringUtils.isBlank(group.getCode())) {
			messages.add(new Message(group.getGroupId(), "error", "code", new Localized(Lang.ENGLISH, "Group code must not be blank")));
		} else if (!group.getCode().matches("[A-Z0-9_]{1,20}")) {
			messages.add(new Message(group.getGroupId(), "error", "code", new Localized(Lang.ENGLISH, "Group code must match: [A-Z0-9_]{1,20}")));
		}

		// Make sure the existing code didn't change
		if (group.getGroupId() != null) {
			try {			
				if (!crm.findGroup(group.getGroupId()).getCode().equals(group.getCode())) {
					messages.add(new Message(group.getGroupId(), "error", "code", new Localized(Lang.ENGLISH, "Group code must not change during updates")));
				}
			} catch (ItemNotFoundException e) {
				/* no existing group, so don't care */
			}
		}

		// Make sure the code is unique
		FilteredPage<Group> groups = crm.findGroups(crm.defaultGroupsFilter().withCode(group.getCode()), GroupsFilter.getDefaultPaging().allItems());
		for (Group existing : groups.getContent()) {
			if (!existing.getGroupId().equals(group.getGroupId())) {
				messages.add(new Message(group.getGroupId(), "error", "code", new Localized(Lang.ENGLISH, "Duplicate code found in another group: " + existing.getGroupId())));
			}
		}

		// Make sure there is an English description
		if (StringUtils.isBlank(group.getName(Lang.ENGLISH))) {
			messages.add(new Message(group.getGroupId(), "error", "englishName", new Localized(Lang.ENGLISH, "An English description is required")));
		} else if (group.getName(Lang.ENGLISH).length() > 50) {
			messages.add(new Message(group.getGroupId(), "error", "englishName", new Localized(Lang.ENGLISH, "English name must be 50 characters or less")));
		}

		// Make sure there is a French description
		if (StringUtils.isBlank(group.getName(Lang.FRENCH))) {
			messages.add(new Message(group.getGroupId(), "error", "frenchName", new Localized(Lang.ENGLISH, "An French description is required")));
		} else if (group.getName(Lang.FRENCH).length() > 50) {
			messages.add(new Message(group.getGroupId(), "error", "frenchName", new Localized(Lang.ENGLISH, "French name must be 50 characters or less")));
		}

		return messages;
	}
	
}