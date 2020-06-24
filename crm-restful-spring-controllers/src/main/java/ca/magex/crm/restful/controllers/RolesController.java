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
import ca.magex.crm.api.filters.RolesFilter;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;
import ca.magex.json.model.JsonObject;

@Controller
public class RolesController extends AbstractCrmController {

	@GetMapping("/rest/roles")
	public void findRoles(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, Role.class, (messages, transformer, locale) -> { 
			return createPage(
				crm.findRoles(
					extractRolesFilter(locale, req), 
					extractPaging(RolesFilter.getDefaultPaging(), req)
				), transformer, locale);
		});
	}
	
	private RolesFilter extractRolesFilter(Locale locale, HttpServletRequest req) throws BadRequestException {
		Identifier groupId = req.getParameter("groupId") == null ? null : new Identifier(req.getParameter("groupId"));
		Status status = req.getParameter("status") == null ? null : Status.valueOf(crm.findOptionByLocalizedName(Crm.STATUSES, locale, req.getParameter("status")).getCode().toUpperCase());
		if (req.getParameter("name") != null) {
			String name = req.getParameter("name");
			if (locale.equals(Lang.ENGLISH)) {
				return new RolesFilter(groupId, name, null, null, status);
			} else if (locale.equals(Lang.FRENCH)) {
				return new RolesFilter(groupId, null, name, null, status);
			} else {
				return new RolesFilter(groupId, null, null, name, status);
			}
		} else {
			String englishName = req.getParameter("englishName") == null ? null : req.getParameter("englishName");
			String frenchName = req.getParameter("frenchName") == null ? null : req.getParameter("frenchName");
			String code = req.getParameter("code") == null ? null : req.getParameter("code");
			return new RolesFilter(groupId, englishName, frenchName, code, status);
		}
	}

	@PostMapping("/rest/roles")
	public void createRole(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, Role.class, (messages, transformer, locale) -> {
			JsonObject body = extractBody(req);
			Identifier groupId = getIdentifier(body, "groupId", null, null, messages);
			String code = getString(body, "code", "", null, messages);
			String englishName = getString(body, "englishName", "", null, messages);
			String frenchName = getString(body, "frenchName", "", null, messages);
			Localized name = new Localized(code, englishName, frenchName);
			validate(messages);
			return transformer.format(crm.createRole(groupId, name), locale);
		});
	}

	@GetMapping("/rest/roles/{roleId}")
	public void getRole(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("roleId") Identifier roleId) throws IOException {
		handle(req, res, Role.class, (messages, transformer, locale) -> {
			return transformer.format(crm.findRole(roleId), locale);
		});
	}

	@PatchMapping("/rest/roles/{roleId}")
	public void updateRole(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("roleId") Identifier roleId) throws IOException {
		handle(req, res, Role.class, (messages, transformer, locale) -> {
			JsonObject body = extractBody(req);
			String code = getString(body, "code", "", null, messages);
			String englishName = getString(body, "englishName", "", null, messages);
			String frenchName = getString(body, "frenchName", "", null, messages);
			Localized name = new Localized(code, englishName, frenchName);
			validate(messages);
			crm.updateRoleName(roleId, name);
			return transformer.format(crm.findRole(roleId), locale);
		});
	}

	@PutMapping("/rest/roles/{roleId}/enable")
	public void enableRole(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("roleId") Identifier roleId) throws IOException {
		handle(req, res, Role.class, (messages, transformer, locale) -> {
			confirm(extractBody(req), roleId, messages);
			return transformer.format(crm.enableRole(roleId), locale);
		});
	}

	@PutMapping("/rest/roles/{roleId}/disable")
	public void disableRole(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("roleId") Identifier roleId) throws IOException {
		handle(req, res, Role.class, (messages, transformer, locale) -> {
			confirm(extractBody(req), roleId, messages);
			return transformer.format(crm.disableRole(roleId), locale);
		});
	}

}