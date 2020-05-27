package ca.magex.crm.amnesia.services;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import ca.magex.crm.amnesia.Lookups;
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
import ca.magex.crm.resource.CrmLookupLoader;

@Service
@Primary
@Profile(MagexCrmProfiles.CRM_DATASTORE_CENTRALIZED)
public class AmnesiaLookupService implements CrmLookupService {
	
	private CrmLookupLoader lookupLoader;

	private Lookups<Status, String> statuses;
	
	private Lookups<Province, String> caProvinces;
	
	private Lookups<Province, String> usProvinces;
	
	private Lookups<Province, String> mxProvinces;
	
	private Lookups<Country, String> countries;
	
	private Lookups<Salutation, String> salutations;
	
	private Lookups<Language, String> languages;
	
	private Lookups<BusinessSector, String> sectors;
	
	private Lookups<BusinessUnit, String> units;
	
	private Lookups<BusinessClassification, String> classifications;

	public AmnesiaLookupService(CrmLookupLoader lookupLoader) {
		this.lookupLoader = lookupLoader;
	}
	
	@PostConstruct
	public AmnesiaLookupService initialize() {
		statuses = new Lookups<Status, String>(Arrays.asList(Status.values()), Status.class, String.class);
		caProvinces = new Lookups<Province, String>(lookupLoader.loadLookup(Province.class, "CaProvince.csv"), Province.class, String.class);
		usProvinces = new Lookups<Province, String>(lookupLoader.loadLookup(Province.class, "UsProvince.csv"), Province.class, String.class);
		mxProvinces = new Lookups<Province, String>(lookupLoader.loadLookup(Province.class, "MxProvince.csv"), Province.class, String.class);
		countries = new Lookups<Country, String>(lookupLoader.loadLookup(Country.class, "Country.csv"), Country.class, String.class);
		salutations = new Lookups<Salutation, String>(lookupLoader.loadLookup(Salutation.class, "Salutation.csv"), Salutation.class, String.class);
		languages = new Lookups<Language, String>(lookupLoader.loadLookup(Language.class, "Language.csv"), Language.class, String.class);
		sectors = new Lookups<BusinessSector, String>(lookupLoader.loadLookup(BusinessSector.class, "BusinessSector.csv"), BusinessSector.class, String.class);
		units = new Lookups<BusinessUnit, String>(lookupLoader.loadLookup(BusinessUnit.class, "BusinessUnit.csv"), BusinessUnit.class, String.class);
		classifications = new Lookups<BusinessClassification, String>(lookupLoader.loadLookup(BusinessClassification.class, "BusinessClassification.csv"), BusinessClassification.class, String.class);
		return this;
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

	public List<Country> findCountries() {
		return countries.getOptions();
	}
	
	public Country findCountryByCode(String code) throws ItemNotFoundException {
		return countries.findByCode(code);
	}
	
	public Country findCountryByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return countries.findByName(locale, name);
	}

	@Override
	public List<Province> findProvinces(String country) {
		if (country.equals("CA")) {
			return caProvinces.getOptions();
		} else if (country.equals("US")) {
			return usProvinces.getOptions();
		} else if (country.equals("MX")) {
			return mxProvinces.getOptions();
		} else {
			throw new IllegalArgumentException("No list of provinces for country: " + country);
		}
	}

	@Override
	public Province findProvinceByCode(@NotNull String province, @NotNull String country) {
		if (country.equals("CA")) {
			return caProvinces.findByCode(province);
		} else if (country.equals("US")) {
			return usProvinces.findByCode(province);
		} else if (country.equals("MX")) {
			return mxProvinces.findByCode(province);
		} else {
			throw new IllegalArgumentException("No list of provinces for country: " + country);
		}
	}

	@Override
	public Province findProvinceByLocalizedName(@NotNull Locale locale, @NotNull String province,
			@NotNull String country) {
		String code = findCountryByLocalizedName(locale, country).getCode();
		if (code.equals("CA")) {
			return caProvinces.findByName(locale, province);
		} else if (code.equals("US")) {
			return usProvinces.findByName(locale, province);
		} else if (code.equals("MX")) {
			return mxProvinces.findByName(locale, province);
		} else {
			throw new IllegalArgumentException("No list of provinces for country: " + country);
		}
	}

	public List<Salutation> findSalutations() {
		return salutations.getOptions();
	}
	
	public Salutation findSalutationByCode(String code) throws ItemNotFoundException {
		return salutations.findByCode(code);
	}
	
	public Salutation findSalutationByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return salutations.findByName(locale, name);
	}

	@Override
	public List<Language> findLanguages() {
		return languages.getOptions();
	}

	@Override
	public Language findLanguageByCode(String code) throws ItemNotFoundException {
		return languages.findByCode(code);
	}

	@Override
	public Language findLanguageByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return languages.findByName(locale, name);
	}

	@Override
	public List<BusinessSector> findBusinessSectors() {
		return sectors.getOptions();
	}

	@Override
	public BusinessSector findBusinessSectorByCode(String code) throws ItemNotFoundException {
		return sectors.findByCode(code);
	}

	@Override
	public BusinessSector findBusinessSectorByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return sectors.findByName(locale, name);
	}

	@Override
	public List<BusinessUnit> findBusinessUnits() {
		return units.getOptions();
	}

	@Override
	public BusinessUnit findBusinessUnitByCode(String code) throws ItemNotFoundException {
		return units.findByCode(code);
	}

	@Override
	public BusinessUnit findBusinessUnitByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return units.findByName(locale, name);
	}

	@Override
	public List<BusinessClassification> findBusinessClassifications() {
		return classifications.getOptions();
	}

	@Override
	public BusinessClassification findBusinessClassificationByCode(String code) throws ItemNotFoundException {
		return classifications.findByCode(code);
	}

	@Override
	public BusinessClassification findBusinessClassificationByLocalizedName(Locale locale, String name)
			throws ItemNotFoundException {
		return classifications.findByName(locale, name);
	}

}