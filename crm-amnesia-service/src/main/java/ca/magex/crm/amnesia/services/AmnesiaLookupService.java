package ca.magex.crm.amnesia.services;

import java.util.List;
import java.util.Locale;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import ca.magex.crm.amnesia.AmnesiaDB;
import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.lookup.BusinessClassification;
import ca.magex.crm.api.lookup.BusinessSector;
import ca.magex.crm.api.lookup.BusinessUnit;
import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.lookup.Language;
import ca.magex.crm.api.lookup.Province;
import ca.magex.crm.api.lookup.Salutation;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.api.system.Status;

@Service
@Primary
@Profile(MagexCrmProfiles.CRM_DATASTORE_CENTRALIZED)
public class AmnesiaLookupService implements CrmLookupService {
	
	private AmnesiaDB db;

	public AmnesiaLookupService(AmnesiaDB db) {
		this.db = db;
	}
	
	@Override
	public List<Status> findStatuses() {
		return db.getStatuses().getOptions();
	}
	
	@Override
	public Status findStatusByCode(String code) throws ItemNotFoundException {
		return db.getStatuses().findByCode(code);
	}
	
	@Override
	public Status findStatusByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return db.getStatuses().findByName(locale, name);
	}

	@Override
	public List<Country> findCountries() {
		return db.getCountries().getOptions();
	}
	
	@Override
	public Country findCountryByCode(String code) throws ItemNotFoundException {
		return db.getCountries().findByCode(code.toUpperCase());
	}
	
	@Override
	public Country findCountryByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return db.getCountries().findByName(locale, name);
	}

	@Override
	public List<Province> findProvinces(String country) {
		if (country.equalsIgnoreCase("CA")) {
			return db.getCaProvinces().getOptions();
		} else if (country.equalsIgnoreCase("US")) {
			return db.getUsProvinces().getOptions();
		} else if (country.equalsIgnoreCase("MX")) {
			return db.getMxProvinces().getOptions();
		} else {
			throw new IllegalArgumentException("No list of provinces for country: " + country);
		}
	}

	@Override
	public Province findProvinceByCode(String province, String country) {
		if (country.equalsIgnoreCase("CA")) {
			return db.getCaProvinces().findByCode(province.toUpperCase());
		} else if (country.equalsIgnoreCase("US")) {
			return db.getUsProvinces().findByCode(province.toUpperCase());
		} else if (country.equalsIgnoreCase("MX")) {
			return db.getMxProvinces().findByCode(province.toUpperCase());
		} else {
			throw new IllegalArgumentException("No list of provinces for country: " + country);
		}
	}

	@Override
	public Province findProvinceByLocalizedName(Locale locale, String province,
			String country) {
		Country ctry = findCountryByLocalizedName(locale, country);
		if (ctry == null) {
			return null;
		}
		
		if (ctry.getCode().equalsIgnoreCase("CA")) {
			return db.getCaProvinces().findByName(locale, province);
		} else if (ctry.getCode().equalsIgnoreCase("US")) {
			return db.getUsProvinces().findByName(locale, province);
		} else if (ctry.getCode().equalsIgnoreCase("MX")) {
			return db.getMxProvinces().findByName(locale, province);
		} else {
			throw new IllegalArgumentException("No list of provinces for country: " + country);
		}
	}

	@Override
	public List<Salutation> findSalutations() {
		return db.getSalutations().getOptions();
	}
	
	@Override
	public Salutation findSalutationByCode(String code) throws ItemNotFoundException {
		return db.getSalutations().findByCode(code);
	}
	
	@Override
	public Salutation findSalutationByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return db.getSalutations().findByName(locale, name);
	}

	@Override
	public List<Language> findLanguages() {
		return db.getLanguages().getOptions();
	}

	@Override
	public Language findLanguageByCode(String code) throws ItemNotFoundException {
		return db.getLanguages().findByCode(code.toUpperCase());
	}

	@Override
	public Language findLanguageByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return db.getLanguages().findByName(locale, name);
	}

	@Override
	public List<BusinessSector> findBusinessSectors() {
		return db.getSectors().getOptions();
	}

	@Override
	public BusinessSector findBusinessSectorByCode(String code) throws ItemNotFoundException {
		return db.getSectors().findByCode(code);
	}

	@Override
	public BusinessSector findBusinessSectorByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return db.getSectors().findByName(locale, name);
	}

	@Override
	public List<BusinessUnit> findBusinessUnits() {
		return db.getUnits().getOptions();
	}

	@Override
	public BusinessUnit findBusinessUnitByCode(String code) throws ItemNotFoundException {
		return db.getUnits().findByCode(code);
	}

	@Override
	public BusinessUnit findBusinessUnitByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return db.getUnits().findByName(locale, name);
	}

	@Override
	public List<BusinessClassification> findBusinessClassifications() {
		return db.getClassifications().getOptions();
	}

	@Override
	public BusinessClassification findBusinessClassificationByCode(String code) throws ItemNotFoundException {
		return db.getClassifications().findByCode(code);
	}

	@Override
	public BusinessClassification findBusinessClassificationByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return db.getClassifications().findByName(locale, name);
	}
}