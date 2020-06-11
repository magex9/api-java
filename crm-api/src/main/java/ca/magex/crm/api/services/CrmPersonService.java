package ca.magex.crm.api.services;

import javax.validation.constraints.NotNull;

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
			@NotNull Identifier organizationId, 
			@NotNull PersonName name, 
			@NotNull MailingAddress address, 
			@NotNull Communication communication, 
			@NotNull BusinessPosition position) {
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
		@NotNull Identifier organizationId, 
		@NotNull PersonName name, 
		@NotNull MailingAddress address, 
		@NotNull Communication communication, 
		@NotNull BusinessPosition position);

	PersonSummary enablePerson(
		@NotNull Identifier personId
	);

	PersonSummary disablePerson(
		@NotNull Identifier personId
	);

	PersonDetails updatePersonName(
		@NotNull Identifier personId, 
		@NotNull PersonName name
	);

	PersonDetails updatePersonAddress(
		@NotNull Identifier personId, 
		@NotNull MailingAddress address
	);

	PersonDetails updatePersonCommunication(
		@NotNull Identifier personId, 
		@NotNull Communication communication
	);

	PersonDetails updatePersonBusinessPosition(
		@NotNull Identifier personId, 
		@NotNull BusinessPosition position
	);

	PersonSummary findPersonSummary(
		@NotNull Identifier personId
	);

	PersonDetails findPersonDetails(
		@NotNull Identifier personId
	);

	long countPersons(
		@NotNull PersonsFilter filter
	);

	FilteredPage<PersonSummary> findPersonSummaries(
		@NotNull PersonsFilter filter, 
		@NotNull Paging paging
	);
	
	FilteredPage<PersonDetails> findPersonDetails(
		@NotNull PersonsFilter filter, 
		@NotNull Paging paging
	);
	
	default FilteredPage<PersonDetails> findPersonDetails(@NotNull PersonsFilter filter) {
		return findPersonDetails(filter, PersonsFilter.getDefaultPaging());
	}
	
	default FilteredPage<PersonSummary> findPersonSummaries(@NotNull PersonsFilter filter) {
		return findPersonSummaries(filter, PersonsFilter.getDefaultPaging());
	}
	
	default FilteredPage<PersonSummary> findActivePersonSummariesForOrg(@NotNull Identifier organizationId) {
		return findPersonSummaries(new PersonsFilter(organizationId, null, Status.ACTIVE), PersonsFilter.getDefaultPaging());
	}
	
	default PersonsFilter defaultPersonsFilter() {
		return new PersonsFilter();
	};
}