package ca.magex.crm.api.store;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;

import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.User;
import ca.magex.crm.api.observer.CrmUpdateNotifier;
import ca.magex.crm.api.system.Configuration;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.id.ConfigurationIdentifier;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OptionIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.system.id.UserIdentifier;

public interface CrmStore {
	
	public static final String BASE_58 = "123456789abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ";
	
	public static String generateId() {
		return RandomStringUtils.random(10, BASE_58);
	}
	
	public String encode(Object obj) throws IOException;
	
	public Object decode(String text) throws IOException, ClassNotFoundException;
	
	public CrmUpdateNotifier getNotifier();
	
	public Map<ConfigurationIdentifier, Configuration> getConfigurations();
	
	public Map<OptionIdentifier, Option> getOptions();

	public Map<OrganizationIdentifier, OrganizationDetails> getOrganizations();
	
	public Map<LocationIdentifier, LocationDetails> getLocations();

	public Map<PersonIdentifier, PersonDetails> getPersons();

	public Map<UserIdentifier, User> getUsers();
	
	default public void reset() {
		getNotifier().clear();
		getConfigurations().clear();
		getOptions().clear();
		getOrganizations().clear();
		getLocations().clear();
		getPersons().clear();
		getUsers().clear();
	}
	
	default public void dump(OutputStream os) {
		try {
			dump(getConfigurations(), os);
			dump(getOptions(), os);
			dump(getOrganizations(), os);
			dump(getLocations(), os);
			dump(getPersons(), os);
			dump(getUsers(), os);
		} catch (Exception e) {
			throw new RuntimeException("Unable to dump store to outputstream", e);
		}
	}
	
	default void dump(Map<? extends Identifier, ? extends Serializable> map, OutputStream os) {
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