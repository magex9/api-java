package ca.magex.crm.amnesia.services;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.magex.crm.amnesia.AmnesiaDB;
import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.test.AbstractUserServiceTests;
import ca.magex.crm.test.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
@ActiveProfiles(MagexCrmProfiles.CRM_DATASTORE_CENTRALIZED)
public class AmnesiaUserServiceTests extends AbstractUserServiceTests {

	@Autowired
	private AmnesiaDB db;
	
	@Autowired
	private CrmUserService users;

	private PersonDetails adam = new PersonDetails(new Identifier("Adam"), new Identifier("DC"), Status.ACTIVE, "Adam", null, null, null, null);

	private PersonDetails bob = new PersonDetails(new Identifier("Bob"), new Identifier("DC"), Status.ACTIVE, "Bob", null, null, null, null);

	@Before
	public void reset() {
		db.reset();
		db.savePerson(adam);
		db.savePerson(bob);
		db.saveRole(new Role(new Identifier("ADM"), new Identifier("AA"), "ADM", Status.ACTIVE, new Localized("ADM")));
		db.saveRole(new Role(new Identifier("USR"), new Identifier("ZZ"), "USR", Status.ACTIVE, new Localized("USR")));
		db.saveRole(new Role(new Identifier("PPL"), new Identifier("ZZ"), "PPL", Status.ACTIVE, new Localized("PPL")));
	}

	@Override
	public CrmUserService getUserService() {
		return users;
	}

	@Override
	public PersonDetails getAdam() {
		return adam;
	}

	@Override
	public PersonDetails getBob() {
		return bob;
	}

}
