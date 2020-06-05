package ca.magex.crm.transform.json;

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
import ca.magex.crm.api.services.Crm;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.test.TestConfig;
import ca.magex.crm.transform.json.JsonTransformer;
import ca.magex.json.model.JsonFormatter;
import ca.magex.json.model.JsonObject;

public class OrganizationTransformerTest {

//	@Autowired private Crm crm;
//	
//	@Test
//	public void testOrganizationJson() throws Exception {
//
//		Identifier organizationId = new Identifier("abc");
//		Status status = Status.ACTIVE;
//		String displayName = "Junit Test";
//		Identifier mainLocationId = new Identifier("locationRef");
//		Identifier mainContactId = new Identifier("contactRef");
//		List<String> groups = new ArrayList<String>();
//		groups.add(new Group(new Identifier("group"), Status.ACTIVE, GROUP).getCode());
//		OrganizationDetails organization = new OrganizationDetails(organizationId, status, displayName, mainLocationId, mainContactId, groups);
//		JsonTransformer transformer = new JsonTransformer(crm, Lang.ENGLISH, false);
//
//		JsonObject obj = transformer.formatOrganizationDetails(organization);
//		String json = JsonFormatter.formatted(obj);
//
//		assertEquals("{\n" + 
//				"  \"organizationId\": \"abc\",\n" + 
//				"  \"status\": \"Active\",\n" + 
//				"  \"displayName\": \"Junit Test\",\n" + 
//				"  \"mainLocationId\": \"locationRef\",\n" + 
//				"  \"mainContactId\": \"contactRef\",\n" + 
//				"  \"groups\": [\"GRP\"]\n" + 
//				"}", json);
//
//		OrganizationDetails reloaded = transformer.parseOrganizationDetails(obj);
//
//		assertEquals(organization.getOrganizationId(), reloaded.getOrganizationId());
//		assertEquals(organization.getDisplayName(), reloaded.getDisplayName());
//		assertEquals(organization.getStatus(), reloaded.getStatus());
//		assertEquals(organization.getMainLocationId(), reloaded.getMainLocationId());
//	}
//
//	@Test
//	public void testOrganizationLinkedJson() throws Exception {
//
//		Identifier organizationId = new Identifier("abc");
//		Status status = Status.ACTIVE;
//		String displayName = "Junit Test";
//		Identifier mainLocationId = new Identifier("locationRef");
//		Identifier mainContactId = new Identifier("contactRef");
//		List<String> groups = new ArrayList<String>();
//		groups.add(new Group(new Identifier("group"), Status.ACTIVE, GROUP).getCode());
//		OrganizationDetails organization = new OrganizationDetails(organizationId, status, displayName, mainLocationId, mainContactId, groups);
//		JsonTransformer transformer = new JsonTransformer(crm, Lang.ENGLISH, true);
//
//		JsonObject obj = transformer.formatOrganizationDetails(organization);
//		String json = JsonFormatter.formatted(obj);
//
//		assertEquals("{\n" + 
//				"  \"@context\": \"http://magex9.github.io/api/\",\n" + 
//				"  \"@type\": \"OrganizationDetails\",\n" + 
//				"  \"@id\": \"abc\",\n" + 
//				"  \"status\": {\n" + 
//				"    \"@type\": \"Status\",\n" + 
//				"    \"@value\": \"active\",\n" + 
//				"    \"@en\": \"Active\",\n" + 
//				"    \"@fr\": \"Actif\"\n" + 
//				"  },\n" + 
//				"  \"displayName\": \"Junit Test\",\n" + 
//				"  \"mainLocationId\": {\n" + 
//				"    \"@type\": \"LocationDetails\",\n" + 
//				"    \"@id\": \"locationRef\"\n" + 
//				"  },\n" + 
//				"  \"mainContactId\": {\n" + 
//				"    \"@type\": \"PersonDetails\",\n" + 
//				"    \"@id\": \"contactRef\"\n" + 
//				"  },\n" + 
//				"  \"groups\": [\"GRP\"]\n" + 
//				"}", json);
//
//		OrganizationDetails reloaded = transformer.parseOrganizationDetails(obj);
//
//		assertEquals(organization.getOrganizationId(), reloaded.getOrganizationId());
//		assertEquals(organization.getDisplayName(), reloaded.getDisplayName());
//		assertEquals(organization.getStatus(), reloaded.getStatus());
//		assertEquals(organization.getMainLocationId(), reloaded.getMainLocationId());
//	}

}
