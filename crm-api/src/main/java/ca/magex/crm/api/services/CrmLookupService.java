package ca.magex.crm.api.services;

import java.util.List;
import java.util.Locale;

import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.lookup.Salutation;
import ca.magex.crm.api.system.Role;
import ca.magex.crm.api.system.Status;

public interface CrmLookupService {
	
	List<Status> findStatuses();
	Status findStatusByCode(String code) throws ItemNotFoundException;
	Status findStatusByLocalizedName(Locale locale, String name) throws ItemNotFoundException;
	
	List<Role> findRoles();
	Role findRoleByCode(String code) throws ItemNotFoundException;
	Role findRoleByLocalizedName(Locale locale, String name) throws ItemNotFoundException;
	
	List<Country> findCountries();
	Country findCountryByCode(String code) throws ItemNotFoundException;
	Country findCountryByLocalizedName(Locale locale, String name) throws ItemNotFoundException;
	
	List<Salutation> findSalutations();
	Salutation findSalutationByCode(Integer code) throws ItemNotFoundException;
	Salutation findSalutationByLocalizedName(Locale locale, String name) throws ItemNotFoundException;
    
}