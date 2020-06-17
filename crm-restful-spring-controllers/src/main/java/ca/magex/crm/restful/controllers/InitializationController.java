package ca.magex.crm.restful.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import ca.magex.crm.api.common.PersonName;
import ca.magex.json.model.JsonBoolean;
import ca.magex.json.model.JsonFormatter;
import ca.magex.json.model.JsonObject;

@Controller
public class InitializationController extends AbstractCrmController {

	@GetMapping("/rest/initialized")
	public void initialized(HttpServletRequest req, HttpServletResponse res) throws IOException {
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(JsonFormatter.formatted(new JsonBoolean(crm.isInitialized())));
	}

	@PostMapping("/rest/initialize")
	public void initialize(HttpServletRequest req, HttpServletResponse res) throws IOException {
		if (!crm.isInitialized()) {
			JsonObject body = extractBody(req);
			String organization = body.getString("displayName");
			String firstName = body.getString("firstName");
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