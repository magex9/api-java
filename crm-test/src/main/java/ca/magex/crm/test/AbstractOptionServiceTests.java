package ca.magex.crm.test;

import static ca.magex.crm.test.CrmAsserts.SYSTEM_EMAIL;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_ORG;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_PERSON;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import ca.magex.crm.api.authentication.CrmAuthenticationService;
import ca.magex.crm.api.filters.OptionsFilter;
import ca.magex.crm.api.services.CrmConfigurationService;
import ca.magex.crm.api.services.CrmOptionService;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;

@Transactional
public abstract class AbstractOptionServiceTests {

	/**
	 * Configuration Service used to setup the system for testing
	 * @return
	 */
	protected abstract CrmConfigurationService config();
	
	/**
	 * Configuration Service used to setup the system for testing
	 * @return
	 */
	protected abstract CrmServices crm();
	
	/**
	 * Authentication service used to allow an authenticated test
	 * @return
	 */
	protected abstract CrmAuthenticationService auth();
	
	/**
	 * The CRM Services to be tested
	 * @return
	 */
	protected abstract CrmOptionService options();
	
	@Before
	public void setup() {
		config().reset();
		config().initializeSystem(SYSTEM_ORG, SYSTEM_PERSON, SYSTEM_EMAIL, "admin", "admin");
		auth().login("admin", "admin");
	}
	
	@After
	public void cleanup() {
		auth().logout();
	}
	
	@Test
	public void testInitializedRoles() {
		Option option = options().findOptionByCode(Type.BUSINESS_ROLE, "IMIT/DEV/APPS/DEV");
		Assert.assertNotNull(option);
		Assert.assertEquals("Developer", option.getName(Lang.ENGLISH));
		Assert.assertEquals("Développeur", option.getName(Lang.FRENCH));
	}
	
	@Test
	public void testAuthenticationOptions() {
		/*
		 * Create the following Authentication Hierarchy
		 * EXT
		 * 		SND
		 * 			ADMIN
		 * 			USER
		 * 		RCV
		 * 			ADMIN
		 * 			USER
		 * INT
		 * 		ADMIN
		 * 		USER
		 * 
		 */
		Option external = options().prototypeOption(null, Type.AUTHENTICATION_GROUP, new Localized("EXT", "External", "Externe"));
		external = options().createOption(external);
		Assert.assertEquals(external, options().findOption(external.getOptionId()));
		Assert.assertEquals(external, options().findOptionByCode(Type.AUTHENTICATION_GROUP, external.getCode()));
		Assert.assertEquals("/options/authentication-groups/EXT", external.getOptionId().toString());
		Assert.assertEquals("EXT", external.getName().getCode());
		Assert.assertEquals("External", external.getName().getEnglishName());
		Assert.assertEquals("Externe", external.getName().getFrenchName());
		Assert.assertEquals(Status.ACTIVE, external.getStatus());
		
		Option internal = options().prototypeOption(null, Type.AUTHENTICATION_GROUP, new Localized("INT", "Internal", "Interne"));
		internal = options().createOption(internal);
		Assert.assertEquals(internal, options().findOption(internal.getOptionId()));
		Assert.assertEquals(internal, options().findOptionByCode(Type.AUTHENTICATION_GROUP, internal.getCode()));
		Assert.assertEquals("/options/authentication-groups/INT", internal.getOptionId().toString());
		Assert.assertEquals("INT", internal.getName().getCode());
		Assert.assertEquals("Internal", internal.getName().getEnglishName());
		Assert.assertEquals("Interne", internal.getName().getFrenchName());
		Assert.assertEquals(Status.ACTIVE, internal.getStatus());
				
		Option eSending = options().createOption(external.getOptionId(), Type.AUTHENTICATION_GROUP, new Localized("SND", "Sending", "Envoi"));
		Assert.assertEquals(eSending, options().findOption(eSending.getOptionId()));
		Assert.assertEquals(eSending, options().findOptionByCode(Type.AUTHENTICATION_GROUP, eSending.getCode()));
		Assert.assertEquals("/options/authentication-groups/EXT/SND", eSending.getOptionId().toString());
		Assert.assertEquals("EXT/SND", eSending.getName().getCode());
		Assert.assertEquals("Sending", eSending.getName().getEnglishName());
		Assert.assertEquals("Envoi", eSending.getName().getFrenchName());
		Assert.assertEquals(Status.ACTIVE, eSending.getStatus());
		
		Option eReceiving = options().createOption(external.getOptionId(), Type.AUTHENTICATION_GROUP, new Localized("RCV", "Receiving", "Réception"));
		Assert.assertEquals(eReceiving, options().findOption(eReceiving.getOptionId()));
		Assert.assertEquals(eReceiving, options().findOptionByCode(Type.AUTHENTICATION_GROUP, eReceiving.getCode()));
		Assert.assertEquals("/options/authentication-groups/EXT/RCV", eReceiving.getOptionId().toString());
		Assert.assertEquals("EXT/RCV", eReceiving.getName().getCode());
		Assert.assertEquals("Receiving", eReceiving.getName().getEnglishName());
		Assert.assertEquals("Réception", eReceiving.getName().getFrenchName());
		Assert.assertEquals(Status.ACTIVE, eReceiving.getStatus());
		
		Option esAdmin = options().createOption(eSending.getOptionId(), Type.AUTHENTICATION_ROLE, new Localized("ADM", "Admin", "Admin"));
		Assert.assertEquals(esAdmin, options().findOption(esAdmin.getOptionId()));
		Assert.assertEquals(esAdmin, options().findOptionByCode(Type.AUTHENTICATION_ROLE, esAdmin.getCode()));
		Assert.assertEquals("/options/authentication-roles/EXT/SND/ADM", esAdmin.getOptionId().toString());
		Assert.assertEquals("EXT/SND/ADM", esAdmin.getName().getCode());
		Assert.assertEquals("Admin", esAdmin.getName().getEnglishName());
		Assert.assertEquals("Admin", esAdmin.getName().getFrenchName());
		Assert.assertEquals(Status.ACTIVE, esAdmin.getStatus());
		
		Option esUser = options().createOption(eSending.getOptionId(), Type.AUTHENTICATION_ROLE, new Localized("USR", "User", "User"));
		Assert.assertEquals(esUser, options().findOption(esUser.getOptionId()));
		Assert.assertEquals(esUser, options().findOptionByCode(Type.AUTHENTICATION_ROLE, esUser.getCode()));
		Assert.assertEquals("/options/authentication-roles/EXT/SND/USR", esUser.getOptionId().toString());
		Assert.assertEquals("EXT/SND/USR", esUser.getName().getCode());
		Assert.assertEquals("User", esUser.getName().getEnglishName());
		Assert.assertEquals("User", esUser.getName().getFrenchName());
		Assert.assertEquals(Status.ACTIVE, esUser.getStatus());
		
		Option erAdmin = options().createOption(eReceiving.getOptionId(), Type.AUTHENTICATION_ROLE, new Localized("ADM", "Admin", "Admin"));
		Assert.assertEquals(erAdmin, options().findOption(erAdmin.getOptionId()));
		Assert.assertEquals(erAdmin, options().findOptionByCode(Type.AUTHENTICATION_ROLE, erAdmin.getCode()));
		Assert.assertEquals("/options/authentication-roles/EXT/RCV/ADM", erAdmin.getOptionId().toString());
		Assert.assertEquals("EXT/RCV/ADM", erAdmin.getName().getCode());
		Assert.assertEquals("Admin", erAdmin.getName().getEnglishName());
		Assert.assertEquals("Admin", erAdmin.getName().getFrenchName());
		Assert.assertEquals(Status.ACTIVE, erAdmin.getStatus());
		
		Option erUser = options().createOption(eReceiving.getOptionId(), Type.AUTHENTICATION_ROLE, new Localized("USR", "User", "User"));
		Assert.assertEquals(erUser, options().findOption(erUser.getOptionId()));
		Assert.assertEquals(erUser, options().findOptionByCode(Type.AUTHENTICATION_ROLE, erUser.getCode()));
		Assert.assertEquals("/options/authentication-roles/EXT/RCV/USR", erUser.getOptionId().toString());
		Assert.assertEquals("EXT/RCV/USR", erUser.getName().getCode());
		Assert.assertEquals("User", erUser.getName().getEnglishName());
		Assert.assertEquals("User", erUser.getName().getFrenchName());
		Assert.assertEquals(Status.ACTIVE, erUser.getStatus());
		
		Option iAdmin = options().createOption(internal.getOptionId(), Type.AUTHENTICATION_ROLE, new Localized("ADM", "Admin", "Admin"));
		Assert.assertEquals(iAdmin, options().findOption(iAdmin.getOptionId()));
		Assert.assertEquals(iAdmin, options().findOptionByCode(Type.AUTHENTICATION_ROLE, iAdmin.getCode()));
		Assert.assertEquals("/options/authentication-roles/INT/ADM", iAdmin.getOptionId().toString());
		Assert.assertEquals("INT/ADM", iAdmin.getName().getCode());
		Assert.assertEquals("Admin", iAdmin.getName().getEnglishName());
		Assert.assertEquals("Admin", iAdmin.getName().getFrenchName());
		Assert.assertEquals(Status.ACTIVE, iAdmin.getStatus());
		
		Option iUser = options().createOption(internal.getOptionId(), Type.AUTHENTICATION_ROLE, new Localized("USR", "User", "User"));
		Assert.assertEquals(iUser, options().findOption(iUser.getOptionId()));
		Assert.assertEquals(iUser, options().findOptionByCode(Type.AUTHENTICATION_ROLE, iUser.getCode()));
		Assert.assertEquals("/options/authentication-roles/INT/USR", iUser.getOptionId().toString());
		Assert.assertEquals("INT/USR", iUser.getName().getCode());
		Assert.assertEquals("User", iUser.getName().getEnglishName());
		Assert.assertEquals("User", iUser.getName().getFrenchName());
		Assert.assertEquals(Status.ACTIVE, iUser.getStatus());
		
		/* test disabling an option */
		iUser = options().disableOption(iUser.getOptionId());
		Assert.assertEquals(iUser, options().findOption(iUser.getOptionId()));
		Assert.assertEquals(iUser, options().findOptionByCode(Type.AUTHENTICATION_ROLE, iUser.getCode()));
		Assert.assertEquals("/options/authentication-roles/INT/USR", iUser.getOptionId().toString());
		Assert.assertEquals("INT/USR", iUser.getName().getCode());
		Assert.assertEquals("User", iUser.getName().getEnglishName());
		Assert.assertEquals("User", iUser.getName().getFrenchName());
		Assert.assertEquals(Status.INACTIVE, iUser.getStatus());
		
		/* test enable an option */
		iUser = options().enableOption(iUser.getOptionId());
		Assert.assertEquals(iUser, options().findOption(iUser.getOptionId()));
		Assert.assertEquals(iUser, options().findOptionByCode(Type.AUTHENTICATION_ROLE, iUser.getCode()));
		Assert.assertEquals("/options/authentication-roles/INT/USR", iUser.getOptionId().toString());
		Assert.assertEquals("INT/USR", iUser.getName().getCode());
		Assert.assertEquals("User", iUser.getName().getEnglishName());
		Assert.assertEquals("User", iUser.getName().getFrenchName());
		Assert.assertEquals(Status.ACTIVE, iUser.getStatus());
		
		
		/* test updating an option name */
		iUser = options().updateOptionName(iUser.getOptionId(), new Localized(iUser.getCode(), "Internal User", "User Interne"));
		Assert.assertEquals(iUser, options().findOption(iUser.getOptionId()));
		Assert.assertEquals(iUser, options().findOptionByCode(Type.AUTHENTICATION_ROLE, iUser.getCode()));
		Assert.assertEquals("/options/authentication-roles/INT/USR", iUser.getOptionId().toString());
		Assert.assertEquals("INT/USR", iUser.getName().getCode());
		Assert.assertEquals("Internal User", iUser.getName().getEnglishName());
		Assert.assertEquals("User Interne", iUser.getName().getFrenchName());
		Assert.assertEquals(Status.ACTIVE, iUser.getStatus());
		
		/* find all of our groups within the external group */
		FilteredPage<Option> optionsPage = options().findOptions(
				new OptionsFilter().withType(Type.AUTHENTICATION_GROUP).withParentId(external.getOptionId()), 
				OptionsFilter.getDefaultPaging());
		Assert.assertEquals(1, optionsPage.getNumber());
		Assert.assertEquals(10, optionsPage.getSize());
		Assert.assertEquals(2, optionsPage.getNumberOfElements());
		Assert.assertTrue(optionsPage.getContent().contains(eSending));
		Assert.assertTrue(optionsPage.getContent().contains(eReceiving));
		
		/* find all of our ADM coded roles */
		optionsPage = options().findOptions(
				new OptionsFilter().withType(Type.AUTHENTICATION_ROLE).withName(new Localized(Lang.ROOT, "ADM")), 
				OptionsFilter.getDefaultPaging());
		Assert.assertEquals(1, optionsPage.getNumber());
		Assert.assertEquals(10, optionsPage.getSize());
		Assert.assertEquals(3, optionsPage.getNumberOfElements());
		Assert.assertTrue(optionsPage.getContent().contains(iAdmin));
		Assert.assertTrue(optionsPage.getContent().contains(esAdmin));
		Assert.assertTrue(optionsPage.getContent().contains(erAdmin));
		
		/* find all of our Admin named roles */
		optionsPage = options().findOptions(
				new OptionsFilter().withType(Type.AUTHENTICATION_ROLE).withName(new Localized(Lang.ENGLISH, "Admin")), 
				OptionsFilter.getDefaultPaging());
		Assert.assertEquals(1, optionsPage.getNumber());
		Assert.assertEquals(10, optionsPage.getSize());
		Assert.assertEquals(3, optionsPage.getNumberOfElements());
		Assert.assertTrue(optionsPage.getContent().contains(iAdmin));
		Assert.assertTrue(optionsPage.getContent().contains(esAdmin));
		Assert.assertTrue(optionsPage.getContent().contains(erAdmin));
	}
}
