package ca.magex.crm.api.store.basic;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.User;
import ca.magex.crm.api.observer.CrmUpdateNotifier;
import ca.magex.crm.api.store.CrmStore;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Type;

/**
 * Simple In memory implementation of the Datastore using ConcurrentHashMap
 * 
 * @author Scott
 */
public class BasicStore implements CrmStore {
	
	private CrmUpdateNotifier notifier; 
	
	private Map<Identifier, Serializable> configurations;
	
	private Map<Identifier, Type> types;
	
	private Map<Identifier, Option> options;
	
	private Map<Identifier, OrganizationDetails> organizations;
	
	private Map<Identifier, LocationDetails> locations;
	
	private Map<Identifier, PersonDetails> persons;
	
	private Map<Identifier, User> users;
	
	/**
	 * Creates a new Basic Store with no data associated to it
	 */
	public BasicStore() {
		notifier = new CrmUpdateNotifier();
		configurations = new ConcurrentHashMap<Identifier, Serializable>();
		options = new ConcurrentHashMap<Identifier, Option>();
		organizations = new ConcurrentHashMap<Identifier, OrganizationDetails>();
		locations = new ConcurrentHashMap<Identifier, LocationDetails>();
		persons = new ConcurrentHashMap<Identifier, PersonDetails>();
		users = new ConcurrentHashMap<Identifier, User>();
	}
	
	@Override
	public CrmUpdateNotifier getNotifier() {
		return notifier;
	}
	
	@Override
	public Map<Identifier, Serializable> getConfigurations() {
		return configurations;
	}
	
	@Override
	public Map<Identifier, Type> getTypes() {
		return types;
	}
	
	@Override
	public Map<Identifier, Option> getOptions() {
		return options;
	}

	@Override
	public Map<Identifier, OrganizationDetails> getOrganizations() {
		return organizations;
	}

	@Override
	public Map<Identifier, LocationDetails> getLocations() {
		return locations;
	}

	@Override
	public Map<Identifier, PersonDetails> getPersons() {
		return persons;
	}

	@Override
	public Map<Identifier, User> getUsers() {
		return users;
	}
}