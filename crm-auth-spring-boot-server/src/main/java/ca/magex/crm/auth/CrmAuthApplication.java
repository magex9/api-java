package ca.magex.crm.auth;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.annotation.Bean;

import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.common.Telephone;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;

@SpringBootApplication(scanBasePackages = {
		"ca.magex.crm.api",					// Generic CRM beans 
		"ca.magex.crm.auth",				// auth server configuration
		"ca.magex.crm.resource", 			// lookup data
		"ca.magex.crm.amnesia",				// crm implementation
		"ca.magex.crm.hazelcast",			// crm implementation
		"ca.magex.crm.spring.security",		// security implementation
		"ca.magex.crm.policy"
})
public class CrmAuthApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(CrmAuthApplication.class);
		/* generate a file called application.pid, used to track the running process */
		app.addListeners(new ApplicationPidFileWriter());
		app.run(args);
	}
	
	@Bean
	public ApplicationRunner dataInitializer() {
		return new ApplicationRunner() {			
			
			@Autowired CrmServices crm;
			
			@Override
			public void run(ApplicationArguments args) throws Exception {
				Logger LOG = LoggerFactory.getLogger(CrmAuthApplication.class);
				
				Group system = crm.createGroup("SYS", new Localized("System", "Système"));
				crm.createRole(system.getGroupId(), "CRM_ADMIN", new Localized("Admin", "Admin"));
				crm.createRole(system.getGroupId(), "SYS_ADMIN", new Localized("SysAdmin", "SysAdmin"));
				crm.createRole(system.getGroupId(), "AUTH_REQUEST", new Localized("AuthRequest", "AuthRequest"));
				
				LOG.info("Creating MageX Organization");
				OrganizationDetails magex = crm.createOrganization("MageX", List.of(system.getCode()));
				Locale locale = Lang.ENGLISH;

				LOG.info("Creating MageX HeadQuarters");
				LocationDetails hq = crm.createLocation(magex.getOrganizationId(), "Head Quarters", "HQ", new MailingAddress("1 Main Street", "Ottawa", "Ontario", "Canada", "K1S 1B9"));
				crm.updateOrganizationMainLocation(magex.getOrganizationId(), hq.getLocationId());
				
				LOG.info("Creating MageX Administrator");
				PersonDetails administrator = crm.createPerson(
						magex.getOrganizationId(),
						new PersonName(null, "Admin", "", "Admin"),
						new MailingAddress("123 Main Street", "Ottawa", "Ontario", "Canada", "K1S 1B9"),
						new Communication("Crm Administrator", crm.findLanguageByCode("en").getName(locale), "crmadmin@magex.ca", new Telephone("613-555-5555"), "613-555-5556"),
						new BusinessPosition(crm.findBusinessSectorByCode("4").getName(locale), crm.findBusinessUnitByCode("4").getName(locale), crm.findBusinessClassificationByCode("4").getName(locale)));
				crm.updateOrganizationMainContact(magex.getOrganizationId(), administrator.getPersonId());
		
				/* create crm user with admin/admin */
				LOG.info("Creating admin user");
				User crmAdminUser = crm.createUser(administrator.getPersonId(), "admin", Arrays.asList("CRM_ADMIN"));
				crm.changePassword(crmAdminUser.getUserId(), "", "admin");				
		
				/* create system user with sysadmin/sysadmin */
				LOG.info("Creating sysadmin user");
				User sysAdminUser = crm.createUser(administrator.getPersonId(), "sysadmin", Arrays.asList("SYS_ADMIN"));
				crm.changePassword(sysAdminUser.getUserId(), "", "sysadmin");
				
				LOG.info("Creating CRM Application Background Person");
				PersonDetails appCrm = crm.createPerson(
						magex.getOrganizationId(),
						new PersonName(null, "Application", "", "CRM"),
						null,
						null,
						null);
				
				LOG.info("Creating app_crm user");
				/* create system user with app_crm/NutritionFactsPer1Can */
				User appCrmUser = crm.createUser(appCrm.getPersonId(), "app_crm", Arrays.asList("AUTH_REQUEST"));
				crm.changePassword(appCrmUser.getUserId(), "", "NutritionFactsPer1Can");
			}
		};
	}
}
