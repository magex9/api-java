package ca.magex.crm.api.repositories;

import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.store.CrmStore;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;

public interface CrmUserRepository {

	public static final String CONTEXT = "/users";
	
	default Identifier generateUserId() {
		return CrmStore.generateId(CONTEXT);
	}
	
	public FilteredPage<User> findUsers(UsersFilter filter, Paging paging); 

	public long countUsers(UsersFilter filter); 
	
	public User findUser(Identifier userId);

	public User saveUser(User user);
	
}
