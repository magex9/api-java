package ca.magex.crm.api.policies.basic;

import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.policies.CrmGroupPolicy;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.services.CrmGroupService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public class BasicGroupPolicy implements CrmGroupPolicy {

	private CrmGroupService groups;

	/**
	 * Basic Group Policy handles presence and status checks require for policy approval
	 * 
	 * @param groups
	 */
	public BasicGroupPolicy(CrmGroupService groups) {
		this.groups = groups;
	}

	@Override
	public boolean canCreateGroup() {
		/* always return true */
		return true;
	}

	@Override
	public boolean canViewGroup(String group) {
		try {
			/* can view a group if it exists */
			return groups.findGroupByCode(group) != null;
		} catch (ItemNotFoundException e) {
			throw new ItemNotFoundException("Group Code '" + group + "'");
		}
	}

	@Override
	public boolean canViewGroup(Identifier groupId) {
		/* can view a group if it exists */
		if (groups.findGroup(groupId) == null) {
			throw new ItemNotFoundException("Group ID '" + groupId + "'");
		}
		return true;
	}

	@Override
	public boolean canUpdateGroup(Identifier groupId) {
		/* can update a group if it exists and is active */
		Group group = groups.findGroup(groupId);
		if (group == null) {
			throw new ItemNotFoundException("Group ID '" + groupId + "'");
		}
		return group.getStatus() == Status.ACTIVE;
	}

	@Override
	public boolean canEnableGroup(Identifier groupId) {
		/* can enable a group if it exists */
		if (groups.findGroup(groupId) == null) {
			throw new ItemNotFoundException("Group ID '" + groupId + "'");
		}
		return true;
	}

	@Override
	public boolean canDisableGroup(Identifier groupId) {
		/* can disable a group if it exists */
		if (groups.findGroup(groupId) == null) {
			throw new ItemNotFoundException("Group ID '" + groupId + "'");
		}
		return true;
	}

}