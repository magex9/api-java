package ca.magex.crm.hazelcast.service;

import java.util.List;
import java.util.Locale;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.hazelcast.core.HazelcastInstance;

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
@Profile(MagexCrmProfiles.CRM_DATASTORE_DECENTRALIZED)
public class HazelcastLookupService implements CrmLookupService {

	public static String HZ_STATUS_KEY = "statuses";
	public static String HZ_COUNTRY_KEY = "countries";
	public static String HZ_LANGUAGE_KEY = "languages";
	public static String HZ_SALUTATION_KEY = "salutations";
	public static String HZ_SECTOR_KEY = "sectors";
	public static String HZ_UNIT_KEY = "units";
	public static String HZ_CLASSIFICATION_KEY = "classifications";
		
	@Autowired private HazelcastInstance hzInstance;		
	
	@Override
	public List<Status> findStatuses() {
		return hzInstance.getList(HZ_STATUS_KEY);
	}

	@Override
	public Status findStatusByCode(String code) throws ItemNotFoundException {
		return hzInstance.getList(HZ_STATUS_KEY)
				.stream()
				.map((s) -> (Status) s)
				.filter((s) -> StringUtils.equalsIgnoreCase(s.getCode(), code))
				.findFirst()
				.orElseThrow(() -> new ItemNotFoundException("Status '" + code + "'"));
	}

	@Override
	public Status findStatusByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return hzInstance.getList(HZ_STATUS_KEY)
				.stream()
				.map((s) -> (Status) s)
				.filter((s) -> StringUtils.equalsIgnoreCase(s.getName(locale), name))
				.findFirst()
				.orElseThrow(() -> new ItemNotFoundException("Status[" + locale + "] '" + name + "'"));
	}	

	@Override
	public List<Country> findCountries() {
		return hzInstance.getList(HZ_COUNTRY_KEY);
	}

	@Override
	public Country findCountryByCode(String code) throws ItemNotFoundException {
		return hzInstance.getList(HZ_COUNTRY_KEY)
				.stream()
				.map((s) -> (Country) s)
				.filter((s) -> StringUtils.equalsIgnoreCase(s.getCode(), code))
				.findFirst()
				.orElseThrow(() -> new ItemNotFoundException("Country '" + code + "'"));
	}

	@Override
	public Country findCountryByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return hzInstance.getList(HZ_COUNTRY_KEY)
				.stream()
				.map((s) -> (Country) s)
				.filter((s) -> StringUtils.equalsIgnoreCase(s.getName(locale), name))
				.findFirst()
				.orElseThrow(() -> new ItemNotFoundException("Country[" + locale + "] '" + name + "'"));
	}
	
	@Override
	public List<Province> findProvinces(String country) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Province findProvinceByCode(@NotNull String province, @NotNull String country) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Province findProvinceByLocalizedName(@NotNull Locale locale, @NotNull String province,
			@NotNull String country) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Language> findLanguages() {
		return hzInstance.getList(HZ_LANGUAGE_KEY);
	}

	@Override
	public Language findLanguageByCode(String code) throws ItemNotFoundException {
		return hzInstance.getList(HZ_LANGUAGE_KEY)
				.stream()
				.map((s) -> (Language) s)
				.filter((s) -> StringUtils.equalsIgnoreCase(s.getCode(), code))
				.findFirst()
				.orElseThrow(() -> new ItemNotFoundException("Language '" + code + "'"));
	}

	@Override
	public Language findLanguageByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return hzInstance.getList(HZ_LANGUAGE_KEY)
				.stream()
				.map((s) -> (Language) s)
				.filter((s) -> StringUtils.equalsIgnoreCase(s.getName(locale), name))
				.findFirst()
				.orElseThrow(() -> new ItemNotFoundException("Language[" + locale + "] '" + name + "'"));
	}

	@Override
	public List<Salutation> findSalutations() {
		return hzInstance.getList(HZ_SALUTATION_KEY);
	}

	@Override
	public Salutation findSalutationByCode(String code) throws ItemNotFoundException {
		return hzInstance.getList(HZ_SALUTATION_KEY)
				.stream()
				.map((s) -> (Salutation) s)
				.filter((s) -> StringUtils.equalsIgnoreCase(s.getCode(), code))
				.findFirst()
				.orElseThrow(() -> new ItemNotFoundException("Salutation '" + code + "'"));
	}

	@Override
	public Salutation findSalutationByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return hzInstance.getList(HZ_SALUTATION_KEY)
				.stream()
				.map((s) -> (Salutation) s)
				.filter((s) -> StringUtils.equalsIgnoreCase(s.getName(locale), name))
				.findFirst()
				.orElseThrow(() -> new ItemNotFoundException("Salutation[" + locale + "] '" + name + "'"));
	}

	@Override
	public List<BusinessSector> findBusinessSectors() {
		return hzInstance.getList(HZ_SECTOR_KEY);
	}

	@Override
	public BusinessSector findBusinessSectorByCode(String code) throws ItemNotFoundException {
		return hzInstance.getList(HZ_SECTOR_KEY)
				.stream()
				.map((s) -> (BusinessSector) s)
				.filter((s) -> StringUtils.equalsIgnoreCase(s.getCode(), code))
				.findFirst()
				.orElseThrow(() -> new ItemNotFoundException("BusinessSector '" + code + "'"));
	}

	@Override
	public BusinessSector findBusinessSectorByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return hzInstance.getList(HZ_SECTOR_KEY)
				.stream()
				.map((s) -> (BusinessSector) s)
				.filter((s) -> StringUtils.equalsIgnoreCase(s.getName(locale), name))
				.findFirst()
				.orElseThrow(() -> new ItemNotFoundException("BusinessSector[" + locale + "] '" + name + "'"));
	}

	@Override
	public List<BusinessUnit> findBusinessUnits() {
		return hzInstance.getList(HZ_UNIT_KEY);
	}

	@Override
	public BusinessUnit findBusinessUnitByCode(String code) throws ItemNotFoundException {
		return hzInstance.getList(HZ_UNIT_KEY)
				.stream()
				.map((s) -> (BusinessUnit) s)
				.filter((s) -> StringUtils.equalsIgnoreCase(s.getCode(), code))
				.findFirst()
				.orElseThrow(() -> new ItemNotFoundException("BusinessUnit '" + code + "'"));
	}

	@Override
	public BusinessUnit findBusinessUnitByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return hzInstance.getList(HZ_UNIT_KEY)
				.stream()
				.map((s) -> (BusinessUnit) s)
				.filter((s) -> StringUtils.equalsIgnoreCase(s.getName(locale), name))
				.findFirst()
				.orElseThrow(() -> new ItemNotFoundException("BusinessUnit[" + locale + "] '" + name + "'"));
	}

	@Override
	public List<BusinessClassification> findBusinessClassifications() {
		return hzInstance.getList(HZ_CLASSIFICATION_KEY);
	}

	@Override
	public BusinessClassification findBusinessClassificationByCode(String code) throws ItemNotFoundException {
		return hzInstance.getList(HZ_CLASSIFICATION_KEY)
				.stream()
				.map((s) -> (BusinessClassification) s)
				.filter((s) -> StringUtils.equalsIgnoreCase(s.getCode(), code))
				.findFirst()
				.orElseThrow(() -> new ItemNotFoundException("BusinessClassification '" + code + "'"));
	}

	@Override
	public BusinessClassification findBusinessClassificationByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return hzInstance.getList(HZ_CLASSIFICATION_KEY)
				.stream()
				.map((s) -> (BusinessClassification) s)
				.filter((s) -> StringUtils.equalsIgnoreCase(s.getName(locale), name))
				.findFirst()
				.orElseThrow(() -> new ItemNotFoundException("BusinessClassification[" + locale + "] '" + name + "'"));
	}
}