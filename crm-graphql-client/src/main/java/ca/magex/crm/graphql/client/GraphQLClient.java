package ca.magex.crm.graphql.client;

import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import ca.magex.crm.graphql.exceptions.GraphQLClientException;
import ca.magex.crm.graphql.model.GraphQLRequest;
import ca.magex.crm.spring.security.jwt.JwtRequest;
import ca.magex.crm.spring.security.jwt.JwtToken;

/**
 * HTTP client that handles executing GraphQL queries and returning the response
 * 
 * @author Jonny
 *
 */
public abstract class GraphQLClient {

	private static final Logger LOG = LoggerFactory.getLogger(GraphQLClient.class);

	protected String endpoint;
	protected Properties queries;
	
	private String authToken;
	private RestTemplate restTemplate = new RestTemplate();

	/**
	 * constructs a new Service for the given graphql endpoint
	 * 
	 * @param endpoint
	 */
	public GraphQLClient(String endpoint, String queryResource) {
		this.endpoint = endpoint;
		this.queries = new Properties();
		try {
			try (InputStream in = CrmServicesGraphQLClientImpl.class.getResource(queryResource).openStream()) {
				this.queries.load(in);
			}
		} catch (Exception e) {
			throw new GraphQLClientException("Cannot load queries from resource '" + queryResource + "'", e);
		}
	}

	/**
	 * runs our authentication mechanism
	 * 
	 * @param authEndpoint
	 * @param username
	 * @param password
	 */
	public void authenticateJwt(String authEndpoint, String username, String password) {
		ResponseEntity<JwtToken> response = restTemplate.exchange(
				RequestEntity
					.post(URI.create(authEndpoint))
					.contentType(MediaType.APPLICATION_JSON)
					.body(new JwtRequest(username, password)), 
				JwtToken.class);
		if (!response.getStatusCode().is2xxSuccessful()) {
			throw new GraphQLClientException(response.getStatusCode().getReasonPhrase());
		}
		this.authToken = response.getBody().getToken();		
	}

	/**
	 * Executes the given query and returns the data field
	 * 
	 * @param <T>
	 * @param queryId
	 * @param queryName
	 * @param variables
	 * @return
	 */
	protected <T> T performGraphQLQueryWithVariables(String queryId, String queryName, Map<String, Object> variables) {
		return performGraphQLQuery(queryName, constructRequestWithVariables(queryId, variables));
	}

	/**
	 * Executes the given query and returns the data field
	 * 
	 * @param <T>
	 * @param queryId
	 * @param queryName
	 * @param params
	 * @return
	 */
	protected <T> T performGraphQLQueryWithSubstitution(String queryId, String queryName, Object... params) {
		return performGraphQLQuery(queryName, constructRequestWithSubstitution(queryId, params));
	}

	@SuppressWarnings("unchecked")
	private <T> T performGraphQLQuery(String queryName, GraphQLRequest request) {
		long t1 = System.currentTimeMillis();
		try {			
			ResponseEntity<String> response = restTemplate.exchange(
					authToken == null ?
						RequestEntity
							.post(URI.create(endpoint))
							.contentType(MediaType.APPLICATION_JSON)
							.body(request) :
						RequestEntity
							.post(URI.create(endpoint))
							.contentType(MediaType.APPLICATION_JSON)
							.header("Authorization", "Bearer " + authToken)
							.body(request),
						String.class);
			if (response.getStatusCode().is2xxSuccessful()) {
				JSONObject json = new JSONObject(response.getBody());				
				JSONArray errors = json.getJSONArray("errors");
				if (errors.length() == 0) {
					JSONObject data = json.getJSONObject("data");
					if (data.get(queryName) == JSONObject.NULL) {
						throw new GraphQLClientException("Null data returned without Error");
					}
					return (T) data.get(queryName);
				} else {
					throw new GraphQLClientException(errors.toString(3));
				}
			}
			else {
				throw new GraphQLClientException("Error performing graphql query " + queryName + ", " + response.getStatusCode().getReasonPhrase());
			}
		}
		catch(JSONException jsone) {
			throw new GraphQLClientException("Unable to parse response", jsone);
		} finally {
			if (LOG.isDebugEnabled()) {
				LOG.debug("execution of " + request + " took " + (System.currentTimeMillis() - t1) + "ms.");
			}
		}
	}

	/**
	 * Construct the POST Body as a graphQL StringEntity using Variables
	 * 
	 * @param queryName
	 * @param variables
	 * @return
	 * @throws Exception
	 */
	private GraphQLRequest constructRequestWithVariables(String queryName, Map<String, Object> variables) {		
		GraphQLRequest request = new GraphQLRequest();
		request.setQuery(queries.getProperty(queryName));
		request.getVariables().putAll(variables);
		return request;
	}

	/**
	 * constructs the POST body as a graphQL StringEntity
	 * 
	 * @param queryName
	 * @return
	 * @throws Exception
	 */
	private GraphQLRequest constructRequestWithSubstitution(String queryName, Object... params) {
		GraphQLRequest request = new GraphQLRequest();
		String query = queries.getProperty(queryName);
		for (int param = 0; param < params.length; param++) {
			query = query.replace("${" + param + "}", toVariableReplacementValue(params[param]));
		}
		request.setQuery(query);
		return request;		
	}

	/**
	 * helper method for converting a value to a variable replacement
	 * 
	 * @param value
	 * @return
	 */
	protected String toVariableReplacementValue(Object value) {
		if (value == null) {
			return "";
		}
		if (value instanceof List) {
			List<?> l = (List<?>) value;
			if (l.isEmpty()) {
				return "";
			}
			else {
				return "\"" + StringUtils.join(l, "\",\"") + "\"";
			}
		} else {
			return value.toString();
		}
	}
}