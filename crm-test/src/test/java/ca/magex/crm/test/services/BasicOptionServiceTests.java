package ca.magex.crm.test.services;

import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.magex.crm.api.CrmProfiles;
import ca.magex.crm.test.config.BasicTestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { BasicTestConfig.class })
@ActiveProfiles(CrmProfiles.CRM_DATASTORE_CENTRALIZED)
public class BasicOptionServiceTests {

}
