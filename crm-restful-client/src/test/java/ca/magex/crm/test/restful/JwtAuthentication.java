package ca.magex.crm.test.restful;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mashape.unirest.http.Unirest;

import ca.magex.crm.test.CrmServicesTestSuite;
import ca.magex.crm.test.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class AmnesiaOrganizationServiceTest extends AbstractJUnit4SpringContextTests {

	@Autowired private CrmServicesTestSuite crmServicesTest;
	
	@Test
	public void testCrmServices() {
		crmServicesTest.runAllTests();
	}
}
	public static void main(String[] args) throws Exception {
		String token = getToken("admin", "admin");
		assertTh
		assertMatches(token, "[A-Za-z0-9\\.\\-]{50}");
		System.out.println(token);
		String config = getConfig(token);
		System.out.println(config);
	}
	
	public static String getConfig(String key) throws Exception {
		return Unirest.get("http://localhost:8080/api.json")
			.header("Content-Type", "application/json")
			.header("Authorization", "Bearer " + key)
			.asString()
			.getBody();
	}

	private static String getToken(String username, String password) throws Exception {
		return new JSONObject(Unirest.post("http://localhost:8080/authenticate")
			.header("Content-Type", "application/json")
			.body("{ \"username\": \"" + username + "\", \"password\": \"" + password + "\" }")
			.asString()
			.getBody()).getString("token");
	}
	
	
}
