package ca.magex.crm.restful.controllers;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.filters.OptionsFilter;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Status;
import ca.magex.json.model.JsonObject;

@Controller
public class OptionsController extends AbstractCrmController {

	@GetMapping("/rest/options")
	public void findOptions(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, Option.class, (messages, transformer, locale) -> { 
			return createPage(
				crm.findOptions(
					extractOptionsFilter(locale, req), 
					extractPaging(OptionsFilter.getDefaultPaging(), req)
				), transformer, locale);
		});
	}
	
	private OptionsFilter extractOptionsFilter(Locale locale, HttpServletRequest req) throws BadRequestException {
		Identifier lookupId = req.getParameter("lookupId") == null ? null : new Identifier(req.getParameter("lookupId"));
		Status status = req.getParameter("status") == null ? null : Status.valueOf(crm.findOptionByLocalizedName(Crm.STATUS, locale, req.getParameter("status")).getCode().toUpperCase());
		if (req.getParameter("name") != null) {
			String name = req.getParameter("name");
			if (locale.equals(Lang.ENGLISH)) {
				return new OptionsFilter(lookupId, name, null, null, status);
			} else if (locale.equals(Lang.FRENCH)) {
				return new OptionsFilter(lookupId, null, name, null, status);
			} else {
				return new OptionsFilter(lookupId, null, null, name, status);
			}
		} else {
			String englishName = req.getParameter("englishName") == null ? null : req.getParameter("englishName");
			String frenchName = req.getParameter("frenchName") == null ? null : req.getParameter("frenchName");
			String code = req.getParameter("code") == null ? null : req.getParameter("code");
			return new OptionsFilter(lookupId, englishName, frenchName, code, status);
		}
	}

	@PostMapping("/rest/options")
	public void createOption(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, Option.class, (messages, transformer, locale) -> {
			JsonObject body = extractBody(req);
			Identifier lookupId = getIdentifier(body, "lookupId", null, null, messages);
			String code = getString(body, "code", "", null, messages);
			String englishName = getString(body, "englishName", "", null, messages);
			String frenchName = getString(body, "frenchName", "", null, messages);
			Localized name = new Localized(code, englishName, frenchName);
			validate(messages);
			return transformer.format(crm.createOption(lookupId, name), locale);
		});
	}

	@GetMapping("/rest/options/{optionId}")
	public void getOption(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("optionId") Identifier optionId) throws IOException {
		handle(req, res, Option.class, (messages, transformer, locale) -> {
			return transformer.format(crm.findOption(optionId), locale);
		});
	}

	@PatchMapping("/rest/options/{optionId}")
	public void updateOption(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("optionId") Identifier optionId) throws IOException {
		handle(req, res, Option.class, (messages, transformer, locale) -> {
			JsonObject body = extractBody(req);
			String code = getString(body, "code", "", null, messages);
			String englishName = getString(body, "englishName", "", null, messages);
			String frenchName = getString(body, "frenchName", "", null, messages);
			Localized name = new Localized(code, englishName, frenchName);
			validate(messages);
			crm.updateOptionName(optionId, name);
			return transformer.format(crm.findOption(optionId), locale);
		});
	}

	@PutMapping("/rest/options/{optionId}/enable")
	public void enableOption(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("optionId") Identifier optionId) throws IOException {
		handle(req, res, Option.class, (messages, transformer, locale) -> {
			confirm(extractBody(req), optionId, messages);
			return transformer.format(crm.enableOption(optionId), locale);
		});
	}

	@PutMapping("/rest/options/{optionId}/disable")
	public void disableOption(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("optionId") Identifier optionId) throws IOException {
		handle(req, res, Option.class, (messages, transformer, locale) -> {
			confirm(extractBody(req), optionId, messages);
			return transformer.format(crm.disableOption(optionId), locale);
		});
	}

}