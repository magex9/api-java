package ca.magex.crm.restful.controllers;
	
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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

	@GetMapping("/api/lookup/salutations")
	public void findSalutations(HttpServletRequest req, HttpServletResponse res) throws Exception {
		handle(req, res, Salutation.class, (messages, transformer) -> { 
			return createList(crm.findSalutations(), transformer, extractLocale(req));
		});
	}

	@GetMapping("/api/lookup/languages")
	public void findLanguages(HttpServletRequest req, HttpServletResponse res) throws Exception {
		handle(req, res, Language.class, (messages, transformer) -> { 
			return createList(crm.findLanguages(), transformer, extractLocale(req), new Localized.Comparator<Language>(extractLocale(req), List.of("EN", "FR"), List.of()));
		});
	}

	@GetMapping("/api/lookup/countries")
	public void findCountries(HttpServletRequest req, HttpServletResponse res) throws Exception {
		handle(req, res, Country.class, (messages, transformer) -> { 
			return createList(crm.findCountries(), transformer, extractLocale(req), new Localized.Comparator<Country>(extractLocale(req), List.of("CA", "US", "MX"), List.of("ZZ")));
		});
	}

	@GetMapping("/api/lookup/countries/{country}/provinces")
	public void findCountries(HttpServletRequest req, HttpServletResponse res, String country) throws Exception {
		handle(req, res, Province.class, (messages, transformer) -> { 
			return createList(crm.findProvinces(country), transformer, extractLocale(req));
		});
	}

	@GetMapping("/api/lookup/business/sectors")
	public void findBusinessSectors(HttpServletRequest req, HttpServletResponse res) throws Exception {
		handle(req, res, BusinessSector.class, (messages, transformer) -> { 
			return createList(crm.findBusinessSectors(), transformer, extractLocale(req));
		});
	}

	@GetMapping("/api/lookup/business/units")
	public void findBusinessUnits(HttpServletRequest req, HttpServletResponse res) throws Exception {
		handle(req, res, BusinessUnit.class, (messages, transformer) -> { 
			return createList(crm.findBusinessUnits(), transformer, extractLocale(req));
		});
	}

	@GetMapping("/api/lookup/business/classifications")
	public void findBusinessClassifications(HttpServletRequest req, HttpServletResponse res) throws Exception {
		handle(req, res, BusinessClassification.class, (messages, transformer) -> { 
			return createList(crm.findBusinessClassifications(), transformer, extractLocale(req));
		});
	}
	
}