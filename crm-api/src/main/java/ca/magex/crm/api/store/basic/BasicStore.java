package ca.magex.crm.api.store.basic;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.UserDetails;
import ca.magex.crm.api.event.CrmEventNotifier;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.store.CrmStore;
import ca.magex.crm.api.system.Configuration;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.id.ConfigurationIdentifier;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OptionIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.system.id.UserIdentifier;

/**
 * Simple In memory implementation of the Datastore using ConcurrentHashMap
 * 
 * @author Scott
 */
public class BasicStore implements CrmStore {
	
	private CrmEventNotifier notifier; 
	
	private Map<ConfigurationIdentifier, Configuration> configurations;
	
	private Map<OptionIdentifier, Option> options;
	
	private Map<OrganizationIdentifier, OrganizationDetails> organizations;
	
	private Map<LocationIdentifier, LocationDetails> locations;
	
	private Map<PersonIdentifier, PersonDetails> persons;
	
	private Map<UserIdentifier, UserDetails> users;
	
	/**
	 * Creates a new Basic Store with no data associated to it
	 */
	public BasicStore() {
		notifier = new CrmEventNotifier();
		configurations = new ConcurrentHashMap<>();
		options = new ConcurrentHashMap<>();
		organizations = new ConcurrentHashMap<>();
		locations = new ConcurrentHashMap<>();
		persons = new ConcurrentHashMap<>();
		users = new ConcurrentHashMap<>();
	}
	
	@Override
	public String encode(Object obj, CrmServices crm) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(obj);
		oos.close();
		return Base64.getEncoder().encodeToString(baos.toByteArray());
	}

	@Override
	public Object decode(String text, CrmServices crm) throws IOException, ClassNotFoundException {
		byte[] data = Base64.getDecoder().decode(text);
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
		Object o = ois.readObject();
		ois.close();
		return o;
	}
	
	@Override
	public CrmEventNotifier getNotifier() {
		return notifier;
	}

	@Override
	public Map<ConfigurationIdentifier, Configuration> getConfigurations() {
		return configurations;
	}
	
	@Override
	public Map<OptionIdentifier, Option> getOptions() {
		return options;
	}

	@Override
	public Map<OrganizationIdentifier, OrganizationDetails> getOrganizations() {
		return organizations;
	}

	@Override
	public Map<LocationIdentifier, LocationDetails> getLocations() {
		return locations;
	}

	@Override
	public Map<PersonIdentifier, PersonDetails> getPersons() {
		return persons;
	}

	@Override
	public Map<UserIdentifier, UserDetails> getUsers() {
		return users;
	}
}