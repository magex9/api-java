package ca.magex.crm.graphql.model;

import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonObject;

public class GraphQLResponse {

	private JsonObject data;
	private JsonArray errors = new JsonArray();
	
	public JsonObject getData() {
		return data;
	}
	
	public void setData(JsonObject data) {
		this.data = data;
	}
	
	public JsonArray getErrors() {
		return errors;
	}
	
	public void setErrors(JsonArray errors) {
		this.errors = errors;
	}	
}
