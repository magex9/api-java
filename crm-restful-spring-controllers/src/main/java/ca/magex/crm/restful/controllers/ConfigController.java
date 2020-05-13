package ca.magex.crm.restful.controllers;

import static ca.magex.crm.restful.controllers.ContentExtractor.action;
import static ca.magex.crm.restful.controllers.ContentExtractor.extractBody;
import static ca.magex.crm.restful.controllers.ContentExtractor.getContentType;
import static ca.magex.crm.restful.controllers.ContentExtractor.getTransformer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.secured.SecuredCrmServices;
import ca.magex.crm.mapping.data.DataArray;
import ca.magex.crm.mapping.data.DataBoolean;
import ca.magex.crm.mapping.data.DataElement;
import ca.magex.crm.mapping.data.DataFormatter;
import ca.magex.crm.mapping.data.DataObject;
import ca.magex.crm.mapping.data.DataParser;

@Controller
public class ConfigController {

	@Autowired
	private SecuredCrmServices crm;

	@GetMapping("/api.yaml")
	public void getYamlConfig(HttpServletRequest req, HttpServletResponse res) throws Exception {
		res.setStatus(200);
		try (InputStream is = getClass().getResource("/public/crm.yaml").openStream()) {
			StreamUtils.copy(is, res.getOutputStream());
		} catch (IOException ioe) {
			throw new RuntimeException("Error loading crm.yaml");
		}
	}

	@GetMapping("/api.json")
	public void getJsonConfig(HttpServletRequest req, HttpServletResponse res) throws Exception {
		try (InputStream is = getClass().getResource("/public/crm.yaml").openStream()) {
			ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
			Object obj = yamlReader.readValue(is, Object.class);
			ObjectMapper jsonWriter = new ObjectMapper();
			String json = jsonWriter.writeValueAsString(obj);
			res.setStatus(200);
			res.getWriter().write(DataFormatter.formatted((DataObject)DataParser.parse(json)));
		} catch (IOException ioe) {
			throw new RuntimeException("Error loading crm.yaml");
		}
	}
	
	@GetMapping("/api")
	public void dashboard(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String currentUserId = "asdfasdf";
		List<DataElement> actions = new ArrayList<DataElement>();
		actions.add(action("organizations", "Organizations", "get", "/api/organizations"));
		actions.add(action("locations", "Locations", "get", "/api/locations"));
		actions.add(action("persons", "People", "get", "/api/persons"));
		actions.add(action("account", "Account", "get", "/api/persons/" + currentUserId));
		DataObject data = new DataObject().with("_links", new DataArray(actions));
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(DataFormatter.formatted(data));
	}
	
	@GetMapping("/initialized")
	public void initialized(HttpServletRequest req, HttpServletResponse res) throws IOException {
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(DataFormatter.formatted(new DataBoolean(crm.isInitialized())));
	}
	
	@PostMapping("/initialize")
	public void initialize(HttpServletRequest req, HttpServletResponse res) throws IOException {
		if (!crm.isInitialized()) {
			DataObject body = extractBody(req);
			String organization = body.getString("displayName");
			PersonName name = getTransformer(req, crm).parsePersonName("legalName", body);
			String email = body.getString("email");
			String username = body.getString("username");
			String password = body.getString("password");
			crm.initializeSystem(organization, name, email, username, password);
		}
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(DataFormatter.formatted(new DataBoolean(true)));
	}
	
}