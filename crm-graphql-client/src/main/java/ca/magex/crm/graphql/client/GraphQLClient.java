package ca.magex.crm.graphql.client;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
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

import ca.magex.crm.graphql.exceptions.GraphQLClientException;

/**
 * HTTP client that handles executing GraphQL queries and returning the response
 * 
 * @author Jonny
 *
 */
public abstract class GraphQLClient implements Closeable {

	private static final Logger LOG = LoggerFactory.getLogger(GraphQLClient.class);

	protected String endpoint;
	protected Properties queries;
	protected CloseableHttpClient httpclient;

	private String jwtToken;

	/**
	 * constructs a new Service for the given graphql endpoint
	 * 
	 * @param endpoint
	 */
	public GraphQLClient(String endpoint, String queryResource) {
		this.endpoint = endpoint;
		this.httpclient = HttpClients.createDefault();
		this.queries = new Properties();
		try {
			try (InputStream in = OrganizationServiceGraphQLClient.class.getResource(queryResource).openStream()) {
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
		long t1 = System.currentTimeMillis();
		try {
			HttpPost httpPost = new HttpPost(authEndpoint);
			JSONObject json = new JSONObject();
			json.put("username", username);
			json.put("password", password);
			httpPost.setEntity(new StringEntity(json.toString()));
			httpPost.setHeader("Content-Type", "application/json");
			try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					JSONObject token = new JSONObject(StreamUtils.copyToString(response.getEntity().getContent(), Charset.forName("UTF-8")));
					this.jwtToken = token.getString("token");
				} else {
					throw new GraphQLClientException("Status: " + response.getStatusLine().getStatusCode());
				}
			}
		} catch (Exception e) {
			throw new GraphQLClientException("Error during authentication", e);
		} finally {
			if (LOG.isDebugEnabled()) {
				LOG.debug("execution of authenticate() took " + (System.currentTimeMillis() - t1) + "ms.");
			}
		}
	}

	@Override
	public void close() throws IOException {
		httpclient.close();
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
		return performGraphQLQueryd(queryName, constructEntityWithVariables(queryId, variables));
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
		return performGraphQLQueryd(queryName, constructEntityWithSubstitution(queryId, params));
	}

	@SuppressWarnings("unchecked")
	private <T> T performGraphQLQueryd(String queryName, HttpEntity entity) {
		long t1 = System.currentTimeMillis();
		try {
			if (jwtToken == null) {
				throw new GraphQLClientException("Not Authenticated");
			}
			HttpPost httpPost = new HttpPost(endpoint);
			httpPost.addHeader("Authorization", "Bearer " + jwtToken);
			httpPost.setHeader("Content-Type", "application/json");
			httpPost.setEntity(entity);
			try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					JSONObject json = new JSONObject(StreamUtils.copyToString(response.getEntity().getContent(), Charset.forName("UTF-8")));
					JSONArray errors = json.getJSONArray("errors");
					if (errors.length() == 0) {
						return (T) json.getJSONObject("data").get(queryName);
					} else {
						throw new GraphQLClientException(errors.toString(3));
					}
				} else {
					throw new GraphQLClientException("Status: " + response.getStatusLine().getStatusCode());
				}
			}
		} catch (Exception e) {
			throw new GraphQLClientException("Error performing graphql query " + queryName, e);
		} finally {
			if (LOG.isDebugEnabled()) {
				LOG.debug("execution of " + queryName + "(" + entity + ") took " + (System.currentTimeMillis() - t1) + "ms.");
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
	private HttpEntity constructEntityWithVariables(String queryName, Map<String, Object> variables) {
		try {
			JSONObject request = new JSONObject();
			request.put("query", queries.getProperty(queryName));
			request.put("variables", new JSONObject(variables));
			return new StringEntity(request.toString(3));
		} catch (Exception e) {
			throw new GraphQLClientException("Unable to construct entity with variables", e);
		}
	}

	/**
	 * constructs the POST body as a graphQL StringEntity
	 * 
	 * @param queryName
	 * @return
	 * @throws Exception
	 */
	private HttpEntity constructEntityWithSubstitution(String queryName, Object... params) {
		try {
			JSONObject request = new JSONObject();
			String query = queries.getProperty(queryName);
			for (int param = 0; param < params.length; param++) {
				query = query.replace("${" + param + "}", toVariableReplacementValue(params[param]));
			}
			request.put("query", query);
			return new StringEntity(request.toString(3));
		} catch (Exception e) {
			throw new GraphQLClientException("Unable to construct entity with variables", e);
		}
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
			return "\"" + StringUtils.join(l, "\",\"") + "\"";
		} else {
			return value.toString();
		}
	}
}