package ca.magex.crm.api.services.basic;

import ca.magex.crm.api.filters.GroupsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.repositories.CrmRepositories;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.services.CrmGroupService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;

public class BasicGroupService implements CrmGroupService {

	private CrmRepositories repos;

	public BasicGroupService(CrmRepositories repos) {
		this.repos = repos;
	}

	@Override
	public Group createGroup(Localized name) {
		return repos.saveGroup(new Group(repos.generateId(), Status.ACTIVE, name));
	}

	@Override
	public Group findGroup(Identifier groupId) {
		return repos.findGroup(groupId);
	}

	@Override
	public Group updateGroupName(Identifier groupId, Localized name) {
		Group group = repos.findGroup(groupId);
		if (group == null) {
			return null;
		}
		return repos.saveGroup(group.withName(name));
	}

	@Override
	public Group enableGroup(Identifier groupId) {
		Group group = repos.findGroup(groupId);
		if (group == null) {
			return null;
		}
		return repos.saveGroup(group.withStatus(Status.ACTIVE));
	}

	@Override
	public Group disableGroup(Identifier groupId) {
		Group group = repos.findGroup(groupId);
		if (group == null) {
			return null;
		}
		return repos.saveGroup(group.withStatus(Status.INACTIVE));
	}

	@Override
	public FilteredPage<Group> findGroups(GroupsFilter filter, Paging paging) {
		return repos.findGroups(filter, paging);
	}

}