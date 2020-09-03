package ca.magex.crm.transform.json;

import static ca.magex.crm.test.CrmAsserts.GROUP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.AuthenticationGroupIdentifier;
import ca.magex.crm.test.CrmAsserts;
import ca.magex.crm.test.config.BasicTestConfig;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonPair;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { BasicTestConfig.class })
public class CommonJsonTransformerTests {

	@Autowired private Crm crm;
	
	@Test
	public void testFormatTextNull() throws Exception {
		AbstractJsonTransformer<Option> transformer = new OptionJsonTransformer(crm);
		transformer.formatText(null, null, null);
	}
	
	@Test
	public void testFormatTextKey() throws Exception {
		AbstractJsonTransformer<Option> transformer = new OptionJsonTransformer(crm);
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		Option option = new Option(new AuthenticationGroupIdentifier("o"), 
				new AuthenticationGroupIdentifier("p"), Type.AUTHENTICATION_GROUP, Status.ACTIVE, false, GROUP, 100L);
		transformer.formatText(pairs, "code", option);
	}
	
	@Test
	public void testDataToStringAndBack() throws Exception {
		Long millenium = CrmAsserts.YEAR_2000_EPOCH_MILLIS;
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(millenium);
		assertEquals("Sat Jan 01 00:00:00 EST 2000", cal.getTime().toString());
		
		ZonedDateTime zdt = JsonObject.parseDateTime(millenium);
		assertEquals(2000, zdt.getYear());
		assertEquals(1, zdt.getMonthValue());
		assertEquals(1, zdt.getDayOfMonth());
		assertEquals(0, zdt.getHour());
		assertEquals(0, zdt.getMinute());
		assertEquals(0, zdt.getSecond());
		assertEquals(ZoneId.systemDefault(), zdt.getZone());
		
		assertEquals("2000-01-01T00:00:00-05:00", JsonObject.formatDateTime(zdt));
		
		ZonedDateTime parsed = JsonObject.parseDateTime(JsonObject.formatDateTime(zdt));
		assertEquals(millenium.longValue(), parsed.toEpochSecond() * 1000);
	}
	
	@Test
	public void testGetPropertyOptions() throws Exception {
		AbstractJsonTransformer<Option> transformer = new OptionJsonTransformer(crm);
		Option option = new Option(new AuthenticationGroupIdentifier("o"), 
				new AuthenticationGroupIdentifier("p"), Type.AUTHENTICATION_GROUP, Status.ACTIVE, false, GROUP, 100L);
		try {
			transformer.getProperty(null, "code", String.class);
			fail("Illegal arguments");
		} catch (IllegalArgumentException e) {
			assertEquals("Object cannot be null", e.getMessage());
		}
		try {
			transformer.getProperty(option, null, String.class);
			fail("Illegal arguments");
		} catch (IllegalArgumentException e) {
			assertEquals("Key cannot be null", e.getMessage());
		}
		try {
			transformer.getProperty(option, "code", null);
			fail("Illegal arguments");
		} catch (IllegalArgumentException e) {
			assertEquals("Type cannot be null", e.getMessage());
		}
		try {
			transformer.getProperty(option, "code", Option.class);
			fail("Illegal arguments");
		} catch (IllegalArgumentException e) {
			assertEquals("Unable to get code from {\"type\":\"AUTHENTICATION_GROUPS\",\"optionId\":\"\\/options\\/authentication-groups\\/o\",\"parentId\":\"\\/options\\/authentication-groups\\/p\",\"status\":\"ACTIVE\",\"mutable\":false,\"name\":{\"code\":\"GRP\",\"en\":\"Group\",\"fr\":\"Groupe\"}}", e.getMessage());
		}
		try {
			transformer.getProperty(option, "invalid", String.class);
			fail("Illegal arguments");
		} catch (IllegalArgumentException e) {
			assertEquals("Unable to get invalid from {\"type\":\"AUTHENTICATION_GROUPS\",\"optionId\":\"\\/options\\/authentication-groups\\/o\",\"parentId\":\"\\/options\\/authentication-groups\\/p\",\"status\":\"ACTIVE\",\"mutable\":false,\"name\":{\"code\":\"GRP\",\"en\":\"Group\",\"fr\":\"Groupe\"}}", e.getMessage());
		}
	}
	
}
