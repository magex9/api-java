package ca.magex.crm.graphql.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Description("Controller for handling the query UI")
@CrossOrigin
public class GraphQLQueryController {

	@Value("${server.external.address:}") String serverAddress;
	@Value("${server.port:8080}") String serverPort;
	@Value("${server.servlet.context-path:/crm}") String contextPath;
	
	@GetMapping("/graphql/query")
	public String getQueryUI(Model model, HttpServletRequest req) {
		model.addAttribute("username", req.getUserPrincipal().getName());
		if (StringUtils.isBlank(serverAddress)) {
			model.addAttribute("server", contextPath);
		}
		else {
			model.addAttribute("server", "http://" + serverAddress + ":" + serverPort + contextPath);
		}
		return "query";
	}
}