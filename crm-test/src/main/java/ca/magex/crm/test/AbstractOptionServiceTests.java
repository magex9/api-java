package ca.magex.crm.test;

import static ca.magex.crm.test.CrmAsserts.SYSTEM_EMAIL;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_ORG;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_PERSON;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.authentication.CrmAuthenticationService;
import ca.magex.crm.api.filters.OptionsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;

@Transactional
public abstract class AbstractOptionServiceTests {

	@Autowired
	protected Crm crm;
		
	@Autowired
	protected CrmAuthenticationService auth;
	
	@Before
	public void setup() {
		crm.reset();
		crm.initializeSystem(SYSTEM_ORG, SYSTEM_PERSON, SYSTEM_EMAIL, "admin", "admin");
		auth.login("admin", "admin");
	}
	
	@After
	public void cleanup() {
		auth.logout();
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
		Option external = crm.prototypeOption(null, Type.AUTHENTICATION_GROUP, new Localized("EXT", "External", "Externe"));
		external = crm.createOption(external);
		Assert.assertEquals(external, crm.findOption(external.getOptionId()));
		Assert.assertEquals(external, crm.findOptionByCode(Type.AUTHENTICATION_GROUP, external.getCode()));
		Assert.assertEquals("/options/authentication-groups/EXT", external.getOptionId().toString());
		Assert.assertEquals("EXT", external.getName().getCode());
		Assert.assertEquals("External", external.getName().getEnglishName());
		Assert.assertEquals("Externe", external.getName().getFrenchName());
		Assert.assertEquals(Status.ACTIVE, external.getStatus());
		
		Option internal = crm.prototypeOption(null, Type.AUTHENTICATION_GROUP, new Localized("INT", "Internal", "Interne"));
		internal = crm.createOption(internal);
		Assert.assertEquals(internal, crm.findOption(internal.getOptionId()));
		Assert.assertEquals(internal, crm.findOptionByCode(Type.AUTHENTICATION_GROUP, internal.getCode()));
		Assert.assertEquals("/options/authentication-groups/INT", internal.getOptionId().toString());
		Assert.assertEquals("INT", internal.getName().getCode());
		Assert.assertEquals("Internal", internal.getName().getEnglishName());
		Assert.assertEquals("Interne", internal.getName().getFrenchName());
		Assert.assertEquals(Status.ACTIVE, internal.getStatus());
				
		Option eSending = crm.createOption(external.getOptionId(), Type.AUTHENTICATION_GROUP, new Localized("SND", "Sending", "Envoi"));
		Assert.assertEquals(eSending, crm.findOption(eSending.getOptionId()));
		Assert.assertEquals(eSending, crm.findOptionByCode(Type.AUTHENTICATION_GROUP, eSending.getCode()));
		Assert.assertEquals("/options/authentication-groups/EXT/SND", eSending.getOptionId().toString());
		Assert.assertEquals("EXT/SND", eSending.getName().getCode());
		Assert.assertEquals("Sending", eSending.getName().getEnglishName());
		Assert.assertEquals("Envoi", eSending.getName().getFrenchName());
		Assert.assertEquals(Status.ACTIVE, eSending.getStatus());
		
		Option eReceiving = crm.createOption(external.getOptionId(), Type.AUTHENTICATION_GROUP, new Localized("RCV", "Receiving", "Réception"));
		Assert.assertEquals(eReceiving, crm.findOption(eReceiving.getOptionId()));
		Assert.assertEquals(eReceiving, crm.findOptionByCode(Type.AUTHENTICATION_GROUP, eReceiving.getCode()));
		Assert.assertEquals("/options/authentication-groups/EXT/RCV", eReceiving.getOptionId().toString());
		Assert.assertEquals("EXT/RCV", eReceiving.getName().getCode());
		Assert.assertEquals("Receiving", eReceiving.getName().getEnglishName());
		Assert.assertEquals("Réception", eReceiving.getName().getFrenchName());
		Assert.assertEquals(Status.ACTIVE, eReceiving.getStatus());
		
		Option esAdmin = crm.createOption(eSending.getOptionId(), Type.AUTHENTICATION_ROLE, new Localized("ADM", "Admin", "Admin"));
		Assert.assertEquals(esAdmin, crm.findOption(esAdmin.getOptionId()));
		Assert.assertEquals(esAdmin, crm.findOptionByCode(Type.AUTHENTICATION_ROLE, esAdmin.getCode()));
		Assert.assertEquals("/options/authentication-roles/EXT/SND/ADM", esAdmin.getOptionId().toString());
		Assert.assertEquals("EXT/SND/ADM", esAdmin.getName().getCode());
		Assert.assertEquals("Admin", esAdmin.getName().getEnglishName());
		Assert.assertEquals("Admin", esAdmin.getName().getFrenchName());
		Assert.assertEquals(Status.ACTIVE, esAdmin.getStatus());
		
		Option esUser = crm.createOption(eSending.getOptionId(), Type.AUTHENTICATION_ROLE, new Localized("USR", "User", "User"));
		Assert.assertEquals(esUser, crm.findOption(esUser.getOptionId()));
		Assert.assertEquals(esUser, crm.findOptionByCode(Type.AUTHENTICATION_ROLE, esUser.getCode()));
		Assert.assertEquals("/options/authentication-roles/EXT/SND/USR", esUser.getOptionId().toString());
		Assert.assertEquals("EXT/SND/USR", esUser.getName().getCode());
		Assert.assertEquals("User", esUser.getName().getEnglishName());
		Assert.assertEquals("User", esUser.getName().getFrenchName());
		Assert.assertEquals(Status.ACTIVE, esUser.getStatus());
		
		Option erAdmin = crm.createOption(eReceiving.getOptionId(), Type.AUTHENTICATION_ROLE, new Localized("ADM", "Admin", "Admin"));
		Assert.assertEquals(erAdmin, crm.findOption(erAdmin.getOptionId()));
		Assert.assertEquals(erAdmin, crm.findOptionByCode(Type.AUTHENTICATION_ROLE, erAdmin.getCode()));
		Assert.assertEquals("/options/authentication-roles/EXT/RCV/ADM", erAdmin.getOptionId().toString());
		Assert.assertEquals("EXT/RCV/ADM", erAdmin.getName().getCode());
		Assert.assertEquals("Admin", erAdmin.getName().getEnglishName());
		Assert.assertEquals("Admin", erAdmin.getName().getFrenchName());
		Assert.assertEquals(Status.ACTIVE, erAdmin.getStatus());
		
		Option erUser = crm.createOption(eReceiving.getOptionId(), Type.AUTHENTICATION_ROLE, new Localized("USR", "User", "User"));
		Assert.assertEquals(erUser, crm.findOption(erUser.getOptionId()));
		Assert.assertEquals(erUser, crm.findOptionByCode(Type.AUTHENTICATION_ROLE, erUser.getCode()));
		Assert.assertEquals("/options/authentication-roles/EXT/RCV/USR", erUser.getOptionId().toString());
		Assert.assertEquals("EXT/RCV/USR", erUser.getName().getCode());
		Assert.assertEquals("User", erUser.getName().getEnglishName());
		Assert.assertEquals("User", erUser.getName().getFrenchName());
		Assert.assertEquals(Status.ACTIVE, erUser.getStatus());
		
		Option iAdmin = crm.createOption(internal.getOptionId(), Type.AUTHENTICATION_ROLE, new Localized("ADM", "Admin", "Admin"));
		Assert.assertEquals(iAdmin, crm.findOption(iAdmin.getOptionId()));
		Assert.assertEquals(iAdmin, crm.findOptionByCode(Type.AUTHENTICATION_ROLE, iAdmin.getCode()));
		Assert.assertEquals("/options/authentication-roles/INT/ADM", iAdmin.getOptionId().toString());
		Assert.assertEquals("INT/ADM", iAdmin.getName().getCode());
		Assert.assertEquals("Admin", iAdmin.getName().getEnglishName());
		Assert.assertEquals("Admin", iAdmin.getName().getFrenchName());
		Assert.assertEquals(Status.ACTIVE, iAdmin.getStatus());
		
		Option iUser = crm.createOption(internal.getOptionId(), Type.AUTHENTICATION_ROLE, new Localized("USR", "User", "User"));
		Assert.assertEquals(iUser, crm.findOption(iUser.getOptionId()));
		Assert.assertEquals(iUser, crm.findOptionByCode(Type.AUTHENTICATION_ROLE, iUser.getCode()));
		Assert.assertEquals("/options/authentication-roles/INT/USR", iUser.getOptionId().toString());
		Assert.assertEquals("INT/USR", iUser.getName().getCode());
		Assert.assertEquals("User", iUser.getName().getEnglishName());
		Assert.assertEquals("User", iUser.getName().getFrenchName());
		Assert.assertEquals(Status.ACTIVE, iUser.getStatus());
		
		/* test disabling an option */
		iUser = crm.disableOption(iUser.getOptionId());
		Assert.assertEquals(iUser, crm.findOption(iUser.getOptionId()));
		Assert.assertEquals(iUser, crm.findOptionByCode(Type.AUTHENTICATION_ROLE, iUser.getCode()));
		Assert.assertEquals("/options/authentication-roles/INT/USR", iUser.getOptionId().toString());
		Assert.assertEquals("INT/USR", iUser.getName().getCode());
		Assert.assertEquals("User", iUser.getName().getEnglishName());
		Assert.assertEquals("User", iUser.getName().getFrenchName());
		Assert.assertEquals(Status.INACTIVE, iUser.getStatus());
		
		/* test enable an option */
		iUser = crm.enableOption(iUser.getOptionId());
		Assert.assertEquals(iUser, crm.findOption(iUser.getOptionId()));
		Assert.assertEquals(iUser, crm.findOptionByCode(Type.AUTHENTICATION_ROLE, iUser.getCode()));
		Assert.assertEquals("/options/authentication-roles/INT/USR", iUser.getOptionId().toString());
		Assert.assertEquals("INT/USR", iUser.getName().getCode());
		Assert.assertEquals("User", iUser.getName().getEnglishName());
		Assert.assertEquals("User", iUser.getName().getFrenchName());
		Assert.assertEquals(Status.ACTIVE, iUser.getStatus());
		
		
		/* test updating an option name */
		iUser = crm.updateOptionName(iUser.getOptionId(), new Localized(iUser.getCode(), "Internal User", "User Interne"));
		Assert.assertEquals(iUser, crm.findOption(iUser.getOptionId()));
		Assert.assertEquals(iUser, crm.findOptionByCode(Type.AUTHENTICATION_ROLE, iUser.getCode()));
		Assert.assertEquals("/options/authentication-roles/INT/USR", iUser.getOptionId().toString());
		Assert.assertEquals("INT/USR", iUser.getName().getCode());
		Assert.assertEquals("Internal User", iUser.getName().getEnglishName());
		Assert.assertEquals("User Interne", iUser.getName().getFrenchName());
		Assert.assertEquals(Status.ACTIVE, iUser.getStatus());
		
		/* find our options */
		OptionsFilter filter = new OptionsFilter().withType(Type.AUTHENTICATION_GROUP).withParentId(external.getOptionId());
		Paging paging = new Paging(1, 10, Sort.by("englishName"));
		
		FilteredPage<Option> optionsPage = crm.findOptions(filter, paging);
		Assert.assertEquals(1, optionsPage.getNumber());
		Assert.assertEquals(10, optionsPage.getSize());
		Assert.assertEquals(2, optionsPage.getNumberOfElements());
		
	}		
}
