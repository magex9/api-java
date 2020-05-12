package ca.magex.crm.restful.controllers;
	
import static ca.magex.crm.restful.controllers.ContentExtractor.*;
import static ca.magex.crm.restful.controllers.ContentExtractor.extractLocale;
import static ca.magex.crm.restful.controllers.ContentExtractor.extractOrganizationId;
import static ca.magex.crm.restful.controllers.ContentExtractor.extractStatus;
import static ca.magex.crm.restful.controllers.ContentExtractor.getContentType;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.lookup.Salutation;
import ca.magex.crm.api.secured.SecuredCrmServices;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.mapping.data.DataArray;
import ca.magex.crm.mapping.data.DataElement;
import ca.magex.crm.mapping.data.DataFormatter;
import ca.magex.crm.mapping.data.DataObject;
import ca.magex.crm.mapping.data.DataText;

@Controller
public class LookupsController {

	@Autowired
	private SecuredCrmServices crm;
	
	public LocationsFilter extractLocationFilter(HttpServletRequest req) throws BadRequestException {
		return new LocationsFilter(extractOrganizationId(req), extractDisplayName(req), extractReference(req), extractStatus(req));
	}

	@GetMapping("/api/lookup/countries")
	public void findCountries(HttpServletRequest req, HttpServletResponse res) throws Exception {
		Locale locale = extractLocale(req);
		List<Country> countries = crm.findCountries();
		DataObject data = new DataObject()
			.with("total", countries.size())
			.with("content", new DataArray(countries.stream().map(c -> transformLookup(c, locale)).collect(Collectors.toList())));
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(DataFormatter.formatted(data));
	}

	@GetMapping("/api/lookup/salutations")
	public void findSalutations(HttpServletRequest req, HttpServletResponse res) throws Exception {
		Locale locale = extractLocale(req);
		List<Salutation> salutations = crm.findSalutations();
		DataObject data = new DataObject()
			.with("total", salutations.size())
			.with("content", new DataArray(salutations.stream().map(c -> transformLookup(c, locale)).collect(Collectors.toList())));
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(DataFormatter.formatted(data));
	}
	
	public DataElement transformLookup(Object obj, Locale locale) {
		if (obj == null)
			return null;
		try {
			if (locale != null)
				return new DataText((String)obj.getClass().getMethod("getName", new Class[] { Locale.class }).invoke(obj, new Object[] { locale }));
			return new DataObject()
				.with("@value", obj.getClass().getMethod("getCode", new Class[] { }).invoke(obj, new Object[] { }))
				.with("@en", obj.getClass().getMethod("getName", new Class[] { Locale.class }).invoke(obj, new Object[] { Lang.ENGLISH }))
				.with("@fr", obj.getClass().getMethod("getName", new Class[] { Locale.class }).invoke(obj, new Object[] { Lang.FRENCH }));
		} catch (Exception e) {
			throw new RuntimeException("Problem transforming lookup: " + obj, e);
		}
	}
	
}