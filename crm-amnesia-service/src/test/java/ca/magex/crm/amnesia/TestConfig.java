package ca.magex.crm.amnesia;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import ca.magex.crm.amnesia.services.CrmServicesTestSuite;

@Configuration
@ComponentScan(basePackages = {"ca.magex.crm.amnesia"})
public class TestConfig {
	
	@Bean
	public CrmServicesTestSuite getCrmServicesTest() {
		return new CrmServicesTestSuite();
	}
	
}
