package ca.magex.crm.api.store.basic;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.User;
import ca.magex.crm.api.observer.CrmUpdateNotifier;
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
	
	private CrmUpdateNotifier notifier; 
	
	private Map<ConfigurationIdentifier, Configuration> configurations;
	
	private Map<OptionIdentifier, Option> options;
	
	private Map<OrganizationIdentifier, OrganizationDetails> organizations;
	
	private Map<LocationIdentifier, LocationDetails> locations;
	
	private Map<PersonIdentifier, PersonDetails> persons;
	
	private Map<UserIdentifier, User> users;
	
	/**
	 * Creates a new Basic Store with no data associated to it
	 */
	public BasicStore() {
		notifier = new CrmUpdateNotifier();
		configurations = new ConcurrentHashMap<>();
		options = new ConcurrentHashMap<>();
		organizations = new ConcurrentHashMap<>();
		locations = new ConcurrentHashMap<>();
		persons = new ConcurrentHashMap<>();
		users = new ConcurrentHashMap<>();
	}
	
	@Override
	public CrmUpdateNotifier getNotifier() {
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
	public Map<UserIdentifier, User> getUsers() {
		return users;
	}
}