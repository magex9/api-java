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
	
	private AmnesiaDB amnesiaDb;

	public AmnesiaLookupService(AmnesiaDB amnesiaDb) {
		this.amnesiaDb = amnesiaDb;
	}
	
	@Override
	public List<Status> findStatuses() {
		return amnesiaDb.getStatuses().getOptions();
	}
	
	@Override
	public Status findStatusByCode(String code) throws ItemNotFoundException {
		return amnesiaDb.getStatuses().findByCode(code);
	}
	
	@Override
	public Status findStatusByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return amnesiaDb.getStatuses().findByName(locale, name);
	}

	@Override
	public List<Country> findCountries() {
		return amnesiaDb.getCountries().getOptions();
	}
	
	@Override
	public Country findCountryByCode(String code) throws ItemNotFoundException {
		return amnesiaDb.getCountries().findByCode(code);
	}
	
	@Override
	public Country findCountryByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return amnesiaDb.getCountries().findByName(locale, name);
	}

	@Override
	public List<Province> findProvinces(String country) {
		if (country.equals("CA")) {
			return amnesiaDb.getCaProvinces().getOptions();
		} else if (country.equals("US")) {
			return amnesiaDb.getUsProvinces().getOptions();
		} else if (country.equals("MX")) {
			return amnesiaDb.getMxProvinces().getOptions();
		} else {
			throw new IllegalArgumentException("No list of provinces for country: " + country);
		}
	}

	@Override
	public Province findProvinceByCode(String province, String country) {
		if (country.equals("CA")) {
			return amnesiaDb.getCaProvinces().findByCode(province);
		} else if (country.equals("US")) {
			return amnesiaDb.getUsProvinces().findByCode(province);
		} else if (country.equals("MX")) {
			return amnesiaDb.getMxProvinces().findByCode(province);
		} else {
			throw new IllegalArgumentException("No list of provinces for country: " + country);
		}
	}

	@Override
	public Province findProvinceByLocalizedName(Locale locale, String province,
			String country) {
		String code = findCountryByLocalizedName(locale, country).getCode();
		if (code.equals("CA")) {
			return amnesiaDb.getCaProvinces().findByName(locale, province);
		} else if (code.equals("US")) {
			return amnesiaDb.getUsProvinces().findByName(locale, province);
		} else if (code.equals("MX")) {
			return amnesiaDb.getMxProvinces().findByName(locale, province);
		} else {
			throw new IllegalArgumentException("No list of provinces for country: " + country);
		}
	}

	@Override
	public List<Salutation> findSalutations() {
		return amnesiaDb.getSalutations().getOptions();
	}
	
	@Override
	public Salutation findSalutationByCode(String code) throws ItemNotFoundException {
		return amnesiaDb.getSalutations().findByCode(code);
	}
	
	@Override
	public Salutation findSalutationByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return amnesiaDb.getSalutations().findByName(locale, name);
	}

	@Override
	public List<Language> findLanguages() {
		return amnesiaDb.getLanguages().getOptions();
	}

	@Override
	public Language findLanguageByCode(String code) throws ItemNotFoundException {
		return amnesiaDb.getLanguages().findByCode(code);
	}

	@Override
	public Language findLanguageByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return amnesiaDb.getLanguages().findByName(locale, name);
	}

	@Override
	public List<BusinessSector> findBusinessSectors() {
		return amnesiaDb.getSectors().getOptions();
	}

	@Override
	public BusinessSector findBusinessSectorByCode(String code) throws ItemNotFoundException {
		return amnesiaDb.getSectors().findByCode(code);
	}

	@Override
	public BusinessSector findBusinessSectorByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return amnesiaDb.getSectors().findByName(locale, name);
	}

	@Override
	public List<BusinessUnit> findBusinessUnits() {
		return amnesiaDb.getUnits().getOptions();
	}

	@Override
	public BusinessUnit findBusinessUnitByCode(String code) throws ItemNotFoundException {
		return amnesiaDb.getUnits().findByCode(code);
	}

	@Override
	public BusinessUnit findBusinessUnitByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return amnesiaDb.getUnits().findByName(locale, name);
	}

	@Override
	public List<BusinessClassification> findBusinessClassifications() {
		return amnesiaDb.getClassifications().getOptions();
	}

	@Override
	public BusinessClassification findBusinessClassificationByCode(String code) throws ItemNotFoundException {
		return amnesiaDb.getClassifications().findByCode(code);
	}

	@Override
	public BusinessClassification findBusinessClassificationByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return amnesiaDb.getClassifications().findByName(locale, name);
	}
}