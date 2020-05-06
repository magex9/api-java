package ca.magex.crm.graphql.controller;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ca.magex.crm.graphql.service.GraphQLCrmServices;
import ca.magex.crm.graphql.util.MapBuilder;
import graphql.ExecutionInput;

@RestController
public class GraphQLController implements CrmGraphQLController {
	private static Logger logger = LoggerFactory.getLogger(GraphQLController.class);

	@Autowired @Qualifier("graphQLOrganizationService") private GraphQLCrmServices graphQLService;

	@Override
	public ResponseEntity<Object> doQuery(@RequestBody String request, HttpServletRequest req, HttpServletResponse res) throws JSONException {
		Principal principal = req.getUserPrincipal();
		logger.info("Entering doQuery@" + getClass().getSimpleName() + " as " + (principal == null ? "Anonymous" : principal.getName()));
		
		/* 
		 * we need to parse the request ourselves because mapping to the GraphQLRequest object is broken for variables, 
		 * and we want to catch json exceptions and return a 400 instead of a 500 
		 */
		try {
			JSONObject jsonRequest = new JSONObject(request);
			
			MapBuilder variablesBuilder = new MapBuilder();
			if (jsonRequest.has("variables") && jsonRequest.get("variables") != JSONObject.NULL) {
				JSONObject variables = jsonRequest.getJSONObject("variables");
				if (variables.length() > 0) {
					JSONArray variableNames = variables.names();
					for (int i=0; i<variableNames.length(); i++) {
						variablesBuilder.withEntry(variableNames.getString(i), variables.get(variableNames.getString(i)));
					}
				}
			}
	
			ExecutionInput executionInput = ExecutionInput.newExecutionInput()
					.query(jsonRequest.getString("query"))
					.variables(variablesBuilder.build())
					.context(req)
					.build();
	
			return new ResponseEntity<>(graphQLService.getGraphQL().execute(executionInput), HttpStatus.OK);
		}
		catch(JSONException jsone) {
			return new ResponseEntity<Object>(jsone.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@Override
	public ResponseEntity<Object> doQueryAsGet(String query, HttpServletRequest req, HttpServletResponse res) throws JSONException {
		Principal principal = req.getUserPrincipal();
		logger.info("Entering doQuery@" + getClass().getSimpleName() + " as " + (principal == null ? "Anonymous" : principal.getName()));
		
		/* 
		 * we need to parse the request ourselves because mapping to the GraphQLRequest object is broken for variables, 
		 * and we want to catch json exceptions and return a 400 instead of a 500 
		 */
			MapBuilder variablesBuilder = new MapBuilder();
//			if (jsonRequest.has("variables") && jsonRequest.get("variables") != JSONObject.NULL) {
//				JSONObject variables = jsonRequest.getJSONObject("variables");
//				if (variables.length() > 0) {
//					JSONArray variableNames = variables.names();
//					for (int i=0; i<variableNames.length(); i++) {
//						variablesBuilder.withEntry(variableNames.getString(i), variables.get(variableNames.getString(i)));
//					}
//				}
//			}
	
			ExecutionInput executionInput = ExecutionInput.newExecutionInput()
					.query(URLDecoder.decode(query, Charset.forName("UTF8")))
					.variables(variablesBuilder.build())
					.context(req)
					.build();
	
			return new ResponseEntity<>(graphQLService.getGraphQL().execute(executionInput), HttpStatus.OK);
		
	}
}