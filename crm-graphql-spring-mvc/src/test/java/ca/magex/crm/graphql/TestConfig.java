package ca.magex.crm.graphql;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import ca.magex.crm.api.services.Crm;

@Configuration
@ComponentScan(basePackages = {"ca.magex.crm.graphql"})
public class TestConfig {

	@Bean
	public Crm getCrm() {
		return Mockito.mock(Crm.class);
	}	
}
