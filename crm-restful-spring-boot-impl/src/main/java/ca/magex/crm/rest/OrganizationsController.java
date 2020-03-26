package ca.magex.crm.rest;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import ca.magex.crm.api.crm.Organization;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.services.SecuredOrganizationService;
import ca.magex.crm.api.system.Identifier;

@RestController
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class OrganizationsController {

	private SecuredOrganizationService organizations;
	
	public OrganizationsController(SecuredOrganizationService organizations) {
		this.organizations = organizations;
	}
	
	@GetMapping("/organizations")
	public List<Organization> all() {
		return organizations.findOrganizations(new OrganizationsFilter()).getContent();
	}
	
	@PostMapping("/organizations")
	public Organization createOrganization(String organizationName) {
		return organizations.createOrganization(organizationName);
	}
	
	@GetMapping("/organizations/{organizationId}")
	public Organization one(@PathVariable String organizationId) {
		return organizations.findOrganization(new Identifier(organizationId));
	}
	
}
