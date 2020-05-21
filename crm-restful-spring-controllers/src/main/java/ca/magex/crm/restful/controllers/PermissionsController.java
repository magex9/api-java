package ca.magex.crm.restful.controllers;

import static ca.magex.crm.restful.controllers.ContentExtractor.extractBody;
import static ca.magex.crm.restful.controllers.ContentExtractor.extractPaging;
import static ca.magex.crm.restful.controllers.ContentExtractor.getContentType;
import static ca.magex.crm.restful.controllers.ContentExtractor.getTransformer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.filters.GroupsFilter;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.secured.SecuredCrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.mapping.data.DataArray;
import ca.magex.crm.mapping.data.DataElement;
import ca.magex.crm.mapping.data.DataFormatter;
import ca.magex.crm.mapping.data.DataObject;
import ca.magex.crm.mapping.data.DataParser;
import ca.magex.crm.mapping.json.JsonTransformer;

@Controller
public class PermissionsController {

	@Autowired
	private SecuredCrmServices crm;
	
	@GetMapping("/api/groups")
	public void findGroups(HttpServletRequest req, HttpServletResponse res) throws IOException {
		JsonTransformer transformer = getTransformer(req, crm);
		Page<Group> page = crm.findGroups(extractGroupsFilter(req), extractPaging(req));
		DataObject data = createPage(page, e -> transformer.formatGroup(e));
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(DataFormatter.formatted(data));
	}
	
	public GroupsFilter extractGroupsFilter(HttpServletRequest req) throws BadRequestException {
		Map<String, Object> entries = new HashMap<String, Object>();
		req.getParameterMap().entrySet().forEach(e -> {
			entries.put(e.getKey(), e.getValue()[0]);
		});
		return new GroupsFilter(entries);
	}

	@PostMapping("/api/groups")
	public void createGroup(HttpServletRequest req, HttpServletResponse res) throws IOException {
		JsonTransformer transformer = getTransformer(req, crm);
		DataObject body = extractBody(req);
		String code = body.getString("code");
		String englishName = body.getString("englishName");
		String frenchName = body.getString("frenchName");
		Localized name = new Localized(code, englishName, frenchName);
		DataElement data = transformer.formatGroup(crm.createGroup(name));
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(DataFormatter.formatted(data));
	}

	@GetMapping("/api/groups/{groupId}")
	public void getGroup(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("groupId") String id) throws IOException {
		JsonTransformer transformer = getTransformer(req, crm);
		Identifier groupId = new Identifier(id);
		DataElement data = transformer.formatGroup(crm.findGroup(groupId));
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(DataFormatter.formatted(data));
	}

	@PatchMapping("/api/groups/{groupId}")
	public void updateGroup(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("groupId") String id) throws IOException {
		JsonTransformer transformer = getTransformer(req, crm);
		Identifier groupId = new Identifier(id);
		DataObject body = extractBody(req);
		String code = body.getString("code");
		String englishName = body.getString("englishName");
		String frenchName = body.getString("frenchName");
		Localized name = new Localized(code, englishName, frenchName);
		crm.updateGroupName(groupId, name);
		DataElement data = transformer.formatGroup(crm.findGroup(groupId));
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(DataFormatter.formatted(data));
	}
	
	public <T> DataObject createPage(Page<T> page, Function<T, DataElement> mapper) {
		return new DataObject()
			.with("page", page.getNumber())
			.with("total", page.getTotalElements())
			.with("hasNext", page.hasNext())
			.with("hasPrevious", page.hasPrevious())
			.with("content", new DataArray(page.getContent().stream().map(mapper).collect(Collectors.toList())));
	}
	
	public static DataObject extractBody(HttpServletRequest req) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		StreamUtils.copy(req.getInputStream(), baos);		
		return DataParser.parseObject(new String(baos.toByteArray()));
	}

}