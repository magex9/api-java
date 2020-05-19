package ca.magex.crm.graphql.model;

import java.util.HashMap;
import java.util.Map;

public class GraphQLRequest {

	private String query;
	private Map<String,Object> variables = new HashMap<>();
	
	public void setQuery(String query) {
		this.query = query;
	}
	
	public String getQuery() {
		return query;
	}
	
	public Map<String, Object> getVariables() {
		return variables;
	}
}