package ca.magex.crm.api.store;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;

import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.observer.CrmUpdateNotifier;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Type;

public interface CrmStore {
	
	public static final String BASE_58 = "123456789abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ";
	
	public static Identifier generateId(String context) {
		return new Identifier(context + "/" + RandomStringUtils.random(10, BASE_58));
	}
	
	public CrmUpdateNotifier getNotifier();
	
	public Map<Identifier, Serializable> getConfigurations();
	
	public Map<Identifier, Type> getTypes();

	public Map<Identifier, Option> getOptions();

	public Map<Identifier, OrganizationDetails> getOrganizations();
	
	public Map<Identifier, LocationDetails> getLocations();

	public Map<Identifier, PersonDetails> getPersons();

	public Map<Identifier, User> getUsers();
	
	default public void reset() {
		getNotifier().clear();
		getConfigurations().clear();
		getTypes().clear();
		getOptions().clear();
		getOrganizations().clear();
		getLocations().clear();
		getPersons().clear();
		getUsers().clear();
	}
	
	default public void dump(OutputStream os) {
		dump(getConfigurations(), os);
		dump(getTypes(), os);
		dump(getOptions(), os);
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
