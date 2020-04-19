package ca.magex.crm.graphql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.system.Role;

/**
 * Application Runner that Sets up some initial users for the system
 * 
 * @author Jonny
 *
 */
@Component
public class CrmSetup implements ApplicationRunner {
	
	private static Logger LOG = LoggerFactory.getLogger(CrmSetup.class);
	
	@Autowired private CrmOrganizationService organizationService;
	@Autowired private CrmPersonService personService;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		LOG.info("Creating Magex Organization");
		
		/* create the default organization */
		OrganizationDetails magex = organizationService.createOrganization("Magex");

		LOG.info("Creating CRM Admin");
		PersonDetails admin = personService.createPerson(magex.getOrganizationId(),
				new PersonName(null, "Crm", "", "Admin"), null, null, null);
		personService.addUserRole(admin.getPersonId(), new Role("CRM_ADMIN", "admin", "admin"));
		admin = personService.setUserPassword(admin.getPersonId(), "admin");
		LOG.info("CRM Admin: " + admin.getUser().getUserName());

		LOG.info("Creating System Admin");
		PersonDetails system = personService.createPerson(magex.getOrganizationId(),
				new PersonName(null, "System", "", "Admin"), null, null, null);
		personService.addUserRole(system.getPersonId(), new Role("SYSTEM_ADMIN", "admin", "admin"));
		system = personService.setUserPassword(system.getPersonId(), "admin");

		LOG.info("System Admin: " + system.getUser().getUserName());
	}
}