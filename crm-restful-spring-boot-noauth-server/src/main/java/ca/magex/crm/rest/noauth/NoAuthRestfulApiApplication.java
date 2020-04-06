package ca.magex.crm.rest.controllers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import ca.magex.crm.amnesia.services.AmnesiaFactory;
import ca.magex.crm.amnesia.services.AmnesiaTestDataPopulator;
import ca.magex.crm.api.services.SecuredCrmServices;

@SpringBootApplication
public class RestfulApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestfulApiApplication.class, args);
	}

	@Bean
	public SecuredCrmServices organizations() {
		return AmnesiaTestDataPopulator.populate(AmnesiaFactory.getAnonymousService());
	}

}
