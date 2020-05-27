package ca.magex.crm.spring.security.jwt.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.magex.crm.api.MagexCrmProfiles;

@Controller
@Profile(MagexCrmProfiles.AUTH_EMBEDDED_JWT)
public class SwaggerAuthenticationController {
	
	@Value("${server.external.address:localhost}") String serverAddress;
	@Value("${server.port}") String serverPort;
	@Value("${server.servlet.context-path}") String contextPath;
	
	
	@GetMapping("/auth-yaml")
	public void getSwaggerYaml(HttpServletResponse res) throws IOException {
		try (InputStream yaml = getClass().getResourceAsStream("/auth.yaml")) {
			String yamlContents = StreamUtils.copyToString(yaml, Charset.forName("UTF-8"));
			yamlContents = yamlContents.replace("${serverAddress}", serverAddress);
			yamlContents = yamlContents.replace("${serverPort}", serverPort);
			yamlContents = yamlContents.replace("${contextPath}", contextPath);
			res.getWriter().append(yamlContents);
			res.getWriter().flush();
		}
	}
	
	@ResponseBody
	@GetMapping("/auth")	
	public String getSwaggerAuthPage(HttpServletResponse res) throws IOException {
		try (InputStream html = getClass().getResourceAsStream("/auth-swagger.html")) {
			String htmlContents = StreamUtils.copyToString(html, Charset.forName("UTF-8"));
			return htmlContents.replace("${contextPath}", contextPath);
		}
	}
}
