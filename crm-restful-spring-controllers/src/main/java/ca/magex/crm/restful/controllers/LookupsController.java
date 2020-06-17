package ca.magex.crm.restful.controllers;
	
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import ca.magex.crm.api.lookup.BusinessClassification;
import ca.magex.crm.api.lookup.BusinessSector;
import ca.magex.crm.api.lookup.BusinessUnit;
import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.lookup.Language;
import ca.magex.crm.api.lookup.Province;
import ca.magex.crm.api.lookup.Salutation;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;

@Controller
public class LookupsController extends AbstractCrmController {

	@GetMapping("/rest/lookup/status")
	public void findStatuses(HttpServletRequest req, HttpServletResponse res) throws Exception {
		handle(req, res, Status.class, (messages, transformer, locale) -> { 
			return createList(crm.findStatuses(), transformer, locale);
		});
	}

	@GetMapping("/rest/lookup/status/{status}")
	public void findStatus(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("status") String status) throws Exception {
		handle(req, res, Status.class, (messages, transformer, locale) -> { 
			return transformer.format(crm.findStatusByCode(status), locale);
		});
	}

	@GetMapping("/rest/lookup/salutations")
	public void findSalutations(HttpServletRequest req, HttpServletResponse res) throws Exception {
		handle(req, res, Salutation.class, (messages, transformer, locale) -> { 
			return createList(crm.findSalutations(), transformer, locale, new Localized.Comparator<Salutation>(locale));
		});
	}

	@GetMapping("/rest/lookup/salutations/{salutation}")
	public void findSalutations(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("salutation") String salutation) throws Exception {
		handle(req, res, Salutation.class, (messages, transformer, locale) -> { 
			return transformer.format(crm.findSalutationByCode(salutation), locale);
		});
	}

	@GetMapping("/rest/lookup/languages")
	public void findLanguages(HttpServletRequest req, HttpServletResponse res) throws Exception {
		handle(req, res, Language.class, (messages, transformer, locale) -> { 
			return createList(crm.findLanguages(), transformer, locale, new Localized.Comparator<Language>(locale, List.of("EN", "FR"), List.of()));
		});
	}

	@GetMapping("/rest/lookup/languages/{language}")
	public void findLanguage(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("language") String language) throws Exception {
		handle(req, res, Language.class, (messages, transformer, locale) -> { 
			return transformer.format(crm.findLanguageByCode(language), locale);
		});
	}

	@GetMapping("/rest/lookup/countries")
	public void findCountries(HttpServletRequest req, HttpServletResponse res) throws Exception {
		handle(req, res, Country.class, (messages, transformer, locale) -> { 
			return createList(crm.findCountries(), transformer, locale, new Localized.Comparator<Country>(locale, List.of("CA", "US", "MX"), List.of("ZZ")));
		});
	}

	@GetMapping("/rest/lookup/countries/{country}")
	public void findCountry(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("country") String country) throws Exception {
		handle(req, res, Country.class, (messages, transformer, locale) -> { 
			return transformer.format(crm.findCountryByCode(country), locale);
		});
	}

	@GetMapping("/rest/lookup/countries/{country}/provinces")
	public void findProvinces(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("country") String country) throws Exception {
		handle(req, res, Province.class, (messages, transformer, locale) -> { 
			return createList(crm.findProvinces(country), transformer, locale, new Localized.Comparator<Province>(locale));
		});
	}

	@GetMapping("/rest/lookup/countries/{country}/provinces/{province}")
	public void findProvince(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("country") String country, @PathVariable("province") String province) throws Exception {
		handle(req, res, Province.class, (messages, transformer, locale) -> { 
			return transformer.format(crm.findProvinceByCode(province, country), locale);
		});
	}

	@GetMapping("/rest/lookup/business/sectors")
	public void findBusinessSectors(HttpServletRequest req, HttpServletResponse res) throws Exception {
		handle(req, res, BusinessSector.class, (messages, transformer, locale) -> { 
			return createList(crm.findBusinessSectors(), transformer, locale, new Localized.Comparator<BusinessSector>(locale));
		});
	}

	@GetMapping("/rest/lookup/business/sectors/{sector}")
	public void findBusinessSectors(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("sector") String sector) throws Exception {
		handle(req, res, BusinessSector.class, (messages, transformer, locale) -> { 
			return transformer.format(crm.findBusinessSectorByCode(sector), locale);
		});
	}

	@GetMapping("/rest/lookup/business/units")
	public void findBusinessUnits(HttpServletRequest req, HttpServletResponse res) throws Exception {
		handle(req, res, BusinessUnit.class, (messages, transformer, locale) -> { 
			return createList(crm.findBusinessUnits(), transformer, locale, new Localized.Comparator<BusinessUnit>(locale));
		});
	}

	@GetMapping("/rest/lookup/business/units/{unit}")
	public void findBusinessUnits(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("unit") String unit) throws Exception {
		handle(req, res, BusinessUnit.class, (messages, transformer, locale) -> { 
			return transformer.format(crm.findBusinessUnitByCode(unit), locale);
		});
	}

	@GetMapping("/rest/lookup/business/classifications")
	public void findBusinessClassifications(HttpServletRequest req, HttpServletResponse res) throws Exception {
		handle(req, res, BusinessClassification.class, (messages, transformer, locale) -> { 
			return createList(crm.findBusinessClassifications(), transformer, locale, new Localized.Comparator<BusinessClassification>(locale));
		});
	}
	
	@GetMapping("/rest/lookup/business/classifications/{classification}")
	public void findBusinessClassifications(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("classification") String classification) throws Exception {
		handle(req, res, BusinessClassification.class, (messages, transformer, locale) -> { 
			return transformer.format(crm.findBusinessClassificationByCode(classification), locale);
		});
	}
	
}