package ca.magex.crm.api.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.RolesFilter;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.system.Status;

public interface CrmRoleService {

	default Role prototypeRole(Identifier groupId, Localized name) {
		return new Role(null, groupId, Status.PENDING, name);
	}

	default Role createRole(Role prototype) {
		return createRole(prototype.getGroupId(), prototype.getName());
	}

	Role createRole(Identifier groupId, Localized name);

	Role findRole(Identifier roleId);

	default Role findRoleByCode(String code) {
		return findRoles(
			defaultRolesFilter().withCode(code), 
			RolesFilter.getDefaultPaging()
		).getSingleItem();
	};

	Role updateRoleName(Identifier roleId, Localized name);

	Role enableRole(Identifier roleId);

	Role disableRole(Identifier roleId);

	FilteredPage<Role> findRoles(RolesFilter filter, Paging paging);

	default FilteredPage<Role> findRoles(RolesFilter filter) {
		return findRoles(filter, defaultRolePaging());
	}
	
	default List<Role> findRoles() {
		return findRoles(
			defaultRolesFilter(), 
			RolesFilter.getDefaultPaging().allItems()
		).getContent();
	}
	
	default RolesFilter defaultRolesFilter() {
		return new RolesFilter();
	}

	default Paging defaultRolePaging() {
		return new Paging(RolesFilter.getSortOptions().get(0));
	}

	static List<Message> validateRole(Crm crm, Role role) {
		List<Message> messages = new ArrayList<Message>();

		// Status
		if (role.getStatus() == null) {
			messages.add(new Message(role.getRoleId(), "error", "status", new Localized(Lang.ENGLISH, "Status is mandatory for a role")));
		} else if (role.getStatus() == Status.PENDING && role.getRoleId() != null) {
			messages.add(new Message(role.getRoleId(), "error", "status", new Localized(Lang.ENGLISH, "Pending statuses should not have identifiers")));
		}

		// Must be a valid role code
		if (StringUtils.isBlank(role.getCode())) {
			messages.add(new Message(role.getRoleId(), "error", "code", new Localized(Lang.ENGLISH, "Role code must not be blank")));
		} else if (!role.getCode().matches("[A-Z0-9_]{1,20}")) {
			messages.add(new Message(role.getRoleId(), "error", "code", new Localized(Lang.ENGLISH, "Role code must match: [A-Z0-9_]{1,20}")));
		}

		// Make sure the existing code didn't change
		if (role.getRoleId() != null) {
			try {
				if (!crm.findRole(role.getRoleId()).getCode().equals(role.getCode())) {
					messages.add(new Message(role.getRoleId(), "error", "code", new Localized(Lang.ENGLISH, "Role code must not change during updates")));
				}
			} catch (ItemNotFoundException e) {
				/* no existing role, so don't care */
			}
		}

		// Make sure the code is unique
		FilteredPage<Role> roles = crm.findRoles(crm.defaultRolesFilter().withCode(role.getCode()), RolesFilter.getDefaultPaging().allItems());
		for (Role existing : roles.getContent()) {
			if (!existing.getRoleId().equals(role.getRoleId())) {
				messages.add(new Message(role.getRoleId(), "error", "code", new Localized(Lang.ENGLISH, "Duplicate code found in another role: " + existing.getGroupId())));
			}
		}

		// Make sure there is an English description
		if (StringUtils.isBlank(role.getName(Lang.ENGLISH))) {
			messages.add(new Message(role.getRoleId(), "error", "englishName", new Localized(Lang.ENGLISH, "An English description is required")));
		} else if (role.getName(Lang.ENGLISH).length() > 50) {
			messages.add(new Message(role.getRoleId(), "error", "englishName", new Localized(Lang.ENGLISH, "English name must be 50 characters or less")));
		}

		// Make sure there is a French description
		if (StringUtils.isBlank(role.getName(Lang.FRENCH))) {
			messages.add(new Message(role.getRoleId(), "error", "frenchName", new Localized(Lang.ENGLISH, "An French description is required")));
		} else if (role.getName(Lang.FRENCH).length() > 50) {
			messages.add(new Message(role.getRoleId(), "error", "frenchName", new Localized(Lang.ENGLISH, "French name must be 50 characters or less")));
		}

		return messages;
	}

}