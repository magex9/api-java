package ca.magex.crm.restful.controllers;

import java.io.IOException;
import java.io.InputStream;

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
import ca.magex.crm.api.services.Crm;
import ca.magex.json.model.JsonBoolean;
import ca.magex.json.model.JsonFormatter;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonParser;

@Controller
public class InitializationController extends AbstractCrmController {

	@Autowired
	private Crm crm;

	@GetMapping("/api.yaml")
	public void getYamlConfig(HttpServletRequest req, HttpServletResponse res) throws IOException {
		res.setStatus(200);
		try (InputStream is = getClass().getResource("/public/crm.yaml").openStream()) {
			StreamUtils.copy(is, res.getOutputStream());
		}
	}

	@GetMapping("/api.json")
	public void getJsonConfig(HttpServletRequest req, HttpServletResponse res) throws IOException {
		try (InputStream is = getClass().getResource("/public/crm.yaml").openStream()) {
			ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
			Object obj = yamlReader.readValue(is, Object.class);
			ObjectMapper jsonWriter = new ObjectMapper();
			String json = jsonWriter.writeValueAsString(obj);
			res.setStatus(200);
			res.getWriter().write(JsonFormatter.formatted((JsonObject)JsonParser.parse(json)));
		}
	}
	
	@GetMapping("/initialized")
	public void initialized(HttpServletRequest req, HttpServletResponse res) throws IOException {
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(JsonFormatter.formatted(new JsonBoolean(crm.isInitialized())));
	}
	
	@PostMapping("/initialize")
	public void initialize(HttpServletRequest req, HttpServletResponse res) throws IOException {
		if (!crm.isInitialized()) {
			JsonObject body = extractBody(req);
			String organization = body.getString("displayName");
			String firstName= body.getString("firstName");
			String lastName = body.getString("lastName");
			String email = body.getString("email");
			String username = body.getString("username");
			String password = body.getString("password");
			crm.initializeSystem(organization, new PersonName(null, firstName, null, lastName), email, username, password);
		}
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(JsonFormatter.formatted(new JsonBoolean(true)));
	}
	
}