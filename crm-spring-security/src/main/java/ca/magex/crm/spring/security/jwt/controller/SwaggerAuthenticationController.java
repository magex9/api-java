package ca.magex.crm.spring.security.jwt.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.magex.crm.spring.security.auth.AuthProfiles;

@Controller
@Profile({AuthProfiles.EMBEDDED_HMAC, AuthProfiles.EMBEDDED_RSA})
public class SwaggerAuthenticationController {

	@Value("${server.external.address:}") String serverAddress;
	@Value("${server.port}") String serverPort;
	@Value("${server.servlet.context-path}") String contextPath;

	@GetMapping("/auth-yaml")
	public void getSwaggerYaml(HttpServletResponse res) throws IOException {
		try (InputStream yaml = getClass().getResourceAsStream("/auth.yaml")) {
			String server = StringUtils.isBlank(serverAddress) ?
					contextPath : "http://" + serverAddress + ":" + serverPort + contextPath;
			String yamlContents = StreamUtils.copyToString(yaml, Charset.forName("UTF-8"))
					.replace("${server}", server);
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
