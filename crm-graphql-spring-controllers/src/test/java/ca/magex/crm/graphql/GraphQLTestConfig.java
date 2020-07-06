package ca.magex.crm.graphql;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
		"ca.magex.crm.graphql"
		})
public class GraphQLTestConfig {

//	/* autowired services */
//	@Autowired private CrmInitializationService initializationService;	
//	@Autowired private CrmLookupService lookupService;	
//	@Autowired private CrmOrganizationService organizationService;
//	@Autowired private CrmLocationService locationService;
//	@Autowired private CrmPersonService personService;
//	@Autowired private CrmUserService userService;
//	@Autowired private CrmPermissionService permissionService;
//		
//	@Bean
//	@Primary
//	@Override
//	public Crm crm() {		
//		return new Crm(
//				initializationService, 
//				lookupService, 
//				permissionService, 
//				organizationService, 
//				locationService, 
//				personService,
//				userService, 
//				crmPolicies());
//	}
//	
//	@Bean
//	@Override
//	public CrmPolicies crmPolicies() {
//		return new BasicPolicies(lookupService, permissionService, organizationService, locationService, personService, userService);
//	}
}
