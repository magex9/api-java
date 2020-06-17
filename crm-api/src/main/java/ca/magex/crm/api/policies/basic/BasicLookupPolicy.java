package ca.magex.crm.api.policies.basic;

import java.util.Locale;

import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.policies.CrmLookupPolicy;
import ca.magex.crm.api.services.CrmLookupService;

public class BasicLookupPolicy implements CrmLookupPolicy {

	private CrmLookupService lookups;
	
	/**
	 * Basic Lookup Policy handles presence and status checks require for policy approval
	 * 
	 * @param organizations
	 */
	public BasicLookupPolicy(CrmLookupService lookups) {
		this.lookups = lookups;
	}

	@Override
	public boolean canViewStatusLookup(String StatusLookup, Locale locale) {
		if (lookups.findStatusByLocalizedName(locale, StatusLookup) == null) {
			throw new ItemNotFoundException("Status[" + locale + "] '" + StatusLookup + "'");
		}
		return true;
	}

	@Override
	public boolean canViewCountryLookup(String CountryLookup, Locale locale) {
		if (lookups.findCountryByLocalizedName(locale, CountryLookup) == null) {
			throw new ItemNotFoundException("Country[" + locale + "] '" + CountryLookup + "'");
		}
		return true;
	}

	@Override
	public boolean canViewProvinceLookup(String countryLookup, String provinceLookup, Locale locale) {
		if (lookups.findProvinceByLocalizedName(locale, provinceLookup, countryLookup) == null) {
			throw new ItemNotFoundException("Province[" + locale + "] '" + provinceLookup + "'");
		}
		return true;
	}

	@Override
	public boolean canViewLanguageLookup(String languageLookup, Locale locale) {
		if (lookups.findLanguageByLocalizedName(locale, languageLookup) == null) {
			throw new ItemNotFoundException("Language[" + locale + "] '" + languageLookup + "'");
		}
		return true;
	}

	@Override
	public boolean canViewSalutationLookup(String salutationLookup, Locale locale) {
		if (lookups.findSalutationByLocalizedName(locale, salutationLookup) == null) {
			throw new ItemNotFoundException("Salutation[" + locale + "] '" + salutationLookup + "'");
		}
		return true;
	}

	@Override
	public boolean canViewBusinessSectorLookup(String sectorLookup, Locale locale) {
		if (lookups.findBusinessSectorByLocalizedName(locale, sectorLookup) == null) {
			throw new ItemNotFoundException("BusinessSector[" + locale + "] '" + sectorLookup + "'");
		}
		return true;
	}

	@Override
	public boolean canViewBusinessUnitLookup(String unitLookup, Locale locale) {
		if (lookups.findBusinessUnitByLocalizedName(locale, unitLookup) == null) {
			throw new ItemNotFoundException("BusinessUnit[" + locale + "] '" + unitLookup + "'");
		}
		return true;
	}

	@Override
	public boolean canViewBusinessClassificationLookup(String classificationLookup, Locale locale) {
		if (lookups.findBusinessClassificationByLocalizedName(locale, classificationLookup) == null) {
			throw new ItemNotFoundException("BusinessClassification[" + locale + "] '" + classificationLookup + "'");
		}
		return true;
	}
}