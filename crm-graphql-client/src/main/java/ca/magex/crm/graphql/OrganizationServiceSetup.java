package ca.magex.crm.graphql;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.util.StreamUtils;

public class OrganizationServiceSetup {

	private static final String GRAPHQL_ENDPOINT = "http://localhost:9002/crm/graphql";
	private static final Logger LOG = LoggerFactory.getLogger(OrganizationServiceSetup.class);
	
	private static Properties testQueries = new Properties();
	private static CloseableHttpClient httpclient = HttpClients.createDefault();
	
	public static void main(String[] args) throws Exception {		
		/* load our test queries */
		try (InputStream in = OrganizationServiceSetup.class.getResource("/setup-queries.txt").openStream()) {
			OrganizationServiceSetup.testQueries.load(in);
		}
		
		int orgCount = performGraphQLQuery("countOrganizations").getInt("countOrganizations");
		LOG.info("Currently " + orgCount + " Orgs");
		
		String johnnuyOrgId = performGraphQLQuery("createOrganization")
				.getJSONObject("createOrganization")
				.getString("organizationId");
		LOG.info("Created johnnuy.org with id " + johnnuyOrgId);
		
		orgCount = performGraphQLQuery("countOrganizations").getInt("countOrganizations");
		LOG.info("Now there are " + orgCount + " Orgs");		
		
		JSONObject headQuarters = performGraphQLQuery("createLocation", johnnuyOrgId)
				.getJSONObject("createLocation");		
		LOG.info("Create johnnuy.org head Quarters: " + headQuarters.toString(3));
		
		JSONObject mainLocation = performGraphQLQuery("updateOrganizationMainLocation", johnnuyOrgId, headQuarters.getString("locationId"))
				.getJSONObject("updateOrganizationMainLocation")
				.getJSONObject("mainLocation");
		LOG.info("Updated johnnuy.org main location to: " + mainLocation.getString("displayName"));
		
		String displayName = performGraphQLQuery("updateOrganizationName", johnnuyOrgId, "Johnnuy Technologies")
				.getJSONObject("updateOrganizationName")
				.getString("displayName");
		LOG.info("Update johnnuy.org to " + displayName);
		
		JSONObject org = performGraphQLQuery("findOrganization", johnnuyOrgId)
				.getJSONObject("findOrganization");
		LOG.info("Retrieved Org: " + org.toString(3));
		
		org = performGraphQLQuery("disableOrganization", johnnuyOrgId)
				.getJSONObject("disableOrganization");
		LOG.info("Disabled Org: " + org.toString(3));
		
		org = performGraphQLQuery("enableOrganization", johnnuyOrgId)
				.getJSONObject("enableOrganization");
		LOG.info("Enabled Org: " + org.toString(3));
		
		String hqName = performGraphQLQuery("updateLocationName", headQuarters.getString("locationId"), "Johnnuy.org HQ")
				.getJSONObject("updateLocationName")
				.getString("displayName");
		LOG.info("Renamed HQ to: "+ hqName);
		
		httpclient.close();
	}
	
	/**
	 * Executes the given query and returns the data field
	 * @param queryName
	 * @return
	 * @throws Exception
	 */
	private static JSONObject performGraphQLQuery(String queryName, String ... params) throws Exception {
		LOG.info("performing GraphQL query '" + queryName + "'");
		HttpPost httpPost = new HttpPost(GRAPHQL_ENDPOINT);
		httpPost.setEntity(constructEntity(queryName, params));
		try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
			JSONObject json = new JSONObject(StreamUtils.copyToString(response.getEntity().getContent(), Charset.forName("UTF-8")));
			JSONArray errors = json.getJSONArray("errors");
			if (errors.length() == 0) {
				return json.getJSONObject("data");
			}
			else {
				LOG.error(errors.toString(3));
				return null;
			}
		}
	}
	
	/**
	 * constructs the POST body as a graphQL StringEntity
	 * @param queryName
	 * @return
	 * @throws Exception
	 */
	private static HttpEntity constructEntity(String queryName, String ... params) throws Exception {
		JSONObject request = new JSONObject();
		String query = OrganizationServiceSetup.testQueries.getProperty(queryName);
		for (int param=0; param<params.length; param++) {
			query = query.replace("${" + param + "}", params[param]);
		}
		request.put("query", query);
		return new StringEntity(request.toString(3));
	}
}
