package ca.magex.crm.api.store;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;

import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lookup;
import ca.magex.crm.api.system.Option;

public interface CrmStore {
	
	public static final String BASE_58 = "123456789abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ";
	
	public static Identifier generateId(Class<?> type) {
		return new Identifier(RandomStringUtils.random(10, BASE_58));
	}
	
	public Map<Identifier, Serializable> getConfigurations();
	
	public Map<Identifier, Lookup> getLookups();

	public Map<Identifier, Option> getOptions();

	public Map<Identifier, Group> getGroups();

	public Map<Identifier, Role> getRoles();

	public Map<Identifier, OrganizationDetails> getOrganizations();
	
	public Map<Identifier, LocationDetails> getLocations();

	public Map<Identifier, PersonDetails> getPersons();

	public Map<Identifier, User> getUsers();
	
	default public void reset() {
		getConfigurations().clear();
		getLookups().clear();
		getOptions().clear();
		getGroups().clear();
		getRoles().clear();
		getOrganizations().clear();
		getLocations().clear();
		getPersons().clear();
		getUsers().clear();
	}
	
	default public void dump(OutputStream os) {
		dump(getConfigurations(), os);
		dump(getLookups(), os);
		dump(getOptions(), os);
		dump(getGroups(), os);
		dump(getRoles(), os);
		dump(getOrganizations(), os);
		dump(getLocations(), os);
		dump(getPersons(), os);
		dump(getUsers(), os);
	}
	
	public static void dump(Map<Identifier, ? extends Serializable> map, OutputStream os) {
		map.keySet()
			.stream()
			.sorted((x, y) -> x.toString().compareTo(y.toString()))
			.forEach(key -> {
				try {
					os.write(new String(key + " => " + map.get(key) + "\n").getBytes());
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			});
	}

}
