package ca.magex.crm.amnesia.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;

import ca.magex.crm.amnesia.AmnesiaDB;
import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.common.User;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.filters.PageBuilder;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Role;
import ca.magex.crm.api.system.Status;

public class AmnesiaPersonService implements CrmPersonService {

	private AmnesiaDB db;
	
	public AmnesiaPersonService(AmnesiaDB db) {
		this.db = db;
	}

	public PersonDetails createPerson(Identifier organizationId, PersonName legalName, MailingAddress address, Communication communication, BusinessPosition unit) {
		return db.savePerson(new PersonDetails(db.generateId(), organizationId, Status.ACTIVE, legalName.getDisplayName(), legalName, address, communication, unit, null));
	}

	public PersonDetails updatePersonName(Identifier personId, PersonName legalName) {
		return db.savePerson(findPersonDetails(personId).withLegalName(legalName));
	}

	public PersonDetails updatePersonAddress(Identifier personId, MailingAddress address) {		
		return db.savePerson(findPersonDetails(personId).withAddress(address));
	}

	public PersonDetails updatePersonCommunication(Identifier personId, Communication communication) {		
		return db.savePerson(findPersonDetails(personId).withCommunication(communication));
	}
	
	@Override
	public PersonDetails updatePersonBusinessUnit(Identifier personId, BusinessPosition position) {		
		return db.savePerson(findPersonDetails(personId).withPosition(position));
	}

	public PersonSummary enablePerson(Identifier personId) {		
		return db.savePerson(findPersonDetails(personId).withStatus(Status.ACTIVE));
	}

	public PersonSummary disablePerson(Identifier personId) {		
		return db.savePerson(findPersonDetails(personId).withStatus(Status.INACTIVE));
	}
	
	@Override
	public PersonSummary findPersonSummary(Identifier personId) {
		return db.findPerson(personId);
	}
	
	public PersonDetails findPersonDetails(Identifier personId) {
		return db.findPerson(personId);
	}
	
	public Stream<PersonDetails> apply(PersonsFilter filter) {
		return db.findByType(PersonDetails.class)
			.filter(p -> StringUtils.isNotBlank(filter.getDisplayName()) ? p.getDisplayName().contains(filter.getDisplayName()) : true)
			.filter(i -> filter.getStatus() != null ? i.getStatus().equals(filter.getStatus()) : true);		
	}
	
	@Override
	public long countPersons(PersonsFilter filter) {
		return apply(filter).count();
	}
	
	public Page<PersonSummary> findPersonSummaries(PersonsFilter filter, Paging paging) {
		return PageBuilder.buildPageFor(apply(filter)
			.map(i -> SerializationUtils.clone(i))
			.sorted(filter.getComparator(paging))
			.collect(Collectors.toList()), paging);
	}
	
	public Page<PersonDetails> findPersonDetails(PersonsFilter filter, Paging paging) {
		return PageBuilder.buildPageFor(apply(filter)
			.map(i -> SerializationUtils.clone(i))
			.sorted(filter.getComparator(paging))
			.collect(Collectors.toList()), paging);
	}

	public PersonDetails addUserRole(Identifier personId, Role role) {
		PersonDetails person = findPersonDetails(personId);		
		/* first time we add a role we need to generate the user name */
		if (person.getUser() == null) {
			String userName = person.getLegalName().getFirstName().substring(0, 1) + "X" + person.getLegalName().getLastName().substring(0, 1) + countPersons(new PersonsFilter());
			person = person.withUser(new User(userName, Arrays.asList(role)));
		}
		else {
			List<Role> roles = new ArrayList<Role>(person.getUser().getRoles());
			roles.add(role);
			person = person.withUser(person.getUser().withRoles(roles));
		}
		return db.savePerson(person);
	}

	public PersonDetails removeUserRole(Identifier personId, Role role) {
		PersonDetails person = db.findPerson(personId);
		List<Role> roles = new ArrayList<Role>(person.getUser().getRoles());
		roles.remove(role);
		return db.savePerson(person.withUser(person.getUser().withRoles(roles)));
	}

}