package ca.magex.crm.restful.controllers;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.filters.OptionsFilter;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.IdentifierFactory;
import ca.magex.crm.api.system.id.OptionIdentifier;
import ca.magex.crm.transform.json.StatusJsonTransformer;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonText;

@Controller
@CrossOrigin
public class RestfulOptionController extends AbstractRestfulController {
	
	@GetMapping("/rest/types")
	public void findTypes(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, Type.class, (messages, transformer, locale) -> { 
			return createList(Arrays.asList(Type.values()), transformer, locale);
		});
	}

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
	
	@GetMapping("/rest/options/count")
	public void countOptions(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, Option.class, (messages, transformer, locale) -> {
			return new JsonObject().with("total", crm.countOptions(extractOptionsFilter(locale, req)));
		});
	}
	
	private OptionsFilter extractOptionsFilter(Locale locale, HttpServletRequest req) throws BadRequestException {
		OptionIdentifier parentId = req.getParameter("parentId") == null ? null : IdentifierFactory.forOptionId(req.getParameter("parentId"));
		Status status = req.getParameter("status") == null ? null : new StatusJsonTransformer(crm).parseJsonText(new JsonText(req.getParameter("status")), locale);
		Type type = req.getParameter("type") == null ? null : Type.of(req.getParameter("type").toUpperCase());
		Localized name = new Localized(
			req.getParameter("name") == null ? "" : req.getParameter("name"), 
			req.getParameter("name.en") == null ? "" : req.getParameter("name.en"), 
			req.getParameter("name.fr") == null ? "" : req.getParameter("name.fr")
		);
		return new OptionsFilter(name, parentId, type, status);
	}

	@PostMapping("/rest/options")
	public void createOption(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, Option.class, (messages, transformer, locale) -> {
			JsonObject body = extractBody(req);
			OptionIdentifier parentId = getIdentifier(body, "parentId", false, null, null, messages);
			Type type = getObject(Type.class, body, "type", false, null, null, messages, locale);
			Localized name = getObject(Localized.class, body, "name", false, null, null, messages, locale);
			validate(messages);
			return transformer.format(crm.createOption(parentId, type, name), locale);
		});
	}

	@GetMapping("/rest/options/{typeCode}/**")
	public void findOptionByCode(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("typeCode") String typeCode) throws IOException {
		handle(req, res, Option.class, (messages, transformer, locale) -> {
			OptionIdentifier optionId = IdentifierFactory.forOptionId(req.getRequestURI().split(req.getContextPath() + "/rest")[1]);
			return transformer.format(crm.findOption(optionId), locale);
		});
	}

	@PatchMapping("/rest/options/{typeCode}/**")
	public void updateOptionName(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("typeCode") String typeCode) throws IOException {
		handle(req, res, Option.class, (messages, transformer, locale) -> {
			OptionIdentifier optionId = IdentifierFactory.forOptionId(req.getRequestURI().split(req.getContextPath() + "/rest")[1]);
			JsonObject body = extractBody(req);
			String code = getString(body, "code", false, "", null, messages);
			String english = getString(body, "english", false, "", null, messages);
			String french = getString(body, "french", false, "", null, messages);
			Localized name = new Localized(code, english, french);
			validate(messages);
			crm.updateOptionName(optionId, name);
			return transformer.format(crm.findOption(optionId), locale);
		});
	}

	@PutMapping("/rest/options/{typeCode}/**/enable")
	public void enableOption(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("typeCode") String typeCode) throws IOException {
		handle(req, res, Option.class, (messages, transformer, locale) -> {
			OptionIdentifier optionId = IdentifierFactory.forOptionId(req.getRequestURI().split(req.getContextPath() + "/rest")[1].replaceAll("/enable$", ""));
			confirm(extractBody(req), optionId, messages);
			return transformer.format(crm.enableOption(optionId), locale);
		});
	}

	@PutMapping("/rest/options/{typeCode}/**/disable")
	public void disableOption(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("typeCode") String typeCode) throws IOException {
		handle(req, res, Option.class, (messages, transformer, locale) -> {
			OptionIdentifier optionId = IdentifierFactory.forOptionId(req.getRequestURI().split(req.getContextPath() + "/rest")[1].replaceAll("/disable$", ""));
			confirm(extractBody(req), optionId, messages);
			return transformer.format(crm.disableOption(optionId), locale);
		});
	}

}