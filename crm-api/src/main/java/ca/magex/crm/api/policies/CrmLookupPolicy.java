package ca.magex.crm.api.policies;

import java.util.Locale;

public interface CrmLookupPolicy {

	boolean canViewStatusLookup(String StatusLookup, Locale locale);
	
	boolean canViewCountryLookup(String CountryLookup, Locale locale);
	
	boolean canViewProvinceLookup(String countryLookup, String provinceLookup, Locale locale);
	
	boolean canViewLanguageLookup(String languageLookup, Locale locale);
	
	boolean canViewSalutationLookup(String salutationLookup, Locale locale);
	
	boolean canViewBusinessSectorLookup(String sectorLookup, Locale locale);
	
	boolean canViewBusinessUnitLookup(String unitLookup, Locale locale);
	
	boolean canViewBusinessClassificationLookup(String classificationLookup, Locale locale);
}
