package ca.magex.crm.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ca.magex.crm.api.Crm;

@Controller
public class LoginController {

	@Autowired private Crm crm = null;
	
	@GetMapping("/login")
	public String doLogin() {
		if (!crm.isInitialized()) {
			return "redirect:/initialize";
		}
		return "login";
	}
}
