package ca.magex.crm.graphql.client;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.util.StreamUtils;

import ca.magex.crm.graphql.exceptions.GraphQLClientException;

public abstract class GraphQLClient implements Closeable {

	protected String endpoint;
	protected Properties queries;
	protected CloseableHttpClient httpclient;
	
	/**
	 * constructs a new Service for the given graphql endpoint
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
		}
		catch(Exception e) {
			throw new GraphQLClientException("Cannot load queries from resource '" + queryResource + "'", e);
		}
	}
	
	@Override
	public void close() throws IOException {
		httpclient.close();
	}
	
	/**
	 * Executes the given query and returns the data field
	 * @param queryName
	 * @return
	 */
	protected JSONObject performGraphQLQuery(String queryName, Object ... params) {
		try {
			HttpPost httpPost = new HttpPost(endpoint);
			httpPost.setEntity(constructEntity(queryName, params));
			try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
				JSONObject json = new JSONObject(StreamUtils.copyToString(response.getEntity().getContent(), Charset.forName("UTF-8")));
				JSONArray errors = json.getJSONArray("errors");
				if (errors.length() == 0) {
					return json.getJSONObject("data").getJSONObject(queryName);
				}
				else {
					throw new GraphQLClientException(errors.toString(3));
				}
			}
		}
		catch(Exception e) {
			throw new GraphQLClientException("Error performing graphql query " + queryName, e);
		}
	}
	
	/**
	 * constructs the POST body as a graphQL StringEntity
	 * @param queryName
	 * @return
	 * @throws Exception
	 */
	protected HttpEntity constructEntity(String queryName, Object ... params) throws Exception {
		JSONObject request = new JSONObject();
		String query = queries.getProperty(queryName);
		for (int param=0; param<params.length; param++) {
			query = query.replace("${" + param + "}", toString(params[param]));
		}
		request.put("query", query);
		return new StringEntity(request.toString(3));
	}
	
	protected String toString(Object value) {
		if (value == null) {
			return "";
		}
		else {
			return value.toString();
		}
	}
}