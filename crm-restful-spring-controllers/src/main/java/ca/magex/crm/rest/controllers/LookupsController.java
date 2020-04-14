package ca.magex.crm.rest.controllers;
	
import static ca.magex.crm.rest.controllers.ContentExtractor.extractDisplayName;
import static ca.magex.crm.rest.controllers.ContentExtractor.extractLocale;
import static ca.magex.crm.rest.controllers.ContentExtractor.extractOrganizationId;
import static ca.magex.crm.rest.controllers.ContentExtractor.extractStatus;
import static ca.magex.crm.rest.controllers.ContentExtractor.getContentType;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ca.magex.crm.amnesia.Lang;
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.services.SecuredCrmServices;
import ca.magex.crm.mapping.data.DataArray;
import ca.magex.crm.mapping.data.DataFormatter;
import ca.magex.crm.mapping.data.DataObject;

@Controller
public class LookupsController {

	@Autowired
	private SecuredCrmServices crm;
	
	public LocationsFilter extractLocationFilter(HttpServletRequest req) throws BadRequestException {
		return new LocationsFilter(extractOrganizationId(req), extractDisplayName(req), extractStatus(req));
	}

	@GetMapping("/api/lookup/countries")
	public void findCountries(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String code = req.getParameter("code");
		String name = req.getParameter("name");
		Locale locale = extractLocale(req);
		DataObject data = null;
		if (code != null) {
			data = transformLookup(crm.findCountryByCode(code));
		} else if (name != null && locale != null) {
			data = transformLookup(crm.findCountryByLocalizedName(locale, name));
		} else {
			List<Country> countries = crm.findCountries();
			data = new DataObject()
				.with("total", countries.size())
				.with("content", new DataArray(countries.stream().map(c -> transformLookup(c)).collect(Collectors.toList())));
		}
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(DataFormatter.formatted(data));
	}
	
	public DataObject transformLookup(Object obj) {
		if (obj == null)
			return null;
		try {
			return new DataObject()
				.with("code", obj.getClass().getMethod("getCode", new Class[] { }).invoke(obj, new Object[] { }))
				.with("@en", obj.getClass().getMethod("getName", new Class[] { Locale.class }).invoke(obj, new Object[] { Lang.ENGLISH }))
				.with("@fr", obj.getClass().getMethod("getName", new Class[] { Locale.class }).invoke(obj, new Object[] { Lang.FRENCH }));
		} catch (Exception e) {
			throw new RuntimeException("Problem transforming lookup: " + obj, e);
		}
	}
	
}