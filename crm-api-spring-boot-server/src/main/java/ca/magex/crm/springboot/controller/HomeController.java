package ca.magex.crm.springboot.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import ca.magex.crm.api.services.CrmConfigurationService;

@Controller()
public class HomeController {

	@Autowired private CrmConfigurationService config;

	@GetMapping("/")
	public String getRoot() {
		if (!config.isInitialized()) {
			return "redirect:/initialize";
		}
		return "redirect:/home";
	}

	@GetMapping("/home")
	public String getHomePage(Model model, HttpServletRequest req) {
		model.addAttribute("username", req.getUserPrincipal().getName());
		return "home";
	}
}