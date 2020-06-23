package ca.magex.crm.test.validation;

import static ca.magex.crm.test.CrmAsserts.GROUP;
import static ca.magex.crm.test.CrmAsserts.assertMessage;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.CrmProfiles;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.services.CrmGroupService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.test.config.BasicTestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { BasicTestConfig.class })
@ActiveProfiles(CrmProfiles.CRM_DATASTORE_CENTRALIZED)
public class CrmGroupValidationTests {

	@Autowired
	private Crm crm;
	
	@Before
	public void setup() {
		crm.reset();
	}
	
	@Test
	public void testGroupStatusMissing() throws Exception {
		Identifier groupId = new Identifier("group");
		Group group = new Group(groupId, null, GROUP);
		List<Message> messages = CrmGroupService.validateGroup(crm, group);
		messages.forEach(System.out::println);
		assertEquals(1, messages.size());
		assertMessage(messages.get(0), groupId, "error", "status", "Status is mandatory for a group");
	}
	
	@Test
	public void testGroupStatusPendingWithId() throws Exception {
		Identifier groupId = new Identifier("group");
		Group group = new Group(groupId, Status.PENDING, GROUP);
		List<Message> messages = CrmGroupService.validateGroup(crm, group);
		assertEquals(1, messages.size());
		assertMessage(messages.get(0), groupId, "error", "status", "Pending statuses should not have identifiers");
	}
	
	@Test
	public void testGroupStatusPendingWithoutId() throws Exception {
		Group group = new Group(null, Status.PENDING, GROUP);
		List<Message> messages = CrmGroupService.validateGroup(crm, group);
		assertEquals(0, messages.size());
	}
	
	
	
}
