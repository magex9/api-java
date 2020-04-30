package ca.magex.crm.graphql.model;

import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;

public class GraphQLResponse {

	private JSONObject data;
	private JSONArray errors = new JSONArray();
	
	public JSONObject getData() {
		return data;
	}
	
	public void setData(JSONObject data) {
		this.data = data;
	}
	
	public JSONArray getErrors() {
		return errors;
	}
	
	public void setErrors(JSONArray errors) {
		this.errors = errors;
	}	
}
