package ca.magex.crm.rest;

import java.text.SimpleDateFormat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import ca.magex.crm.amnesia.services.OrganizationServiceAmnesiaImpl;
import ca.magex.crm.amnesia.services.OrganizationServiceTestDataPopulator;
import ca.magex.crm.api.crm.Organization;
import ca.magex.crm.api.services.OrganizationPolicyBasicImpl;
import ca.magex.crm.api.services.SecuredOrganizationService;
import ca.magex.crm.api.system.Identifier;

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
	
	@Bean
	public Jackson2ObjectMapperBuilderCustomizer customizer(SecuredOrganizationService organizations) {
		return new Jackson2ObjectMapperBuilderCustomizer() {
			@Override
			public void customize(Jackson2ObjectMapperBuilder builder) {
				builder
					.serializerByType(Identifier.class, new IdentifierSerializer())
					.serializerByType(Organization.class, new OrganizationSerializer(organizations))
					.indentOutput(true)
					.dateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
			}
		};
	}

}