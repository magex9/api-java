package ca.magex.crm.rest.controllers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import ca.magex.crm.amnesia.services.OrganizationServiceAmnesiaImpl;
import ca.magex.crm.amnesia.services.OrganizationServiceTestDataPopulator;
import ca.magex.crm.api.services.OrganizationPolicyBasicImpl;
import ca.magex.crm.api.services.SecuredOrganizationService;

@SpringBootApplication
public class RestfulApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestfulApiApplication.class, args);
	}

	@Bean
	public SecuredOrganizationService organizations() {
		OrganizationServiceAmnesiaImpl service = new OrganizationServiceAmnesiaImpl();
		OrganizationServiceTestDataPopulator.populate(service);
		//OrganizationPolicyAmnesiaImpl policy = new OrganizationPolicyAmnesiaImpl(service);
		OrganizationPolicyBasicImpl policy = new OrganizationPolicyBasicImpl();
		return new SecuredOrganizationService(service, policy);
	}

}
