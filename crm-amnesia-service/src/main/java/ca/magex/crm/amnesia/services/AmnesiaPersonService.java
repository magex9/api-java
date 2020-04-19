package ca.magex.crm.amnesia.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
import ca.magex.crm.api.services.CrmPasswordService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Role;
import ca.magex.crm.api.system.Status;

@Service
@Primary
public class AmnesiaPersonService implements CrmPersonService {

	@Autowired private AmnesiaDB db;
	@Autowired private CrmPasswordService passwordService;
	@Autowired(required=false) private PasswordEncoder passwordEncoder;

	@Override
	public PersonDetails createPerson(Identifier organizationId, PersonName legalName, MailingAddress address, Communication communication, BusinessPosition unit) {
		return db.savePerson(new PersonDetails(db.generateId(), organizationId, Status.ACTIVE, legalName.getDisplayName(), legalName, address, communication, unit, new User(generateUserName(legalName), Collections.emptyList())));
	}

	@Override
	public PersonDetails updatePersonName(Identifier personId, PersonName legalName) {
		return db.savePerson(findPersonDetails(personId).withLegalName(legalName).withDisplayName(legalName.getDisplayName()));
	}

	@Override
	public PersonDetails updatePersonAddress(Identifier personId, MailingAddress address) {
		return db.savePerson(findPersonDetails(personId).withAddress(address));
	}

	@Override
	public PersonDetails updatePersonCommunication(Identifier personId, Communication communication) {
		return db.savePerson(findPersonDetails(personId).withCommunication(communication));
	}

	@Override
	public PersonDetails updatePersonBusinessPosition(Identifier personId, BusinessPosition position) {
		return db.savePerson(findPersonDetails(personId).withPosition(position));
	}

	@Override
	public PersonSummary enablePerson(Identifier personId) {
		return db.savePerson(findPersonDetails(personId).withStatus(Status.ACTIVE));
	}

	@Override
	public PersonSummary disablePerson(Identifier personId) {
		return db.savePerson(findPersonDetails(personId).withStatus(Status.INACTIVE));
	}

	@Override
	public PersonSummary findPersonSummary(Identifier personId) {
		return db.findPerson(personId);
	}

	@Override
	public PersonDetails findPersonDetails(Identifier personId) {
		return db.findPerson(personId);
	}

	@Override
	public long countPersons(PersonsFilter filter) {
		return applyFilter(filter).count();
	}

	@Override
	public Page<PersonSummary> findPersonSummaries(PersonsFilter filter, Paging paging) {
		return PageBuilder.buildPageFor(applyFilter(filter)
				.map(i -> SerializationUtils.clone(i))
				.sorted(filter.getComparator(paging))
				.collect(Collectors.toList()), paging);
	}

	@Override
	public Page<PersonDetails> findPersonDetails(PersonsFilter filter, Paging paging) {
		return PageBuilder.buildPageFor(applyFilter(filter)
				.map(i -> SerializationUtils.clone(i))
				.sorted(filter.getComparator(paging))
				.collect(Collectors.toList()), paging);
	}

	@Override
	public PersonDetails setUserRoles(Identifier personId, List<Role> roles) {
		PersonDetails person = findPersonDetails(personId);
		person = person.withUser(person.getUser().withRoles(roles));
		return db.savePerson(person);
	}

	@Override
	public PersonDetails setUserPassword(Identifier personId, String password) {
		PersonDetails person = findPersonDetails(personId);
		if (passwordEncoder == null) {
			passwordService.setPassword(personId, password);
		} else {
			passwordService.setPassword(personId, passwordEncoder.encode(password));
		}
		return person;
	}

	@Override
	public PersonDetails addUserRole(Identifier personId, Role role) {
		PersonDetails person = findPersonDetails(personId);
		List<Role> roles = new ArrayList<Role>(person.getUser().getRoles());
		roles.add(role);
		return setUserRoles(personId, roles);
	}

	@Override
	public PersonDetails removeUserRole(Identifier personId, Role role) {
		PersonDetails person = findPersonDetails(personId);
		List<Role> roles = new ArrayList<Role>(person.getUser().getRoles());
		roles.remove(role);
		return setUserRoles(personId, roles);
	}

	private Stream<PersonDetails> applyFilter(PersonsFilter filter) {
		return db.findByType(PersonDetails.class)
				.filter(p -> StringUtils.isNotBlank(filter.getDisplayName()) ? p.getDisplayName().contains(filter.getDisplayName()) : true)
				.filter(p -> filter.getStatus() != null ? filter.getStatus().equals(p.getStatus()) : true)
				.filter(p -> filter.getUserName() != null ? p.getUser() != null && StringUtils.equals(p.getUser().getUserName(), filter.getUserName()) : true);
	}
}