package ca.magex.crm.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WelcomePageController {
	
	@GetMapping("/")
	public String getWelcomePage() {
		return "forward:" + "/auth";
	}
}
