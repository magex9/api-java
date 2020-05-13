package ca.magex.crm.resource;

public class CrmDataInitializer {

//	private static final Logger LOG = LoggerFactory.getLogger(CrmDataInitializer.class);
//
//	public static void initialize(CrmServices crm) {
//
//		CrmSetup.initializeRoles(crm);
//		
//		LOG.info("Creating MageX Organization");
//		OrganizationDetails magex = organizationService.createOrganization("MageX");
//		Locale locale = Lang.ENGLISH;
//		
//		for (int i = 1; i < 25; i++) {
//			String name = Character.toString((int)'A' + i - 1);
//			LocationDetails location = locationService.createLocation(magex.getOrganizationId(), "Location " + name, "LOC" + i, new MailingAddress((100 + i) + " Main Street", "Ottawa", "Ontario", "Canada", "K1S 1B9"));
//			if (i < 5)
//				locationService.disableLocation(location.getLocationId());
//		}
//		
//		PersonDetails crmAdmin = personService.createPerson(
//				magex.getOrganizationId(),
//				new PersonName(null, "Crm", "", "Admin"),
//				new MailingAddress("123 Main Street", "Ottawa", "Ontario", "Canada", "K1S 1B9"),
//				new Communication("Crm Administrator", lookupService.findLanguageByCode("en").getName(locale), "crmadmin@magex.ca", new Telephone("613-555-5555"), "613-555-5556"),
//				new BusinessPosition(lookupService.findBusinessSectorByCode("4").getName(locale), lookupService.findBusinessUnitByCode("4").getName(locale), lookupService.findBusinessClassificationByCode("4").getName(locale)));
//
//		/* create crm user with admin/admin */
//		User crmAdminUser = userService.createUser(crmAdmin.getPersonId(), "admin", Arrays.asList("CRM_ADMIN"));
//		passwordService.updatePassword(crmAdminUser.getUserId().toString(), "admin");
//
//		PersonDetails sysAdmin = personService.createPerson(
//				magex.getOrganizationId(),
//				new PersonName(null, "System", "", "Admin"),
//				new MailingAddress("123 Main Street", "Ottawa", "Ontario", "Canada", "K1S 1B9"),
//				new Communication("System Administrator", lookupService.findLanguageByCode("en").getName(locale), "sysadmin@magex.ca", new Telephone("613-555-5555"), "613-555-5556"),
//				new BusinessPosition(lookupService.findBusinessSectorByCode("4").getName(locale), lookupService.findBusinessUnitByCode("4").getName(locale), lookupService.findBusinessClassificationByCode("4").getName(locale)));
//
//		/* create system user with sysadmin/sysadmin */
//		User sysAdminUser = userService.createUser(sysAdmin.getPersonId(), "sysadmin", Arrays.asList("SYS_ADMIN"));
//		passwordService.updatePassword(sysAdminUser.getUserId().toString(), "sysadmin");
//		
//		LOG.info("Creating System Application User");
//		PersonDetails appCrm = personService.createPerson(
//				magex.getOrganizationId(),
//				new PersonName(null, "Application", "", "CRM"),
//				null,
//				null,
//				null);
//		
//		/* create system user with app_crm/NutritionFactsPer1Can */
//		User appCrmUser = userService.createUser(appCrm.getPersonId(), "app_crm", Arrays.asList("AUTH_REQUEST"));
//		passwordService.updatePassword(appCrmUser.getUserId().toString(), "NutritionFactsPer1Can");
//	}
	
}
