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
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SwaggerRestfulController {

	@Value("${server.external.address:localhost}") 
	private String serverAddress;
	
	@Value("${server.port:9002}") 
	private String serverPort;
	
	@Value("${server.servlet.context-path:/crm}") 
	private String contextPath;
	
	@GetMapping("/rest/api.json")
	public void getJsonConfig(HttpServletRequest req, HttpServletResponse res) throws IOException {		
		try (InputStream is = getClass().getResource("/crm.json").openStream()) {
			String contents = StreamUtils.copyToString(is, Charset.forName("UTF-8"))
				.replace("${serverAddress}", serverAddress)
				.replace("${serverPort}", serverPort)
				.replace("${contextPath}", contextPath);
			res.getWriter().append(contents);
			res.getWriter().flush();
		}
	}
	
	@ResponseBody
	@GetMapping("/rest")
	public String getSwaggerRestfulPage(HttpServletRequest req, HttpServletResponse res) throws IOException {
		try (InputStream html = getClass().getResourceAsStream("/crm-restful-swagger.html")) {
			String htmlContents = StreamUtils.copyToString(html, Charset.forName("UTF-8"));
			return htmlContents.replace("${contextPath}", contextPath);
		}
	}
}
