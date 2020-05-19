package ca.magex.crm.mapping.json;

import static ca.magex.crm.test.CrmAsserts.GROUP;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.secured.SecuredCrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.mapping.data.DataFormatter;
import ca.magex.crm.mapping.data.DataObject;
import ca.magex.crm.test.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
@ActiveProfiles(MagexCrmProfiles.CRM_DATASTORE_CENTRALIZED)
public class OrganizationTransformerTest {

	@Autowired private SecuredCrmServices crm;
	
	@Test
	public void testOrganizationJson() throws Exception {

		Identifier organizationId = new Identifier("abc");
		Status status = Status.ACTIVE;
		String displayName = "Junit Test";
		Identifier mainLocationId = new Identifier("locationRef");
		Identifier mainContactId = new Identifier("contactRef");
		List<String> groups = new ArrayList<String>();
		groups.add(new Group(new Identifier("group"), Status.ACTIVE, GROUP).getCode());
		OrganizationDetails organization = new OrganizationDetails(organizationId, status, displayName, mainLocationId, mainContactId, groups);
		JsonTransformer transformer = new JsonTransformer(crm, Lang.ENGLISH, false);

		DataObject obj = transformer.formatOrganizationDetails(organization);
		String json = DataFormatter.formatted(obj);

		assertEquals("{\n" + 
				"  \"organizationId\": \"abc\",\n" + 
				"  \"status\": \"Active\",\n" + 
				"  \"displayName\": \"Junit Test\",\n" + 
				"  \"mainLocationId\": \"locationRef\",\n" + 
				"  \"mainContactId\": \"contactRef\",\n" + 
				"  \"groupIds\": [\"group\"]\n" + 
				"}", json);

		OrganizationDetails reloaded = transformer.parseOrganizationDetails(obj);

		assertEquals(organization.getOrganizationId(), reloaded.getOrganizationId());
		assertEquals(organization.getDisplayName(), reloaded.getDisplayName());
		assertEquals(organization.getStatus(), reloaded.getStatus());
		assertEquals(organization.getMainLocationId(), reloaded.getMainLocationId());
	}

	@Test
	public void testOrganizationLinkedData() throws Exception {

		Identifier organizationId = new Identifier("abc");
		Status status = Status.ACTIVE;
		String displayName = "Junit Test";
		Identifier mainLocationId = new Identifier("locationRef");
		Identifier mainContactId = new Identifier("contactRef");
		List<String> groups = new ArrayList<String>();
		groups.add(new Group(new Identifier("group"), Status.ACTIVE, GROUP).getCode());
		OrganizationDetails organization = new OrganizationDetails(organizationId, status, displayName, mainLocationId, mainContactId, groups);
		JsonTransformer transformer = new JsonTransformer(crm, Lang.ENGLISH, true);

		DataObject obj = transformer.formatOrganizationDetails(organization);
		String json = DataFormatter.formatted(obj);

		assertEquals("{\n" + 
				"  \"@context\": \"http://magex9.github.io/api/\",\n" + 
				"  \"@type\": \"OrganizationDetails\",\n" + 
				"  \"@id\": \"abc\",\n" + 
				"  \"status\": {\n" + 
				"    \"@type\": \"Status\",\n" + 
				"    \"@value\": \"active\",\n" + 
				"    \"@en\": \"Active\",\n" + 
				"    \"@fr\": \"Actif\"\n" + 
				"  },\n" + 
				"  \"displayName\": \"Junit Test\",\n" + 
				"  \"mainLocationId\": {\n" + 
				"    \"@type\": \"LocationDetails\",\n" + 
				"    \"@id\": \"locationRef\"\n" + 
				"  },\n" + 
				"  \"mainContactId\": {\n" + 
				"    \"@type\": \"LocationDetails\",\n" + 
				"    \"@id\": \"contactRef\"\n" + 
				"  },\n" + 
				"  \"groupIds\": [{\n" + 
				"    \"@type\": \"Group\",\n" + 
				"    \"@id\": \"group\"\n" + 
				"  }]\n" + 
				"}", json);

		OrganizationDetails reloaded = transformer.parseOrganizationDetails(obj);

		assertEquals(organization.getOrganizationId(), reloaded.getOrganizationId());
		assertEquals(organization.getDisplayName(), reloaded.getDisplayName());
		assertEquals(organization.getStatus(), reloaded.getStatus());
		assertEquals(organization.getMainLocationId(), reloaded.getMainLocationId());
	}

}
