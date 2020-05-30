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

import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.filters.GroupsFilter;
import ca.magex.crm.api.filters.RolesFilter;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;
import ca.magex.json.model.JsonObject;

@Controller
public class PermissionsController extends AbstractCrmController {

	@GetMapping("/api/groups")
	public void findGroups(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, Group.class, (messages, transformer) -> { 
			return createPage(
				crm.findGroups(
					extractGroupsFilter(extractLocale(req), req), 
					extractPaging(GroupsFilter.getDefaultPaging(), req)
				), transformer, extractLocale(req)
			);
		});
	}
	
	private GroupsFilter extractGroupsFilter(Locale locale, HttpServletRequest req) throws BadRequestException {
		Status status = req.getParameter("status") == null ? null : crm.findStatusByLocalizedName(locale, req.getParameter("status"));
		if (req.getParameter("name") != null) {
			String name = req.getParameter("name");
			if (locale.equals(Lang.ENGLISH)) {
				return new GroupsFilter(name, null, null, status);
			} else if (locale.equals(Lang.FRENCH)) {
				return new GroupsFilter(null, name, null, status);
			} else {
				return new GroupsFilter(null, null, name, status);
			}
		} else {
			String englishName = req.getParameter("englishName") == null ? null : req.getParameter("englishName");
			String frenchName = req.getParameter("frenchName") == null ? null : req.getParameter("frenchName");
			String code = req.getParameter("code") == null ? null : req.getParameter("code");
			return new GroupsFilter(englishName, frenchName, code, status);
		}
	}

	@PostMapping("/api/groups")
	public void createGroup(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, Group.class, (messages, transformer) -> {
			JsonObject body = extractBody(req);
			String code = getString(body, "code", "", null, messages);
			String englishName = getString(body, "englishName", "", null, messages);
			String frenchName = getString(body, "frenchName", "", null, messages);
			Localized name = new Localized(code, englishName, frenchName);
			validate(messages);
			return transformer.format(crm.createGroup(name), extractLocale(req));
		});
	}

	@GetMapping("/api/groups/{groupId}")
	public void getGroup(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("groupId") Identifier groupId) throws IOException {
		handle(req, res, Group.class, (messages, transformer) -> {
			return transformer.format(crm.findGroup(groupId), extractLocale(req));
		});
	}

	@PatchMapping("/api/groups/{groupId}")
	public void updateGroup(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("groupId") Identifier groupId) throws IOException {
		handle(req, res, Group.class, (messages, transformer) -> {
			JsonObject body = extractBody(req);
			String code = getString(body, "code", "", null, messages);
			String englishName = getString(body, "englishName", "", null, messages);
			String frenchName = getString(body, "frenchName", "", null, messages);
			Localized name = new Localized(code, englishName, frenchName);
			validate(messages);
			crm.updateGroupName(groupId, name);
			return transformer.format(crm.findGroup(groupId), extractLocale(req));
		});
	}

	@PutMapping("/api/groups/{groupId}/enable")
	public void enableGroup(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("groupId") Identifier groupId) throws IOException {
		handle(req, res, Group.class, (messages, transformer) -> {
			confirm(extractBody(req), groupId, messages);
			crm.enableGroup(groupId);
			return transformer.format(crm.findGroup(groupId), extractLocale(req));
		});
	}

	@PutMapping("/api/groups/{groupId}/disable")
	public void disableGroup(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("groupId") Identifier groupId) throws IOException {
		handle(req, res, Group.class, (messages, transformer) -> {
			confirm(extractBody(req), groupId, messages);
			crm.disableGroup(groupId);
			return transformer.format(crm.findGroup(groupId), extractLocale(req));
		});
	}

	@GetMapping("/api/roles")
	public void findRoles(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, Role.class, (messages, transformer) -> { 
			return createPage(
				crm.findRoles(
					extractRolesFilter(extractLocale(req), req), 
					extractPaging(RolesFilter.getDefaultPaging(), req)
				), transformer, extractLocale(req));
		});
	}
	
	private RolesFilter extractRolesFilter(Locale locale, HttpServletRequest req) throws BadRequestException {
		Identifier groupId = req.getParameter("groupId") == null ? null : new Identifier(req.getParameter("groupId"));
		Status status = req.getParameter("status") == null ? null : crm.findStatusByLocalizedName(locale, req.getParameter("status"));
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

	@PostMapping("/api/roles")
	public void createRole(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, Role.class, (messages, transformer) -> {
			JsonObject body = extractBody(req);
			Identifier groupId = getIdentifier(body, "groupId", null, null, messages);
			String code = getString(body, "code", "", null, messages);
			String englishName = getString(body, "englishName", "", null, messages);
			String frenchName = getString(body, "frenchName", "", null, messages);
			Localized name = new Localized(code, englishName, frenchName);
			validate(messages);
			return transformer.format(crm.createRole(groupId, name), extractLocale(req));
		});
	}

	@GetMapping("/api/roles/{roleId}")
	public void getRole(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("roleId") Identifier roleId) throws IOException {
		handle(req, res, Role.class, (messages, transformer) -> {
			return transformer.format(crm.findRole(roleId), extractLocale(req));
		});
	}

	@PatchMapping("/api/roles/{roleId}")
	public void updateRole(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("roleId") Identifier roleId) throws IOException {
		handle(req, res, Role.class, (messages, transformer) -> {
			JsonObject body = extractBody(req);
			String code = getString(body, "code", "", null, messages);
			String englishName = getString(body, "englishName", "", null, messages);
			String frenchName = getString(body, "frenchName", "", null, messages);
			Localized name = new Localized(code, englishName, frenchName);
			validate(messages);
			crm.updateRoleName(roleId, name);
			return transformer.format(crm.findRole(roleId), extractLocale(req));
		});
	}

	@PutMapping("/api/roles/{roleId}/enable")
	public void enableRole(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("roleId") Identifier roleId) throws IOException {
		handle(req, res, Role.class, (messages, transformer) -> {
			confirm(extractBody(req), roleId, messages);
			return transformer.format(crm.enableRole(roleId), extractLocale(req));
		});
	}

	@PutMapping("/api/roles/{roleId}/disable")
	public void disableRole(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("roleId") Identifier roleId) throws IOException {
		handle(req, res, Role.class, (messages, transformer) -> {
			confirm(extractBody(req), roleId, messages);
			return transformer.format(crm.disableRole(roleId), extractLocale(req));
		});
	}

}