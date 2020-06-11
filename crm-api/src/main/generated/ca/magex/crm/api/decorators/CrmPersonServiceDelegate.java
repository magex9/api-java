package ca.magex.crm.api.decorators;

import ca.magex.crm.api.services.CrmPersonService;

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

public class CrmPersonServiceDelegate implements CrmPersonService {
	
	private CrmPersonService delegate;
	
	public CrmPersonServiceDelegate(CrmPersonService delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public PersonDetails prototypePerson(Identifier organizationId, PersonName name, MailingAddress address, Communication communication, BusinessPosition position) {
		return delegate.prototypePerson(organizationId, name, address, communication, position);
	}
	
	@Override
	public PersonDetails createPerson(PersonDetails prototype) {
		return delegate.createPerson(prototype);
	}
	
	@Override
	public PersonDetails createPerson(Identifier organizationId, PersonName name, MailingAddress address, Communication communication, BusinessPosition position) {
		return delegate.createPerson(organizationId, name, address, communication, position);
	}
	
	@Override
	public PersonSummary enablePerson(Identifier personId) {
		return delegate.enablePerson(personId);
	}
	
	@Override
	public PersonSummary disablePerson(Identifier personId) {
		return delegate.disablePerson(personId);
	}
	
	@Override
	public PersonDetails updatePersonName(Identifier personId, PersonName name) {
		return delegate.updatePersonName(personId, name);
	}
	
	@Override
	public PersonDetails updatePersonAddress(Identifier personId, MailingAddress address) {
		return delegate.updatePersonAddress(personId, address);
	}
	
	@Override
	public PersonDetails updatePersonCommunication(Identifier personId, Communication communication) {
		return delegate.updatePersonCommunication(personId, communication);
	}
	
	@Override
	public PersonDetails updatePersonBusinessPosition(Identifier personId, BusinessPosition position) {
		return delegate.updatePersonBusinessPosition(personId, position);
	}
	
	@Override
	public PersonSummary findPersonSummary(Identifier personId) {
		return delegate.findPersonSummary(personId);
	}
	
	@Override
	public PersonDetails findPersonDetails(Identifier personId) {
		return delegate.findPersonDetails(personId);
	}
	
	@Override
	public long countPersons(PersonsFilter filter) {
		return delegate.countPersons(filter);
	}
	
	@Override
	public FilteredPage<PersonSummary> findPersonSummaries(PersonsFilter filter, Paging paging) {
		return delegate.findPersonSummaries(filter, paging);
	}
	
	@Override
	public FilteredPage<PersonDetails> findPersonDetails(PersonsFilter filter, Paging paging) {
		return delegate.findPersonDetails(filter, paging);
	}
	
	@Override
	public FilteredPage<PersonDetails> findPersonDetails(PersonsFilter filter) {
		return delegate.findPersonDetails(filter);
	}
	
	@Override
	public FilteredPage<PersonSummary> findPersonSummaries(PersonsFilter filter) {
		return delegate.findPersonSummaries(filter);
	}
	
	@Override
	public FilteredPage<PersonSummary> findActivePersonSummariesForOrg(Identifier organizationId) {
		return delegate.findActivePersonSummariesForOrg(organizationId);
	}
	
	@Override
	public PersonsFilter defaultPersonsFilter() {
		return delegate.defaultPersonsFilter();
	}
	
}
