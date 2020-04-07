package ca.magex.crm.graphql.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/")
@Controller
public class WelcomePageController {

	@GetMapping
	public String getWelcomePage() {
		return "forward:/graphiql";
	}
}
