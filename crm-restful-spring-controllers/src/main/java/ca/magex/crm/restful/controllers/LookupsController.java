package ca.magex.crm.restful.controllers;
	
import static ca.magex.crm.restful.controllers.ContentExtractor.extractDisplayName;
import static ca.magex.crm.restful.controllers.ContentExtractor.extractLocale;
import static ca.magex.crm.restful.controllers.ContentExtractor.extractOrganizationId;
import static ca.magex.crm.restful.controllers.ContentExtractor.extractReference;
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
import ca.magex.crm.api.services.Crm;
import ca.magex.crm.api.system.Lang;
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonFormatter;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonText;

@Controller
public class LookupsController {

	@Autowired
	private Crm crm;
	
	public LocationsFilter extractLocationFilter(HttpServletRequest req) throws BadRequestException {
		return new LocationsFilter(extractOrganizationId(req), extractDisplayName(req), extractReference(req), extractStatus(req));
	}

	@GetMapping("/api/lookup/countries")
	public void findCountries(HttpServletRequest req, HttpServletResponse res) throws Exception {
		Locale locale = extractLocale(req);
		List<Country> countries = crm.findCountries();
		JsonObject data = new JsonObject()
			.with("total", countries.size())
			.with("content", new JsonArray(countries.stream().map(c -> transformLookup(c, locale)).collect(Collectors.toList())));
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(JsonFormatter.formatted(data));
	}

	@GetMapping("/api/lookup/salutations")
	public void findSalutations(HttpServletRequest req, HttpServletResponse res) throws Exception {
		Locale locale = extractLocale(req);
		List<Salutation> salutations = crm.findSalutations();
		JsonObject data = new JsonObject()
			.with("total", salutations.size())
			.with("content", new JsonArray(salutations.stream().map(c -> transformLookup(c, locale)).collect(Collectors.toList())));
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(JsonFormatter.formatted(data));
	}
	
	public JsonElement transformLookup(Object obj, Locale locale) {
		if (obj == null)
			return null;
		try {
			if (locale != null)
				return new JsonText((String)obj.getClass().getMethod("getName", new Class[] { Locale.class }).invoke(obj, new Object[] { locale }));
			return new JsonObject()
				.with("@value", obj.getClass().getMethod("getCode", new Class[] { }).invoke(obj, new Object[] { }))
				.with("@en", obj.getClass().getMethod("getName", new Class[] { Locale.class }).invoke(obj, new Object[] { Lang.ENGLISH }))
				.with("@fr", obj.getClass().getMethod("getName", new Class[] { Locale.class }).invoke(obj, new Object[] { Lang.FRENCH }));
		} catch (Exception e) {
			throw new RuntimeException("Problem transforming lookup: " + obj, e);
		}
	}
	
}