package ca.magex.crm.graphql.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
		"ca.magex.crm.springboot",
		})
public class GraphQLClientTestConfig {

}
