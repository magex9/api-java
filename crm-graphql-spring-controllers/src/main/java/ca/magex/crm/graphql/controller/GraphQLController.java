package ca.magex.crm.graphql.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ca.magex.crm.graphql.service.GraphQLCrmServices;
import ca.magex.crm.graphql.util.MapBuilder;
import graphql.ExecutionInput;

@RestController
@CrossOrigin
public class GraphQLController implements CrmGraphQLController {
	
	private static Logger logger = LoggerFactory.getLogger(GraphQLController.class);

	private GraphQLCrmServices graphQLService;
	
	public GraphQLController(GraphQLCrmServices graphQLService) {
		this.graphQLService = graphQLService;
	}
	
	@ResponseBody
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value="/graphql/schema")
	public String doGetSchema() throws IOException {
		try (InputStream schema = getClass().getResourceAsStream("/crm.graphql")) {
			return StreamUtils.copyToString(schema, Charset.forName("UTF-8"));
		}
	}

	@Override
	public ResponseEntity<Object> doQuery(String content, HttpServletRequest req, HttpServletResponse res) throws JSONException {
		Principal principal = req.getUserPrincipal();
		logger.info("Entering doQuery@" + getClass().getSimpleName() + " as " + (principal == null ? "Anonymous" : principal.getName()));
		
		if (content == null) {
			return new ResponseEntity<Object>("No content provided", HttpStatus.BAD_REQUEST);
		}
		
		try {
			String query = null;
			MapBuilder variablesBuilder = new MapBuilder();
			/* check if we have been provided a json query, or a graphql query directly */
			if (req.getHeader(HttpHeaders.CONTENT_TYPE) == null) {
				return new ResponseEntity<Object>("No " + HttpHeaders.CONTENT_TYPE + " specified, expected one of 'application/json' or 'application/graphql'", HttpStatus.BAD_REQUEST);
			}
			else if (req.getHeader(HttpHeaders.CONTENT_TYPE).contains(MediaType.APPLICATION_JSON_VALUE)) {
				JSONObject jsonRequest = new JSONObject(content);
				query = jsonRequest.getString("query");
				if (jsonRequest.has("variables") && jsonRequest.get("variables") != JSONObject.NULL) {
					parseVariables(variablesBuilder, jsonRequest.getJSONObject("variables"));
				}
			}
			else if (req.getHeader(HttpHeaders.CONTENT_TYPE).contains("application/graphql")) {
				query = content;
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
				
		MapBuilder variablesBuilder = new MapBuilder();
		try {
			if (variables != null) {
				parseVariables(variablesBuilder, new JSONObject(variables));
			}
		}
		catch(JSONException jsone) {
			logger.warn("Non parseable variables provided: " + jsone.getMessage());
			return new ResponseEntity<Object>("Non parseable variables provided", HttpStatus.BAD_REQUEST);
		}
		
		/* do not accept mutations over GET */
		if (query.contains("mutation")) {
			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.ALLOW, "POST");
			return new ResponseEntity<Object>("Cannot process mutation over GET method", headers, HttpStatus.METHOD_NOT_ALLOWED);
		}
			
		ExecutionInput executionInput = ExecutionInput.newExecutionInput()
				.query(URLDecoder.decode(query, Charset.forName("UTF8")))
				.variables(variablesBuilder.build())
				.context(req)
				.build();

		return new ResponseEntity<>(graphQLService.getGraphQL().execute(executionInput), HttpStatus.OK);
	}
	
	/**
	 * helper method for parsing the variables sent in as json format
	 * @param variablesBuilder
	 * @param jsonVariables
	 * @return
	 * @throws JSONException
	 */
	private MapBuilder parseVariables(MapBuilder variablesBuilder, JSONObject jsonVariables) throws JSONException {
		if (jsonVariables.length() > 0) {
			JSONArray variableNames = jsonVariables.names();
			for (int i=0; i<variableNames.length(); i++) {
				Object variable = jsonVariables.get(variableNames.getString(i));
				/* here we need to handle an array variable */
				if (variable instanceof JSONArray) {
					JSONArray variableArray = (JSONArray) variable;
					List<Object> variableList = new ArrayList<>();
					for (int j=0; j<variableArray.length(); j++) {
						variableList.add(variableArray.get(j));
					}
					variablesBuilder.withEntry(variableNames.getString(i), variableList);
				}
				else {
					variablesBuilder.withEntry(variableNames.getString(i), variable);
				}
			}
		}
		return variablesBuilder;
	}
}