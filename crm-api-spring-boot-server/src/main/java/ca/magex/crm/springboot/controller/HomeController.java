package ca.magex.crm.springboot.controller;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.services.Crm;
import ca.magex.crm.api.services.CrmAuthenticationService;
import ca.magex.crm.springboot.model.CrmInitializationRequestVO;

@Controller
public class HomeController {

	@Autowired private Crm crm = null;
	@Autowired private CrmAuthenticationService auth;

	@GetMapping("/")
	public String getWelcomePage(Model model) {
		if (!crm.isInitialized()) {
			model.addAttribute("initializationRequest", new CrmInitializationRequestVO());
			return "initialize";
		}
//		if (auth.getCurrentUser() != null) {		
		return "home";
//		}
//		else {
//			return "redirect:/login";
//		}		
	}

	@PostMapping("/initialize")
	public String initializeCrm(@ModelAttribute CrmInitializationRequestVO initializationRequest) {
		try {
			User initialUser = crm.initializeSystem(
					initializationRequest.getOrganizationName(),
					new PersonName(
							null,
							initializationRequest.getOwnerGivenName(),
							initializationRequest.getOwnerMiddleName(),
							initializationRequest.getOwnerSurname()),
					initializationRequest.getOwnerEmail(),
					initializationRequest.getUsername(),
					initializationRequest.getPassword());
			LoggerFactory.getLogger(getClass()).info("CRM System Initialized with initial user: " + initialUser);
		}
		catch(ApiException e) {
			LoggerFactory.getLogger(getClass()).error("Error Initializing CRM System", e);
		}		
		return "redirect:/";
	}
}