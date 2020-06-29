package ca.magex.crm.test;

import static ca.magex.crm.test.CrmAsserts.SYSTEM_EMAIL;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_ORG;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_PERSON;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.authentication.CrmAuthenticationService;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Option;
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
		Assert.assertEquals("/options/authentication-groups/EXT", external.getOptionId().toString());
		
		Option internal = crm.prototypeOption(null, Type.AUTHENTICATION_GROUP, new Localized("INT", "Internal", "Interne"));
		internal = crm.createOption(internal);
		Assert.assertEquals("/options/authentication-groups/INT", internal.getOptionId().toString());
		
		Option eSending = crm.createOption(external.getOptionId(), Type.AUTHENTICATION_GROUP, new Localized("SND", "Sending", "Envoi"));
		Assert.assertEquals("/options/authentication-groups/EXT/SND", eSending.getOptionId().toString());
		
		Option eReceiving = crm.createOption(external.getOptionId(), Type.AUTHENTICATION_GROUP, new Localized("RCV", "Receiving", "Réception"));
		Assert.assertEquals("/options/authentication-groups/EXT/RCV", eReceiving.getOptionId().toString());
		
		Option esAdmin = crm.createOption(eSending.getOptionId(), Type.AUTHENTICATION_ROLE, new Localized("ADM", "Admin", "Admin"));
		Assert.assertEquals("/options/authentication-roles/EXT/SND/ADM", esAdmin.getOptionId().toString());
		
		Option esUser = crm.createOption(eSending.getOptionId(), Type.AUTHENTICATION_ROLE, new Localized("USR", "User", "User"));
		Assert.assertEquals("/options/authentication-roles/EXT/SND/USR", esUser.getOptionId().toString());
		
		Option erAdmin = crm.createOption(eReceiving.getOptionId(), Type.AUTHENTICATION_ROLE, new Localized("ADM", "Admin", "Admin"));
		Assert.assertEquals("/options/authentication-roles/EXT/RCV/ADM", erAdmin.getOptionId().toString());
		
		Option erUser = crm.createOption(eReceiving.getOptionId(), Type.AUTHENTICATION_ROLE, new Localized("USR", "User", "User"));
		Assert.assertEquals("/options/authentication-roles/EXT/RCV/USR", erUser.getOptionId().toString());
		
		Option iAdmin = crm.createOption(internal.getOptionId(), Type.AUTHENTICATION_ROLE, new Localized("ADM", "Admin", "Admin"));
		Assert.assertEquals("/options/authentication-roles/INT/ADM", iAdmin.getOptionId().toString());
		
		Option iUser = crm.createOption(internal.getOptionId(), Type.AUTHENTICATION_ROLE, new Localized("USR", "User", "User"));
		Assert.assertEquals("/options/authentication-roles/INT/USR", iUser.getOptionId().toString());
	}
	
}
