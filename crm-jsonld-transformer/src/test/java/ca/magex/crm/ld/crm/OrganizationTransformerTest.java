package ca.magex.crm.ld.crm;

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
import ca.magex.crm.api.services.CrmValidation;
import ca.magex.crm.api.services.SecuredCrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.ld.LinkedDataFormatter;
import ca.magex.crm.ld.data.DataObject;
import ca.magex.crm.test.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
public class OrganizationTransformerTest extends AbstractJUnit4SpringContextTests {

	@Autowired private CrmLookupService lookupService;
	@Autowired private CrmValidation validationService;
	@Autowired private CrmOrganizationService organizationService;
	@Autowired private CrmLocationService locationService;
	@Autowired private CrmPersonService personService;

	private SecuredCrmServices service = null;

	@Before
	public void init() {
		AmnesiaAnonymousPolicies policies = new AmnesiaAnonymousPolicies();
		service = new SecuredCrmServices(
				lookupService, validationService,
				organizationService, policies,
				locationService, policies,
				personService, policies);
	}

	@Test
	public void testOrganizationLinkedData() throws Exception {

		Identifier organizationId = new Identifier("abc");
		Status status = Status.ACTIVE;
		String displayName = "Junit Test";
		Identifier mainLocationId = new Identifier("xyz");
		OrganizationDetails organization = new OrganizationDetails(organizationId, status, displayName, mainLocationId);

		DataObject obj = new OrganizationDetailsTransformer(service).format(organization);

		assertEquals("{\n" +
				"  \"displayName\": \"Junit Test\",\n" +
				"  \"status\": \"active\",\n" +
				"  \"mainLocation\": \"xyz\"\n" +
				"}", obj.stringify(LinkedDataFormatter.json()));

		assertEquals("{\n" +
				"  \"@context\": \"http://magex9.github.io/schema/crm\",\n" +
				"  \"@type\": \"OrganizationDetails\",\n" +
				"  \"@value\": \"abc\",\n" +
				"  \"@id\": \"http://magex9.github.io/data/abc\",\n" +
				"  \"displayName\": \"Junit Test\",\n" +
				"  \"status\": {\n" +
				"    \"@context\": \"http://magex9.github.io/schema/system\",\n" +
				"    \"@type\": \"Status\",\n" +
				"    \"@value\": \"active\",\n" +
				"    \"@en\": \"Active\",\n" +
				"    \"@fr\": \"Actif\"\n" +
				"  },\n" +
				"  \"mainLocation\": {\n" +
				"    \"@type\": \"LocationDetails\",\n" +
				"    \"@value\": \"xyz\",\n" +
				"    \"@id\": \"http://magex9.github.io/data/xyz\"\n" +
				"  }\n" +
				"}", obj.formatted());

		OrganizationDetails reloaded = new OrganizationDetailsTransformer(service).parse(obj.formatted());

		assertEquals(organization.getOrganizationId(), reloaded.getOrganizationId());
		assertEquals(organization.getDisplayName(), reloaded.getDisplayName());
		assertEquals(organization.getStatus(), reloaded.getStatus());
		assertEquals(organization.getMainLocationId(), reloaded.getMainLocationId());
	}

}
