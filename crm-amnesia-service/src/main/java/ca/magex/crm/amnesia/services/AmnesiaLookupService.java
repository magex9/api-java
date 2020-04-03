package ca.magex.crm.amnesia.services;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import ca.magex.crm.amnesia.Lookups;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.lookup.Salutation;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.api.system.Role;
import ca.magex.crm.api.system.Status;

public class AmnesiaLookupService implements CrmLookupService {

	private Lookups<Status, String> statuses;
	
	private Lookups<Role, String> roles;
	
	private Lookups<Country, String> countries;
	
	private Lookups<Salutation, Integer> salutations;
	
	public AmnesiaLookupService() {
		statuses = new Lookups<Status, String>(Arrays.asList(Status.values()), Status.class, String.class);
		roles = new Lookups<Role, String>(Role.class, String.class);
		countries = new Lookups<Country, String>(Country.class, String.class);
		salutations = new Lookups<Salutation, Integer>(Salutation.class, Integer.class);
	}
	
	public List<Status> findStatuses() {
		return statuses.getOptions();
	}
	
	public Status findStatusByCode(String code) throws ItemNotFoundException {
		return statuses.findByCode(code);
	}
	
	public Status findStatusByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return statuses.findByName(locale, name);
	}

	public List<Role> findRoles() {
		return roles.getOptions();
	}
	
	public Role findRoleByCode(String code) throws ItemNotFoundException {
		return roles.findByCode(code);
	}
	
	public Role findRoleByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return roles.findByName(locale, name);
	}

	public List<Country> findCountries() {
		return countries.getOptions();
	}
	
	public Country findCountryByCode(String code) throws ItemNotFoundException {
		return countries.findByCode(code);
	}
	
	public Country findCountryByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return countries.findByName(locale, name);
	}

	public List<Salutation> findSalutations() {
		return salutations.getOptions();
	}
	
	public Salutation findSalutationByCode(Integer code) throws ItemNotFoundException {
		return salutations.findByCode(code);
	}
	
	public Salutation findSalutationByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return salutations.findByName(locale, name);
	}

}