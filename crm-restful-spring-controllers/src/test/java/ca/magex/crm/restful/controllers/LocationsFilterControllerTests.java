package ca.magex.crm.restful.controllers;

public class LocationsFilterControllerTests extends AbstractControllerTests {
	
//	private Identifier org1;
//	
//	private Identifier org2;
//	
//	private Identifier locId;
//	
//	private Identifier caId;
//	
//	private Identifier nlId;
//	
//	private Identifier usId;
//	
//	private Identifier mxId;
//	
//	private Identifier enId;
//	
//	private Identifier frId;
//	
//	private Identifier deId;
//	
//	@Before
//	public void setup() {
//		initialize();
//
//		org1 = crm.createOrganization("Org 1", List.of("ORG")).getOrganizationId();
//		org2 = crm.createOrganization("Org 2", List.of("ORG")).getOrganizationId();
//		
//		locId = crm.createLocation(org1, "MAIN", "Main Location", MAILING_ADDRESS).getLocationId();
//		caId = crm.createLocation(org1, "CANADIAN", "Canadian Location", CA_ADDRESS).getLocationId();
//		nlId = crm.createLocation(org1, "NEWFOUNDLAND", "Newfoundland Location", NL_ADDRESS).getLocationId();
//		usId = crm.disableLocation(crm.createLocation(org1, "AMERICAN", "American Location", US_ADDRESS).getLocationId()).getLocationId();
//		mxId = crm.createLocation(org1, "MEXICAN", "Mexican Location", MX_ADDRESS).getLocationId();
//		enId = crm.createLocation(org2, "BRITISH", "British Location", EN_ADDRESS).getLocationId();
//		frId = crm.disableLocation(crm.createLocation(org2, "FRANCE", "France Location", FR_ADDRESS).getLocationId()).getLocationId();
//		deId = crm.createLocation(org2, "GERMAN", "German Location", DE_ADDRESS).getLocationId();
//	}
//	
//	@Test
//	public void testOrganizationFilterDefaultRoot() throws Exception {
//		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
//			.get("/rest/locations"))
//			//.andDo(MockMvcResultHandlers.print())
//			.andExpect(MockMvcResultMatchers.status().isOk())
//			.andReturn().getResponse().getContentAsString());
//		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
//		assertEquals(1, json.getNumber("page"));
//		assertEquals(10, json.getNumber("limit"));
//		assertEquals(9, json.getNumber("total"));
//		assertEquals(false, json.getBoolean("hasNext"));
//		assertEquals(false, json.getBoolean("hasPrevious"));
//		assertEquals(9, json.getArray("content").size());
//		assertEquals(List.of("@type", "locationId", "organizationId", "status", "reference", "displayName"), json.getArray("content").getObject(0).keys());
//		assertEquals("LocationSummary", json.getArray("content").getObject(0).getString("@type"));
//		assertEquals(usId.toString(), json.getArray("content").getObject(0).getString("locationId"));
//		assertEquals(org1.toString(), json.getArray("content").getObject(0).getString("organizationId"));
//		assertEquals("inactive", json.getArray("content").getObject(0).getString("status"));
//		assertEquals("AMERICAN", json.getArray("content").getObject(0).getString("reference"));
//		assertEquals("American Location", json.getArray("content").getObject(0).getString("displayName"));
//		assertEquals(List.of("@type", "locationId", "organizationId", "status", "reference", "displayName"), json.getArray("content").getObject(1).keys());
//		assertEquals("LocationSummary", json.getArray("content").getObject(1).getString("@type"));
//		assertEquals(enId.toString(), json.getArray("content").getObject(1).getString("locationId"));
//		assertEquals(org2.toString(), json.getArray("content").getObject(1).getString("organizationId"));
//		assertEquals("active", json.getArray("content").getObject(1).getString("status"));
//		assertEquals("BRITISH", json.getArray("content").getObject(1).getString("reference"));
//		assertEquals("British Location", json.getArray("content").getObject(1).getString("displayName"));
//		assertEquals(List.of("@type", "locationId", "organizationId", "status", "reference", "displayName"), json.getArray("content").getObject(2).keys());
//		assertEquals("LocationSummary", json.getArray("content").getObject(2).getString("@type"));
//		assertEquals(caId.toString(), json.getArray("content").getObject(2).getString("locationId"));
//		assertEquals(org1.toString(), json.getArray("content").getObject(2).getString("organizationId"));
//		assertEquals("active", json.getArray("content").getObject(2).getString("status"));
//		assertEquals("CANADIAN", json.getArray("content").getObject(2).getString("reference"));
//		assertEquals("Canadian Location", json.getArray("content").getObject(2).getString("displayName"));
//	}
//	
//	@Test
//	public void testOrganizationFilterDefaultEnglish() throws Exception {
//		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
//			.get("/rest/locations")
//			.header("Locale", Lang.ENGLISH))
//			//.andDo(MockMvcResultHandlers.print())
//			.andExpect(MockMvcResultMatchers.status().isOk())
//			.andReturn().getResponse().getContentAsString());
//		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
//		assertEquals(1, json.getNumber("page"));
//		assertEquals(10, json.getNumber("limit"));
//		assertEquals(9, json.getNumber("total"));
//		assertEquals(false, json.getBoolean("hasNext"));
//		assertEquals(false, json.getBoolean("hasPrevious"));
//		assertEquals(9, json.getArray("content").size());
//		assertEquals(List.of("@type", "locationId", "organizationId", "status", "reference", "displayName"), json.getArray("content").getObject(0).keys());
//		assertEquals("LocationSummary", json.getArray("content").getObject(0).getString("@type"));
//		assertEquals(usId.toString(), json.getArray("content").getObject(0).getString("locationId"));
//		assertEquals(org1.toString(), json.getArray("content").getObject(0).getString("organizationId"));
//		assertEquals("Inactive", json.getArray("content").getObject(0).getString("status"));
//		assertEquals("AMERICAN", json.getArray("content").getObject(0).getString("reference"));
//		assertEquals("American Location", json.getArray("content").getObject(0).getString("displayName"));
//		assertEquals(List.of("@type", "locationId", "organizationId", "status", "reference", "displayName"), json.getArray("content").getObject(1).keys());
//		assertEquals("LocationSummary", json.getArray("content").getObject(1).getString("@type"));
//		assertEquals(enId.toString(), json.getArray("content").getObject(1).getString("locationId"));
//		assertEquals(org2.toString(), json.getArray("content").getObject(1).getString("organizationId"));
//		assertEquals("Active", json.getArray("content").getObject(1).getString("status"));
//		assertEquals("BRITISH", json.getArray("content").getObject(1).getString("reference"));
//		assertEquals("British Location", json.getArray("content").getObject(1).getString("displayName"));
//		assertEquals(List.of("@type", "locationId", "organizationId", "status", "reference", "displayName"), json.getArray("content").getObject(2).keys());
//		assertEquals("LocationSummary", json.getArray("content").getObject(2).getString("@type"));
//		assertEquals(caId.toString(), json.getArray("content").getObject(2).getString("locationId"));
//		assertEquals(org1.toString(), json.getArray("content").getObject(2).getString("organizationId"));
//		assertEquals("Active", json.getArray("content").getObject(2).getString("status"));
//		assertEquals("CANADIAN", json.getArray("content").getObject(2).getString("reference"));
//		assertEquals("Canadian Location", json.getArray("content").getObject(2).getString("displayName"));
//	}
//	
//	@Test
//	public void testOrganizationFilterDefaultFrench() throws Exception {
//		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
//			.get("/rest/locations")
//			.header("Locale", Lang.FRENCH))
//			//.andDo(MockMvcResultHandlers.print())
//			.andExpect(MockMvcResultMatchers.status().isOk())
//			.andReturn().getResponse().getContentAsString());
//		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
//		assertEquals(1, json.getNumber("page"));
//		assertEquals(10, json.getNumber("limit"));
//		assertEquals(9, json.getNumber("total"));
//		assertEquals(false, json.getBoolean("hasNext"));
//		assertEquals(false, json.getBoolean("hasPrevious"));
//		assertEquals(9, json.getArray("content").size());
//		assertEquals(List.of("@type", "locationId", "organizationId", "status", "reference", "displayName"), json.getArray("content").getObject(0).keys());
//		assertEquals("LocationSummary", json.getArray("content").getObject(0).getString("@type"));
//		assertEquals(usId.toString(), json.getArray("content").getObject(0).getString("locationId"));
//		assertEquals(org1.toString(), json.getArray("content").getObject(0).getString("organizationId"));
//		assertEquals("Inactif", json.getArray("content").getObject(0).getString("status"));
//		assertEquals("AMERICAN", json.getArray("content").getObject(0).getString("reference"));
//		assertEquals("American Location", json.getArray("content").getObject(0).getString("displayName"));
//		assertEquals(List.of("@type", "locationId", "organizationId", "status", "reference", "displayName"), json.getArray("content").getObject(1).keys());
//		assertEquals("LocationSummary", json.getArray("content").getObject(1).getString("@type"));
//		assertEquals(enId.toString(), json.getArray("content").getObject(1).getString("locationId"));
//		assertEquals(org2.toString(), json.getArray("content").getObject(1).getString("organizationId"));
//		assertEquals("Actif", json.getArray("content").getObject(1).getString("status"));
//		assertEquals("BRITISH", json.getArray("content").getObject(1).getString("reference"));
//		assertEquals("British Location", json.getArray("content").getObject(1).getString("displayName"));
//		assertEquals(List.of("@type", "locationId", "organizationId", "status", "reference", "displayName"), json.getArray("content").getObject(2).keys());
//		assertEquals("LocationSummary", json.getArray("content").getObject(2).getString("@type"));
//		assertEquals(caId.toString(), json.getArray("content").getObject(2).getString("locationId"));
//		assertEquals(org1.toString(), json.getArray("content").getObject(2).getString("organizationId"));
//		assertEquals("Actif", json.getArray("content").getObject(2).getString("status"));
//		assertEquals("CANADIAN", json.getArray("content").getObject(2).getString("reference"));
//		assertEquals("Canadian Location", json.getArray("content").getObject(2).getString("displayName"));
//	}
//
//	@Test
//	public void testFilterByDisplayNameCaseAccentInsensitive() throws Exception {
//		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
//			.get("/rest/locations")
//			.queryParam("displayName", "CAN")
//			.header("Locale", Lang.ENGLISH))
//			//.andDo(MockMvcResultHandlers.print())
//			.andExpect(MockMvcResultMatchers.status().isOk())
//			.andReturn().getResponse().getContentAsString());
//		//JsonAsserts.print(json, "json");
//		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
//		assertEquals(1, json.getNumber("page"));
//		assertEquals(10, json.getNumber("limit"));
//		assertEquals(3, json.getNumber("total"));
//		assertEquals(false, json.getBoolean("hasNext"));
//		assertEquals(false, json.getBoolean("hasPrevious"));
//		assertEquals(3, json.getArray("content").size());
//		assertEquals(List.of("@type", "locationId", "organizationId", "status", "reference", "displayName"), json.getArray("content").getObject(0).keys());
//		assertEquals("LocationSummary", json.getArray("content").getObject(0).getString("@type"));
//		assertEquals(usId.toString(), json.getArray("content").getObject(0).getString("locationId"));
//		assertEquals(org1.toString(), json.getArray("content").getObject(0).getString("organizationId"));
//		assertEquals("Inactive", json.getArray("content").getObject(0).getString("status"));
//		assertEquals("AMERICAN", json.getArray("content").getObject(0).getString("reference"));
//		assertEquals("American Location", json.getArray("content").getObject(0).getString("displayName"));
//		assertEquals(List.of("@type", "locationId", "organizationId", "status", "reference", "displayName"), json.getArray("content").getObject(1).keys());
//		assertEquals("LocationSummary", json.getArray("content").getObject(1).getString("@type"));
//		assertEquals(caId.toString(), json.getArray("content").getObject(1).getString("locationId"));
//		assertEquals(org1.toString(), json.getArray("content").getObject(1).getString("organizationId"));
//		assertEquals("Active", json.getArray("content").getObject(1).getString("status"));
//		assertEquals("CANADIAN", json.getArray("content").getObject(1).getString("reference"));
//		assertEquals("Canadian Location", json.getArray("content").getObject(1).getString("displayName"));
//		assertEquals(List.of("@type", "locationId", "organizationId", "status", "reference", "displayName"), json.getArray("content").getObject(2).keys());
//		assertEquals("LocationSummary", json.getArray("content").getObject(2).getString("@type"));
//		assertEquals(mxId.toString(), json.getArray("content").getObject(2).getString("locationId"));
//		assertEquals(org1.toString(), json.getArray("content").getObject(2).getString("organizationId"));
//		assertEquals("Active", json.getArray("content").getObject(2).getString("status"));
//		assertEquals("MEXICAN", json.getArray("content").getObject(2).getString("reference"));
//		assertEquals("Mexican Location", json.getArray("content").getObject(2).getString("displayName"));
//	}
//	
//	@Test
//	public void testFilterByInactifDesc() throws Exception {
//		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
//			.get("/rest/locations")
//			.queryParam("status", "Inactif")
//			.queryParam("order", "displayName")
//			.queryParam("direction", "desc")
//			.header("Locale", Lang.FRENCH))
//			//.andDo(MockMvcResultHandlers.print())
//			.andExpect(MockMvcResultMatchers.status().isOk())
//			.andReturn().getResponse().getContentAsString());
//		//JsonAsserts.print(json, "json");
//		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
//		assertEquals(1, json.getNumber("page"));
//		assertEquals(10, json.getNumber("limit"));
//		assertEquals(2, json.getNumber("total"));
//		assertEquals(false, json.getBoolean("hasNext"));
//		assertEquals(false, json.getBoolean("hasPrevious"));
//		assertEquals(2, json.getArray("content").size());
//		assertEquals(List.of("@type", "locationId", "organizationId", "status", "reference", "displayName"), json.getArray("content").getObject(0).keys());
//		assertEquals("LocationSummary", json.getArray("content").getObject(0).getString("@type"));
//		assertEquals(frId.toString(), json.getArray("content").getObject(0).getString("locationId"));
//		assertEquals(org2.toString(), json.getArray("content").getObject(0).getString("organizationId"));
//		assertEquals("Inactif", json.getArray("content").getObject(0).getString("status"));
//		assertEquals("FRANCE", json.getArray("content").getObject(0).getString("reference"));
//		assertEquals("France Location", json.getArray("content").getObject(0).getString("displayName"));
//		assertEquals(List.of("@type", "locationId", "organizationId", "status", "reference", "displayName"), json.getArray("content").getObject(1).keys());
//		assertEquals("LocationSummary", json.getArray("content").getObject(1).getString("@type"));
//		assertEquals(usId.toString(), json.getArray("content").getObject(1).getString("locationId"));
//		assertEquals(org1.toString(), json.getArray("content").getObject(1).getString("organizationId"));
//		assertEquals("Inactif", json.getArray("content").getObject(1).getString("status"));
//		assertEquals("AMERICAN", json.getArray("content").getObject(1).getString("reference"));
//		assertEquals("American Location", json.getArray("content").getObject(1).getString("displayName"));
//	}
//	
//	@Test
//	public void testFilterByActiveDesc() throws Exception {
//		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
//			.get("/rest/locations")
//			.queryParam("organization", org1.toString())
//			.queryParam("status", "active")
//			.queryParam("order", "displayName")
//			.queryParam("direction", "desc")
//			.header("Locale", Lang.ROOT))
//			//.andDo(MockMvcResultHandlers.print())
//			.andExpect(MockMvcResultMatchers.status().isOk())
//			.andReturn().getResponse().getContentAsString());
//		//JsonAsserts.print(json, "json");
//		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
//		assertEquals(1, json.getNumber("page"));
//		assertEquals(10, json.getNumber("limit"));
//		assertEquals(4, json.getNumber("total"));
//		assertEquals(false, json.getBoolean("hasNext"));
//		assertEquals(false, json.getBoolean("hasPrevious"));
//		assertEquals(4, json.getArray("content").size());
//		assertEquals(List.of("@type", "locationId", "organizationId", "status", "reference", "displayName"), json.getArray("content").getObject(0).keys());
//		assertEquals("LocationSummary", json.getArray("content").getObject(0).getString("@type"));
//		assertEquals(nlId.toString(), json.getArray("content").getObject(0).getString("locationId"));
//		assertEquals(org1.toString(), json.getArray("content").getObject(0).getString("organizationId"));
//		assertEquals("active", json.getArray("content").getObject(0).getString("status"));
//		assertEquals("NEWFOUNDLAND", json.getArray("content").getObject(0).getString("reference"));
//		assertEquals("Newfoundland Location", json.getArray("content").getObject(0).getString("displayName"));
//		assertEquals(List.of("@type", "locationId", "organizationId", "status", "reference", "displayName"), json.getArray("content").getObject(1).keys());
//		assertEquals("LocationSummary", json.getArray("content").getObject(1).getString("@type"));
//		assertEquals(mxId.toString(), json.getArray("content").getObject(1).getString("locationId"));
//		assertEquals(org1.toString(), json.getArray("content").getObject(1).getString("organizationId"));
//		assertEquals("active", json.getArray("content").getObject(1).getString("status"));
//		assertEquals("MEXICAN", json.getArray("content").getObject(1).getString("reference"));
//		assertEquals("Mexican Location", json.getArray("content").getObject(1).getString("displayName"));
//		assertEquals(List.of("@type", "locationId", "organizationId", "status", "reference", "displayName"), json.getArray("content").getObject(2).keys());
//		assertEquals("LocationSummary", json.getArray("content").getObject(2).getString("@type"));
//		assertEquals(locId.toString(), json.getArray("content").getObject(2).getString("locationId"));
//		assertEquals(org1.toString(), json.getArray("content").getObject(2).getString("organizationId"));
//		assertEquals("active", json.getArray("content").getObject(2).getString("status"));
//		assertEquals("MAIN", json.getArray("content").getObject(2).getString("reference"));
//		assertEquals("Main Location", json.getArray("content").getObject(2).getString("displayName"));
//		assertEquals(List.of("@type", "locationId", "organizationId", "status", "reference", "displayName"), json.getArray("content").getObject(3).keys());
//		assertEquals("LocationSummary", json.getArray("content").getObject(3).getString("@type"));
//		assertEquals(caId.toString(), json.getArray("content").getObject(3).getString("locationId"));
//		assertEquals(org1.toString(), json.getArray("content").getObject(3).getString("organizationId"));
//		assertEquals("active", json.getArray("content").getObject(3).getString("status"));
//		assertEquals("CANADIAN", json.getArray("content").getObject(3).getString("reference"));
//		assertEquals("Canadian Location", json.getArray("content").getObject(3).getString("displayName"));		
//	}
//	
//	@Test
//	public void testFilterByOrganization() throws Exception {
//		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
//			.get("/rest/locations")
//			.queryParam("organization", org2.toString())
//			.header("Locale", Lang.ENGLISH))
//			//.andDo(MockMvcResultHandlers.print())
//			.andExpect(MockMvcResultMatchers.status().isOk())
//			.andReturn().getResponse().getContentAsString());
//		//JsonAsserts.print(json, "json");
//		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
//		assertEquals(1, json.getNumber("page"));
//		assertEquals(10, json.getNumber("limit"));
//		assertEquals(3, json.getNumber("total"));
//		assertEquals(false, json.getBoolean("hasNext"));
//		assertEquals(false, json.getBoolean("hasPrevious"));
//		assertEquals(3, json.getArray("content").size());
//		assertEquals(List.of("@type", "locationId", "organizationId", "status", "reference", "displayName"), json.getArray("content").getObject(0).keys());
//		assertEquals("LocationSummary", json.getArray("content").getObject(0).getString("@type"));
//		assertEquals(enId.toString(), json.getArray("content").getObject(0).getString("locationId"));
//		assertEquals(org2.toString(), json.getArray("content").getObject(0).getString("organizationId"));
//		assertEquals("Active", json.getArray("content").getObject(0).getString("status"));
//		assertEquals("BRITISH", json.getArray("content").getObject(0).getString("reference"));
//		assertEquals("British Location", json.getArray("content").getObject(0).getString("displayName"));
//		assertEquals(List.of("@type", "locationId", "organizationId", "status", "reference", "displayName"), json.getArray("content").getObject(1).keys());
//		assertEquals("LocationSummary", json.getArray("content").getObject(1).getString("@type"));
//		assertEquals(frId.toString(), json.getArray("content").getObject(1).getString("locationId"));
//		assertEquals(org2.toString(), json.getArray("content").getObject(1).getString("organizationId"));
//		assertEquals("Inactive", json.getArray("content").getObject(1).getString("status"));
//		assertEquals("FRANCE", json.getArray("content").getObject(1).getString("reference"));
//		assertEquals("France Location", json.getArray("content").getObject(1).getString("displayName"));
//		assertEquals(List.of("@type", "locationId", "organizationId", "status", "reference", "displayName"), json.getArray("content").getObject(2).keys());
//		assertEquals("LocationSummary", json.getArray("content").getObject(2).getString("@type"));
//		assertEquals(deId.toString(), json.getArray("content").getObject(2).getString("locationId"));
//		assertEquals(org2.toString(), json.getArray("content").getObject(2).getString("organizationId"));
//		assertEquals("Active", json.getArray("content").getObject(2).getString("status"));
//		assertEquals("GERMAN", json.getArray("content").getObject(2).getString("reference"));
//		assertEquals("German Location", json.getArray("content").getObject(2).getString("displayName"));
//	}

}
