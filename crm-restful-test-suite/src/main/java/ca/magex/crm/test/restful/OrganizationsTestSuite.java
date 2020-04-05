package ca.magex.crm.test.restful;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.Unirest;

public class OrganizationsTestSuite {

	public static void main(String[] args) throws Exception {
		Unirest.setTimeouts(0, 0);
		listOrganizations();
		String myBankId = createOrganization("My Bank").getString("@value");
		listOrganizations();
		getOrganization(myBankId);
		String jokerMoneyId = findOrganization("Jokers Money").getJSONObject(0).getString("@value");
		disableOrganization(jokerMoneyId);
		getOrganization(jokerMoneyId);
		enableOrganization(jokerMoneyId);
		getOrganization(jokerMoneyId);
	}
	
	public static JSONArray listOrganizations() throws Exception {
		String body = Unirest.get("http://localhost:8080/api/organizations").header("Accept", "application/json+ld").asString().getBody();
		System.out.println("=====================================");
		System.out.println("== GET http://localhost:8080/api/organizations");
		System.out.println(body);
		System.out.println("=====================================");
		System.out.println();
		return new JSONArray(body);
	}
	
	public static JSONObject createOrganization(String displayName) throws Exception {
		String body = Unirest.post("http://localhost:8080/api/organizations").header("Accept", "application/json+ld")
				  .body("{ \"displayName\": \"" + displayName + "\" }").asString().getBody();
		System.out.println("=====================================");
		System.out.println("== POST http://localhost:8080/api/organizations (" + displayName + ")");
		System.out.println(body);
		System.out.println("=====================================");
		System.out.println();
		return new JSONObject(body);
	}
	
	public static JSONObject getOrganization(String organizationId) throws Exception {
		String body = Unirest.get("http://localhost:8080/api/organizations/" + organizationId).header("Accept", "application/json+ld").asString().getBody();
		System.out.println("=====================================");
		System.out.println("== GET http://localhost:8080/api/organizations/" + organizationId);
		System.out.println(body);
		System.out.println("=====================================");
		System.out.println();
		return new JSONObject(body);
	}
	
	public static JSONArray findOrganization(String displayName) throws Exception {
		String body = Unirest.get("http://localhost:8080/api/organizations?displayName=" + URLEncoder.encode(displayName, StandardCharsets.UTF_8)).header("Accept", "application/json+ld").asString().getBody();
		System.out.println("=====================================");
		System.out.println("== GET http://localhost:8080/api/organizations?displayName=" + URLEncoder.encode(displayName, StandardCharsets.UTF_8));
		System.out.println(body);
		System.out.println("=====================================");
		System.out.println();
		return new JSONArray(body);
	}
	
	public static JSONObject disableOrganization(String organizationId) throws Exception {
		String body = Unirest.put("http://localhost:8080/api/organizations/" + organizationId + "/disable").header("Accept", "application/json+ld").asString().getBody();
		System.out.println("=====================================");
		System.out.println("== PUT http://localhost:8080/api/organizations/" + organizationId + "/disable");
		System.out.println(body);
		System.out.println("=====================================");
		System.out.println();
		return new JSONObject(body);
	}

	public static JSONObject enableOrganization(String organizationId) throws Exception {
		String body = Unirest.put("http://localhost:8080/api/organizations/" + organizationId + "/enable").header("Accept", "application/json+ld").asString().getBody();
		System.out.println("=====================================");
		System.out.println("== PUT http://localhost:8080/api/organizations/" + organizationId + "/enable");
		System.out.println(body);
		System.out.println("=====================================");
		System.out.println();
		return new JSONObject(body);
	}

	public static JSONObject createLocation(String displayName) throws Exception {
		String body = Unirest.post("http://localhost:8080/api/locations").header("Accept", "application/json+ld")
		  .body("{\n" + 
		  		"  \"organizationId\": \"6f4f58d1\",\n" + 
		  		"  \"reference\": \"Second\",\n" + 
		  		"  \"displayName\": \"My Bank\",\n" + 
		  		"  \"address\": {\n" + 
		  		"	\"street\": \"10 Bank St\",\n" + 
		  		"	\"city\": \"Ottawa\",\n" + 
		  		"	\"province\": \"ON\",\n" + 
		  		"	\"country\": {\n" + 
		  		"	  \"code\": \"CA\",\n" + 
		  		"	  \"name\": \"Canada\"\n" + 
		  		"	},\n" + 
		  		"	\"postalCode\": \"K1K1K1\"\n" + 
		  		"  }\n" + 
		  		"}").asString().getBody();
		System.out.println("=====================================");
		System.out.println("== POST http://localhost:8080/api/locations (" + displayName + ")");
		System.out.println(body);
		System.out.println("=====================================");
		System.out.println();
		return new JSONObject(body);
	}
	
}
