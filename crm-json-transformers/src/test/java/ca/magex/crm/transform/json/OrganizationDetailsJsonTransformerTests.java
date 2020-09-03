package ca.magex.crm.transform.json;

import static ca.magex.crm.test.CrmAsserts.SYSTEM_EMAIL;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_ORG;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_PERSON;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.services.CrmConfigurationService;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.transform.Transformer;
import ca.magex.crm.test.config.BasicTestConfig;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { BasicTestConfig.class })
public class OrganizationDetailsJsonTransformerTests {
	
	@Autowired private Crm crm;
	
	@Autowired private CrmConfigurationService config;
	
	private Transformer<OrganizationDetails, JsonElement> transformer;
	
	private OrganizationIdentifier organizationId;
	
	private LocationIdentifier mainLocationId;
	
	private PersonIdentifier mainContactId;
	
	private OrganizationDetails organization;
	
	@Before
	public void setup() {
		config.initializeSystem(SYSTEM_ORG, SYSTEM_PERSON, SYSTEM_EMAIL, "admin", "admin");
		transformer = new OrganizationDetailsJsonTransformer(crm);
		organizationId = new OrganizationIdentifier("Mc9rbFKPqf");
		mainLocationId = new LocationIdentifier("Z9XCi4sCTk");
		mainContactId = new PersonIdentifier("5Z7cuX3K2T");
		organization = new OrganizationDetails(organizationId, Status.ACTIVE, "Org Name", 
			mainLocationId, mainContactId, List.of(
			crm.findOptionByCode(Type.AUTHENTICATION_GROUP, "CRM").getOptionId(),
			crm.findOptionByCode(Type.AUTHENTICATION_GROUP, "ORG").getOptionId()
		), List.of(crm.findOptionByCode(Type.BUSINESS_GROUP,  "EXECS").getOptionId()), 100L);
	}
	
	@Test
	public void testTransformerType() throws Exception {
		assertEquals(OrganizationDetails.class, transformer.getSourceType());
	}

	@Test
	public void testFormatNull() throws Exception {
		assertNull(transformer.format(null, null));
		assertNull(transformer.format(null, Lang.ROOT));
		assertNull(transformer.format(null, Lang.ENGLISH));
		assertNull(transformer.format(null, Lang.FRENCH));
	}
	
	@Test
	public void testLinkedJson() throws Exception {
		JsonObject linked = (JsonObject)transformer.format(organization, null);
		//JsonAsserts.print(linked, "linked");
		assertEquals(List.of("@context", "organizationId", "status", "displayName", "mainLocationId", "mainContactId", "authenticationGroupIds", "businessGroupIds", "lastModified"), linked.keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/organization/OrganizationDetails", linked.getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/organizations/" + organizationId.getCode(), linked.getString("organizationId"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getObject("status").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Statuses", linked.getObject("status").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/statuses/active", linked.getObject("status").getString("@id"));
		assertEquals("ACTIVE", linked.getObject("status").getString("@value"));
		assertEquals("Active", linked.getObject("status").getString("@en"));
		assertEquals("Actif", linked.getObject("status").getString("@fr"));
		assertEquals("Org Name", linked.getString("displayName"));
		assertEquals("http://api.magex.ca/crm/rest/locations/" + mainLocationId.getCode(), linked.getString("mainLocationId"));
		assertEquals("http://api.magex.ca/crm/rest/persons/" + mainContactId.getCode(), linked.getString("mainContactId"));
		assertEquals(2, linked.getArray("authenticationGroupIds").size());
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getArray("authenticationGroupIds").getObject(0).keys());
		assertEquals("http://api.magex.ca/crm/schema/options/AuthenticationGroups", linked.getArray("authenticationGroupIds").getObject(0).getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/authentication-groups/crm", linked.getArray("authenticationGroupIds").getObject(0).getString("@id"));
		assertEquals("CRM", linked.getArray("authenticationGroupIds").getObject(0).getString("@value"));
		assertEquals("Customer Relationship Management", linked.getArray("authenticationGroupIds").getObject(0).getString("@en"));
		assertEquals("Gestion de la relation client", linked.getArray("authenticationGroupIds").getObject(0).getString("@fr"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getArray("authenticationGroupIds").getObject(1).keys());
		assertEquals("http://api.magex.ca/crm/schema/options/AuthenticationGroups", linked.getArray("authenticationGroupIds").getObject(1).getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/authentication-groups/org", linked.getArray("authenticationGroupIds").getObject(1).getString("@id"));
		assertEquals("ORG", linked.getArray("authenticationGroupIds").getObject(1).getString("@value"));
		assertEquals("Organization", linked.getArray("authenticationGroupIds").getObject(1).getString("@en"));
		assertEquals("Organisation", linked.getArray("authenticationGroupIds").getObject(1).getString("@fr"));
		assertEquals(1, linked.getArray("businessGroupIds").size());
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getArray("businessGroupIds").getObject(0).keys());
		assertEquals("http://api.magex.ca/crm/schema/options/BusinessGroups", linked.getArray("businessGroupIds").getObject(0).getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/business-groups/execs", linked.getArray("businessGroupIds").getObject(0).getString("@id"));
		assertEquals("EXECS", linked.getArray("businessGroupIds").getObject(0).getString("@value"));
		assertEquals("Executives", linked.getArray("businessGroupIds").getObject(0).getString("@en"));
		assertEquals("Cadres", linked.getArray("businessGroupIds").getObject(0).getString("@fr"));		
		assertEquals(100L, linked.getNumber("lastModified"));
		assertEquals(organization, transformer.parse(linked, null));
	}
	
	@Test
	public void testRootJson() throws Exception {
		JsonObject root = (JsonObject)transformer.format(organization, Lang.ROOT);
		//JsonAsserts.print(root, "root");
		assertEquals(List.of("organizationId", "status", "displayName", "mainLocationId", "mainContactId", "authenticationGroupIds", "businessGroupIds", "lastModified"), root.keys());
		assertEquals(organizationId.getCode(), root.getString("organizationId"));
		assertEquals("ACTIVE", root.getString("status"));
		assertEquals("Org Name", root.getString("displayName"));
		assertEquals(mainLocationId.getCode(), root.getString("mainLocationId"));
		assertEquals(mainContactId.getCode(), root.getString("mainContactId"));
		assertEquals(2, root.getArray("authenticationGroupIds").size());
		assertEquals("CRM", root.getArray("authenticationGroupIds").getString(0));
		assertEquals("ORG", root.getArray("authenticationGroupIds").getString(1));
		assertEquals(1, root.getArray("businessGroupIds").size());
		assertEquals("EXECS", root.getArray("businessGroupIds").getString(0));
		assertEquals(100L, root.getNumber("lastModified"));
		assertEquals(organization, transformer.parse(root, Lang.ROOT));
	}
	
	@Test
	public void testEnglishJson() throws Exception {
		JsonObject english = (JsonObject)transformer.format(organization, Lang.ENGLISH);
		//JsonAsserts.print(english, "english");
		assertEquals(List.of("organizationId", "status", "displayName", "mainLocationId", "mainContactId", "authenticationGroupIds", "businessGroupIds", "lastModified"), english.keys());
		assertEquals(organizationId.getCode(), english.getString("organizationId"));
		assertEquals("Active", english.getString("status"));
		assertEquals("Org Name", english.getString("displayName"));
		assertEquals(mainLocationId.getCode(), english.getString("mainLocationId"));
		assertEquals(mainContactId.getCode(), english.getString("mainContactId"));
		assertEquals(2, english.getArray("authenticationGroupIds").size());
		assertEquals("Customer Relationship Management", english.getArray("authenticationGroupIds").getString(0));
		assertEquals("Organization", english.getArray("authenticationGroupIds").getString(1));
		assertEquals(1, english.getArray("businessGroupIds").size());
		assertEquals("Executives", english.getArray("businessGroupIds").getString(0));
		assertEquals(100L, english.getNumber("lastModified"));
		assertEquals(organization, transformer.parse(english, Lang.ENGLISH));
	}
	
	@Test
	public void testFrenchJson() throws Exception {
		JsonObject french = (JsonObject)transformer.format(organization, Lang.FRENCH);
		//JsonAsserts.print(french, "french");
		assertEquals(List.of("organizationId", "status", "displayName", "mainLocationId", "mainContactId", "authenticationGroupIds", "businessGroupIds", "lastModified"), french.keys());
		assertEquals(organizationId.getCode(), french.getString("organizationId"));
		assertEquals("Actif", french.getString("status"));
		assertEquals("Org Name", french.getString("displayName"));
		assertEquals(mainLocationId.getCode(), french.getString("mainLocationId"));
		assertEquals(mainContactId.getCode(), french.getString("mainContactId"));
		assertEquals(2, french.getArray("authenticationGroupIds").size());
		assertEquals("Gestion de la relation client", french.getArray("authenticationGroupIds").getString(0));
		assertEquals("Organisation", french.getArray("authenticationGroupIds").getString(1));
		assertEquals(1, french.getArray("businessGroupIds").size());
		assertEquals("Cadres", french.getArray("businessGroupIds").getString(0));
		assertEquals(100L, french.getNumber("lastModified"));
		assertEquals(organization, transformer.parse(french, Lang.FRENCH));
	}
	
}
