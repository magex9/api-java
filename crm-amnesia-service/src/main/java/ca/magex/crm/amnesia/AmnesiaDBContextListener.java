package ca.magex.crm.amnesia;

import java.util.Arrays;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.common.Telephone;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.common.User;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.Lang;

@Component
public class AmnesiaDBContextListener implements ApplicationListener<ContextRefreshedEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(AmnesiaDBContextListener.class);

	@Autowired private AmnesiaDB db;
	@Autowired(required = false) private PasswordEncoder passwordEncoder;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		LOG.info("Creating Magex Organization");
		OrganizationDetails magex = db.api().createOrganization("Magex");
		Locale locale = Lang.ENGLISH;
		
		for (int i = 1; i < 25; i++) {
			String name = Character.toString((int)'A' + i - 1);
			LocationDetails location = db.api().createLocation(magex.getOrganizationId(), "Location " + name, "LOC" + i, new MailingAddress((100 + i) + " Main Street", "Ottawa", "Ontario", "Canada", "K1S 1B9"));
			if (i < 5)
				db.api().disableLocation(location.getLocationId());
		}
		
		PersonDetails crmAdmin = db.api().createPerson(
				magex.getOrganizationId(),
				new PersonName(null, "Crm", "", "Admin"),
				new MailingAddress("123 Main Street", "Ottawa", "Ontario", "Canada", "K1S 1B9"),
				new Communication("Crm Administrator", db.api().findLanguageByCode("en").getName(locale), "crmadmin@magex.ca", new Telephone("613-555-5555"), "613-555-5556"),
				new BusinessPosition(db.api().findBusinessSectorByCode("4").getName(locale), db.api().findBusinessUnitByCode("4").getName(locale), db.api().findBusinessClassificationByCode("4").getName(locale)));

		/* create crm user with admin/admin */
		User crmAdminUser = db.api().createUser(crmAdmin.getPersonId(), "admin", Arrays.asList(db.api().findRoleByCode("CRM_ADMIN").getCode()));
		db.api().setUserPassword(crmAdminUser.getUserId(), passwordEncoder == null ? "admin" : passwordEncoder.encode("admin"));

		PersonDetails sysAdmin = db.api().createPerson(
				magex.getOrganizationId(),
				new PersonName(null, "System", "", "Admin"),
				new MailingAddress("123 Main Street", "Ottawa", "Ontario", "Canada", "K1S 1B9"),
				new Communication("System Administrator", db.api().findLanguageByCode("en").getName(locale), "sysadmin@magex.ca", new Telephone("613-555-5555"), "613-555-5556"),
				new BusinessPosition(db.api().findBusinessSectorByCode("4").getName(locale), db.api().findBusinessUnitByCode("4").getName(locale), db.api().findBusinessClassificationByCode("4").getName(locale)));

		/* create system user with sysadmin/sysadmin */
		User sysAdminUser = db.api().createUser(sysAdmin.getPersonId(), "sysadmin", Arrays.asList(db.api().findRoleByCode("CRM_ADMIN").getCode()));
		db.api().setUserPassword(sysAdminUser.getUserId(), passwordEncoder == null ? "admin" : passwordEncoder.encode("sysadmin"));
	}
	
}
