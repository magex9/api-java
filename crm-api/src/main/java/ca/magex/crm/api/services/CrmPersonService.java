package ca.magex.crm.api.services;

import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public interface CrmPersonService {
	
	default PersonDetails prototypePerson(
			Identifier organizationId, 
			PersonName name, 
			MailingAddress address, 
			Communication communication, 
			BusinessPosition position) {
		return new PersonDetails(null, organizationId, Status.PENDING, name.getDisplayName(), name, address, communication, position);
	};
	
	default PersonDetails createPerson(PersonDetails prototype) {
		return createPerson(
			prototype.getOrganizationId(), 
			prototype.getLegalName(), 
			prototype.getAddress(),
			prototype.getCommunication(),
			prototype.getPosition());
	}

	PersonDetails createPerson(
		Identifier organizationId, 
		PersonName name, 
		MailingAddress address, 
		Communication communication, 
		BusinessPosition position);

	PersonSummary enablePerson(
		Identifier personId
	);

	PersonSummary disablePerson(
		Identifier personId
	);

	PersonDetails updatePersonName(
		Identifier personId, 
		PersonName name
	);

	PersonDetails updatePersonAddress(
		Identifier personId, 
		MailingAddress address
	);

	PersonDetails updatePersonCommunication(
		Identifier personId, 
		Communication communication
	);

	PersonDetails updatePersonBusinessPosition(
		Identifier personId, 
		BusinessPosition position
	);

	PersonSummary findPersonSummary(
		Identifier personId
	);

	PersonDetails findPersonDetails(
		Identifier personId
	);

	long countPersons(
		PersonsFilter filter
	);

	FilteredPage<PersonSummary> findPersonSummaries(
		PersonsFilter filter, 
		Paging paging
	);
	
	FilteredPage<PersonDetails> findPersonDetails(
		PersonsFilter filter, 
		Paging paging
	);
	
	default FilteredPage<PersonDetails> findPersonDetails(PersonsFilter filter) {
		return findPersonDetails(filter, PersonsFilter.getDefaultPaging());
	}
	
	default FilteredPage<PersonSummary> findPersonSummaries(PersonsFilter filter) {
		return findPersonSummaries(filter, PersonsFilter.getDefaultPaging());
	}
	
	default FilteredPage<PersonSummary> findActivePersonSummariesForOrg(Identifier organizationId) {
		return findPersonSummaries(new PersonsFilter(organizationId, null, Status.ACTIVE), PersonsFilter.getDefaultPaging());
	}
	
	default PersonsFilter defaultPersonsFilter() {
		return new PersonsFilter();
	};
}