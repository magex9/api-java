package ca.magex.crm.hazelcast.service;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.services.Crm;
import ca.magex.crm.test.AbstractPermissionServiceTests;
import ca.magex.crm.test.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
@ActiveProfiles(profiles =  {MagexCrmProfiles.CRM_DATASTORE_DECENTRALIZED, MagexCrmProfiles.CRM_NO_AUTH} )
public class HazelcastPermissionServiceTests extends AbstractPermissionServiceTests {

	@Autowired
	public void setCrm(Crm crm) {
		this.crm = crm;
	}
}