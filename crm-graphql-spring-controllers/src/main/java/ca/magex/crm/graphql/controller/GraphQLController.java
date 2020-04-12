package ca.magex.crm.graphql.controller;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.magex.crm.graphql.service.GraphQLCrmServices;
import graphql.ExecutionInput;
import graphql.servlet.internal.GraphQLRequest;

@RequestMapping("/graphql")
@RestController
public class GraphQLController {
	private static Logger logger = LoggerFactory.getLogger(GraphQLController.class);

	@Autowired @Qualifier("graphQLOrganizationService") private GraphQLCrmServices graphQLService;

	@PostMapping
	public ResponseEntity<Object> doQuery(@RequestBody GraphQLRequest request, HttpServletRequest req) throws JSONException {
		Principal principal = req.getUserPrincipal();
		logger.info("Entering doQuery@" + getClass().getSimpleName() + " as " + principal);

		ExecutionInput executionInput = ExecutionInput.newExecutionInput()
				.query(request.getQuery())
				.variables(request.getVariables())
				.context(req)
				.build();

		return new ResponseEntity<>(graphQLService.getGraphQL().execute(executionInput), HttpStatus.OK);
	}
}