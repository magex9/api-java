package ca.magex.crm.amnesia;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import ca.magex.crm.amnesia.generator.AmnesiaBase58IdGenerator;
import ca.magex.crm.amnesia.generator.IdGenerator;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.common.User;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.services.CrmPasswordService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Role;
import ca.magex.crm.api.system.Status;

@Repository
public class AmnesiaDB implements CrmPasswordService {
	
	private static Logger LOG = LoggerFactory.getLogger(AmnesiaDB.class);

	public static final String SYSTEM_ADMIN = "SYS_ADMIN";
	
	public static final String CRM_ADMIN = "CRM_ADMIN";

	public static final String RE_ADMIN = "RE_ADMIN";
	
	private IdGenerator idGenerator;
	
	private Map<Identifier, Serializable> data;
	
	private Map<Identifier, String> passwords;
	
	@Autowired(required=false) private PasswordEncoder passwordEncoder;
	
		
	public AmnesiaDB() {
		idGenerator = new AmnesiaBase58IdGenerator();
		data = new HashMap<Identifier, Serializable>();
		passwords = new HashMap<Identifier, String>();
	}
	
	@PostConstruct
	public void initialize() {
		LOG.info("Creating Magex Organization");
		
		/* create the default organization */
		OrganizationDetails magex = saveOrganization(new OrganizationDetails(generateId(), Status.ACTIVE, "Magex", null));				

		LOG.info("Creating CRM Admin");
		PersonDetails admin = savePerson(
				new PersonDetails(
						generateId(), 
						magex.getOrganizationId(), 
						Status.ACTIVE, 
						"Crm Admin", 
						new PersonName(null, "Crm", "", "Admin"), 
						null, 
						null, 
						null, 
						new User("CXA0", Arrays.asList(new Role("CRM_ADMIN", "admin", "admin")))));
		setPassword(admin.getPersonId(), passwordEncoder == null ? "admin" : passwordEncoder.encode("admin"));
		LOG.info("CRM Admin: " + admin.getUser().getUserName());

		LOG.info("Creating System Admin");
		PersonDetails sysadmin = savePerson(
				new PersonDetails(
						generateId(), 
						magex.getOrganizationId(), 
						Status.ACTIVE, 
						"System Admin", 
						new PersonName(null, "Crm", "", "Admin"), 
						null, 
						null, 
						null, 
						new User("SXA1", Arrays.asList(new Role("SYSTEM_ADMIN", "admin", "admin")))));
		setPassword(sysadmin.getPersonId(), passwordEncoder == null ? "admin" : passwordEncoder.encode("admin"));
		LOG.info("System Admin: " + sysadmin.getUser().getUserName());
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
	
	public void setPassword(Identifier personId, String password) {
		/* only store the encoded password */
		passwords.put(personId, password);
	}
	
	@Override
	public String getPassword(Identifier personId) {
		return passwords.get(personId);
	}
}