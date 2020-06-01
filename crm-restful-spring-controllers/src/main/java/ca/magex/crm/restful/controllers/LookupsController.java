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

	@GetMapping("/api/lookup/status")
	public void findStatuses(HttpServletRequest req, HttpServletResponse res) throws Exception {
		handle(req, res, Status.class, (messages, transformer) -> { 
			return createList(crm.findStatuses(), transformer, extractLocale(req));
		});
	}

	@GetMapping("/api/lookup/status/{status}")
	public void findStatus(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("status") String status) throws Exception {
		handle(req, res, Status.class, (messages, transformer) -> { 
			return transformer.format(crm.findStatusByCode(status), extractLocale(req));
		});
	}

	@GetMapping("/api/lookup/salutations")
	public void findSalutations(HttpServletRequest req, HttpServletResponse res) throws Exception {
		handle(req, res, Salutation.class, (messages, transformer) -> { 
			return createList(crm.findSalutations(), transformer, extractLocale(req), new Localized.Comparator<Salutation>(extractLocale(req)));
		});
	}

	@GetMapping("/api/lookup/salutations/{salutation}")
	public void findSalutations(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("salutation") String salutation) throws Exception {
		handle(req, res, Salutation.class, (messages, transformer) -> { 
			return transformer.format(crm.findSalutationByCode(salutation), extractLocale(req));
		});
	}

	@GetMapping("/api/lookup/languages")
	public void findLanguages(HttpServletRequest req, HttpServletResponse res) throws Exception {
		handle(req, res, Language.class, (messages, transformer) -> { 
			return createList(crm.findLanguages(), transformer, extractLocale(req), new Localized.Comparator<Language>(extractLocale(req), List.of("EN", "FR"), List.of()));
		});
	}

	@GetMapping("/api/lookup/languages/{language}")
	public void findLanguage(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("language") String language) throws Exception {
		handle(req, res, Language.class, (messages, transformer) -> { 
			return transformer.format(crm.findLanguageByCode(language), extractLocale(req));
		});
	}

	@GetMapping("/api/lookup/countries")
	public void findCountries(HttpServletRequest req, HttpServletResponse res) throws Exception {
		handle(req, res, Country.class, (messages, transformer) -> { 
			return createList(crm.findCountries(), transformer, extractLocale(req), new Localized.Comparator<Country>(extractLocale(req), List.of("CA", "US", "MX"), List.of("ZZ")));
		});
	}

	@GetMapping("/api/lookup/countries/{country}")
	public void findCountry(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("country") String country) throws Exception {
		handle(req, res, Country.class, (messages, transformer) -> { 
			return transformer.format(crm.findCountryByCode(country), extractLocale(req));
		});
	}

	@GetMapping("/api/lookup/countries/{country}/provinces")
	public void findProvinces(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("country") String country) throws Exception {
		handle(req, res, Province.class, (messages, transformer) -> { 
			return createList(crm.findProvinces(country), transformer, extractLocale(req), new Localized.Comparator<Province>(extractLocale(req)));
		});
	}

	@GetMapping("/api/lookup/countries/{country}/provinces/{province}")
	public void findProvince(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("country") String country, @PathVariable("province") String province) throws Exception {
		handle(req, res, Province.class, (messages, transformer) -> { 
			return transformer.format(crm.findProvinceByCode(province, country), extractLocale(req));
		});
	}

	@GetMapping("/api/lookup/business/sectors")
	public void findBusinessSectors(HttpServletRequest req, HttpServletResponse res) throws Exception {
		handle(req, res, BusinessSector.class, (messages, transformer) -> { 
			return createList(crm.findBusinessSectors(), transformer, extractLocale(req), new Localized.Comparator<BusinessSector>(extractLocale(req)));
		});
	}

	@GetMapping("/api/lookup/business/sectors/{sector}")
	public void findBusinessSectors(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("sector") String sector) throws Exception {
		handle(req, res, BusinessSector.class, (messages, transformer) -> { 
			return transformer.format(crm.findBusinessSectorByCode(sector), extractLocale(req));
		});
	}

	@GetMapping("/api/lookup/business/units")
	public void findBusinessUnits(HttpServletRequest req, HttpServletResponse res) throws Exception {
		handle(req, res, BusinessUnit.class, (messages, transformer) -> { 
			return createList(crm.findBusinessUnits(), transformer, extractLocale(req), new Localized.Comparator<BusinessUnit>(extractLocale(req)));
		});
	}

	@GetMapping("/api/lookup/business/units/{unit}")
	public void findBusinessUnits(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("unit") String unit) throws Exception {
		handle(req, res, BusinessUnit.class, (messages, transformer) -> { 
			return transformer.format(crm.findBusinessUnitByCode(unit), extractLocale(req));
		});
	}

	@GetMapping("/api/lookup/business/classifications")
	public void findBusinessClassifications(HttpServletRequest req, HttpServletResponse res) throws Exception {
		handle(req, res, BusinessClassification.class, (messages, transformer) -> { 
			return createList(crm.findBusinessClassifications(), transformer, extractLocale(req), new Localized.Comparator<BusinessClassification>(extractLocale(req)));
		});
	}
	
	@GetMapping("/api/lookup/business/classifications/{classification}")
	public void findBusinessClassifications(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("classification") String classification) throws Exception {
		handle(req, res, BusinessClassification.class, (messages, transformer) -> { 
			return transformer.format(crm.findBusinessClassificationByCode(classification), extractLocale(req));
		});
	}
	
}