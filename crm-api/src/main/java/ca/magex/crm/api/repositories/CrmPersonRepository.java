package ca.magex.crm.api.repositories;

import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.store.CrmStore;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.id.PersonIdentifier;

/**
 * Repository interface used for saving/retrieving a Person
 * 
 * @author Jonny
 */
public interface CrmPersonRepository {
	
	/**
	 * returns the next identifier to be assigned to a new Person
	 * 
	 * @return
	 */
	default PersonIdentifier generatePersonId() {
		return new PersonIdentifier(CrmStore.generateId());
	}
	
	/**
	 * Save the given person to the repository
	 * 
	 * @param person
	 * @return
	 */
	public PersonDetails savePersonDetails(PersonDetails person);

	/**
	 * returns the full person details associated with the given personId, 
	 * or null if the personId does not exist
	 * 
	 * @param personId
	 * @return
	 */
	public PersonDetails findPersonDetails(PersonIdentifier personId);

	/**
	 * returns the person summary associated with the given personId, 
	 * or null if the personId does not exist
	 * 
	 * @param personId
	 * @return
	 */
	public PersonSummary findPersonSummary(PersonIdentifier personId);
	
	/**
	 * returns the paged results with the full person details for any person that matches the given filter
	 * 
	 * @param filter
	 * @param paging
	 * @return
	 */
	public FilteredPage<PersonDetails> findPersonDetails(PersonsFilter filter, Paging paging); 

	/**
	 * returns the paged results with the person summary for any person that matches the given filter
	 * 
	 * @param filter
	 * @param paging
	 * @return
	 */
	public FilteredPage<PersonSummary> findPersonSummary(PersonsFilter filter, Paging paging); 

	/**
	 * returns the number of persons that match the given filter
	 * 
	 * @param filter
	 * @return
	 */
	public long countPersons(PersonsFilter filter); 
}
