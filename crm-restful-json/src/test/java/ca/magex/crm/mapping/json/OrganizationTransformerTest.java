package ca.magex.crm.mapping.json;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.magex.crm.amnesia.services.AmnesiaAnonymousPolicies;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.SecuredCrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.mapping.data.DataFormatter;
import ca.magex.crm.mapping.data.DataObject;
import ca.magex.crm.test.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
public class OrganizationTransformerTest extends AbstractJUnit4SpringContextTests {

	@Autowired private CrmLookupService lookupService;
	@Autowired private CrmOrganizationService organizationService;
	@Autowired private CrmLocationService locationService;
	@Autowired private CrmPersonService personService;

	private SecuredCrmServices service = null;

	@Before
	public void init() {
		AmnesiaAnonymousPolicies policies = new AmnesiaAnonymousPolicies();
		service = new SecuredCrmServices(
				lookupService,
				organizationService, policies,
				locationService, policies,
				personService, policies);
	}

	@Test
	public void testOrganizationJson() throws Exception {

		Identifier organizationId = new Identifier("abc");
		Status status = Status.ACTIVE;
		String displayName = "Junit Test";
		Identifier mainLocationId = new Identifier("xyz");
		OrganizationDetails organization = new OrganizationDetails(organizationId, status, displayName, mainLocationId);
		JsonTransformer transformer = new JsonTransformer(service, Lang.ENGLISH, false);

		DataObject obj = transformer.formatOrganizationDetails(organization);
		String json = DataFormatter.formatted(obj);

		assertEquals("{\n" + 
				"  \"organizationId\": \"abc\",\n" + 
				"  \"status\": \"Active\",\n" + 
				"  \"displayName\": \"Junit Test\",\n" + 
				"  \"mainLocationId\": \"xyz\"\n" + 
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
		Identifier mainLocationId = new Identifier("xyz");
		OrganizationDetails organization = new OrganizationDetails(organizationId, status, displayName, mainLocationId);
		JsonTransformer transformer = new JsonTransformer(service, Lang.ENGLISH, true);

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
				"    \"@id\": \"xyz\"\n" + 
				"  }\n" + 
				"}", json);

		OrganizationDetails reloaded = transformer.parseOrganizationDetails(obj);

		assertEquals(organization.getOrganizationId(), reloaded.getOrganizationId());
		assertEquals(organization.getDisplayName(), reloaded.getDisplayName());
		assertEquals(organization.getStatus(), reloaded.getStatus());
		assertEquals(organization.getMainLocationId(), reloaded.getMainLocationId());
	}

}
