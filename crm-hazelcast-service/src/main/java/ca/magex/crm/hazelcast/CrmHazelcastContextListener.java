package ca.magex.crm.hazelcast;

import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;

import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.common.Telephone;
import ca.magex.crm.api.common.User;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.lookup.BusinessClassification;
import ca.magex.crm.api.lookup.BusinessSector;
import ca.magex.crm.api.lookup.BusinessUnit;
import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.lookup.Language;
import ca.magex.crm.api.lookup.Salutation;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.Role;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.resource.CrmLookupLoader;

@Component
public class CrmHazelcastContextListener implements ApplicationListener<ContextRefreshedEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(CrmHazelcastContextListener.class);
	
	@Autowired private CrmLookupLoader lookupLoader;
	@Autowired private HazelcastInstance hzInstance;	
	@Autowired private CrmLookupService lookupService;
	@Autowired private CrmOrganizationService organizationService;
	@Autowired private CrmPersonService personService;
	@Autowired private CrmUserService userService;
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		Map<String, Object> initMap = hzInstance.getMap("init");
		if (initMap.containsKey("timestamp")) {
			Long initTimeStamp = (Long) initMap.get("timestamp");
			for (int i=0; i<10; i++) {				
				if (initTimeStamp == 0L) {
					LOG.info("Waiting for hazelcast to be initialized...");
					try {
						Thread.sleep(TimeUnit.SECONDS.toMillis(3));
					}
					catch(InterruptedException ie) {
						if (Thread.currentThread().isInterrupted()) {
							return;
						}
					}
					initTimeStamp = (Long) initMap.get("timestamp");
				}
				else {
					break;
				}
			}
			LOG.info("Hazelcast CRM Previously Initialized on: " + new Date(initTimeStamp));
			return;
		}			
		
		LOG.info("Initializing Hazelcast CRM");
		LOG.info("Initializing Lookups");
		hzInstance.getList("statuses").addAll(Arrays.asList(Status.values()));
		hzInstance.getList("roles").addAll(lookupLoader.loadLookup(Role.class, "Role.csv"));
		hzInstance.getList("countries").addAll(lookupLoader.loadLookup(Country.class, "Country.csv"));
		hzInstance.getList("languages").addAll(lookupLoader.loadLookup(Language.class, "Language.csv"));
		hzInstance.getList("salutations").addAll(lookupLoader.loadLookup(Salutation.class, "Salutation.csv"));
		hzInstance.getList("sectors").addAll(lookupLoader.loadLookup(BusinessSector.class, "BusinessSector.csv"));
		hzInstance.getList("units").addAll(lookupLoader.loadLookup(BusinessUnit.class, "BusinessUnit.csv"));
		hzInstance.getList("classifications").addAll(lookupLoader.loadLookup(BusinessClassification.class, "BusinessClassification.csv"));
		
		LOG.info("Creating Magex Organization");
		OrganizationDetails magex = organizationService.createOrganization("Magex");

		Locale locale = Locale.CANADA;
		
		LOG.info("Creating CRM Admin");
		PersonDetails crmAdmin = personService.createPerson(
				magex.getOrganizationId(),
				new PersonName(null, "Crm", "", "Admin"),
				new MailingAddress("123 Main Street", "Ottawa", "Ontario", lookupService.findCountryByCode("CA").getName(locale), "K1S 1B9"),
				new Communication("Crm Administrator", lookupService.findLanguageByCode("en").getName(locale), "crmadmin@magex.ca", new Telephone("613-555-5555"), "613-555-5556"),
				new BusinessPosition(lookupService.findBusinessSectorByCode("4").getName(locale), lookupService.findBusinessUnitByCode("4").getName(locale), lookupService.findBusinessClassificationByCode("4").getName(locale)));

		/* create crm user with admin/admin */
		User crmAdminUser = userService.createUser(crmAdmin.getPersonId(), "admin", Arrays.asList(lookupService.findRoleByCode("CRM_ADMIN").getCode()));
		userService.setUserPassword(crmAdminUser.getUserId(), "admin");
		
		LOG.info("Creating System Admin");
		PersonDetails sysAdmin = personService.createPerson(
				magex.getOrganizationId(),
				new PersonName(null, "System", "", "Admin"),
				new MailingAddress("123 Main Street", "Ottawa", "Ontario", lookupService.findCountryByCode("CA").getName(locale), "K1S 1B9"),
				new Communication("System Administrator", lookupService.findLanguageByCode("en").getName(locale), "sysadmin@magex.ca", new Telephone("613-555-5555"), "613-555-5556"),
				new BusinessPosition(lookupService.findBusinessSectorByCode("4").getName(locale), lookupService.findBusinessUnitByCode("4").getName(locale), lookupService.findBusinessClassificationByCode("4").getName(locale)));

		/* create system user with sysadmin/sysadmin */
		User sysAdminUser = userService.createUser(sysAdmin.getPersonId(), "sysadmin", Arrays.asList(lookupService.findRoleByCode("CRM_ADMIN").getCode()));
		userService.setUserPassword(sysAdminUser.getUserId(), "sysadmin");
		
		LOG.info("Creating System Application User");
		PersonDetails appCrm = personService.createPerson(
				magex.getOrganizationId(),
				new PersonName(null, "Application", "", "CRM"),
				null,
				null,
				null);

		/* create system user with app_crm/NutritionFactsPer1Can */
		User appCrmUser = userService.createUser(appCrm.getPersonId(), "app_crm", Arrays.asList(lookupService.findRoleByCode("AUTH_REQUEST").getCode()));
		userService.setUserPassword(appCrmUser.getUserId(), "NutritionFactsPer1Can");
	}		
}
