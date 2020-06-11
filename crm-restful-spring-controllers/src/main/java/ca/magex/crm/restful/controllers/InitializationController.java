package ca.magex.crm.restful.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import ca.magex.crm.api.common.PersonName;
import ca.magex.json.model.JsonBoolean;
import ca.magex.json.model.JsonFormatter;
import ca.magex.json.model.JsonObject;

@Controller
public class InitializationController extends AbstractCrmController {

	private String authAddress = "localhost";
	
	private String authPort = "9012";
	
	@Value("${server.external.address:localhost}") 
	private String serverAddress;
	
	@Value("${server.port:9002}") 
	private String serverPort;
	
	@Value("${server.servlet.context-path:/}") 
	private String contextPath;

	@GetMapping("/openapi.json")
	public void getJsonConfig(HttpServletRequest req, HttpServletResponse res) throws IOException {
		res.setStatus(200);
		try (InputStream is = getClass().getResource("/crm.json").openStream()) {
			String contents = StreamUtils.copyToString(is, Charset.forName("UTF-8"))
				.replace("${serverAddress}", serverAddress)
				.replace("${serverPort}", serverPort)
				.replace("${contextPath}", contextPath);
			res.getWriter().append(contents);
			res.getWriter().flush();
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
	
	@GetMapping("/config")
	public void config(HttpServletRequest req, HttpServletResponse res) throws IOException {
		JsonObject config = new JsonObject()
			.with("auth", "http://" + authAddress + ":" + authPort + contextPath + "/auth")
			.with("openapi", contextPath + "/api.json")
			.with("rest", "http://" + serverAddress + ":" + serverPort + contextPath + "/rest")
			.with("graphql", "http://" + serverAddress + ":" + serverPort + "/graphql")
			.with("server", new JsonObject()
				.with("address", serverAddress)
				.with("port", serverPort)
				.with("context", contextPath));
		res.setStatus(200);
		res.setContentType(getContentType(req));
		res.getWriter().write(JsonFormatter.formatted(config));
	}
	
}