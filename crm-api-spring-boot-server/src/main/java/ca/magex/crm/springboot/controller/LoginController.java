package ca.magex.crm.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ca.magex.crm.api.services.CrmConfigurationService;

@Controller
public class LoginController {

	@Autowired private CrmConfigurationService config;
	
	@GetMapping("/login")
	public String doLogin() {
		if (!config.isInitialized()) {
			return "redirect:/initialize";
		}
		return "login";
	}
}
