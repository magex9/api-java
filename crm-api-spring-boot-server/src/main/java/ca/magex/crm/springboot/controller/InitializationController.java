package ca.magex.crm.springboot.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.services.CrmConfigurationService;
import ca.magex.crm.springboot.model.CrmInitializationRequestVO;

@Controller("crmInitializationController")
public class InitializationController {

	@Autowired private CrmConfigurationService config;
	
	@GetMapping("/initialize")
	public String getInitializeForm(
			Model model,
			HttpServletRequest request,
			HttpServletResponse response) {
		if (config.isInitialized()) {
			return "redirect:/";
		}
		model.addAttribute("crmInitializationRequestVO", new CrmInitializationRequestVO());
		return "initialize";
	}
	
	@PostMapping("/initialize")
	public String initializeCrm(
			@Validated CrmInitializationRequestVO crmInitializationRequestVO, 
			BindingResult bindingResult, 
			HttpServletRequest request,
			HttpServletResponse response) {
		if (config.isInitialized()) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			return "redirect:/";
		}
		try {			
			if (bindingResult.hasErrors()) {
				return "initialize";
			}
			else {
				config.initializeSystem(
					crmInitializationRequestVO.getOrganizationName(),
					new PersonName(
						null,
						crmInitializationRequestVO.getOwnerGivenName(),
						crmInitializationRequestVO.getOwnerMiddleName(),
						crmInitializationRequestVO.getOwnerSurname()),
					crmInitializationRequestVO.getOwnerEmail(),
					crmInitializationRequestVO.getUsername(),
					crmInitializationRequestVO.getPassword());
				LoggerFactory.getLogger(getClass()).info("CRM System Initialized with initial user: " + crmInitializationRequestVO.getUsername());
			}
		}
		catch(ApiException e) {
			LoggerFactory.getLogger(getClass()).error("Error Initializing CRM System", e);
		}		
		return "redirect:/";
	}
}