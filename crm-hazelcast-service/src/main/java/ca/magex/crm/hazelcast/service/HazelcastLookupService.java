package ca.magex.crm.hazelcast.service;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.hazelcast.core.HazelcastInstance;

import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.lookup.BusinessClassification;
import ca.magex.crm.api.lookup.BusinessSector;
import ca.magex.crm.api.lookup.BusinessUnit;
import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.lookup.Language;
import ca.magex.crm.api.lookup.Salutation;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.api.system.Role;
import ca.magex.crm.api.system.Status;

@Service
@Primary
public class HazelcastLookupService implements CrmLookupService {

	@Autowired private HazelcastInstance hzInstance;
	
	@Override
	public List<Status> findStatuses() {
		return hzInstance.getList("statuses");
	}

	@Override
	public Status findStatusByCode(String code) throws ItemNotFoundException {
		return hzInstance.getList("statuses")
				.stream()
				.map((s) -> (Status) s)
				.filter((s) -> StringUtils.equalsIgnoreCase(s.getCode(), code))
				.findFirst()
				.orElseThrow(() -> new ItemNotFoundException(code));
	}

	@Override
	public Status findStatusByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return hzInstance.getList("statuses")
				.stream()
				.map((s) -> (Status) s)
				.filter((s) -> StringUtils.equalsIgnoreCase(s.getName(locale), name))
				.findFirst()
				.orElseThrow(() -> new ItemNotFoundException(locale + "," + name));
	}

	@Override
	public List<Role> findRoles() {
		return hzInstance.getList("roles");
	}

	@Override
	public Role findRoleByCode(String code) throws ItemNotFoundException {
		return hzInstance.getList("roles")
				.stream()
				.map((s) -> (Role) s)
				.filter((s) -> StringUtils.equalsIgnoreCase(s.getCode(), code))
				.findFirst()
				.orElseThrow(() -> new ItemNotFoundException(code));
	}

	@Override
	public Role findRoleByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return hzInstance.getList("roles")
				.stream()
				.map((s) -> (Role) s)
				.filter((s) -> StringUtils.equalsIgnoreCase(s.getName(locale), name))
				.findFirst()
				.orElseThrow(() -> new ItemNotFoundException(locale + "," + name));
	}

	@Override
	public List<Country> findCountries() {
		return hzInstance.getList("countries");
	}

	@Override
	public Country findCountryByCode(String code) throws ItemNotFoundException {
		return hzInstance.getList("countries")
				.stream()
				.map((s) -> (Country) s)
				.filter((s) -> StringUtils.equalsIgnoreCase(s.getCode(), code))
				.findFirst()
				.orElseThrow(() -> new ItemNotFoundException(code));
	}

	@Override
	public Country findCountryByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return hzInstance.getList("countries")
				.stream()
				.map((s) -> (Country) s)
				.filter((s) -> StringUtils.equalsIgnoreCase(s.getName(locale), name))
				.findFirst()
				.orElseThrow(() -> new ItemNotFoundException(locale + "," + name));
	}

	@Override
	public List<Language> findLanguages() {
		return hzInstance.getList("languages");
	}

	@Override
	public Language findLanguageByCode(String code) throws ItemNotFoundException {
		return hzInstance.getList("languages")
				.stream()
				.map((s) -> (Language) s)
				.filter((s) -> StringUtils.equalsIgnoreCase(s.getCode(), code))
				.findFirst()
				.orElseThrow(() -> new ItemNotFoundException(code));
	}

	@Override
	public Language findLanguageByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return hzInstance.getList("languages")
				.stream()
				.map((s) -> (Language) s)
				.filter((s) -> StringUtils.equalsIgnoreCase(s.getName(locale), name))
				.findFirst()
				.orElseThrow(() -> new ItemNotFoundException(locale + "," + name));
	}

	@Override
	public List<Salutation> findSalutations() {
		return hzInstance.getList("salutations");
	}

	@Override
	public Salutation findSalutationByCode(String code) throws ItemNotFoundException {
		return hzInstance.getList("salutations")
				.stream()
				.map((s) -> (Salutation) s)
				.filter((s) -> StringUtils.equalsIgnoreCase(s.getCode(), code))
				.findFirst()
				.orElseThrow(() -> new ItemNotFoundException(code));
	}

	@Override
	public Salutation findSalutationByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return hzInstance.getList("salutations")
				.stream()
				.map((s) -> (Salutation) s)
				.filter((s) -> StringUtils.equalsIgnoreCase(s.getName(locale), name))
				.findFirst()
				.orElseThrow(() -> new ItemNotFoundException(locale + "," + name));
	}

	@Override
	public List<BusinessSector> findBusinessSectors() {
		return hzInstance.getList("sectors");
	}

	@Override
	public BusinessSector findBusinessSectorByCode(String code) throws ItemNotFoundException {
		return hzInstance.getList("sectors")
				.stream()
				.map((s) -> (BusinessSector) s)
				.filter((s) -> StringUtils.equalsIgnoreCase(s.getCode(), code))
				.findFirst()
				.orElseThrow(() -> new ItemNotFoundException(code));
	}

	@Override
	public BusinessSector findBusinessSectorByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return hzInstance.getList("sectors")
				.stream()
				.map((s) -> (BusinessSector) s)
				.filter((s) -> StringUtils.equalsIgnoreCase(s.getName(locale), name))
				.findFirst()
				.orElseThrow(() -> new ItemNotFoundException(locale + "," + name));
	}

	@Override
	public List<BusinessUnit> findBusinessUnits() {
		return hzInstance.getList("units");
	}

	@Override
	public BusinessUnit findBusinessUnitByCode(String code) throws ItemNotFoundException {
		return hzInstance.getList("units")
				.stream()
				.map((s) -> (BusinessUnit) s)
				.filter((s) -> StringUtils.equalsIgnoreCase(s.getCode(), code))
				.findFirst()
				.orElseThrow(() -> new ItemNotFoundException(code));
	}

	@Override
	public BusinessUnit findBusinessUnitByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return hzInstance.getList("units")
				.stream()
				.map((s) -> (BusinessUnit) s)
				.filter((s) -> StringUtils.equalsIgnoreCase(s.getName(locale), name))
				.findFirst()
				.orElseThrow(() -> new ItemNotFoundException(locale + "," + name));
	}

	@Override
	public List<BusinessClassification> findBusinessClassifications() {
		return hzInstance.getList("classifications");
	}

	@Override
	public BusinessClassification findBusinessClassificationByCode(String code) throws ItemNotFoundException {
		return hzInstance.getList("classifications")
				.stream()
				.map((s) -> (BusinessClassification) s)
				.filter((s) -> StringUtils.equalsIgnoreCase(s.getCode(), code))
				.findFirst()
				.orElseThrow(() -> new ItemNotFoundException(code));
	}

	@Override
	public BusinessClassification findBusinessClassificationByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return hzInstance.getList("classifications")
				.stream()
				.map((s) -> (BusinessClassification) s)
				.filter((s) -> StringUtils.equalsIgnoreCase(s.getName(locale), name))
				.findFirst()
				.orElseThrow(() -> new ItemNotFoundException(locale + "," + name));
	}
}