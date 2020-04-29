package ca.magex.crm.amnesia;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import ca.magex.crm.amnesia.generator.AmnesiaBase58IdGenerator;
import ca.magex.crm.amnesia.generator.IdGenerator;
import ca.magex.crm.api.common.User;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.services.CrmPasswordService;
import ca.magex.crm.api.system.Identifier;

@Repository
public class AmnesiaDB implements CrmPasswordService {
	
	public static final String SYSTEM_ADMIN = "SYS_ADMIN";
	
	public static final String CRM_ADMIN = "CRM_ADMIN";

	public static final String RE_ADMIN = "RE_ADMIN";
	
	private IdGenerator idGenerator;
	
	private Map<Identifier, Serializable> data;
	
	private Map<Identifier, String> passwords;
	
	public AmnesiaDB(PasswordEncoder passwordEncoder) {
		idGenerator = new AmnesiaBase58IdGenerator();
		data = new HashMap<Identifier, Serializable>();
		passwords = new HashMap<Identifier, String>();
	}
	
	public Identifier generateId() {
		return idGenerator.generate();
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Serializable> Stream<T> findByType(Class<T> cls) {
		return data.values().stream().filter(c -> c.getClass().equals(cls)).map(c -> (T)c);
	}

	public OrganizationDetails findOrganization(Identifier organizationId) {
		Serializable obj = data.get(organizationId);
		if (obj == null)
			throw new ItemNotFoundException("Unable to find: " + organizationId);
		if (!(obj instanceof OrganizationDetails))
			throw new BadRequestException(organizationId, "error", "class", "Expected OrganizationDetails but got: " + obj.getClass().getName());
		return (OrganizationDetails)SerializationUtils.clone(obj);
	}

	public OrganizationDetails saveOrganization(OrganizationDetails organization) {
		data.put(organization.getOrganizationId(), organization);
		return organization;
	}

	public LocationDetails findLocation(Identifier locationId) {
		Serializable obj = data.get(locationId);
		if (obj == null)
			throw new ItemNotFoundException("Unable to find: " + locationId);
		if (!(obj instanceof LocationDetails))
			throw new BadRequestException(locationId, "error", "class", "Expected LocationDetails but got: " + obj.getClass().getName());
		return (LocationDetails)SerializationUtils.clone(obj);
	}

	public LocationDetails saveLocation(LocationDetails location) {
		data.put(location.getLocationId(), location);
		return location;
	}

	public PersonDetails findPerson(Identifier personId) {
		Serializable obj = data.get(personId);
		if (obj == null)
			throw new ItemNotFoundException("Unable to find: " + personId);
		if (!(obj instanceof PersonDetails))
			throw new BadRequestException(personId, "error", "class", "Expected PersonDetails but got: " + obj.getClass().getName());
		return (PersonDetails)SerializationUtils.clone(obj);
	}

	public PersonDetails savePerson(PersonDetails person) {
		data.put(person.getPersonId(), person);
		return person;
	}
	
	public User saveUser(User user) {
		data.put(user.getUserId(), user);
		return user;
	}
	
	public User findUser(Identifier userId) {
		Serializable obj = data.get(userId);
		if (obj == null)
			throw new ItemNotFoundException("Unable to find: " + userId);
		if (!(obj instanceof User))
			throw new BadRequestException(userId, "error", "class", "Expected User but got: " + obj.getClass().getName());
		return (User)SerializationUtils.clone(obj);
	}	
	
	public void setPassword(Identifier userId, String password) {
		/* only store the encoded password */
		passwords.put(userId, password);
	}
	
	public String getPassword(Identifier userId) {
		return passwords.get(userId);
	}
}