package ca.magex.crm.resource;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.common.Telephone;
import ca.magex.crm.api.common.User;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.Lang;

public class CrmDataInitializer {

	private static final Logger LOG = LoggerFactory.getLogger(CrmDataInitializer.class);

	public static void initialize(
			CrmOrganizationService organizationService, 
			CrmLocationService locationService,
			CrmPersonService personService,
			CrmUserService userService,
			CrmLookupService lookupService) {
		
		LOG.info("Creating Magex Organization");
		OrganizationDetails magex = organizationService.createOrganization("Magex");
		Locale locale = Lang.ENGLISH;
		
		for (int i = 1; i < 25; i++) {
			String name = Character.toString((int)'A' + i - 1);
			LocationDetails location = locationService.createLocation(magex.getOrganizationId(), "Location " + name, "LOC" + i, new MailingAddress((100 + i) + " Main Street", "Ottawa", "Ontario", "Canada", "K1S 1B9"));
			if (i < 5)
				locationService.disableLocation(location.getLocationId());
		}
		
		PersonDetails crmAdmin = personService.createPerson(
				magex.getOrganizationId(),
				new PersonName(null, "Crm", "", "Admin"),
				new MailingAddress("123 Main Street", "Ottawa", "Ontario", "Canada", "K1S 1B9"),
				new Communication("Crm Administrator", lookupService.findLanguageByCode("en").getName(locale), "crmadmin@magex.ca", new Telephone("613-555-5555"), "613-555-5556"),
				new BusinessPosition(lookupService.findBusinessSectorByCode("4").getName(locale), lookupService.findBusinessUnitByCode("4").getName(locale), lookupService.findBusinessClassificationByCode("4").getName(locale)));

		/* create crm user with admin/admin */
		User crmAdminUser = userService.createUser(crmAdmin.getPersonId(), "admin", Arrays.asList(lookupService.findRoleByCode("CRM_ADMIN").getCode()));
		userService.setUserPassword(crmAdminUser.getUserId(), "admin", false);

		PersonDetails sysAdmin = personService.createPerson(
				magex.getOrganizationId(),
				new PersonName(null, "System", "", "Admin"),
				new MailingAddress("123 Main Street", "Ottawa", "Ontario", "Canada", "K1S 1B9"),
				new Communication("System Administrator", lookupService.findLanguageByCode("en").getName(locale), "sysadmin@magex.ca", new Telephone("613-555-5555"), "613-555-5556"),
				new BusinessPosition(lookupService.findBusinessSectorByCode("4").getName(locale), lookupService.findBusinessUnitByCode("4").getName(locale), lookupService.findBusinessClassificationByCode("4").getName(locale)));

		/* create system user with sysadmin/sysadmin */
		User sysAdminUser = userService.createUser(sysAdmin.getPersonId(), "sysadmin", Arrays.asList(lookupService.findRoleByCode("SYS_ADMIN").getCode()));
		userService.setUserPassword(sysAdminUser.getUserId(), "sysadmin", false);
		
		LOG.info("Creating System Application User");
		PersonDetails appCrm = personService.createPerson(
				magex.getOrganizationId(),
				new PersonName(null, "Application", "", "CRM"),
				null,
				null,
				null);
		
		/* create system user with app_crm/NutritionFactsPer1Can */
		User appCrmUser = userService.createUser(appCrm.getPersonId(), "app_crm", Arrays.asList(lookupService.findRoleByCode("AUTH_REQUEST").getCode()));
		userService.setUserPassword(appCrmUser.getUserId(), "NutritionFactsPer1Can", false);
	}
	
}
