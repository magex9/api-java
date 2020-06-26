package ca.magex.crm.restful.controllers;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.filters.LookupsFilter;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Lookup;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Status;
import ca.magex.json.model.JsonObject;

@Controller
public class LookupsController extends AbstractCrmController {

	@GetMapping("/rest/lookups")
	public void findLookups(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, Lookup.class, (messages, transformer, locale) -> { 
			return createPage(
				crm.findLookups(
					extractLookupsFilter(locale, req), 
					extractPaging(LookupsFilter.getDefaultPaging(), req)
				), transformer, locale
			);
		});
	}
	
	private LookupsFilter extractLookupsFilter(Locale locale, HttpServletRequest req) throws BadRequestException {
		Status status = req.getParameter("status") == null ? null : Status.valueOf(crm.findOptionByLocalizedName(Crm.STATUS, locale, req.getParameter("status")).getCode().toUpperCase());
		if (req.getParameter("name") != null) {
			String name = req.getParameter("name");
			if (locale.equals(Lang.ENGLISH)) {
				return new LookupsFilter(name, null, null, null, status);
			} else if (locale.equals(Lang.FRENCH)) {
				return new LookupsFilter(null, name, null, null, status);
			} else {
				return new LookupsFilter(null, null, name, null, status);
			}
		} else {
			String englishName = req.getParameter("englishName") == null ? null : req.getParameter("englishName");
			String frenchName = req.getParameter("frenchName") == null ? null : req.getParameter("frenchName");
			String code = req.getParameter("code") == null ? null : req.getParameter("code");
			return new LookupsFilter(englishName, frenchName, code, null, status);
		}
	}

	@PostMapping("/rest/lookups")
	public void createLookup(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, Lookup.class, (messages, transformer, locale) -> {
			JsonObject body = extractBody(req);
			String code = getString(body, "code", "", null, messages);
			String englishName = getString(body, "englishName", "", null, messages);
			String frenchName = getString(body, "frenchName", "", null, messages);
			Localized name = new Localized(code, englishName, frenchName);
			String parentLookup = getString(body, "parentLookup", null, null, messages);
			String parentCode = getString(body, "parentCode", null, null, messages);
			Option parent = (StringUtils.isNotBlank(parentLookup) && StringUtils.isNotBlank(parentCode)) ? crm.findOption(parentLookup, parentCode) : null;
			validate(messages);
			return transformer.format(crm.createLookup(name, parent), locale);
		});
	}

	@GetMapping("/rest/lookups/{lookupId}")
	public void getLookup(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("lookupId") Identifier lookupId) throws IOException {
		handle(req, res, Lookup.class, (messages, transformer, locale) -> {
			return transformer.format(crm.findLookup(lookupId), locale);
		});
	}

	@PatchMapping("/rest/lookups/{lookupId}")
	public void updateLookup(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("lookupId") Identifier lookupId) throws IOException {
		handle(req, res, Lookup.class, (messages, transformer, locale) -> {
			JsonObject body = extractBody(req);
			String code = getString(body, "code", "", null, messages);
			String englishName = getString(body, "englishName", "", null, messages);
			String frenchName = getString(body, "frenchName", "", null, messages);
			Localized name = new Localized(code, englishName, frenchName);
			validate(messages);
			crm.updateLookupName(lookupId, name);
			return transformer.format(crm.findLookup(lookupId), locale);
		});
	}

	@PutMapping("/rest/lookups/{lookupId}/enable")
	public void enableLookup(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("lookupId") Identifier lookupId) throws IOException {
		handle(req, res, Lookup.class, (messages, transformer, locale) -> {
			confirm(extractBody(req), lookupId, messages);
			crm.enableLookup(lookupId);
			return transformer.format(crm.findLookup(lookupId), locale);
		});
	}

	@PutMapping("/rest/lookups/{lookupId}/disable")
	public void disableLookup(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("lookupId") Identifier lookupId) throws IOException {
		handle(req, res, Lookup.class, (messages, transformer, locale) -> {
			confirm(extractBody(req), lookupId, messages);
			crm.disableLookup(lookupId);
			return transformer.format(crm.findLookup(lookupId), locale);
		});
	}

}