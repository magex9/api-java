package ca.magex.crm.api.repositories;

import ca.magex.crm.api.filters.GroupsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;

public interface CrmGroupRepository {

	public FilteredPage<Group> findGroups(GroupsFilter filter, Paging paging); 
	
	public long countGroups(GroupsFilter filter);

	public Group findGroup(Identifier groupId);
	
	public Group saveGroup(Group group);

}
