package ca.magex.crm.api.repositories;

import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;

public interface CrmPersonRepository {
	
	public Identifier generatePersonId();

	public FilteredPage<PersonDetails> findPersonDetails(PersonsFilter filter, Paging paging); 

	public FilteredPage<PersonSummary> findPersonSummary(PersonsFilter filter, Paging paging); 

	public long countPersons(PersonsFilter filter); 
	
	public PersonDetails findPersonDetails(Identifier personId);

	public PersonSummary findPersonSummary(Identifier personId);

	public PersonDetails savePersonDetails(PersonDetails person);
	
}
