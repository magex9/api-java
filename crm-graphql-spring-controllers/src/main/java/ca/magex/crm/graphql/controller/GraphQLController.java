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
import org.springframework.http.HttpHeaders;
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
	public ResponseEntity<Object> doQuery(@RequestBody(required = false) String request, HttpServletRequest req, HttpServletResponse res) throws JSONException {
		Principal principal = req.getUserPrincipal();
		logger.info("Entering doQuery@" + getClass().getSimpleName() + " as " + (principal == null ? "Anonymous" : principal.getName()));
		
		if (request == null) {
			return new ResponseEntity<Object>("No content provided", HttpStatus.BAD_REQUEST);
		}
		
		try {
			String query = null;
			MapBuilder variablesBuilder = new MapBuilder();
			/* check if we have been provided a json query, or a graphql query directly */
			if (req.getHeader(HttpHeaders.CONTENT_TYPE) == null) {
				return new ResponseEntity<Object>("No " + HttpHeaders.CONTENT_TYPE + " specified, expected one of 'application/json' or 'application/graphql'", HttpStatus.BAD_REQUEST);
			}
			else if (req.getHeader(HttpHeaders.CONTENT_TYPE).contains("application/json")) {
				JSONObject jsonRequest = new JSONObject(request);
				query = jsonRequest.getString("query");
				if (jsonRequest.has("variables") && jsonRequest.get("variables") != JSONObject.NULL) {
					JSONObject variables = jsonRequest.getJSONObject("variables");
					if (variables.length() > 0) {
						JSONArray variableNames = variables.names();
						for (int i=0; i<variableNames.length(); i++) {
							variablesBuilder.withEntry(variableNames.getString(i), variables.get(variableNames.getString(i)));
						}
					}
				}
			}
			else if (req.getHeader(HttpHeaders.CONTENT_TYPE).contains("application/graphql")) {
				query = request;
			}
			else {
				return new ResponseEntity<Object>("Unknown " + HttpHeaders.CONTENT_TYPE + " specified, expected one of 'application/json' or 'application/graphql'", HttpStatus.BAD_REQUEST);
			}
	
			ExecutionInput executionInput = ExecutionInput.newExecutionInput()
					.query(query)
					.variables(variablesBuilder.build())
					.context(req)
					.build();
	
			return new ResponseEntity<>(graphQLService.getGraphQL().execute(executionInput), HttpStatus.OK);
		}
		catch(JSONException jsone) {
			logger.warn("Non parseable contents provided: " + jsone.getMessage());
			return new ResponseEntity<Object>("Non parseable contents provided", HttpStatus.BAD_REQUEST);
		}
	}
	
	@Override
	public ResponseEntity<Object> doQueryAsGet(String query, String variables, HttpServletRequest req, HttpServletResponse res) throws JSONException {
		Principal principal = req.getUserPrincipal();
		logger.info("Entering doQuery@" + getClass().getSimpleName() + " as " + (principal == null ? "Anonymous" : principal.getName()));
		/* do not accept mutations over GET */
		if (query.contains("mutation")) {
			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.ALLOW, "POST");
			return new ResponseEntity<Object>("Cannot process mutation over GET method", headers, HttpStatus.METHOD_NOT_ALLOWED);
		}
				
		MapBuilder variablesBuilder = new MapBuilder();
		try {
			if (variables != null) {
				JSONObject jsonVariables = new JSONObject(variables);			
				if (jsonVariables.length() > 0) {
					JSONArray variableNames = jsonVariables.names();
					for (int i=0; i<variableNames.length(); i++) {
						variablesBuilder.withEntry(variableNames.getString(i), jsonVariables.get(variableNames.getString(i)));
					}
				}
			}
		}
		catch(JSONException jsone) {
			logger.warn("Non parseable variables provided: " + jsone.getMessage());
			return new ResponseEntity<Object>("Non parseable variables provided", HttpStatus.BAD_REQUEST);
		}
			
		ExecutionInput executionInput = ExecutionInput.newExecutionInput()
				.query(URLDecoder.decode(query, Charset.forName("UTF8")))
				.variables(variablesBuilder.build())
				.context(req)
				.build();

		return new ResponseEntity<>(graphQLService.getGraphQL().execute(executionInput), HttpStatus.OK);
	}
}