package ca.magex.crm.restful.client.docs;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.jboss.logging.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.HttpRequestWithBody;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.CrmProfiles;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.restful.client.config.RestfulClientTestConfig;
import ca.magex.crm.spring.security.auth.AuthProfiles;
import ca.magex.json.model.JsonFormatter;
import ca.magex.json.model.JsonObject;
import ca.magex.json.util.FormattedStringBuilder;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = { RestfulClientTestConfig.class })
@ActiveProfiles(profiles = {
		AuthProfiles.EMBEDDED_HMAC,
		CrmProfiles.BASIC,
		CrmProfiles.DEV
})
public class RestfulDocumentationGenerator {
	
	@LocalServerPort private int randomPort;
	
	@Test
	public void testBuildExamples() throws Exception {
		FormattedStringBuilder sb = new FormattedStringBuilder();
		sb.append("<html>");
		sb.append("<body>");
		
		String token = null;
		
		sb.append("<h1>CRM Restful API Usage</h1>");
		
		sb.append("<p>The following documentation will explain how to create a new organization and some of the options available for the system to use.</p>");
		sb.append("<p>Note that the url used in this example will need to be updated to the instance that you have already setup instead of (" + Crm.REST_BASE + ") system and will use \"admin\" for both the username and password of the system administrator that was setup.");
		sb.append("<p>Although this is being done via \"curl\", you can use java, python, nodejs or any other tools to do the same thing.");
		
		sb.append("<h2>Primary Actions</h2>");
		sb.append("<p>In order to get a list of what the application has available to the current user, a list of actions can be retrieved form the root url</p>");
		
		sb.append(restGet("/rest/actions", token, Lang.ENGLISH));
		
		sb.append("<p>Since no JWT token was passed in the user gets a 401 unauthenticated status.  In order to get the token you need to call the authentication server which will give you to token that is valid for a certian amount of time (defaults to 5 hours).");

		sb.append(restPost("/authenticate", token, Lang.ROOT, new JsonObject().with("username", "admin").with("password", "admin")));
		token = authenticate("admin", "admin");
		
		sb.append("<p>Once you have logged in and pass the Bearer token in, you will get access to the resources.</p>");

		sb.append(restGet("/rest/actions", token, Lang.ROOT));

		sb.append("<p>A user can follow these root links to get more information about the system such as requesting the list of organizations</p>");
		
		sb.append(restGet("/rest/organizations", token, Lang.ENGLISH));
		
		sb.append("<p>Since this is a new application, there is only a single organization which we can get a summary or all of the details about.  Most objects use this paradigm so not all information needs to be retrieved for large objects.</p>");
		
		sb.append("<p>We can get the summary of the object by following the view link from the list</p>");
		
		OrganizationIdentifier organizationId = new OrganizationIdentifier(jsonGet("/rest/organizations", token).getArray("content", JsonObject.class).get(0).getString("organizationId").substring(Crm.REST_BASE.length()));
		sb.append(restGet("/rest/" + organizationId, token, Lang.ROOT));

		sb.append("<p>If you want to see all of the details about an object, you can get all of the information by putting /details after the resource you want.  In some cases this may take more time and bandwidth but it gives everything to do with an object.</p>");
		sb.append(restGet("/rest/" + organizationId + "/details", token, Lang.ROOT));

		sb.append("<p>The same resource can be retireved but with the information all in the local of their choice, in French for this example.</p>");
		sb.append(restGet("/rest/" + organizationId + "/details", token, Lang.FRENCH));

		sb.append("<p>Linked data can also be captured so you know what each the context of object in the response by adding the correct content type.  This will extends each of the linked element to have full url's to the information, access to the schema context along with the database, English and French values.</p>");
		sb.append("<p>This may seem like a lot of data, but it allows a user to have all the information they need to completely recreate the object in their programming language.</p>");
		
		sb.append(restGet("/rest/" + organizationId + "/details", token, null));
		
		sb.append("<p>With the linked data, you can gather more information about each of the referenced resources by following the @id JSON-LD attribute.</p>");
		sb.append(restGet("/rest/options/statuses/active", token, null));
		
		sb.append("<p>Sub groups of linked data can also be retrieved so you can get information about different parent group ids, even though the option has the same value.</p>");
		
		sb.append("<p>Newfoundland is a province inside of Canada with the ISO code of NL that can be referenced from the ISO country of CA</p>");
		sb.append(restGet("/rest/options/provinces/ca/nl", token, Lang.ENGLISH));

		sb.append("<p>Nevuo Leon is a province inside of Mexico with the ISO code of NL as well that can be referenced from the ISO country of MX</p>");
		sb.append(restGet("/rest/options/provinces/mx/nl", token, Lang.ENGLISH));
		
		sb.append("</body>");
		sb.append("</html>");
		
		File output = new File("src/main/resources/docs/example.html");
		FileUtils.writeStringToFile(output, sb.toString(), StandardCharsets.UTF_8);
		Logger.getLogger(RestfulDocumentationGenerator.class).info("Writing: " + output.getAbsolutePath());
	}
	
	public JsonObject jsonGet(String endpoint, String token) throws Exception {
		GetRequest request = Unirest.get("http://localhost:" + randomPort + "/crm" + endpoint);
		request = request.header("Content-Type", getContentType(null));
		if (token != null)
			request = request.header("Authorization", "Bearer " + token);
		return new JsonObject(request.asString().getBody());
	}
	
	public String restGet(String endpoint, String token, Locale locale) throws Exception {
		String contentType = getContentType(locale);
		String lang = getLang(locale);
		StringBuilder sb = new StringBuilder();
		sb.append("<div style=\"border: 2px solid #ccc; border-radius: 5px; padding: 5px; background-color: #333; color: #eee; font-weight: bold; overflow: auto;\">");
		sb.append("<pre style=\"margin: 0 0 15px 0; color: #eee\">");
		sb.append("$ curl " + Crm.REST_BASE.replaceAll("/rest", "") + endpoint);
		if (token != null)
			sb.append(" \\\n    -H \"Authorization: Bearer " + token.substring(0, 10) + ".....\"");
		sb.append(" \\\n    -H \"Content-Type: " + contentType + "\"");
		if (lang != null)
			sb.append(" \\\n    -H \"Locale: " + lang + "\"");
		sb.append("</pre>");
		sb.append("<pre style=\"margin: 0; color: #9f9\">");
		GetRequest request = Unirest.get("http://localhost:" + randomPort + "/crm" + endpoint);
		request = request.header("Content-Type", contentType);
		if (lang != null)
			request = request.header("Locale", lang);
		if (token != null)
			request = request.header("Authorization", "Bearer " + token);
		HttpResponse<String> response = request.asString();
		sb.append("Status: " + response.getStatus() + "\n\n");
		sb.append(response.getBody());
		sb.append("</pre>");
		sb.append("</div>");
		return sb.toString();
	}
	
	public String restPost(String endpoint, String token, Locale locale, JsonObject body) throws Exception {
		String contentType = getContentType(locale);
		String lang = getLang(locale);
		StringBuilder sb = new StringBuilder();
		sb.append("<div style=\"border: 2px solid #ccc; border-radius: 5px; padding: 5px; background-color: #333; color: #eee; font-weight: bold; overflow: auto;\">");
		sb.append("<pre style=\"margin: 0 0 15px 0; color: #eee\">");
		sb.append("$ curl -X POST " + Crm.REST_BASE.replaceAll("/rest", "") + endpoint);
		if (token != null)
			sb.append(" \\\n    -H \"Authorization: Bearer " + token.substring(0, 10) + ".....\"");
		sb.append(" \\\n    -H \"Content-Type: " + contentType + "\"");
		if (lang != null)
			sb.append(" \\\n    -H \"Locale: " + lang + "\"");
		if (body != null)
			sb.append(" \\\n    --data '" + JsonFormatter.compact(body) + "'");
		sb.append("</pre>");
		sb.append("<pre style=\"margin: 0; color: #9f9\">");
		HttpRequestWithBody request = Unirest.post("http://localhost:" + randomPort + "/crm" + endpoint);
		request.body(body.toString());
		request = request.header("Content-Type", contentType);
		if (lang != null)
			request = request.header("Locale", lang);
		if (token != null)
			request = request.header("Authorization", "Bearer " + token);
		HttpResponse<String> response = request.asString();
		sb.append("Status: " + response.getStatus() + "\n\n");
		if (endpoint.equals("/authenticate")) {
			sb.append(response.getBody().substring(0, 20) + ".....\"}");
		} else {
			sb.append(response.getBody());
		}
		sb.append("</pre>");
		sb.append("</div>");
		return sb.toString();
	}
	
	public String authenticate(String username, String password) throws Exception {
		HttpRequestWithBody request = Unirest.post("http://localhost:" + randomPort + "/crm/authenticate");
		request.body(new JsonObject().with("username", "admin").with("password", "admin").toString());
		return request.asJson().getBody().getObject().getString("token");
	}
	
	public String getContentType(Locale locale) {
		return locale == null ? "application/json+ld" : "application/json";
	}
	
	public String getLang(Locale locale) {
		return locale == null || locale == Lang.ROOT ? null : Lang.lang2(locale);
	}
	
}
