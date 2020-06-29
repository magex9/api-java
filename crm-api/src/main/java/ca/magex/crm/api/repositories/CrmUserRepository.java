package ca.magex.crm.api.repositories;

import ca.magex.crm.api.crm.User;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.store.CrmStore;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.id.UserIdentifier;

/**
 * Repository interface used for saving/retrieving an Organization
 * 
 * @author Jonny
 */
public interface CrmUserRepository {

	/**
	 * returns the next identifier to be assigned to a new User
	 * 
	 * @return
	 */
	default UserIdentifier generateUserId() {
		return new UserIdentifier(CrmStore.generateId());
	}
		
	/**
	 * Save the given user to the repository
	 * 
	 * @param user
	 * @return
	 */
	public User saveUser(User user);
	
	/**
	 * returns the user details associated with the given userId, 
	 * or null if the userId does not exist
	 * 
	 * @param userId
	 * @return
	 */
	public User findUser(UserIdentifier userId);
	
	/**
	 * returns the paged results with the user details for any user that matches the given filter
	 * 
	 * @param filter
	 * @param paging
	 * @return
	 */
	public FilteredPage<User> findUsers(UsersFilter filter, Paging paging); 

	/**
	 * returns the number of users that match the given filter
	 * 
	 * @param filter
	 * @return
	 */
	public long countUsers(UsersFilter filter); 
}