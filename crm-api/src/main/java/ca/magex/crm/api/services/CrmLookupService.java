package ca.magex.crm.api.services;

import java.util.List;
import java.util.Locale;

import javax.validation.constraints.NotNull;

import ca.magex.crm.api.lookup.BusinessClassification;
import ca.magex.crm.api.lookup.BusinessSector;
import ca.magex.crm.api.lookup.BusinessUnit;
import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.lookup.Language;
import ca.magex.crm.api.lookup.Salutation;
import ca.magex.crm.api.system.Status;

public interface CrmLookupService {
	
	List<Status> findStatuses();
	
	Status findStatusByCode(
		@NotNull String code
	);
	
	Status findStatusByLocalizedName(
		@NotNull Locale locale, 
		@NotNull String name
	);
	
	List<Country> findCountries();
	
	
	Country findCountryByCode(
		@NotNull String code
	);
	
	Country findCountryByLocalizedName(
		@NotNull Locale locale, 
		@NotNull String name
	);
	
	List<Language> findLanguages();
	
	Language findLanguageByCode(
		@NotNull String code
	);
	
	Language findLanguageByLocalizedName(
		@NotNull Locale locale, 
		@NotNull String name
	);
	
	List<Salutation> findSalutations();
	
	Salutation findSalutationByCode(
		@NotNull String code
	);
	
	Salutation findSalutationByLocalizedName(
		@NotNull Locale locale, 
		@NotNull String name
	);
    
	List<BusinessSector> findBusinessSectors();
	
	BusinessSector findBusinessSectorByCode(
		@NotNull String code
	);
	
	BusinessSector findBusinessSectorByLocalizedName(
		@NotNull Locale locale, 
		@NotNull String name
	);
    
	List<BusinessUnit> findBusinessUnits();
	
	BusinessUnit findBusinessUnitByCode(
		@NotNull String code
	);
	
	BusinessUnit findBusinessUnitByLocalizedName(
		@NotNull Locale locale, 
		@NotNull String name
	);
    
	List<BusinessClassification> findBusinessClassifications();
	
	BusinessClassification findBusinessClassificationByCode(
		@NotNull String code
	);
	
	BusinessClassification findBusinessClassificationByLocalizedName(
		@NotNull Locale locale, 
		@NotNull String name
	);
    
}
