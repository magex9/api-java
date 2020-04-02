package ca.magex.crm.ld.crm;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ca.magex.crm.amnesia.services.AmnesiaFactory;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.services.SecuredCrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.ld.LinkedDataFormatter;
import ca.magex.crm.ld.data.DataObject;

public class OrganizationTransformerTest {

	@Test
	public void testOrganizationLinkedData() throws Exception {
		SecuredCrmServices service = AmnesiaFactory.getAnonymousService();
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
